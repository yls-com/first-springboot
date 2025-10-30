package com.ny.service;



import com.ny.entity.Device;

import java.util.List;

public interface DeviceService {

    /**
     * 查询所有设备信息
     * @return 设备列表
     */
    List<Device> findAll();

    /**
     * 根据设备名称查询设备
     * @param name 设备名称
     * @return 设备列表
     */
    List<Device> findByName(String name);

}