package com.wangyanrui.common.command.core;

import com.wangyanrui.common.command.CommandRequest;
import com.wangyanrui.common.command.CommandResponse;

/**
 * created by wangyanrui on 2018-04-03 23:51
 */
public interface CommandFilter {

    /**
     * handler处理之前的过滤
     *
     * @param request
     * @param response
     * @return 是否继续处理
     */
    boolean preHandler(CommandRequest request, CommandResponse response);

    /**
     * handler处理之后的过滤
     *
     * @param request
     * @param response
     * @return 是否继续处理
     */
    boolean postHandler(CommandRequest request, CommandResponse response);


}
