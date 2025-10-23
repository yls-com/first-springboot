package com.ny.service;

import com.ny.entity.Device;
import java.util.List;

public interface DeviceService {

    /**
     * 查询所有的设备信息
     */
    List<Device> findAllDevices();

    /**
     * 根据设备名称查询设备信息
     */
    Device findDeviceByName(String device_name);

    /**
     * 根据设备id删除设备信息
     */
    int deleteDeviceById(Integer device_id);
}

