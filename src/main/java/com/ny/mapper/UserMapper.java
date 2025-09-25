package com.ny.mapper;

import com.ny.entity.User;
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
}
