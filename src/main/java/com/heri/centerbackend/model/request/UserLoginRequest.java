package com.heri.centerbackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求体
 *
 * @author heri
 */
@Data
public class UserLoginRequest implements Serializable {

    private String userAccount;

    private String userPassword;
}
