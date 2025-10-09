package com.ny.mapper;

import com.ny.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

}
