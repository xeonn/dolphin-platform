package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.context.DolphinContextHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The default servlet of the dolphin platform. All communication is based on this servlet.
 */
public class DefaultDolphinServlet extends HttpServlet {

    private DolphinContextHandler dolphinContextHandler;

    public DefaultDolphinServlet(ServletContext servletContext) {
        dolphinContextHandler = new DolphinContextHandler(servletContext);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        dolphinContextHandler.handle(req, resp);
    }
}
