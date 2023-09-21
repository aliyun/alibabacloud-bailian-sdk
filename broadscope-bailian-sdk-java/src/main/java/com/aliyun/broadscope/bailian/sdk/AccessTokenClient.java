/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk;

import com.aliyun.bailian20230601.Client;
import com.aliyun.bailian20230601.models.CreateTokenRequest;
import com.aliyun.bailian20230601.models.CreateTokenResponse;
import com.aliyun.bailian20230601.models.CreateTokenResponseBody;
import com.aliyun.broadscope.bailian.sdk.consts.ConfigConsts;
import com.aliyun.broadscope.bailian.sdk.models.AccessToken;
import com.aliyun.teaopenapi.models.Config;
import org.apache.commons.lang3.StringUtils;

/**
 * Title api access token.<br>
 * Description api access token.<br>
 * Created at 2023-07-03 12:49
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class AccessTokenClient {
    private static final int TOKEN_CREATE_THRESHOLD_MINUTES = 10;

    private final String accessKeyId;

    private final String accessKeySecret;

    private final String agentKey;

    private String endpoint = ConfigConsts.POP_ENDPOINT;

    private AccessToken accessToken;

    public AccessTokenClient(String accessKeyId, String accessKeySecret, String agentKey) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.agentKey = agentKey;
    }

    public String getToken() {
        long timestamp = System.currentTimeMillis() / 1000;
        if (accessToken == null ||
                (accessToken.getExpiredTime() - TOKEN_CREATE_THRESHOLD_MINUTES * 60) < timestamp) {
            accessToken = createToken();
        }

        return accessToken.getToken();
    }

    public AccessToken createToken() {
        if (StringUtils.isEmpty(this.accessKeyId)) {
            throw new BaiLianSdkException("access key id is required");
        }

        if (StringUtils.isEmpty(this.accessKeySecret)) {
            throw new BaiLianSdkException("access key secret is required");
        }

        if (StringUtils.isEmpty(this.agentKey)) {
            throw new BaiLianSdkException("agent key is required");
        }

        Config config = new Config().setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret);

        if (StringUtils.isNotEmpty(this.endpoint)) {
            config.setEndpoint(endpoint);
        }

        try {
            Client client = new Client(config);
            CreateTokenRequest request = new CreateTokenRequest().setAgentKey(agentKey);
            CreateTokenResponse response = client.createToken(request);
            CreateTokenResponseBody body = response.getBody();
            if (body == null || !body.success) {
                String error = body == null ? "create token error" : body.message;
                throw new BaiLianSdkException(error);
            }

            CreateTokenResponseBody.CreateTokenResponseBodyData data = body.getData();
            if (data == null) {
                throw new BaiLianSdkException("create token error, data is null");
            }

            return new AccessToken(data.getToken(), data.getExpiredTime());
        } catch (BaiLianSdkException e) {
            throw e;
        } catch (Exception e) {
            throw new BaiLianSdkException(e);
        }
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
