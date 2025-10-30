package com.ny.mapper;

import com.ny.entity.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceMapper extends MongoRepository<Device, Integer> {

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
     * 根据设备名称模糊查询设备
     * @param name 设备名称关键字
     * @return 匹配的设备列表
     */
    List<Device> findByNameContaining(String name);

}