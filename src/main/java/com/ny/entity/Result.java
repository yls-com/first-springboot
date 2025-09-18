package com.ny.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data  // Lombok自动生成getter/setter，不用手动写
@NoArgsConstructor  // 无参构造（JSON序列化必须，否则前端解析会报错）
@AllArgsConstructor // 全参构造（静态方法里直接用）
public class Result implements Serializable {
    // 状态码：对齐API文档的HTTP标准（200=成功，400=失败，401=未登录，404=资源不存在）
    private Integer code;
    private String msg;
    // Object类型：承载所有业务数据（登录信息、用户信息、设备列表等）
    private Object data;


    // -------------------------- 静态工具方法：和你原来的调用习惯一致 --------------------------
    /**
     * 成功响应（有业务数据）：比如登录返回Token、查询用户返回信息
     */
    public static Result success(Object data) {
        return new Result(200, "操作成功", data);
    }

    /**
     * 成功响应（无业务数据）：比如修改昵称、重置密码
     */
    public static Result success() {
        return new Result(200, "操作成功", null);
    }

    /**
     * 失败响应（通用）：比如参数错误、密码错误
     */
    public static Result error(String msg) {
        return new Result(400, msg, null);
    }

    /**
     * 未登录响应：比如未传Token、Token过期（控制设备必须登录）
     */
    public static Result notLogin(String msg) {
        return new Result(401, msg, null);
    }

    /**
     * 资源不存在响应：比如查不存在的设备ID、用户ID
     */
    public static Result notFound(String msg) {
        return new Result(404, msg, null);
    }
}