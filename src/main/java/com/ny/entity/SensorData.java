package com.ny.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Document(collection = "sensor_data")
@Schema(description = "传感器数据实体类")
public class SensorData {
    @Id
    @Schema(description = "MongoDB文档ID")
    private String id;

    // 实体类属性名为 device_id，数据库字段名也为 device_id
    @Field("device_id")
    @Schema(description = "设备ID")
    private String device_id;

    @Field("temperature")
    @Schema(description = "温度")
    private Double temperature;

    @Field("humidity")
    @Schema(description = "湿度")
    private Double humidity;

    @Field("ledStatus")
    @Schema(description = "LED状态")
    private Boolean ledStatus;

    @Field("time")
    @Schema(description = "时间")
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