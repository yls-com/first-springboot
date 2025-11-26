package com.ny.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromMail;
    public void sendEmail(String to, String code) {
        // 1.创建邮件对象
        SimpleMailMessage message =  new SimpleMailMessage();
        // 2.设置发件人
        message.setFrom(fromMail);
        // 3.设置收件人
        message.setTo(to);
        // 4.设置主题
        message.setSubject("密码重置验证码");
        // 5.设置邮件内容
        message.setText("你的密码重置的验证码为"+code+"，有效期10分钟，请尽快使用");
        mailSender.send(message);
    }
    // 生成验证码
    public String generateCode() {
        // 生成4位随机整数
        int code =(int) (Math.random()*9000)+1000;
        // 转换为字符串
        return  String.valueOf(code);
    }
}