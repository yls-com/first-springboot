package com.ny.controller;

import com.ny.entity.Result;
import com.ny.entity.SensorData;
import com.ny.service.SensorDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/sensor")
@Tag(name = "传感器数据管理", description = "传感器数据相关操作接口")
public class SensorDataController {
    @Autowired
    private SensorDataService sensorDataService;
    
    // 设备ID验证正则表达式
    private static final Pattern DEVICE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-]{1,100}$");
    // 日期格式验证正则表达式
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");

    // 新增数据
    // URL: http://localhost:8081/sensor/save
    @PostMapping("/save")
    @Operation(summary = "保存传感器数据", description = "保存传感器数据")
    public Result save(@Parameter(description = "传感器数据") @RequestBody SensorData sensorData) {
        // 输入验证
        if (sensorData == null) {
            return Result.error("数据不能为空");
        }
        
        if (sensorData.getDevice_id() == null || !DEVICE_ID_PATTERN.matcher(sensorData.getDevice_id()).matches()) {
            return Result.error("设备ID格式不正确");
        }
        
        // 检查数值范围（假设温度范围）
        if (sensorData.getTemperature() != null) {
            if (sensorData.getTemperature() < -50 || sensorData.getTemperature() > 150) {
                return Result.error("温度值超出合理范围");
            }
        }
        
        try {
            SensorData savedData = sensorDataService.save(sensorData);
            return Result.success(savedData);
        } catch (Exception e) {
            return Result.error("数据保存失败：" + e.getMessage());
        }
    }

    // 查询单条数据
    // URL: http://localhost:8081/sensor/{id}
    @GetMapping("/{device_id}")
    @Operation(summary = "根据设备ID查询传感器数据", description = "通过设备ID查询单条传感器数据")
    public Result findById(@Parameter(description = "设备ID") @PathVariable String device_id) {
        // 输入验证
        if (device_id == null || !DEVICE_ID_PATTERN.matcher(device_id).matches()) {
            return Result.error("设备ID格式不正确");
        }
        
        try {
            SensorData data = sensorDataService.findById(device_id);
            if (data == null) {
                return Result.error("数据不存在");
            }
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    // 查询设备某时间段数据（时序查询）
    // URL: http://localhost:8081/sensor/device/{device_id}/time?start={start}&end={end}
    @GetMapping("/device/{device_id}/time")
    @Operation(summary = "查询设备某时间段的数据", description = "根据设备ID和时间范围查询传感器数据")
    public Result findByTimeRange(
            @Parameter(description = "设备ID") @PathVariable String device_id,
            @Parameter(description = "开始时间(格式: yyyy-MM-dd HH:mm:ss)") @RequestParam String start,
            @Parameter(description = "结束时间(格式: yyyy-MM-dd HH:mm:ss)") @RequestParam String end) {
        // 输入验证
        if (device_id == null || !DEVICE_ID_PATTERN.matcher(device_id).matches()) {
            return Result.error("设备ID格式不正确");
        }
        
        if (start == null || !DATE_PATTERN.matcher(start).matches()) {
            return Result.error("开始时间格式不正确，请使用yyyy-MM-dd HH:mm:ss格式");
        }
        
        if (end == null || !DATE_PATTERN.matcher(end).matches()) {
            return Result.error("结束时间格式不正确，请使用yyyy-MM-dd HH:mm:ss格式");
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            
            // 验证时间范围
            if (startDate.after(endDate)) {
                return Result.error("开始时间不能晚于结束时间");
            }
            
            // 限制查询时间范围，避免查询过多数据
            long timeDiff = endDate.getTime() - startDate.getTime();
            if (timeDiff > 30L * 24 * 60 * 60 * 1000) {  // 30天
                return Result.error("查询时间范围不能超过30天");
            }
            
            List<SensorData> dataList = sensorDataService.findByDevice_idAndTimeRange(device_id, startDate, endDate);
            return Result.success(dataList);
        } catch (ParseException e) {
            return Result.error("日期解析错误：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    // 更新数据
    // URL: http://localhost:8081/sensor/update
    @PutMapping("/update")
    @Operation(summary = "更新传感器数据", description = "更新传感器数据")
    public Result update(@Parameter(description = "传感器数据") @RequestBody SensorData sensorData) {
        // 输入验证
        if (sensorData == null) {
            return Result.error("数据不能为空");
        }
        
        if (sensorData.getDevice_id() == null || !DEVICE_ID_PATTERN.matcher(sensorData.getDevice_id()).matches()) {
            return Result.error("设备ID格式不正确");
        }
        
        // 检查数据是否存在
        try {
            SensorData existingData = sensorDataService.findById(sensorData.getDevice_id());
            if (existingData == null) {
                return Result.error("数据不存在");
            }
            
            SensorData updatedData = sensorDataService.update(sensorData);
            return Result.success(updatedData);
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    // 删除数据
    // URL: http://localhost:8081/sensor/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除传感器数据", description = "通过ID删除传感器数据")
    public Result deleteById(@Parameter(description = "数据ID") @PathVariable String id) {
        // 输入验证
        if (id == null || !DEVICE_ID_PATTERN.matcher(id).matches()) {
            return Result.error("ID格式不正确");
        }
        
        try {
            // 检查数据是否存在
            SensorData existingData = sensorDataService.findById(id);
            if (existingData == null) {
                return Result.error("数据不存在");
            }
            
            sensorDataService.deleteById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}