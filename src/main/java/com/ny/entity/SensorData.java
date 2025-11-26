package com.ny.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Date;

@Document(collection = "sensor_data")
public class SensorData {
    @Id
    private String id;

    // 实体类属性名为 device_id，数据库字段名也为 device_id
    @Field("device_id")
    private String device_id;

    @Field("temperature")
    private Double temperature;

    @Field("humidity")
    private Double humidity;

    @Field("ledStatus")
    private Boolean ledStatus;

    @Field("time")
    private Date time;

    // 构造方法、getter、setter
    public SensorData() {}

    public SensorData(String device_id, Double temperature, Double humidity, Boolean ledStatus, Date time) {
        this.device_id = device_id;
        this.temperature = temperature;
        this.humidity = humidity;
        this.ledStatus = ledStatus;
        this.time = time;
    }

    // Getter 和 Setter 方法
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDevice_id() { return device_id; }
    public void setDevice_id(String device_id) { this.device_id = device_id; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public Boolean getLedStatus() { return ledStatus; }
    public void setLedStatus(Boolean ledStatus) { this.ledStatus = ledStatus; }

    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
}