package com.ny.controller;

import com.ny.entity.Device;
import com.ny.entity.Result;
import com.ny.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/device")
@Tag(name = "设备管理", description = "设备相关操作接口")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;
    
    // 设备名称验证正则表达式（只允许中文、英文、数字、下划线）
    private static final Pattern DEVICE_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9_]{1,50}$");
    // 设备类型和房间名称验证正则表达式
    private static final Pattern TEXT_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9_]{1,20}$");

    // http://localhost:8081/device/findAllDevices
    @GetMapping("/findAllDevices")
    @Operation(summary = "查询所有设备", description = "获取系统中的所有设备信息")
    public Result findAllDevices(){
        // 需要添加权限控制
        return Result.success(deviceService.findAll());
    }

    /**
     * 根据设备名称查询设备数据
     * @param name 设备名称
     * @return 设备列表
     */
    // http://localhost:8081/device/findDeviceByName?name=<设备名称>
    @GetMapping("/findDeviceByName")
    @Operation(summary = "根据设备名称查询设备", description = "通过设备名称查找设备信息")
    public Result findDeviceByName(@Parameter(description = "设备名称") @RequestParam String name){
        // 输入验证
        if (name == null || name.trim().isEmpty()) {
            return Result.error("设备名称不能为空");
        }
        if (!DEVICE_NAME_PATTERN.matcher(name).matches()) {
            return Result.error("设备名称格式不正确，只允许中文、英文、数字和下划线，长度不超过50");
        }
        Object result = deviceService.findByName(name);
        if (result == null) {
            return Result.error("设备不存在");
        }
        return Result.success(result);
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
    // http://localhost:8081/device/addDevice
    @PostMapping("/addDevice")
    @Operation(summary = "添加设备", description = "添加单个设备信息")
    public Result addDevice(@Parameter(description = "设备ID") @RequestParam int device_id,
                            @Parameter(description = "设备名称") @RequestParam String name,
                            @Parameter(description = "设备类型") @RequestParam String type,
                            @Parameter(description = "设备状态") @RequestParam String status,
                            @Parameter(description = "设备亮度(可选)") @RequestParam(required = false, defaultValue = "0") int brightness,
                            @Parameter(description = "房间名称") @RequestParam String room) {
        // 输入验证
        if (device_id <= 0) {
            return Result.error("设备ID必须大于0");
        }
        if (name == null || name.trim().isEmpty() || !DEVICE_NAME_PATTERN.matcher(name).matches()) {
            return Result.error("设备名称格式不正确，只允许中文、英文、数字和下划线，长度不超过50");
        }
        if (type == null || type.trim().isEmpty() || !TEXT_PATTERN.matcher(type).matches()) {
            return Result.error("设备类型格式不正确，只允许中文、英文、数字和下划线，长度不超过20");
        }
        if (status == null || status.trim().isEmpty()) {
            return Result.error("设备状态不能为空");
        }
        if (brightness < 0 || brightness > 100) {
            return Result.error("设备亮度必须在0-100之间");
        }
        if (room == null || room.trim().isEmpty() || !TEXT_PATTERN.matcher(room).matches()) {
            return Result.error("房间名称格式不正确，只允许中文、英文、数字和下划线，长度不超过20");
        }
        
        // 检查设备ID是否已存在
        Device existingDevice = deviceService.getDeviceById(device_id);
        if (existingDevice != null) {
            return Result.error("设备ID已存在");
        }
        
        Device device = new Device();
        device.setDevice_id(device_id);
        device.setName(name);
        device.setType(type);
        device.setStatus(status);
        device.setBrightness(brightness);
        device.setRoom(room);
        device.setInstall_time(new java.util.Date()); // 设置安装时间为当前时间
        
        try {
            Device savedDevice = deviceService.addDevice(device);
            return Result.success(savedDevice);
        } catch (Exception e) {
            return Result.error("设备添加失败：" + e.getMessage());
        }
    }

    /**
     * 批量添加设备信息
     * @param devices 设备列表
     * @return 添加结果
     */
    // http://localhost:8081/device/batchAddDevices
    @PostMapping("/batchAddDevices")
    @Operation(summary = "批量添加设备", description = "批量添加多个设备信息")
    public Result batchAddDevices(@Parameter(description = "设备列表") @RequestBody List<Device> devices) {
        // 输入验证
        if (devices == null || devices.isEmpty()) {
            return Result.error("设备列表不能为空");
        }
        if (devices.size() > 100) {
            return Result.error("批量添加设备数量不能超过100个");
        }
        
        // 验证每个设备并检查ID重复
        List<Integer> deviceIds = new ArrayList<>();
        for (Device device : devices) {
            if (device == null) {
                return Result.error("设备列表中存在空设备");
            }
            if (device.getDevice_id() <= 0) {
                return Result.error("设备ID必须大于0");
            }
            if (deviceIds.contains(device.getDevice_id())) {
                return Result.error("设备列表中存在重复的设备ID: " + device.getDevice_id());
            }
            deviceIds.add(device.getDevice_id());
            
            if (device.getName() == null || device.getName().trim().isEmpty() || !DEVICE_NAME_PATTERN.matcher(device.getName()).matches()) {
                return Result.error("设备名称格式不正确，只允许中文、英文、数字和下划线，长度不超过50");
            }
            // 其他验证...
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
    // http://localhost:8081/device/updateDeviceById
    @PostMapping("/updateDeviceById")
    @Operation(summary = "根据设备ID更新设备信息", description = "通过设备ID更新设备信息")
    public Result updateDeviceById(@Parameter(description = "设备ID") @RequestParam int device_id,
                                   @Parameter(description = "新设备名称(可选)") @RequestParam(required = false) String name,
                                   @Parameter(description = "新设备类型(可选)") @RequestParam(required = false) String type,
                                   @Parameter(description = "新设备状态(可选)") @RequestParam(required = false) String status,
                                   @Parameter(description = "新设备亮度(可选)") @RequestParam(required = false, defaultValue = "-1") int brightness,
                                   @Parameter(description = "新房间名称(可选)") @RequestParam(required = false) String room) {
        // 输入验证
        if (device_id <= 0) {
            return Result.error("设备ID必须大于0");
        }
        if (name != null && !name.isEmpty() && !DEVICE_NAME_PATTERN.matcher(name).matches()) {
            return Result.error("设备名称格式不正确，只允许中文、英文、数字和下划线，长度不超过50");
        }
        if (type != null && !type.isEmpty() && !TEXT_PATTERN.matcher(type).matches()) {
            return Result.error("设备类型格式不正确，只允许中文、英文、数字和下划线，长度不超过20");
        }
        if (brightness != -1 && (brightness < 0 || brightness > 100)) {
            return Result.error("设备亮度必须在0-100之间");
        }
        if (room != null && !room.isEmpty() && !TEXT_PATTERN.matcher(room).matches()) {
            return Result.error("房间名称格式不正确，只允许中文、英文、数字和下划线，长度不超过20");
        }
        
        // 检查设备是否存在
        Device existingDevice = deviceService.getDeviceById(device_id);
        if (existingDevice == null) {
            return Result.error("设备不存在");
        }
        
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
        
        try {
            Device updatedDevice = deviceService.updateDeviceById(device_id, device);
            if (updatedDevice != null) {
                return Result.success(updatedDevice);
            } else {
                return Result.error("设备更新失败");
            }
        } catch (Exception e) {
            return Result.error("设备更新失败：" + e.getMessage());
        }
    }

    /**
     * 根据设备ID删除设备数据
     * @param device_id 设备ID
     * @return 删除结果
     */
    // http://localhost:8081/device/deleteDeviceById?device_id=2
    @DeleteMapping("/deleteDeviceById")
    @Operation(summary = "根据设备ID删除设备", description = "通过设备ID删除设备信息")
    public Result deleteDeviceById(@Parameter(description = "设备ID") @RequestParam int device_id) {
        // 输入验证
        if (device_id <= 0) {
            return Result.error("设备ID必须大于0");
        }
        
        // 检查设备是否存在
        Device existingDevice = deviceService.getDeviceById(device_id);
        if (existingDevice == null) {
            return Result.error("设备不存在");
        }
        
        try {
            boolean deleted = deviceService.deleteDeviceById(device_id);
            if (deleted) {
                return Result.success("设备删除成功");
            } else {
                return Result.error("设备删除失败");
            }
        } catch (Exception e) {
            return Result.error("设备删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据设备名称模糊查询设备数据
     * @param name 设备名称关键字
     * @return 匹配的设备列表
     */
    // http://localhost:8081/device/findDeviceByNameContaining?name=厨
    @RequestMapping(value = "/findDeviceByNameContaining", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "根据设备名称模糊查询设备", description = "通过设备名称关键字模糊查找设备信息")
    public Result findDeviceByNameContaining(@Parameter(description = "设备名称关键字") @RequestParam String name) {
        // 输入验证
        if (name == null || name.trim().isEmpty()) {
            return Result.error("搜索关键字不能为空");
        }
        if (name.length() > 50) {
            return Result.error("搜索关键字长度不能超过50个字符");
        }
        return Result.success(deviceService.findByNameContaining(name));
    }

//    6节：实现查询所有的设备数据、实现根据设备名称查询设备数据
//    7节：添加一条设备信息       根据设备id修改设备信息：比如把设备id为2的设备名称改为"灯泡"
//    8节：根据设备id删除设备数据。   根据设备名称模糊查询设备数据。比如你输入 厨 =》得到包含所有的厨字的设备、
//     提交截图以及git提交路径
//    图片命名姓名-功能、
//    提交的时间：5.50下课提交。

}