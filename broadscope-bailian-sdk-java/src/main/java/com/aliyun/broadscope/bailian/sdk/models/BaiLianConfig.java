/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk.models;

import com.aliyun.broadscope.bailian.sdk.consts.ConfigConsts;

/**
 * Title 百炼客户端config.<br>
 * Description 百炼客户端config.<br>
 * Created at 2023-06-07 14:54
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

@Deprecated
public class BaiLianConfig {
    /**
     * 百炼API key
     */
    private String apiKey;

    /**
     * 通义专属大模型服务端请求地址, 如生产环境、预发、私有化部署地址
     */
    private String endpoint = ConfigConsts.ENDPOINT;

    public String getApiKey() {
        return apiKey;
    }

    public BaiLianConfig setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public BaiLianConfig setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }
}
