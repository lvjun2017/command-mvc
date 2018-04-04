package com.wangyanrui.demo;

import com.alibaba.fastjson.JSONObject;
import com.wangyanrui.common.command.Command;
import com.wangyanrui.common.command.CommandRequest;
import com.wangyanrui.common.command.CommandResponse;
import org.springframework.stereotype.Component;

@Command("demo")
@Component
public class DemoCommand {

    // @Command
    public void hello(CommandRequest request, CommandResponse response) {
        JSONObject in = request.getData();

        // ... do businesses and get Result
        Object result = new Object();

        // encapsulation return result
        response.setResult(
                result
        );
    }
}
