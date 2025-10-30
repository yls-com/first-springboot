package com.ny.controller;

import com.ny.entity.Result;
import com.ny.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeviceController {

    @Autowired
    private DeviceService deviceService;
    //`http://localhost:8082/findAllDevices`
    @GetMapping("/findAllDevices")
    public Result findAllDevices(){
        return Result.success(deviceService.findAll());
    }

    /**
     * 根据设备名称查询设备数据
     * @param name 设备名称
     * @return 设备列表
     */
    //http://localhost:8082/findDeviceByName?name=<设备名称>
    @GetMapping("/findDeviceByName")
    public Result findDeviceByName(@RequestParam String name){
        return Result.success(deviceService.findByName(name));
    }

//    6节：实现查询所有的设备数据、实现根据设备名称查询设备数据
//    7节：添加一条设备信息       根据设备id修改设备信息：比如把设备id为2的设备名称改为“灯泡”
//    8节：根据设备id删除设备数据。   根据设备名称模糊查询设备数据。比如你输入 厨 =》得到包含所有的厨字的设备、
//     提交截图以及git提交路径
//    图片命名姓名-功能、
//    提交的时间：5.50下课提交。

}