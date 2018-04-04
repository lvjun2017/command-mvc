package com.wangyanrui.common.command.core;

import com.wangyanrui.common.command.Command;
import com.wangyanrui.common.command.CommandCodec;
import com.wangyanrui.common.command.CommandRequest;
import com.wangyanrui.common.command.CommandResponse;
import com.wangyanrui.common.command.util.AnnotationUtil;
import com.wangyanrui.common.dto.Result;
import com.wangyanrui.common.exception.OperaExceptionHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
     * filters
     */
    private List<CommandFilter> filters = new ArrayList<>();

    /**
     * businesses service list
     */
    private List<Object> handlerList = new ArrayList<>();

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

    public void setHandlerList(List<Object> handlerList) {
        if (CollectionUtils.isNotEmpty(handlerList)) {
            this.handlerList = handlerList;
            dealCommandAnnotation();
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

        // get component bean
        Object component = handlerMapped.get(commandRequest.getComponentName());
        OperaExceptionHandler.nullCheck(component, "no mapped about component : " + commandRequest.getComponentName());

        // get component method
        Method method = getComponentMethod(commandRequest, component);

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
            method.invoke(component, commandRequest, commandResponse);
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
     */
    private void dealCommandAnnotation() {
        for (Object handlerClazz : this.handlerList) {
            OperaExceptionHandler.flagCheck(!AnnotationUtil.hasAnnotation(handlerClazz.getClass(), Command.class),
                    "error mapped relation : " + handlerClazz.getClass() + " (not annotation @Command)");

            // auto generate mapped key
            String componentCommandValue = handlerClazz.getClass().getAnnotation(Command.class).value();
            String componentName = StringUtils.isNotEmpty(componentCommandValue) ?
                    componentCommandValue :
                    StringUtils.uncapitalize(handlerClazz.getClass().getSimpleName());

            /*
               if this handler class has Command annotation
               deal method who tagged command annotation
               pre save this method to handlerMethodCache
             */
            Method[] methods = handlerClazz.getClass().getDeclaredMethods();

            for (Method method : methods) {
                Command annotation = method.getAnnotation(Command.class);
                OperaExceptionHandler.flagCheck(Objects.isNull(method.getAnnotation(Command.class)),
                        "1");
                // "error mapped relation : " + method.getName() + " (not annotation @Command)"
                method.setAccessible(true);

                // auto generate method key
                String methodCommandValue = method.getAnnotation(Command.class).value();
                String methodName = StringUtils.isNotEmpty(methodCommandValue) ?
                        methodCommandValue :
                        StringUtils.uncapitalize(method.getName());

                String mappedKey = componentName + "." + methodName;
                OperaExceptionHandler.flagCheck(this.handlerMethodCache.containsKey(mappedKey),
                        "");

                this.handlerMethodCache.put(mappedKey, method);
            }
        }
    }

    /**
     * get real business method
     *
     * @param commandRequest CommandRequest
     * @param component      You Command Component
     * @return
     */
    private Method getComponentMethod(CommandRequest commandRequest, Object component) {
        // get real service bean's method
        Method method = this.handlerMethodCache.get(commandRequest.getActionName());
        if (null == method) {
            // // not require synchronized because used by ConcurrentHashMap
            // try {
            //     method = component.getClass().getMethod(commandRequest.getMethodName(), CommandRequest.class, CommandResponse.class);
            // } catch (Throwable e) {
            //     OperaExceptionHandler.throwException("no mapped about component.method : " + commandRequest.getActionName());
            // }
            // method.setAccessible(true);
            // handlerMethodCache.put(commandRequest.getActionName(), method);

            OperaExceptionHandler.throwException("no mapped about service.method : " + commandRequest.getActionName());
        }
        return method;
    }


}
