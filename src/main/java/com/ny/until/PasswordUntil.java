package com.ny.until;


import org.apache.commons.codec.digest.DigestUtils;

public class PasswordUntil {

    public static String encryptPassword(String password) {
        return DigestUtils.md5Hex(password);
    }
}
