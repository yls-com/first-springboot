package com.ny.service.impl;

import com.ny.entity.Device;
import com.ny.mapper.DeviceMapper;
import com.ny.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public List<Device> findAllDevices() {
        return deviceMapper.findAllDevices();
    }

    @Override
    public Device findDeviceByName(String device_name) {
        return deviceMapper.findDeviceByName(device_name);
    }

    @Override
    public int deleteDeviceById(Integer device_id) {
        return deviceMapper.deleteDeviceById(device_id);
    }
}
