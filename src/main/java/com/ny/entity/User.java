package com.ny.entity;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Data
@Schema(description = "用户信息实体类")
public class User {

    @Schema(description = "用户ID")
    private int user_id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "密码")
    private String password;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "电话")
    private String phone;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "是否激活")
    private int is_active;
    
    @Schema(description = "创建时间")
    private Date created_time;

    public int getId() {
        return user_id;
    }

    public void setId(int id) {
        this.user_id = id;
    }
}