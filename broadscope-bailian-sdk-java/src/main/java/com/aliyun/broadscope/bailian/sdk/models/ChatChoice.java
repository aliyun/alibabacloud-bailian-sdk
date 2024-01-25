/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk.models;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Title chat choice.<br>
 * Description chat choice.<br>
 * Created at 2024-01-18 17:52
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class ChatChoice {
    /**
     * 生成的内容对应的index
     */
    @JSONField(name = "Index")
    private long index;

    /**
     * 同步响应时返回这个字段
     */
    @JSONField(name = "Message")
    private ChatResponseMessage message;

    /**
     * 停止原因
     */
    @JSONField(name = "FinishReason")
    private String finishReason;

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public ChatResponseMessage getMessage() {
        return message;
    }

    public void setMessage(ChatResponseMessage message) {
        this.message = message;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChatChoice{");
        sb.append("index=").append(index);
        sb.append(", message=").append(message);
        sb.append(", finishReason='").append(finishReason).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
