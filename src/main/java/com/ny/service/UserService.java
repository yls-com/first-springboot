package com.ny.service;

import com.ny.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService {
    public List<User> findAllUser();

    // 定义用户查询接口（根据用户名和密码）
    public User findUser(String username, String password);

    // 登录方法声明
    public User login(String username, String password);

    public User findUserByUsername(String username);

    // 注册方法声明
    public boolean register(User user);

    User findUserByEmail(String email);

    // 发送邮箱验证码
    public void sendEmailCode(String email);

    // 根据邮箱修改密码
    int updatePasswordByEmail( String email, String password);

    // 判断验证码
    boolean checkCode(String email, String code);

    // 根据用户ID修改昵称
    int updateNicknameById(int user_id, String nickname);

    // 验证密码
    boolean verifyPassword(String rawPassword, String encodedPassword);

    // 根据ID查找用户
    User findById(int id);
}
