package com.ny.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ny.entity.SensorData;
import com.ny.service.MqttMessageService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 传感器数据控制器，提供HTTP接口供前端调用
 * 接收请求并转发给服务层处理
 */
@RestController // 标记为REST风格控制器，返回JSON数据
public class SensorDataController {

    // 注入MQTT服务对象（Spring自动注入）
    @Autowired
    private MqttMessageService mqttMessageService;

    /**
     * 获取最新传感器数据的接口
     * 请求方式：GET
     * 访问路径：http://localhost:8081/sensor-data
     * @return 传感器数据对象（自动转为JSON）
     */
    @GetMapping("/sensor-data")
    public SensorData getSensorData() {
        // 打印日志（实际项目中建议用logger）
        System.out.println(mqttMessageService.getSensorData());
        // 调用服务层方法获取数据并返回
        return mqttMessageService.getSensorData();
    }

    /**
     * 设置传感器属性的接口
     * 请求方式：POST
     * 访问路径：http://localhost:8080/set-all-properties
     * 请求体：JSON格式的SensorData对象（包含temperature、humi、led）
     * @param sensorData 前端传入的传感器数据（包含要设置的属性值）
     * @return 操作结果提示
     */
    @PostMapping("/set-all-properties")
    public String setAllProperties(@RequestBody SensorData sensorData) throws MqttException, JsonProcessingException {
        // 调用服务层方法，分别设置温度、湿度、LED属性
        mqttMessageService.setProperty("temperature", sensorData.getTemperature());
        mqttMessageService.setProperty("humi", sensorData.getHumi());
        mqttMessageService.setProperty("led", sensorData.getLed());
        return "success!!"; // 返回成功提示
    }
}