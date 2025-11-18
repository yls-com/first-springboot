package com.ny.entity;

/**
 * 传感器数据实体类，用于封装温度、湿度、LED状态数据
 * 作为数据传输的载体，被服务层和控制层共享
 */
public class SensorData {
    // 温度（double类型，支持小数）
    private double temperature;
    // 湿度（int类型，整数）
    private int humi;
    // LED状态（int类型，通常0表示关闭，1表示开启）
    private int led;

    // 无参构造方法（JSON反序列化、Spring实例化必需）
    public SensorData() {
    }

    // 全参构造方法，用于快速创建对象
    public SensorData(double temperature, int humi, int led) {
        this.temperature = temperature;
        this.humi = humi;
        this.led = led;
    }

    // Getter/Setter方法：用于属性的读写（封装性体现）
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getHumi() {
        return humi;
    }

    public void setHumi(int humi) {
        this.humi = humi;
    }

    public int getLed() {
        return led;
    }

    public void setLed(int led) {
        this.led = led;
    }

    // 重写toString方法：方便日志打印时查看对象内容
    @Override
    public String toString() {
        return "SensorData{" +
                "temperature=" + temperature +
                ", humi=" + humi +
                ", led=" + led +
                '}';
    }
}