/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk;

import com.alibaba.fastjson.JSON;
import com.aliyun.broadscope.bailian.sdk.consts.HttpHeaderConsts;
import com.aliyun.broadscope.bailian.sdk.models.BaiLianConfig;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsRequest;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsResponse;
import com.aliyun.broadscope.bailian.sdk.models.ConnectOptions;
import com.aliyun.broadscope.bailian.sdk.utils.UUIDGenerator;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Title 应用对话客户端.<br>
 * Description 对话交互客户端流式输出.<br>
 * Created at 2023-06-07 14:27
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class ApplicationClient {
    private final OkHttpClient okHttpClient;

    private final BaiLianConfig config;

    private ConnectOptions connectOptions;

    /**
     * 构造实例对象
     * @param config 配置信息
     */
    public ApplicationClient(BaiLianConfig config) {
        checkConfig(config);
        this.config = config;
        this.okHttpClient = buildOkHttpClient();
    }

    public ApplicationClient(BaiLianConfig config, ConnectOptions connectOptions) {
        checkConfig(config);
        this.config = config;
        this.connectOptions = connectOptions;
        this.okHttpClient = buildOkHttpClient();
    }

    /**
     * 创建默认的OkHttpClient
     */
    private OkHttpClient buildOkHttpClient() {
        long connectTimeout = 30000;
        long writeTimeout = 30000;
        long readTimeout = 600000;

        if (connectOptions != null) {
            if (connectOptions.getConnectTimeout() >= 0) {
                connectTimeout = connectOptions.getConnectTimeout();
            }

            if (connectOptions.getWriteTimeout() >= 0) {
                writeTimeout = connectOptions.getWriteTimeout();
            }

            if (connectOptions.getReadTimeout() >= 0) {
                readTimeout = connectOptions.getReadTimeout();
            }
        }

        return new OkHttpClient
                .Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 非流式文本生成
     * @param chatClientRequest prompt请求信息
     * @return 文本生成响应结果
     */
    public CompletionsResponse completions(CompletionsRequest chatClientRequest) {
        checkChatClientRequest(chatClientRequest);

        setRequestIdIfNull(chatClientRequest);

        Headers headers = new Headers.Builder()
                .add(HttpHeaderConsts.Keys.CONTENT_TYPE, HttpHeaderConsts.MediaType.APPLICATION_JSON_UTF8_VALUE)
                .add(HttpHeaderConsts.Keys.ACCEPT, HttpHeaderConsts.MediaType.APPLICATION_JSON_VALUE)
                .add(HttpHeaderConsts.Keys.AUTHORIZATION, "Bearer " + this.config.getApiKey())
                .build();

        chatClientRequest.setStream(false);

        MediaType mediaType = MediaType.parse(HttpHeaderConsts.MediaType.APPLICATION_JSON_UTF8_VALUE);
        String data = JSON.toJSONString(chatClientRequest);
        RequestBody requestBody = RequestBody.create(mediaType, data);

        Request request = new Request.Builder()
                .url(this.config.getEndpoint() + "/v2/app/completions")
                .headers(headers)
                .post(requestBody)
                .build();

        String result;
        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            result = responseBody == null ? "" : responseBody.string();

            if (!response.isSuccessful()) {
                if (StringUtils.isEmpty(result)) {
                    result = String.valueOf(response.code());
                }

                throw new BaiLianSdkException(response.code(), result);
            }

            if (StringUtils.isEmpty(result)) {
                throw new BaiLianSdkException("response body is empty");
            }
        } catch (IOException e) {
            throw new BaiLianSdkException(e);
        }

        if (StringUtils.isEmpty(result)) {
            throw new BaiLianSdkException("response content is empty");
        }

        try {
            return JSON.parseObject(result, CompletionsResponse.class);
        } catch (Exception e) {
            throw new BaiLianSdkException(e);
        }
    }

    /**
     * completions stream
     * @param chatClientRequest prompt请求
     * @param streamEventListener 流式响应监听器
     */
    public void streamCompletions(CompletionsRequest chatClientRequest, StreamEventListener streamEventListener) {
        checkChatClientRequest(chatClientRequest);

        setRequestIdIfNull(chatClientRequest);

        Headers headers = new Headers.Builder()
                .add(HttpHeaderConsts.Keys.CONTENT_TYPE, HttpHeaderConsts.MediaType.APPLICATION_JSON_UTF8_VALUE)
                .add(HttpHeaderConsts.Keys.ACCEPT, HttpHeaderConsts.MediaType.TEXT_EVENT_STREAM_VALUE)
                .add(HttpHeaderConsts.Keys.AUTHORIZATION, "Bearer " + this.config.getApiKey())
                .build();

        chatClientRequest.setStream(true);

        MediaType mediaType = MediaType.parse(HttpHeaderConsts.MediaType.APPLICATION_JSON_UTF8_VALUE);
        String data = JSON.toJSONString(chatClientRequest);
        RequestBody requestBody = RequestBody.create(mediaType, data);

        try {
            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            Request request = new Request.Builder()
                    .url(this.config.getEndpoint() + "/v2/app/completions")
                    .headers(headers)
                    .post(requestBody)
                    .build();
            factory.newEventSource(request, streamEventListener);
        } catch (Exception e) {
            throw new BaiLianSdkException(e);
        }
    }

    private void checkConfig(BaiLianConfig config) {
        if (config == null) {
            throw new BaiLianSdkException("config can not be null");
        }

        String apiKey = StringUtils.trim(config.getApiKey());
        if (StringUtils.isEmpty(apiKey)) {
            throw new BaiLianSdkException("api key is required");
        }

        String endpoint = StringUtils.trim(config.getEndpoint());
        if (StringUtils.isEmpty(endpoint)) {
            throw new BaiLianSdkException("endpoint is required");
        }
    }

    private void checkChatClientRequest(CompletionsRequest request) {
        if (request == null) {
            throw new BaiLianSdkException("chat request can not be null");
        }

        String appId = StringUtils.trim(request.getAppId());
        if (StringUtils.isEmpty(appId)) {
            throw new BaiLianSdkException("app code is required");
        }

        String prompt = StringUtils.trim(request.getPrompt());
        if (StringUtils.isEmpty(prompt)) {
            throw new BaiLianSdkException("input prompt is required");
        }
    }

    private static void setRequestIdIfNull(CompletionsRequest request) {
        String requestId = StringUtils.trim(request.getRequestId());
        if (StringUtils.isEmpty(requestId)) {
            requestId = UUIDGenerator.generate();
            request.setRequestId(requestId);
        }
    }

    public static abstract class StreamEventListener extends EventSourceListener {
        @Override
        public void onEvent(EventSource eventSource, String id, String type,
                            String data) {
            try {
                CompletionsResponse response = JSON.parseObject(data, CompletionsResponse.class);
                onEvent(response);
            } catch (Exception e) {
                onFailure(e, 500, "parse completions response error");
            }
        }

        @Override
        public void onFailure(EventSource eventSource, Throwable t, Response response) {
            int code = 0;
            String body = null;
            Throwable th = t;
            if (response != null) {
                code = response.code();
                try {
                    body = response.body() == null ? "" : response.body().string();
                } catch (IOException e) {
                    th = e;
                }
            }

            onFailure(th, code, body);
            eventSource.cancel();
        }

        @Override
        public void onOpen(EventSource eventSource, Response response) {
            onOpen();
        }

        @Override
        public void onClosed(EventSource eventSource) {
            onClosed();
            eventSource.cancel();
        }

        /**
         * event消息回调函数
         *
         * @param response 回调消息
         */
        public abstract void onEvent(CompletionsResponse response);

        /**
         * event失败消息回调函数
         *
         * @param t 错误消息栈
         * @param code 错误代码
         * @param body 错误内容
         */
        public void onFailure(@Nullable Throwable t, int code, String body) {

        }

        public void onOpen() {

        }

        public void onClosed() {

        }
    }
}
