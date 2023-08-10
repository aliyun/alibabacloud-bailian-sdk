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
 * Title 文档引用类型.<br/>
 * Description 文档引用类型.<br/>
 * Created at 2023-08-01 11:20
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public enum DocReferenceTypeEnum {
    /**
     * 简单模式的doc reference, 在返回数据包含文档引用的基本信息, 不包含文档在文本中的索引位置
     */
    SIMPLE("simple"),

    /**
     * indexed模式的doc reference, 在返回数据包含文档引用的基本信息, 同时包含文档在文本中的索引位置
     */
    INDEXED("indexed"),
    ;

    private final String type;

    DocReferenceTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static DocReferenceTypeEnum typeOf(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }

        Optional<DocReferenceTypeEnum> any = Arrays.stream(values())
                .filter(docReferenceTypeEnum -> type.equals(docReferenceTypeEnum.getType()))
                .findAny();

        return any.orElse(null);
    }
}
