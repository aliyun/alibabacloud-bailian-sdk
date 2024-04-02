/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk;

import com.alibaba.fastjson.JSON;
import com.aliyun.broadscope.bailian.sdk.consts.ConfigConsts;
import com.aliyun.broadscope.bailian.sdk.consts.HttpHeaderConsts;
import com.aliyun.broadscope.bailian.sdk.models.BaiLianConfig;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsRequest;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsResponse;
import com.aliyun.broadscope.bailian.sdk.models.ConnectOptions;
import com.aliyun.broadscope.bailian.sdk.utils.UUIDGenerator;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.time.Duration;
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
    private static final Logger logger = LoggerFactory.getLogger(ApplicationClient.class);

    private static final Integer DEFAULT_CONNECTION_POOL_SIZE = 5;

    private static final Duration DEFAULT_CONNECTION_IDLE_TIMEOUT = Duration.ofSeconds(600);

    private final String token;

    private String endpoint;

    private ConnectOptions connectOptions;

    private final OkHttpClient okHttpClient;



    /**
     * 构造实例对象
     *
     * @param config 配置信息
     */
    @Deprecated
    public ApplicationClient(BaiLianConfig config) {
        if (config == null) {
            throw new BaiLianSdkException("config can not be null");
        }

        this.token = config.getApiKey();
        this.endpoint = config.getEndpoint();
        checkConfig();

        this.okHttpClient = buildOkHttpClient();
    }

    @Deprecated
    public ApplicationClient(BaiLianConfig config, ConnectOptions connectOptions) {
        if (config == null) {
            throw new BaiLianSdkException("config can not be null");
        }

        this.token = config.getApiKey();
        this.endpoint = config.getEndpoint();
        checkConfig();

        this.connectOptions = connectOptions;
        this.okHttpClient = buildOkHttpClient();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;

        private String endpoint;

        private ConnectOptions connectOptions;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder connectOptions(ConnectOptions connectOptions) {
            this.connectOptions = connectOptions;
            return this;
        }

        public ApplicationClient build() {
            return new ApplicationClient(this);
        }
    }

    private ApplicationClient(Builder builder) {
        token = builder.token;
        endpoint = builder.endpoint;
        checkConfig();

        connectOptions = builder.connectOptions;
        this.okHttpClient = buildOkHttpClient();
    }

    /**
     * 创建默认的OkHttpClient
     */
    private OkHttpClient buildOkHttpClient() {
        long connectTimeout = 30000;
        long writeTimeout = 30000;
        long readTimeout = 600000;
        Integer connectionPoolSize = DEFAULT_CONNECTION_POOL_SIZE;

        if (connectOptions != null) {
            if (connectOptions.getConnectTimeout() > 0) {
                connectTimeout = connectOptions.getConnectTimeout();
            }

            if (connectOptions.getWriteTimeout() > 0) {
                writeTimeout = connectOptions.getWriteTimeout();
            }

            if (connectOptions.getReadTimeout() > 0) {
                readTimeout = connectOptions.getReadTimeout();
            }

            if (connectOptions.getConnectPoolSize() > 0) {
                connectionPoolSize = connectOptions.getConnectPoolSize();
            }
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        Dispatcher dispatcher = new Dispatcher();
        if (dispatcher.getMaxRequests() < connectionPoolSize) {
            dispatcher.setMaxRequests(connectionPoolSize);
            dispatcher.setMaxRequestsPerHost(connectionPoolSize);
        }

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(Duration.ofMillis(connectTimeout))
                .readTimeout(Duration.ofMillis(readTimeout))
                .writeTimeout(Duration.ofMillis(writeTimeout))
                .addInterceptor(logging)
                .dispatcher(dispatcher)
                .connectionPool(
                        new ConnectionPool(
                                connectionPoolSize,
                                DEFAULT_CONNECTION_IDLE_TIMEOUT.getSeconds(),
                                TimeUnit.SECONDS));
        return clientBuilder.build();
    }

    /**
     * 非流式文本生成
     *
     * @param chatClientRequest prompt请求信息
     * @return 文本生成响应结果
     */
    public CompletionsResponse completions(CompletionsRequest chatClientRequest) {
        checkChatClientRequest(chatClientRequest);

        setRequestIdIfNull(chatClientRequest);

        Headers headers = new Headers.Builder()
                .add(HttpHeaderConsts.Keys.CONTENT_TYPE, HttpHeaderConsts.MediaType.APPLICATION_JSON_UTF8_VALUE)
                .add(HttpHeaderConsts.Keys.ACCEPT, HttpHeaderConsts.MediaType.APPLICATION_JSON_VALUE)
                .add(HttpHeaderConsts.Keys.AUTHORIZATION, "Bearer " + this.token)
                .build();

        chatClientRequest.setStream(false);

        MediaType mediaType = MediaType.parse(HttpHeaderConsts.MediaType.APPLICATION_JSON_UTF8_VALUE);
        String data = JSON.toJSONString(chatClientRequest);
        RequestBody requestBody = RequestBody.create(mediaType, data);

        Request request = new Request.Builder()
                .url(this.getEndpoint() + "/v2/app/completions")
                .headers(headers)
                .post(requestBody)
                .build();

        logger.info("new request, request id: {}", chatClientRequest.getRequestId());
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
     *
     * @param chatClientRequest   prompt请求
     * @param streamEventListener 流式响应监听器
     */
    public void streamCompletions(CompletionsRequest chatClientRequest, StreamEventListener streamEventListener) {
        checkChatClientRequest(chatClientRequest);

        setRequestIdIfNull(chatClientRequest);

        Headers headers = new Headers.Builder()
                .add(HttpHeaderConsts.Keys.CONTENT_TYPE, HttpHeaderConsts.MediaType.APPLICATION_JSON_UTF8_VALUE)
                .add(HttpHeaderConsts.Keys.ACCEPT, HttpHeaderConsts.MediaType.TEXT_EVENT_STREAM_VALUE)
                .add(HttpHeaderConsts.Keys.AUTHORIZATION, "Bearer " + this.token)
                .build();

        chatClientRequest.setStream(true);

        MediaType mediaType = MediaType.parse(HttpHeaderConsts.MediaType.APPLICATION_JSON_UTF8_VALUE);
        String data = JSON.toJSONString(chatClientRequest);
        RequestBody requestBody = RequestBody.create(mediaType, data);

        try {
            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);

            logger.info("build event request, request id: {}", chatClientRequest.getRequestId());
            Request request = new Request.Builder()
                    .url(this.getEndpoint() + "/v2/app/completions")
                    .headers(headers)
                    .post(requestBody)
                    .build();

            logger.info("new event source, request id: {}", chatClientRequest.getRequestId());
            factory.newEventSource(request, streamEventListener);

            logger.info("after new event source, request id: {}", chatClientRequest.getRequestId());
        } catch (Exception e) {
            throw new BaiLianSdkException(e);
        }
    }

    /**
     * completions stream
     *
     * @param chatClientRequest prompt请求
     */
    public Flux<CompletionsResponse> streamCompletions(CompletionsRequest chatClientRequest) {
        Sinks.Many<CompletionsResponse> sink = Sinks.many().unicast().onBackpressureBuffer();
        streamCompletions(chatClientRequest, new StreamEventListener() {
            @Override
            public void onEvent(CompletionsResponse response) {

            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                try {
                    CompletionsResponse response = JSON.parseObject(data, CompletionsResponse.class);
                    sink.tryEmitNext(response);
                } catch (Exception e) {
                    sink.tryEmitError(e);
                }
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                try {
                    if (t == null && response != null) {
                        String body = response.body() == null ? "" : response.body().string();
                        String message = String.format("code=%d, message=%s", response.code(), body);
                        t = new BaiLianSdkException(message);
                    }

                    if (t != null) {
                        sink.tryEmitError(t);
                    }
                } catch (IOException e) {
                    sink.tryEmitError(e);
                } finally {
                    eventSource.cancel();
                }
            }

            @Override
            public void onOpen(EventSource eventSource, Response response) {
            }

            @Override
            public void onClosed(EventSource eventSource) {
                try {
                    sink.tryEmitComplete();
                } finally {
                    eventSource.cancel();
                }
            }
        });

        return sink.asFlux();
    }

    private void checkConfig() {
        String token = StringUtils.trim(this.token);
        if (StringUtils.isEmpty(token)) {
            throw new BaiLianSdkException("api key is required");
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
        if (StringUtils.isEmpty(prompt) && (request.getMessages() == null || request.getMessages().size() == 0)) {
            throw new BaiLianSdkException("prompt or messages is required");
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
         * @param t    错误消息栈
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

    public String getEndpoint() {
        if (StringUtils.isBlank(endpoint)) {
            endpoint = ConfigConsts.ENDPOINT;
        }
        return endpoint;
    }
}
