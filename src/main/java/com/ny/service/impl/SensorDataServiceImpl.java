package com.ny.service.impl;

import com.ny.entity.SensorData;
import com.ny.mapper.SensorDataMapper;
import com.ny.service.SensorDataService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Date;

@Service
public class SensorDataServiceImpl implements SensorDataService {
    @Autowired
    private SensorDataMapper sensorDataMapper;

    @Override
    public SensorData save(SensorData sensorData) {
        return sensorDataMapper.save(sensorData);
    }

    @Override
    public SensorData findById(String id) {
        return sensorDataMapper.findById(id).orElse(null);
    }

    @Override
    public List<SensorData> findByDevice_idAndTimeRange(String device_id, Date start, Date end) {
        return sensorDataMapper.findByDevice_idAndTimeBetween(device_id, start, end);
    }

    @Override
    public SensorData update(SensorData sensorData) {
        if (sensorData.getId() == null) {
            throw new IllegalArgumentException("更新数据必须包含ID");
        }
        return sensorDataMapper.save(sensorData);
    }

    @Override
    public void deleteById(String id) {
        sensorDataMapper.deleteById(id);
    }


}