/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk.models;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Title chat assistant message.<br>
 * Description chat assistant message.<br>
 * Created at 2024-01-18 17:45
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class ChatAssistantMessage extends ChatRequestMessage{
    /**
     * message发送对象
     */
    @JSONField(name = "Role")
    private String role;

    /**
     * message消息体
     */
    @JSONField(name = "Content")
    private String content;

    public ChatAssistantMessage(String content) {
        this.content = content;
    }

    public String getRole() {
        return "assistant";
    }

    public String getContent() {
        return content;
    }

    public ChatAssistantMessage setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        return "ChatAssistantMessage{" + "role='" + role + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
