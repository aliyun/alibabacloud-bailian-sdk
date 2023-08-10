/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk;

/**
 * Title 百炼sdk exception.<br/>
 * Description 百炼sdk exception.<br/>
 * Created at 2023-06-07 15:03
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class BaiLianSdkException extends RuntimeException {
    int code;

    public BaiLianSdkException() {
        super();
    }

    public BaiLianSdkException(int code, String message) {
        super(message);
        this.code = code;
    }


    public BaiLianSdkException(String message) {
        super(message);
    }

    public BaiLianSdkException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaiLianSdkException(Throwable cause) {
        super(cause);
    }

    protected BaiLianSdkException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public int getCode() {
        return code;
    }
}
