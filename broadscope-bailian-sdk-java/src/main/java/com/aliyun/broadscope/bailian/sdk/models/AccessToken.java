/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk.models;

/**
 * Title access token.<br>
 * Description access token.<br>
 * Created at 2023-07-03 13:19
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class AccessToken {
    private String token;

    private Long expiredTime;

    public AccessToken() {
    }

    public AccessToken(String token, Long expiredTime) {
        this.token = token;
        this.expiredTime = expiredTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Long expiredTime) {
        this.expiredTime = expiredTime;
    }

    @Override
    public String toString() {
        return "AccessTokenResponse{" + "token='" + token + '\'' +
               ", expiredTime=" + expiredTime +
               '}';
    }
}
