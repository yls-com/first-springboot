package com.ny.mapper;

import com.ny.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user")
    List<User> findAllUser();

    // 登录查询方法
    @Select("select * from user where username = #{username} and password = #{password}")
    User findUser(@Param("username") String username, @Param("password") String password);

    @Select("select * from user where username = #{username}")
    User findUserByName(@Param("username") String username);

    // 注册方法
    @Insert("INSERT INTO user(username, password, nickname, phone, email, is_active, created_time) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{phone}, #{email}, #{is_active}, #{created_time})")
    int registertUser(User user);

    //根据邮箱号码查询用户是否存在
    @Select("select * from user where email = #{email}")
    User findUserByEmail(@Param("email") String email);

    //根据邮箱修改密码
    @Update("update user set password = #{password} where email = #{email}")
    int updatePasswordByEmail(@Param("email") String email, @Param("password") String password);


}
