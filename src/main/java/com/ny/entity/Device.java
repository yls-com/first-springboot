package com.ny.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "devices")
public class Device {
    @Id
    private String id;

    @Field("device_id")
    private int device_id;

    @Field("name")
    private String name;

    @Field("type")
    private String type;

    @Field("status")
    private String status;

    @Field("brightness")
    private int brightness;

    @Field("room")
    private String room;

    @Field("install_time")
    private Date install_time;
}