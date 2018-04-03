package com.wangyanrui.common.command;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * created by wangyanrui on 2018-04-03 23:21
 */
@Data
@Accessors(chain = true)
public class CommandResponse {
    /**
     * Mainly used for filter processing
     */
    private Exception exception;
    /**
     * result of businesses processing
     */
    private Object result;
}
