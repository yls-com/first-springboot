package com.ny.controller;

import com.ny.entity.Device;
import com.ny.entity.Result;
import com.ny.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DeviceController {

    @Autowired
    private DeviceService deviceService;
    //http://localhost:8081/findAllDevices
    @GetMapping("/findAllDevices")
    public Result findAllDevices(){
        return Result.success(deviceService.findAll());
    }

    /**
     * 根据设备名称查询设备数据
     * @param name 设备名称
     * @return 设备列表
     */
    //http://localhost:8081/findDeviceByName?name=<设备名称>
    @GetMapping("/findDeviceByName")
    public Result findDeviceByName(@RequestParam String name){
        return Result.success(deviceService.findByName(name));
    }

    /**
     * 添加一条设备信息
     * @param device_id 设备ID
     * @param name 设备名称
     * @param type 设备类型
     * @param status 设备状态
     * @param brightness 设备亮度（可选，默认为0）
     * @param room 房间名称
     * @return 添加结果
     */
    //http://localhost:8081/addDevice
    @PostMapping("/addDevice")
    public Result addDevice(@RequestParam int device_id,
                            @RequestParam String name,
                            @RequestParam String type,
                            @RequestParam String status,
                            @RequestParam(required = false, defaultValue = "0") int brightness,
                            @RequestParam String room) {
        Device device = new Device();
        device.setDevice_id(device_id);
        device.setName(name);
        device.setType(type);
        device.setStatus(status);
        device.setBrightness(brightness);
        device.setRoom(room);
        device.setInstall_time(new java.util.Date()); // 设置安装时间为当前时间
        Device savedDevice = deviceService.addDevice(device);
        return Result.success(savedDevice);
    }

    /**
     * 批量添加设备信息
     * @param devices 设备列表
     * @return 添加结果
     */
//http://localhost:8081/batchAddDevices
    @PostMapping("/batchAddDevices")
    public Result batchAddDevices(@RequestBody List<Device> devices) {
        if (devices == null || devices.isEmpty()) {
            return Result.error("设备列表不能为空");
        }

        List<Device> savedDevices = new ArrayList<>();
        for (Device device : devices) {
            // 设置安装时间为当前时间
            device.setInstall_time(new java.util.Date());
            Device savedDevice = deviceService.addDevice(device);
            if (savedDevice != null) {
                savedDevices.add(savedDevice);
            }
        }

        if (savedDevices.size() == devices.size()) {
            return Result.success(savedDevices);
        } else {
            return Result.error("部分设备添加失败");
        }
    }

    /**
     * 根据设备ID修改设备信息
     * @param device_id 设备ID
     * @param name 新的设备名称（可选）
     * @param type 新的设备类型（可选）
     * @param status 新的设备状态（可选）
     * @param brightness 新的设备亮度（可选）
     * @param room 新的房间名称（可选）
     * @return 更新后的设备信息
     */
    //http://localhost:8081/updateDeviceById
    @PostMapping("/updateDeviceById")
    public Result updateDeviceById(@RequestParam int device_id,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) String type,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false, defaultValue = "-1") int brightness,
                                   @RequestParam(required = false) String room) {
        Device device = new Device();
        device.setDevice_id(device_id);
        if (name != null && !name.isEmpty()) {
            device.setName(name);
        }
        if (type != null && !type.isEmpty()) {
            device.setType(type);
        }
        if (status != null && !status.isEmpty()) {
            device.setStatus(status);
        }
        if (brightness != -1) {
            device.setBrightness(brightness);
        }
        if (room != null && !room.isEmpty()) {
            device.setRoom(room);
        }
        
        Device updatedDevice = deviceService.updateDeviceById(device_id, device);
        if (updatedDevice != null) {
            return Result.success(updatedDevice);
        } else {
            return Result.error("设备不存在");
        }
    }

    /**
     * 根据设备ID删除设备数据
     * @param device_id 设备ID
     * @return 删除结果
     */
    //http://localhost:8081/deleteDeviceById?device_id=2
    @DeleteMapping("/deleteDeviceById")
    public Result deleteDeviceById(@RequestParam int device_id) {
        boolean deleted = deviceService.deleteDeviceById(device_id);
        if (deleted) {
            return Result.success("设备删除成功");
        } else {
            return Result.error("设备不存在");
        }
    }
    
    /**
     * 根据设备名称模糊查询设备数据
     * @param name 设备名称关键字
     * @return 匹配的设备列表
     */
    //http://localhost:8081/findDeviceByNameContaining?name=厨
    @RequestMapping(value = "/findDeviceByNameContaining", method = {RequestMethod.GET, RequestMethod.POST})
    public Result findDeviceByNameContaining(@RequestParam String name) {
        return Result.success(deviceService.findByNameContaining(name));
    }

//    6节：实现查询所有的设备数据、实现根据设备名称查询设备数据
//    7节：添加一条设备信息       根据设备id修改设备信息：比如把设备id为2的设备名称改为"灯泡"
//    8节：根据设备id删除设备数据。   根据设备名称模糊查询设备数据。比如你输入 厨 =》得到包含所有的厨字的设备、
//     提交截图以及git提交路径
//    图片命名姓名-功能、
//    提交的时间：5.50下课提交。

}