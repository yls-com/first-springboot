package com.ny.controller;

import com.ny.entity.DeviceData;
import com.ny.service.HuaweiDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HuaWeiDeviceController {

    @Autowired
    private HuaweiDeviceService deviceService;

    @GetMapping("/device/properties")
    public DeviceData getProperties() {
        Map<String, Object> properties = deviceService.getCurrentProperties();
        return new DeviceData(properties);
    }

    //http://localhost:8081/device/properties
    @PostMapping("/device/properties")
    public String setProperties(@RequestBody DeviceData deviceData) {
        deviceService.reportProperties(deviceData.getProperties());
        return "属性上报成功";
    }
}