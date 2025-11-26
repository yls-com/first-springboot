package com.ny.controller;

import com.ny.entity.Result;
import com.ny.entity.SensorData;
import com.ny.service.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

@RestController
@RequestMapping("/sensor")
public class SensorDataController {
    @Autowired
    private SensorDataService sensorDataService;

    // 新增数据
    // URL: http://localhost:8081/sensor/save
    @PostMapping("/save")
    public SensorData save(@RequestBody SensorData sensorData) {
        return sensorDataService.save(sensorData);
    }

    // 查询单条数据
    // URL: http://localhost:8081/sensor/{id}
    @GetMapping("/{device_id}")
    public SensorData findById(@PathVariable String device_id) {
        return sensorDataService.findById(device_id);
    }

    // 查询设备某时间段数据（时序查询）
    // URL: http://localhost:8081/sensor/device/{device_id}/time?start={start}&end={end}
    @GetMapping("/device/{device_id}/time")
    public List<SensorData> findByTimeRange(
            @PathVariable String device_id,
            @RequestParam String start,  // 格式：yyyy-MM-dd HH:mm:ss
            @RequestParam String end     // 格式：yyyy-MM-dd HH:mm:ss
    ) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = sdf.parse(start);
        Date endDate = sdf.parse(end);
        return sensorDataService.findByDevice_idAndTimeRange(device_id, startDate, endDate);
    }

    // 更新数据
    // URL: http://localhost:8081/sensor/update
    @PutMapping("/update")
    public SensorData update(@RequestBody SensorData sensorData) {
        return sensorDataService.update(sensorData);
    }

    // 删除数据
    // URL: http://localhost:8081/sensor/{id}
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id) {
        sensorDataService.deleteById(id);
        return Result.success("删除成功");
    }
}