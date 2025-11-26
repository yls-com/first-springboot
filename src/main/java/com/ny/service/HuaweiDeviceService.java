package com.ny.service;

import com.huaweicloud.sdk.iot.device.IoTDevice;
import com.huaweicloud.sdk.iot.device.client.requests.ServiceProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
public class HuaweiDeviceService {

    @Value("${huawei.iot.server.uri}")
    private String serverUri;

    @Value("${huawei.iot.device.id}")
    private String deviceId;

    @Value("${huawei.iot.device.secret}")
    private String deviceSecret;

    private IoTDevice device;
    private Map<String, Object> currentProperties = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            // 加载证书
            File caFile = loadCertificate();

            // 创建设备
            device = new IoTDevice(serverUri, deviceId, deviceSecret, caFile);

            if (device.init() == 0) {
                log.info("华为云设备连接成功");
                initializeProperties();
            } else {
                log.error("华为云设备连接失败");
            }
        } catch (Exception e) {
            log.error("初始化华为云设备失败", e);
        }
    }

    private File loadCertificate() throws Exception {
        String caPath = "ca.jks";
        String tmpPath = "huawei-tmp-" + caPath;
        File tmpFile = new File(tmpPath);

        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(caPath)) {
            if (resource != null) {
                Files.copy(resource, tmpFile.toPath(), REPLACE_EXISTING);
                return tmpFile;
            } else {
                throw new Exception("证书文件未找到: " + caPath);
            }
        }
    }
    //根据自己定义的设备属性更改
    private void initializeProperties() {
        currentProperties.put("temp", 25.0);
        currentProperties.put("humi", 50.0);
        currentProperties.put("led", 0);
    }

    /**
     * 上报属性
     */
    public void reportProperties(Map<String, Object> properties) {
        if (device == null) return;

        // 更新本地属性
        currentProperties.putAll(properties);

        // 创建服务属性
        ServiceProperty serviceProperty = new ServiceProperty();
        serviceProperty.setServiceId("temp-humi");//替换成自己的服务属性
        serviceProperty.setProperties(properties);

        // 上报属性
        device.getClient().reportProperties(java.util.Arrays.asList(serviceProperty),
                new com.huaweicloud.sdk.iot.device.transport.ActionListener() {
                    @Override
                    public void onSuccess(Object context) {
                        log.debug("属性上报成功");
                    }

                    @Override
                    public void onFailure(Object context, Throwable throwable) {
                        log.error("属性上报失败", throwable);
                    }
                });
    }

    /**
     * 获取当前属性
     */
    public Map<String, Object> getCurrentProperties() {
        return new HashMap<>(currentProperties);
    }
}