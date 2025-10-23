package com.ny.controller;

import com.ny.entity.Device;
import com.ny.entity.Result;
import com.ny.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /**
     * 查询所有设备信息
     * http://localhost:8082/selectAllDevice
     */
    @GetMapping("/selectAllDevice")
    public Result getAllDevices() {
        return Result.success(deviceService.findAllDevices());
    }

    /**
     * 根据设备名称查询设备信息
     * http://localhost:8082/findByDeviceName?device_name=
     */
    @GetMapping("/findByDeviceName")
    public Result getDevicesByName(@RequestParam("device_name") String device_name) {
        Device device = deviceService.findDeviceByName(device_name);
        if (device!=null){
            return Result.success(device);
        }else {
            return Result.error("设备不存在");
        }
    }

    /**
     * 根据设备ID删除设备信息
     * http://localhost:8082/deleteByDeviceId?device_id=
     */
    @DeleteMapping("/deleteByDeviceId")
    public Result deleteDevice(@RequestParam("device_id") Integer device_id) {
        int n = deviceService.deleteDeviceById(device_id);
        if (n > 0) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
}
