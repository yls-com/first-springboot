package com.ny.service;

import com.ny.entity.SensorData;

import java.util.List;
import java.util.Date;

public interface SensorDataService {
    // 新增数据
    SensorData save(SensorData sensorData);

    // 查询单条数据（按ID）
    SensorData findById(String id);

    // 查询多条数据（按设备ID和时间范围）
    List<SensorData> findByDevice_idAndTimeRange(String device_id, Date start, Date end);

    // 更新数据
    SensorData update(SensorData sensorData);

    // 删除数据（按ID）
    void deleteById(String id);


}