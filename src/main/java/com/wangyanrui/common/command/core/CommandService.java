package com.wangyanrui.common.command.core;

import com.wangyanrui.common.command.CommandCodec;
import com.wangyanrui.common.command.CommandRequest;
import com.wangyanrui.common.command.CommandResponse;
import com.wangyanrui.common.dto.Result;
import com.wangyanrui.common.exception.OperaExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CommandService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * save filter
     */
    private List<CommandFilter> filters = new ArrayList<>();
    /**
     * save real service
     */
    private Map<String, Object> handlerMapped = new ConcurrentHashMap<>();
    /**
     * save real service method
     */
    private Map<String, Method> handlerMethodCache = new ConcurrentHashMap<>();

    public void setFilters(List<CommandFilter> filters) {
        this.filters = filters;
    }

    public void setHandlerMapped(Map<String, Object> handlerMapped) {
        this.handlerMapped = handlerMapped;
    }

    Result handle(HttpServletRequest req) {
        // build command request bean
        CommandRequest commandRequest = CommandCodec.decodeRequest(req);

        // get real service bean
        Object realService = handlerMapped.get(commandRequest.getServiceName());
        OperaExceptionHandler.nullCheck(realService, "no mapped about service : " + commandRequest.getServiceName());

        // get real service bean's method
        Method method = handlerMethodCache.get(commandRequest.getMethodName());
        if (null == method) {
            // not require synchronized because used by ConcurrentHashMap
            try {
                method = realService.getClass().getMethod(commandRequest.getMethodName(), CommandRequest.class, CommandResponse.class);
            } catch (Throwable e) {
                OperaExceptionHandler.throwException("no mapped about service.method : " + commandRequest.getActionName());
            }
            method.setAccessible(true);
            handlerMethodCache.put(commandRequest.getActionName(), method);
        }


        CommandResponse commandResponse = new CommandResponse();

        // preHandler
        for (CommandFilter filter : filters) {
            boolean isContain = filter.preHandler(commandRequest, commandResponse);
            if (!isContain) {
                return CommandCodec.encodeFilterResult();
            }
        }

        // do businesses
        if (Objects.isNull(commandResponse.getException())) {
            try {
                method.invoke(realService, commandRequest, commandResponse);
            } catch (IllegalAccessException | InvocationTargetException e) {
                OperaExceptionHandler.throwException(e);
            }
        }


        // postHandler
        for (int i = filters.size() - 1; i >= 0; i--) {
            CommandFilter filter = filters.get(i);
            boolean isContain = filter.preHandler(commandRequest, commandResponse);
            if (!isContain) {
                // return CommandCodec.encodeFilterResult();
                break;
            }
        }


        return CommandCodec.encodeResult(commandResponse);
    }
}
