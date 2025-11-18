package com.ny.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ny.entity.SensorData;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import javax.net.ssl.SSLContext;

/**
 * MQTT消息服务类，负责与华为云MQTT服务器的连接、消息订阅/发布
 * 实现MqttCallback接口，处理连接断开、消息到达等回调事件
 */
@Service // 标记为Spring服务组件，交由Spring容器管理
public class MqttMessageService implements MqttCallback {

    // 日志工具：用于打印运行日志（替代System.out）
    private static final Logger logger = LoggerFactory.getLogger(MqttMessageService.class);

    // 从配置文件注入华为云MQTT连接参数
    @Value("${huawei.mqtt.host}")
    private String host; // MQTT服务器地址

    @Value("${huawei.mqtt.port}")
    private int port; // MQTT服务器端口（8883为SSL/TLS默认端口）

    @Value("${huawei.mqtt.clientId}")
    private String clientId; // 客户端ID（华为云要求唯一）

    @Value("${huawei.mqtt.username}")
    private String username; // 连接用户名（华为云设备认证信息）

    @Value("${huawei.mqtt.password}")
    private String password; // 连接密码（华为云设备认证信息）

    // 订阅主题：接收设备属性设置指令（华为云物模型标准主题格式）
    private String subscribeTopic = "$oc/devices/6917e62cf69b1239b07e7ce8_sht30-01/sys/commands/#";
    // 发布主题：上报设备属性数据（华为云物模型标准主题格式）
    private String publishTopic = "$oc/devices/6917e62cf69b1239b07e7ce8_sht30-01/sys/properties/report";

    // 传感器数据对象：存储最新接收的传感器数据
    private SensorData sensorData;
    // 连接状态标记：记录客户端是否已连接
    private boolean isConnected = false;
    // MQTT客户端实例：Paho库的核心类，负责连接和消息处理
    private MqttClient client;
    // 线程锁：保证多线程下操作sensorData的线程安全
    private final Object lock = new Object();
    // MQTT服务器连接地址（格式：ssl://host:port）
    private String brokerUrl;

    /**
     * 初始化方法：Spring容器启动时自动调用
     * 完成MQTT客户端初始化、连接服务器、订阅主题
     */
    @PostConstruct
    public void init() {
        try {
            // 构建MQTT服务器连接地址 (华为云使用SSL连接)
            this.brokerUrl = "ssl://" + host + ":" + port;

            // 初始化传感器数据对象
            sensorData = new SensorData();
            // 创建MQTT客户端：参数分别为服务器地址、客户端ID、消息持久化方式（内存持久化）
            client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            // 设置回调对象：当前类实现了MqttCallback，处理连接断开、消息到达等事件
            client.setCallback(this);

            // 配置连接参数
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username); // 设置用户名
            options.setPassword(password.toCharArray()); // 设置密码（字符数组形式）
            options.setConnectionTimeout(30); // 设置连接超时时间
            options.setKeepAliveInterval(60); // 设置心跳包发送间隔
            options.setCleanSession(true); // 设置是否清除会话
            
            // 配置SSL连接
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, new SecureRandom());
            options.setSocketFactory(sslContext.getSocketFactory());
            
            // 设置SSL主机名验证器（开发环境可以禁用，生产环境建议启用）
            options.setHttpsHostnameVerificationEnabled(false);
            
            // 连接MQTT服务器
            client.connect(options);
            logger.info("成功连接到 MQTT 服务器：{}", brokerUrl);
            isConnected = true;

            // 订阅主题（默认QoS=1，确保消息至少到达一次）
            client.subscribe(subscribeTopic);

        } catch (MqttException | NoSuchAlgorithmException | KeyManagementException e) {
            // 连接失败时记录错误日志
            logger.error("连接 MQTT 服务器失败：{}", e.getMessage(), e);
            isConnected = false;
        }
    }

    /**
     * 连接丢失回调：当MQTT连接断开时自动调用
     * @param cause 连接丢失的原因
     */
    @Override
    public void connectionLost(Throwable cause) {
        logger.warn("MQTT 连接丢失：{}", cause.getMessage());
        // 尝试重新连接
        reconnect();
    }

    /**
     * 重新连接方法：当连接丢失后尝试重新连接并订阅主题
     */
    private void reconnect() {
        // 如果客户端为空或已连接，则无需重连
        if (client == null || client.isConnected()) return;

        try {
            // 重新配置连接参数
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(60);
            options.setCleanSession(true);
            
            // 配置SSL连接
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, new SecureRandom());
            options.setSocketFactory(sslContext.getSocketFactory());
            options.setHttpsHostnameVerificationEnabled(false);
            
            client.connect(options); // 重新连接
            client.subscribe(subscribeTopic); // 重新订阅主题
            logger.info("MQTT 已重新连接并订阅主题");
        } catch (MqttException | NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("MQTT 重连失败", e);
        }
    }

    /**
     * 消息到达回调：当订阅的主题有新消息时自动调用
     * @param topic 消息所属的主题
     * @param message 收到的MQTT消息对象
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // 将消息 payload 转换为字符串（payload为字节数组）
        String payload = new String(message.getPayload());
        // 使用Jackson解析JSON格式的消息
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(payload);

        // 校验消息格式：确保包含对应的命令字段
        if (rootNode.has("paras") && rootNode.get("paras").has("temperature")
                && rootNode.get("paras").has("humi") && rootNode.get("paras").has("led")) {

            // 加锁修改sensorData，避免多线程并发修改导致数据不一致
            synchronized (lock) {
                // 从JSON中提取属性值并设置到sensorData
                sensorData.setTemperature(rootNode.get("paras").get("temperature").asDouble());
                sensorData.setHumi(rootNode.get("paras").get("humi").asInt());
                sensorData.setLed(rootNode.get("paras").get("led").asInt());
            }

            // 打印日志，确认消息处理结果
            logger.info("收到消息：{}", sensorData);
            logger.info("温度：{}", sensorData.getTemperature());
            logger.info("湿度（humi）：{}", sensorData.getHumi());
        } else {
            logger.warn("未找到所需属性");
        }
    }

    /**
     * 消息交付完成回调：当QoS>0时，消息确认交付后调用
     * 此处不使用QoS1/2，暂时空实现
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 不使用 QoS1/2 可不实现
    }

    /**
     * 发布属性设置消息到MQTT服务器
     * @param propertyName 属性名称（如"temperature"、"led"）
     * @param value 属性值（支持Boolean、Double、Integer类型）
     */
    public void setProperty(String propertyName, Object value) {
        // 校验客户端状态：未初始化或未连接则直接返回
        if (client == null || !client.isConnected()) {
            logger.warn("MQTT 客户端未正确初始化或未连接");
            return;
        }

        try {
            // 构建JSON格式的消息体（符合华为云物模型协议）
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("services", mapper.createArrayNode().add(mapper.createObjectNode()
                    .put("service_id", "sht30")
                    .set("properties", mapper.createObjectNode()
                            .putPOJO(propertyName, value))));

            // 创建MQTT消息对象，将JSON转换为字节数组
            MqttMessage mqttMessage = new MqttMessage(mapper.writeValueAsBytes(rootNode));
            // 发布消息：参数为主题、消息体、QoS=1（确保至少到达一次）、retain=false（不保留消息）
            client.publish(publishTopic, mqttMessage.getPayload(), 1, false);

        } catch (MqttException | JsonProcessingException e) {
            logger.error("发布 MQTT 消息失败", e);
        }
    }

    /**
     * 获取连接状态
     * @return 是否已连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 获取最新的传感器数据（线程安全）
     * @return 传感器数据对象
     */
    public SensorData getSensorData() {
        synchronized (lock) {
            return sensorData; // 实际项目中建议返回副本（如new SensorData(...)），避免外部修改
        }
    }

    /**
     * 销毁方法：Spring容器关闭时自动调用
     * 断开MQTT连接，释放资源
     */
    @PreDestroy
    public void destroy() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect(); // 断开连接
                logger.info("MQTT 客户端已断开连接");
            }
        } catch (MqttException e) {
            logger.error("MQTT 断开连接失败", e);
        }
    }
}