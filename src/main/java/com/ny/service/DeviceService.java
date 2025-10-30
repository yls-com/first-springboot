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

    /**
     * 添加设备信息
     * @param device 设备对象
     * @return 添加的设备对象
     */
    Device addDevice(Device device);

    /**
     * 根据设备ID更新设备信息
     * @param device_id 设备ID
     * @param device 包含更新信息的设备对象
     * @return 更新后的设备对象，如果设备不存在则返回null
     */
    Device updateDeviceById(int device_id, Device device);
    
    /**
     * 根据设备ID删除设备
     * @param device_id 设备ID
     * @return 删除成功返回true，设备不存在返回false
     */
    boolean deleteDeviceById(int device_id);
    
    /**
     * 根据设备名称模糊查询设备
     * @param name 设备名称关键字
     * @return 匹配的设备列表
     */
    List<Device> findByNameContaining(String name);
}