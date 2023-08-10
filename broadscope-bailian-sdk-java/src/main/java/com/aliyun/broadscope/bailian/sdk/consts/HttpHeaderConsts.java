/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk.consts;

/**
 * Title HttpHeader常量.<br/>
 * Description HttpHeader常量.<br/>
 * Created at 2023-06-07 16:23
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public interface HttpHeaderConsts {
    interface Keys {
        String CONTENT_TYPE = "Content-Type";

        String ACCEPT = "Accept";

        String AUTHORIZATION = "Authorization";
    }

    interface MediaType {
        String APPLICATION_JSON_VALUE = "application/json";

        String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

        String TEXT_EVENT_STREAM_VALUE = "text/event-stream";
    }
}
