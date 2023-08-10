/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk.consts;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Title Embedding文本类型枚举类.<br/>
 * Description Embedding文本类型枚举类.<br/>
 * Created at 2023-07-27 16:33
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public enum EmbeddingTextTypeEnum {
    /**
     * Embedding query类型的文本, 用来检索召回
     */
    QUERY("query"),

    /**
     * Embedding document类型的文本, 用来保存文档向量至向量数据库
     */
    DOCUMENT("document"),
    ;

    private final String type;

    EmbeddingTextTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static EmbeddingTextTypeEnum typeOf(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }

        Optional<EmbeddingTextTypeEnum> any = Arrays.stream(values())
                .filter(embeddingTextTypeEnum -> type.equals(embeddingTextTypeEnum.getType()))
                .findAny();
        return any.orElse(null);
    }
}
