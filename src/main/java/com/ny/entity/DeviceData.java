package com.ny.entity;

import lombok.Data;
import java.util.Map;

@Data
public class DeviceData {
    private Map<String, Object> properties;

    public DeviceData() {}

    public DeviceData(Map<String, Object> properties) {
        this.properties = properties;
    }
}