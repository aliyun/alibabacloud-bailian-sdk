/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk.models;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

/**
 * Title 对话客户端请求.<br/>
 * Description 对话客户端请求.<br/>
 * Created at 2023-06-07 15:24
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class CompletionsRequest implements Serializable {
    /**
     * 请求唯一标识
     */
    @JSONField(name = "RequestId")
    private String requestId;

    /**
     * 上下文sessionId, 若传入了history, 则优先以history作为多轮对话历史, 上下文sessionId会失效
     */
    @JSONField(name = "SessionId")
    private String sessionId;

    /**
     * 百炼应用的code
     */
    @JSONField(name = "AppId")
    private String appId;

    /**
     * 提示词
     */
    @JSONField(name = "Prompt")
    private String prompt;

    /**
     * 随机性参数
     * 取值范围从0-1的浮点型
     * 值越大表示准确性越高，随机性越差
     */
    @JSONField(name = "TopP")
    private double topP;


    /**
     * 是否流式输出
     */
    @JSONField(name = "Stream")
    private boolean stream;

    /**
     * 调用api插件的业务透传参数
     */
    @JSONField(name = "BizParams")
    private JSONObject bizParams;

    /**
     * 是否包含大模型thoughts结果
     */
    @JSONField(name = "HasThoughts")
    private boolean hasThoughts;

    /**
     * 多轮对话历史
     */
    @JSONField(name = "History")
    private List<ChatQaPair> history;

    /**
     * 文档引用类型, 取值为simple、indexed
     * simple: 简单模式的doc reference, 在返回数据包含文档引用的基本信息, 不包含文档在文本中的索引位置
     * indexed: indexed模式的doc reference, 在返回数据包含文档引用的基本信息, 同时包含文档在文本中的索引位置
     */
    @JSONField(name = "DocReferenceType")
    private String docReferenceType;

    public static class ChatQaPair implements Serializable {
        /**
         * 用户的query
         */
        @JSONField(name = "User")
        private String user;

        /**
         * 模型的答案
         */
        @JSONField(name = "Bot")
        private String bot;

        public ChatQaPair() {
        }

        public ChatQaPair(String user, String bot) {
            this.user = user;
            this.bot = bot;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getBot() {
            return bot;
        }

        public void setBot(String bot) {
            this.bot = bot;
        }

        @Override
        public String toString() {
            return "ChatQaPair{" + "user='" + user + '\'' +
                   ", bot='" + bot + '\'' +
                   '}';
        }
    }

    public String getRequestId() {
        return requestId;
    }

    public CompletionsRequest setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public CompletionsRequest setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public CompletionsRequest setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getPrompt() {
        return prompt;
    }

    public CompletionsRequest setPrompt(String prompt) {
        this.prompt = prompt;
        return this;
    }

    public double getTopP() {
        return topP;
    }

    public CompletionsRequest setTopP(double topP) {
        this.topP = topP;
        return this;
    }

    public boolean isStream() {
        return stream;
    }

    public CompletionsRequest setStream(boolean stream) {
        this.stream = stream;
        return this;
    }

    public JSONObject getBizParams() {
        return bizParams;
    }

    public CompletionsRequest setBizParams(JSONObject bizParams) {
        this.bizParams = bizParams;
        return this;
    }

    public boolean isHasThoughts() {
        return hasThoughts;
    }

    public CompletionsRequest setHasThoughts(boolean hasThoughts) {
        this.hasThoughts = hasThoughts;
        return this;
    }

    public List<ChatQaPair> getHistory() {
        return history;
    }

    public CompletionsRequest setHistory(List<ChatQaPair> history) {
        this.history = history;
        return this;
    }

    public String getDocReferenceType() {
        return docReferenceType;
    }

    public CompletionsRequest setDocReferenceType(String docReferenceType) {
        this.docReferenceType = docReferenceType;
        return this;
    }

    @Override
    public String toString() {
        return "CompletionsRequest{" + "requestId='" + requestId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", appId='" + appId + '\'' +
                ", prompt='" + prompt + '\'' +
                ", topP=" + topP +
                ", stream=" + stream +
                ", bizParams=" + bizParams +
                ", hasThoughts=" + hasThoughts +
                ", history=" + history +
                ", docReferenceType='" + docReferenceType + '\'' +
                '}';
    }
}