package com.ny.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Data
@Document(collection = "devices")
@Schema(description = "设备信息实体类")
public class Device {
    @Id
    @Schema(description = "MongoDB文档ID")
    private String id;

    @Field("device_id")
    @Schema(description = "设备ID")
    private int device_id;

    @Field("name")
    @Schema(description = "设备名称")
    private String name;

    @Field("type")
    @Schema(description = "设备类型")
    private String type;

    @Field("status")
    @Schema(description = "设备状态")
    private String status;

    @Field("brightness")
    @Schema(description = "设备亮度")
    private int brightness;

    @Field("room")
    @Schema(description = "房间名称")
    private String room;

    @Field("install_time")
    @Schema(description = "安装时间")
    private Date install_time;
}