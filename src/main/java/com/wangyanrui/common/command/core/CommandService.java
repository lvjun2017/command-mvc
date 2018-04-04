package com.wangyanrui.common.command.core;

import com.wangyanrui.common.command.Command;
import com.wangyanrui.common.command.CommandCodec;
import com.wangyanrui.common.command.CommandRequest;
import com.wangyanrui.common.command.CommandResponse;
import com.wangyanrui.common.command.util.AnnotationUtil;
import com.wangyanrui.common.dto.Result;
import com.wangyanrui.common.exception.OperaExceptionHandler;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * filters
     */
    private List<CommandFilter> filters = new ArrayList<>();
    /**
     * businesses service mapped relationship
     */
    private Map<String, Object> handlerMapped = new ConcurrentHashMap<>();
    /**
     * businesses service's method cache
     * key   : action(service.method)
     * value : special method
     */
    private Map<String, Method> handlerMethodCache = new ConcurrentHashMap<>();

    public void setFilters(List<CommandFilter> filters) {
        this.filters = filters;
    }

    public void setHandlerMapped(Map<String, Object> handlerMapped) {
        if (MapUtils.isNotEmpty(handlerMapped)) {
            this.handlerMapped = handlerMapped;
            dealCommandAnnotation(this.handlerMapped);
        } else {
            logger.info("no init mapped relationship. ");
        }
    }

    /**
     * core
     *
     * @param req  HttpServletRequest
     * @param resp HttpServletResponse
     * @return {@link Result}
     */
    Result handle(HttpServletRequest req, HttpServletResponse resp) {
        // build command request bean
        CommandRequest commandRequest = CommandCodec.decodeRequest(req);

        // get real service bean
        Object realService = handlerMapped.get(commandRequest.getServiceName());
        OperaExceptionHandler.nullCheck(realService, "no mapped about service : " + commandRequest.getServiceName());

        // get business method
        Method method = getBusinessMethod(commandRequest, realService);

        CommandResponse commandResponse = new CommandResponse();

        // preHandler
        for (CommandFilter filter : filters) {
            boolean isContain = filter.preHandler(commandRequest, commandResponse);
            if (!isContain) {
                return CommandCodec.encodeResult(commandResponse);
            }
        }

        // do businesses
        try {
            method.invoke(realService, commandRequest, commandResponse);
        } catch (IllegalAccessException | InvocationTargetException e) {
            OperaExceptionHandler.throwException(e);
        }

        // postHandler
        for (int i = filters.size() - 1; i >= 0; i--) {
            CommandFilter filter = filters.get(i);
            boolean isContain = filter.preHandler(commandRequest, commandResponse);
            if (!isContain) {
                break;
            }
        }

        return CommandCodec.encodeResult(commandResponse);
    }

    /**
     * deal all handler, pre save method to handlerMethodCache
     *
     * @param handlerMapped
     */
    private void dealCommandAnnotation(Map<String, Object> handlerMapped) {
        for (Map.Entry<String, Object> entry : handlerMapped.entrySet()) {
            String serviceName = entry.getKey();
            Object handlerClazz = entry.getValue();

            OperaExceptionHandler.flagCheck(!AnnotationUtil.hasAnnotation(handlerClazz.getClass(), Command.class),
                    "error mapped relation : " + serviceName + " (not annotation @Command)");

            // if this handler class has Command annotation
            // deal method who tagged command annotation
            // pre save this method to handlerMethodCache
            Method[] methods = handlerClazz.getClass().getDeclaredMethods();

            for (Method method : methods) {
                if (AnnotationUtil.hasAnnotation(method, Command.class)) {
                    method.setAccessible(true);
                    handlerMapped.put(serviceName + "." + method.getName(), method);
                }
            }

        }
    }

    /**
     * get real business method
     *
     * @param commandRequest
     * @param realService
     * @return
     */
    private Method getBusinessMethod(CommandRequest commandRequest, Object realService) {
        // get real service bean's method
        Method method = handlerMethodCache.get(commandRequest.getActionName());
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
        return method;
    }


}
