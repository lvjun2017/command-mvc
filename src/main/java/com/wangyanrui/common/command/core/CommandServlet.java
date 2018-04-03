package com.wangyanrui.common.command.core;

import com.alibaba.fastjson.JSONObject;
import com.wangyanrui.common.command.CommandConst;
import com.wangyanrui.common.creator.ResultCreator;
import com.wangyanrui.common.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CommandServlet extends HttpServlet {

    private static final long serialVersionUID = 2391748561331450723L;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String DEFAULT_ENCODING = CommandConst.DEFAULT_ENCODING;
    private static final String RESPONSE_CONTENT_TYPE = CommandConst.RESPONSE_CONTENT_TYPE;

    private CommandService commandService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
        // super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(DEFAULT_ENCODING);
        resp.setContentType(RESPONSE_CONTENT_TYPE);

        PrintWriter out = resp.getWriter();


        Result result;
        try {
            result = commandService.handle(req);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            out.println(JSONObject.toJSONString(ResultCreator.getFail(e.getMessage())));
            out.close();
            // resp.sendError(400, "error request");
            return;
        }

        out.println(JSONObject.toJSONString(result));
        out.close();
        // super.doPost(req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
        logger.debug("Command Servlet destroy success. ");
    }

    @Override
    public void init() throws ServletException {
        // init command service
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        this.commandService = applicationContext.getBean(CommandService.class);

        super.init();
        logger.debug("Command Servlet init success. ");
    }

}
