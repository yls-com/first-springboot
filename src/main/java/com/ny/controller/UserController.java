package com.ny.controller;

import com.ny.entity.Result;
import com.ny.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/selectAllUser")
    public Result selectAllUser(){
        return Result.success(userService.findAllUser());
    }
}

