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
    public List<Device> findAll() {
        return deviceMapper.findAll();
    }

    @Override
    public List<Device> findByName(String name) {
        return deviceMapper.findByName(name);
    }
}