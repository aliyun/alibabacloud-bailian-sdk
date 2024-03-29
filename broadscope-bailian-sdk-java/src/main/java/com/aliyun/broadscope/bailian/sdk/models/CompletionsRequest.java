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
 * Title 对话客户端请求.<br>
 * Description 对话客户端请求.<br>
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
    private Double topP;

    /**
     * 是否流式输出
     */
    @JSONField(name = "Stream")
    private Boolean stream;

    /**
     * 调用api插件的业务透传参数
     */
    @JSONField(name = "BizParams")
    private JSONObject bizParams;

    /**
     * 是否包含大模型thoughts结果
     */
    @JSONField(name = "HasThoughts")
    private Boolean hasThoughts;

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

    @JSONField(name = "Parameters")
    private Parameter parameters;

    /**
     * 文档标签ID
     */
    @JSONField(name = "DocTagIds")
    @Deprecated
    private List<Long> docTagIds;

    /**
     * 文档标签codes
     */
    @JSONField(name = "DocTagCodes")
    private List<String> docTagCodes;

    /**
     * 消息列表
     */
    @JSONField(name = "Messages")
    private List<ChatRequestMessage> messages;

    public static class ChatQaPair implements Serializable {
        private static final long serialVersionUID = -3051114257693095299L;
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

    public static class Parameter implements Serializable{

        private static final long serialVersionUID = 8408299257902302971L;

        /**
         * 模型参数
         */
        @JSONField(name = "TopK")
        private Integer topK;

        /**
         * 随机种子
         */
        @JSONField(name = "Seed")
        private Integer seed;

        /**
         * 是否使用原始的prompt
         */
        @JSONField(name = "UseRawPrompt")
        private Boolean useRawPrompt;

        /**
         * 采样温度
         * 采样温度在0到2之间。较高的值(如0.8)将使输出更加随机，而较低的值(如0.2)将使输出更加集中和确定。
         * 我们通常建议修改temperature或top_p，但不建议两者都修改。
         */
        @JSONField(name = "Temperature")
        private Double temperature;

        /**
         * 模型生成内容的最大长度
         * 生成的token的最大数量。输入token数量和生成token数量的总长度受模型上下文长度的限制。
         */
        @JSONField(name = "MaxTokens")
        private Integer maxTokens;

        /**
         * API将停止生成token，最多支持4个停用词（停用句）。
         */
        @JSONField(name = "Stop")
        private List<String> stop;


        /**
         * true 逐个增量输出非叠加模式
         * false 增量输出叠加模式
         */
        @JSONField(name = "IncrementalOutput")
        private Boolean incrementalOutput;

        /**
         * 返回结果类型
         * [text|message]
         * 默认为text
         * 当为message时，输出参考message结果示例。
         * 推荐优先使用message格式。
         */
        @JSONField(name = "ResultFormat")
        private String resultFormat;

        /**
         * 是否启用搜索
         */
        @JSONField(name = "EnableSearch")
        private Boolean enableSearch;

        public Integer getTopK() {
            return topK;
        }

        public Parameter setTopK(Integer topK) {
            this.topK = topK;
            return this;
        }

        public Integer getSeed() {
            return seed;
        }

        public Parameter setSeed(Integer seed) {
            this.seed = seed;
            return this;
        }

        public Boolean isUseRawPrompt() {
            return useRawPrompt;
        }

        public Parameter setUseRawPrompt(Boolean useRawPrompt) {
            this.useRawPrompt = useRawPrompt;
            return this;
        }

        public Boolean getUseRawPrompt() {
            return useRawPrompt;
        }

        public Double getTemperature() {
            return temperature;
        }

        public Parameter setTemperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Integer getMaxTokens() {
            return maxTokens;
        }

        public Parameter setMaxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public List<String> getStop() {
            return stop;
        }

        public Parameter setStop(List<String> stop) {
            this.stop = stop;
            return this;
        }

        public Boolean getIncrementalOutput() {
            return incrementalOutput;
        }

        public Parameter setIncrementalOutput(Boolean incrementalOutput) {
            this.incrementalOutput = incrementalOutput;
            return this;
        }

        public String getResultFormat() {
            return resultFormat;
        }

        public Parameter setResultFormat(String resultFormat) {
            this.resultFormat = resultFormat;
            return this;
        }

        public Boolean getEnableSearch() {
            return enableSearch;
        }

        public Parameter setEnableSearch(Boolean enableSearch) {
            this.enableSearch = enableSearch;
            return this;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Parameter{");
            sb.append("topK=").append(topK);
            sb.append(", seed=").append(seed);
            sb.append(", useRawPrompt=").append(useRawPrompt);
            sb.append(", temperature=").append(temperature);
            sb.append(", maxTokens=").append(maxTokens);
            sb.append(", stop=").append(stop);
            sb.append(", incrementalOutput=").append(incrementalOutput);
            sb.append(", resultFormat='").append(resultFormat).append('\'');
            sb.append(", enableSearch=").append(enableSearch);
            sb.append('}');
            return sb.toString();
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

    public Double getTopP() {
        return topP;
    }

    public CompletionsRequest setTopP(Double topP) {
        this.topP = topP;
        return this;
    }

    public Boolean isStream() {
        return stream;
    }

    public CompletionsRequest setStream(Boolean stream) {
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

    public Boolean isHasThoughts() {
        return hasThoughts;
    }

    public CompletionsRequest setHasThoughts(Boolean hasThoughts) {
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

    public Parameter getParameters() {
        return parameters;
    }

    public CompletionsRequest setParameters(Parameter parameters) {
        this.parameters = parameters;
        return this;
    }

    @Deprecated
    public List<Long> getDocTagIds() {
        return docTagIds;
    }

    @Deprecated
    public CompletionsRequest setDocTagIds(List<Long> docTagIds) {
        this.docTagIds = docTagIds;
        return this;
    }

    public List<String> getDocTagCodes() {
        return docTagCodes;
    }

    public CompletionsRequest setDocTagCodes(List<String> docTagCodes) {
        this.docTagCodes = docTagCodes;
        return this;
    }

    public List<ChatRequestMessage> getMessages() {
        return messages;
    }

    public CompletionsRequest setMessages(List<ChatRequestMessage> messages) {
        this.messages = messages;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CompletionsRequest{");
        sb.append("requestId='").append(requestId).append('\'');
        sb.append(", sessionId='").append(sessionId).append('\'');
        sb.append(", appId='").append(appId).append('\'');
        sb.append(", prompt='").append(prompt).append('\'');
        sb.append(", topP=").append(topP);
        sb.append(", stream=").append(stream);
        sb.append(", bizParams=").append(bizParams);
        sb.append(", hasThoughts=").append(hasThoughts);
        sb.append(", history=").append(history);
        sb.append(", docReferenceType='").append(docReferenceType).append('\'');
        sb.append(", parameters=").append(parameters);
        sb.append(", docTagIds=").append(docTagIds);
        sb.append(", docTagCodes=").append(docTagCodes);
        sb.append(", messages=").append(messages);
        sb.append('}');
        return sb.toString();
    }
}