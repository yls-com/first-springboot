package com.ny.mapper;

import com.ny.entity.Device;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeviceMapper {

    /**
     * 查询所有的设备信息
     */
    @Select("SELECT * FROM device")
    List<Device> findAllDevices();

    /**
     * 根据设备名称查询设备信息
     */
    @Select("SELECT * FROM device WHERE device_name = #{device_name}")
    Device findDeviceByName(@Param("device_name") String device_name);

    /**
     * 根据设备id删除设备信息
     */
    @Delete("DELETE FROM device WHERE device_id = #{device_id}")
    int deleteDeviceById(@Param("device_id") Integer device_id);
}
