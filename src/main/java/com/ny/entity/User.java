package com.ny.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private int user_id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private int is_active;
    private Date created_time;

}
