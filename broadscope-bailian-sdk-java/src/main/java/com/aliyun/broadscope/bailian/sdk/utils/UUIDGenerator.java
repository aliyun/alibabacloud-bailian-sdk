/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk.utils;

import java.util.UUID;

/**
 * Title uuid generator.<br/>
 * Description uuid generator.<br/>
 * Created at 2023-06-07 17:20
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class UUIDGenerator {
    public static String generate() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }
}
