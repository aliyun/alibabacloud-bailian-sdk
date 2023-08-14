/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk.models;

/**
 * Title 连接选项.<br>
 * Description 连接选项.<br>
 * Created at 2023-07-03 21:11
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class ConnectOptions {
    private long connectTimeout;

    private long writeTimeout;

    private long readTimeout;

    public ConnectOptions() {
    }

    public ConnectOptions(long connectTimeout, long writeTimeout, long readTimeout) {
        this.connectTimeout = connectTimeout;
        this.writeTimeout = writeTimeout;
        this.readTimeout = readTimeout;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public String toString() {
        return "ConnectOptions{" + "connectTimeout=" + connectTimeout +
               ", writeTimeout=" + writeTimeout +
               ", readTimeout=" + readTimeout +
               '}';
    }
}