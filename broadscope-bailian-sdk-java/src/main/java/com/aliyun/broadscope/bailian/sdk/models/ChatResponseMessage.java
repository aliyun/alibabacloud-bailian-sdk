/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk.models;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Title chat response message.<br>
 * Description chat response message.<br>
 * Created at 2024-01-18 18:47
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class ChatResponseMessage {
    /**
     * message发送对象
     * user 用户输入
     * assistant 模型响应
     * system 输入给模型的系统级知识
     * tool 工具生成的内容
     * 必填
     */
    @JSONField(name = "Role")
    private String role;

    /**
     * message消息体
     * 有tool_calls 、 function_call的情况下content可能为空
     */
    @JSONField(name = "Content")
    private String content;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChatResponseMessage{");
        sb.append("role='").append(role).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
