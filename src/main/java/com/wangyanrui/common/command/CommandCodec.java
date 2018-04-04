package com.wangyanrui.common.command;

import com.alibaba.fastjson.JSONObject;
import com.wangyanrui.common.creator.ResultCreator;
import com.wangyanrui.common.dto.Result;
import com.wangyanrui.common.exception.OperaException;
import com.wangyanrui.common.exception.OperaExceptionHandler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

/**
 * created by wangyanrui on 2018-04-03 21:39
 */
public class CommandCodec {

    /**
     * get CommandRequest
     *
     * @param req
     * @return
     */
    public static CommandRequest decodeRequest(HttpServletRequest req) throws OperaException {
        // convert request params
        JSONObject in = null;
        try {
            in = convertParam(req);
        } catch (IOException e) {
            OperaExceptionHandler.throwException("error request param format");
        }

        // build command request param
        String action = in.getString(CommandConst.REQUEST_ACTION);
        OperaExceptionHandler.nullCheck(action,
                "error param : (not contain action string)");
        OperaExceptionHandler.flagCheck(!action.contains("."),
                "error param : (action string format error, must contain '.')");

        String[] split = action.split("\\.");
        OperaExceptionHandler.flagCheck(split.length != 2,
                "error param : (action string format error, just contain one '.')");

        String serviceName = split[0];
        String methodName = split[1];

        return new CommandRequest()
                .setActionName(action)
                .setServiceName(serviceName)
                .setMethodName(methodName)
                .setData(in);
    }

    /**
     * get Result
     *
     * @param response
     * @return
     */
    public static Result encodeResult(CommandResponse response) {
        Object result = response.getResult();
        return ResultCreator.getSuccess(result);
    }

    /**
     * convert GenericParams and JSON InputStream params
     *
     * @param req
     * @return
     * @throws IOException
     */
    private static JSONObject convertParam(HttpServletRequest req) throws IOException {
        JSONObject in = new JSONObject();

        // try to get json params
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(req.getInputStream(), CommandConst.DEFAULT_ENCODING));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        String jsonParam = responseStrBuilder.toString();
        if (StringUtils.isNotEmpty(jsonParam)) {
            JSONObject jsonParams = JSONObject.parseObject(jsonParam);
            in.putAll(jsonParams);
        }

        // try to get generic params
        Enumeration<String> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();

            // String[] value =req.getParameterValues(paramName);
            String value = req.getParameter(paramName);// just read first element

            checkDuplicateParam(in, paramName);
            in.put(paramName, value); // JSONObject use HashMap to implement put method, manual check duplicate is not required
        }


        return in;
    }

    /**
     * check whether GenericParams and JSON InputStreams is duplicate
     *
     * @param in
     * @param paramName
     * @throws IOException
     */
    private static void checkDuplicateParam(JSONObject in, String paramName) {
        OperaExceptionHandler.flagCheck(in.keySet().contains(paramName),
                "duplicate param: " + paramName);
    }
}
