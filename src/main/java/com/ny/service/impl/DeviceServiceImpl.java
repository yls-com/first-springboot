package com.ny.service.impl;

import com.ny.entity.Device;
import com.ny.mapper.DeviceMapper;
import com.ny.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Device addDevice(Device device) {
        return deviceMapper.save(device);
    }

    @Override
    public Device updateDeviceById(int device_id, Device updatedDevice) {
        // 查找现有设备
        List<Device> devices = deviceMapper.findAll();
        Device existingDevice = null;
        for (Device device : devices) {
            if (device.getDevice_id() == device_id) {
                existingDevice = device;
                break;
            }
        }
        
        if (existingDevice == null) {
            return null;
        }
        
        // 更新设备信息
        if (updatedDevice.getName() != null && !updatedDevice.getName().isEmpty()) {
            existingDevice.setName(updatedDevice.getName());
        }
        if (updatedDevice.getType() != null && !updatedDevice.getType().isEmpty()) {
            existingDevice.setType(updatedDevice.getType());
        }
        if (updatedDevice.getStatus() != null && !updatedDevice.getStatus().isEmpty()) {
            existingDevice.setStatus(updatedDevice.getStatus());
        }
        if (updatedDevice.getBrightness() != -1) {
            existingDevice.setBrightness(updatedDevice.getBrightness());
        }
        if (updatedDevice.getRoom() != null && !updatedDevice.getRoom().isEmpty()) {
            existingDevice.setRoom(updatedDevice.getRoom());
        }
        
        // 保存更新后的设备
        return deviceMapper.save(existingDevice);
    }

    @Override
    public boolean deleteDeviceById(int device_id) {
        // 查找要删除的设备
        List<Device> devices = deviceMapper.findAll();
        Device deviceToDelete = null;
        for (Device device : devices) {
            if (device.getDevice_id() == device_id) {
                deviceToDelete = device;
                break;
            }
        }
        
        // 如果找到了设备，则删除它
        if (deviceToDelete != null) {
            deviceMapper.delete(deviceToDelete);
            return true;
        }
        
        // 如果没找到设备，返回false
        return false;
    }




}