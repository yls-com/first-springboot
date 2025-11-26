package com.ny.service.impl;

import com.ny.entity.User;
import com.ny.mapper.UserMapper;
import com.ny.service.EmailService;
import com.ny.service.UserService;
import com.ny.until.PasswordUntil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<User> findAllUser() {
        return userMapper.findAllUser();
    }

    // 实现用户查询逻辑（密码MD5加密后比对）
    @Override
    public User findUser(String username, String password) {
        if (password == null) {
            return null; // 如果密码为空，直接返回null
        }
        return userMapper.findUser(username, PasswordUntil.md5(password));
    }

    // 实现登录方法
    @Override
    public User login(String username, String password) {
        // 使用加密后的密码进行登录验证
        return userMapper.findUser(username, PasswordUntil.md5(password));
    }
    @Override
    public User findUserByUsername(String username) {
        return userMapper.findUserByName(username);
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.findUserByName(user.getUsername());
        if (existingUser != null) {
            return false; // 用户名已存在，注册失败
        }

        // 设置默认值
        user.setIs_active(1); // 默认激活状态
        user.setCreated_time(new Date()); // 设置创建时间

        //进行密码MD5加密
        String password = user.getPassword();
        String pwd = PasswordUntil.encryptPassword(password);
        user.setPassword(pwd);
        // 执行插入操作
        int result = userMapper.registertUser(user);
        return result > 0; // 插入成功返回true，否则返回false
    }

    @Override
    public User findUserByEmail(String email) {
        return userMapper.findUserByEmail(email);
    }

    @Override
    public void sendEmailCode(String email) {

            String code = emailService.generateCode();
            // 把验证码存储到redis数据中,进行验证和过期的
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            ops.set(email, code,10, TimeUnit.MINUTES);
            emailService.sendEmail(email, code);


    }

    @Override
    public int updatePasswordByEmail(String email, String password) {
        // 密码加密
        password = PasswordUntil.encryptPassword(password);
        //        redisTemplate.delete(email);清除redis的验证码
        return userMapper.updatePasswordByEmail(email, password);
    }
    @Override
    public boolean checkCode(String email, String code) {
        // 从redis中获取验证码
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String redisCode = ops.get(email);
        if (redisCode != null && redisCode.equals(code)) {
            return true;
        }
        return false;
    }

    @Override
    public int updateNicknameById(int user_id, String nickname) {
        return userMapper.updateNicknameById(user_id, nickname);
    }


}