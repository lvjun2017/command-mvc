package com.wangyanrui.common.command;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommandRequest {
    /**
     * Eg: user.save
     */
    private String actionName;
    /**
     * Eg. user
     */
    private String componentName;
    /**
     * Eg. save
     */
    private String methodName;
    private JSONObject data;
}
