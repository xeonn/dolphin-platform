package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.context.DolphinContextHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The default servlet of the dolphin platform. All communication is based on this servlet.
 */
public class DolphinPlatformServlet extends HttpServlet {

    private DolphinContextHandler dolphinContextHandler;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        DolphinContextHandler.getInstance().handle(req, resp);
    }
}
