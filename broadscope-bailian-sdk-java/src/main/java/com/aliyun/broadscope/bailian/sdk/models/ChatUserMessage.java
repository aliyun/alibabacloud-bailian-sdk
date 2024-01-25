/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk.models;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Title chat user message.<br>
 * Description chat user message.<br>
 * Created at 2024-01-18 17:40
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class ChatUserMessage extends ChatRequestMessage{
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

    public ChatUserMessage(String content) {
        this.content = content;
    }

    public String getRole() {
        return "user";
    }

    public String getContent() {
        return content;
    }

    public ChatUserMessage setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        return "ChatUserMessage{" + "role='" + role + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
