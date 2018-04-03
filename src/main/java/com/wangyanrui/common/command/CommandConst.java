package com.wangyanrui.common.command;

/**
 * created by wangyanrui on 2018-04-03 21:39
 */
public interface CommandConst {
    /**
     * default encoding
     */
    String DEFAULT_ENCODING = "UTF-8";

    /**
     * default content type
     */
    String DEFAULT_CONTENT_TYPE = "application/json";
    /**
     * response content type
     */
    String RESPONSE_CONTENT_TYPE = DEFAULT_CONTENT_TYPE + "; charset=" + DEFAULT_ENCODING;

    // ==================== 消息封装字段 ====================
    String REQUEST_ACTION = "action";
}
