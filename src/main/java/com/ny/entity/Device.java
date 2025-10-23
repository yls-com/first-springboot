package com.ny.entity;

import java.util.Date;

public class Device {
    /**
     * 设备唯一标识
     */
    private Integer device_id;

    /**
     * 设备序列号（硬件唯一标识）
     */
    private String device_sn;

    /**
     * 设备名称（如客厅主灯）
     */
    private String device_name;

    /**
     * 设备类型：1-灯光 2-空调 3-门锁 4-窗帘 5-传感器
     */
    private Byte device_type;

    /**
     * 所属房间ID
     */
    private Integer room_id;

    /**
     * 在线状态：0-离线 1-在线
     */
    private Byte status;

    /**
     * 通信协议（WiFi/MQTT/ZigBee）
     */
    private String protocol;

    /**
     * 创建时间
     */
    private Date created_time;

    // Getters and Setters

    public Integer getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Integer device_id) {
        this.device_id = device_id;
    }

    public String getDevice_sn() {
        return device_sn;
    }

    public void setDevice_sn(String device_sn) {
        this.device_sn = device_sn;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public Byte getDevice_type() {
        return device_type;
    }

    public void setDevice_type(Byte device_type) {
        this.device_type = device_type;
    }

    public Integer getRoom_id() {
        return room_id;
    }

    public void setRoom_id(Integer room_id) {
        this.room_id = room_id;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }
}
