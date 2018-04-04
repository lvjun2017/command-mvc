package com.wangyanrui.common.command.service;

import com.wangyanrui.common.command.Command;
import com.wangyanrui.common.command.CommandRequest;
import com.wangyanrui.common.command.CommandResponse;

@Command
public class DemoService {

    @Command
    public void sayHello(CommandRequest request, CommandResponse response) {

        System.out.println("hello");
    }

}
