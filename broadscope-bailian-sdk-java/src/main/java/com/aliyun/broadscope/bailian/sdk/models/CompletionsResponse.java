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
 * Title 客户端对话响应.<br>
 * Description 客户端对话响应.<br>
 * Created at 2023-06-07 16:33
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class CompletionsResponse implements Serializable {
    /**
     * 请求处理是否成功
     */
    @JSONField(name = "Success")
    private boolean success = true;

    /**
     * 请求失败code
     */
    @JSONField(name = "Code")
    private String code;

    /**
     * 请求失败描述
     */
    @JSONField(name = "Message")
    private String message;

    /**
     * 请求Id
     */
    @JSONField(name = "RequestId")
    private String requestId;

    /**
     * 请求处理结果
     */
    @JSONField(name = "Data")
    private Data data;

    public static class Data implements Serializable {
        private static final long serialVersionUID = -2717404558710025579L;

        /**
         * 大模型请求id
         */
        @JSONField(name = "responseId")
        private String responseId;

        /**
         * 上下文sessionId
         */
        @JSONField(name = "SessionId")
        private String sessionId;

        /**
         * 文本生成的内容
         */
        @JSONField(name = "Text")
        private String text;

        /**
         * 将Debug信息存储下来
         */
        @JSONField(name = "Thoughts")
        private List<Thought> thoughts;

        /**
         * 文档引用
         */
        @JSONField(name = "DocReferences")
        private List<DocReference> docReferences;

        public String getResponseId() {
            return responseId;
        }

        public void setResponseId(String responseId) {
            this.responseId = responseId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<Thought> getThoughts() {
            return thoughts;
        }

        public void setThoughts(List<Thought> thoughts) {
            this.thoughts = thoughts;
        }

        public List<DocReference> getDocReferences() {
            return docReferences;
        }

        public void setDocReferences(List<DocReference> docReferences) {
            this.docReferences = docReferences;
        }

        @Override
        public String toString() {
            return "Data{" + "responseId='" + responseId + '\'' +
                    ", sessionId='" + sessionId + '\'' +
                    ", text='" + text + '\'' +
                    ", thoughts=" + thoughts +
                    ", docReferences=" + docReferences +
                    '}';
        }
    }

    public static class Thought implements Serializable {
        private static final long serialVersionUID = -3740672347955312773L;
        /**
         * 模型思考过程
         */
        @JSONField(name = "Thought")
        private String thought;

        /**
         * action的类型
         * type : response 代表最终结果 - 对应response
         * type : api 代表调用api - 对应action, actionInput, observation
         *
         */
        @JSONField(name = "ActionType")
        private String actionType;

        /**
         * 响应返回
         */
        @JSONField(name = "Response")
        private String response;

        /**
         * 插件名称
         */
        @JSONField(name = "ActionName")
        private String actionName;

        /**
         * 大模型产生的api code，用于标识调用哪个api
         */
        @JSONField(name = "Action")
        private String action;

        /**
         * 入参的流式结果
         */
        @JSONField(name = "ActionInputStream")
        private String actionInputStream;

        /**
         * 大模型产生的api调用的入参
         */
        @JSONField(name = "ActionInput")
        private JSONObject actionInput;

        /**
         * api的返回结果
         */
        @JSONField(name = "Observation")
        private String observation;

        public String getThought() {
            return thought;
        }

        public void setThought(String thought) {
            this.thought = thought;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public String getActionName() {
            return actionName;
        }

        public void setActionName(String actionName) {
            this.actionName = actionName;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getActionInputStream() {
            return actionInputStream;
        }

        public void setActionInputStream(String actionInputStream) {
            this.actionInputStream = actionInputStream;
        }

        public JSONObject getActionInput() {
            return actionInput;
        }

        public void setActionInput(JSONObject actionInput) {
            this.actionInput = actionInput;
        }

        public String getObservation() {
            return observation;
        }

        public void setObservation(String observation) {
            this.observation = observation;
        }

        @Override
        public String toString() {
            return "Thought{" + "thought='" + thought + '\'' +
                   ", actionType='" + actionType + '\'' +
                   ", response='" + response + '\'' +
                   ", actionName='" + actionName + '\'' +
                   ", action='" + action + '\'' +
                   ", actionInputStream='" + actionInputStream + '\'' +
                   ", actionInput=" + actionInput +
                   ", observation='" + observation + '\'' +
                   '}';
        }
    }

    public static class DocReference implements Serializable {

        private static final long serialVersionUID = -5771627478159312044L;

        /**
         * 引用的角标索引
         */
        @JSONField(name = "IndexId")
        private String indexId;

        /**
         * 引用的文档标题
         */
        @JSONField(name = "Title")
        private String title;

        /**
         * 引用的文档Id
         */
        @JSONField(name = "DocId")
        private String docId;

        /**
         * 引用的文档名
         */
        @JSONField(name = "DocName")
        private String docName;

        /**
         * 文档下载地址
         */
        @JSONField(name = "DocUrl")
        private String docUrl;

        /**
         * 引用的文档内容
         */
        @JSONField(name = "Text")
        private String text;

        /**
         * biz Id
         */
        @JSONField(name = "BizId")
        private String bizId;

        public String getIndexId() {
            return indexId;
        }

        public void setIndexId(String indexId) {
            this.indexId = indexId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public String getDocName() {
            return docName;
        }

        public void setDocName(String docName) {
            this.docName = docName;
        }

        public String getDocUrl() {
            return docUrl;
        }

        public void setDocUrl(String docUrl) {
            this.docUrl = docUrl;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getBizId() {
            return bizId;
        }

        public void setBizId(String bizId) {
            this.bizId = bizId;
        }

        @Override
        public String toString() {
            return "DocReference{" + "indexId='" + indexId + '\'' +
                    ", title='" + title + '\'' +
                    ", docId='" + docId + '\'' +
                    ", docName='" + docName + '\'' +
                    ", docUrl='" + docUrl + '\'' +
                    ", text='" + text + '\'' +
                    ", bizId='" + bizId + '\'' +
                    '}';
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CompletionsResponse{" + "success=" + success +
               ", code='" + code + '\'' +
               ", message='" + message + '\'' +
               ", requestId='" + requestId + '\'' +
               ", data=" + data +
               '}';
    }
}