package com.ny.mapper;

import com.ny.entity.Device;
import com.ny.entity.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Date;

public interface SensorDataMapper extends MongoRepository<SensorData, String> {
    // 按设备ID和时间范围查询（时序数据典型查询）
    @Query("{ 'device_id' : ?0, 'time' : { $gte: ?1, $lte: ?2 } }")
    List<SensorData> findByDevice_idAndTimeBetween(String device_id, Date start, Date end);

    // 自定义查询（示例：按设备ID和温度范围查询）
    @Query("{ 'device_id' : ?0, 'temperature' : { $gte : ?1, $lte : ?2 } }")
    List<SensorData> findByDevice_idAndTemperatureRange(String device_id, Double min, Double max);

}