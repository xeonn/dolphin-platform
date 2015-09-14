package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.context.DolphinContextHandler;
import org.opendolphin.core.server.ServerDolphin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;

/**
 * The default servlet of the dolphin platform. All communication is based on this servlet.
 */
public class DefaultDolphinServlet extends HttpServlet {

    private DolphinContextHandler dolphinContextHandler;

    public DefaultDolphinServlet() {
        dolphinContextHandler = new DolphinContextHandler(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        ServerDolphin dolphin = dolphinContextHandler.getCurrentDolphinContext(req, resp).getDolphin();
        try {
          //  def requestJson = req.reader.text
            //  log.finest "received json: $requestJson"
            //  def commands = dolphin.serverConnector.codec.decode(requestJson)
            // def results = new LinkedList()
            // for (command in commands) { // a subclass could override this for less defensive exception handling
            //    log.finest "processing $command"
            //    results.addAll dolphin.serverConnector.receive(command)
            // }
            // def jsonResponse = dolphin.serverConnector.codec.encode(results)
            // log.finest "sending json response: $jsonResponse"
            // resp.outputStream << jsonResponse
            // resp.outputStream.close()
        } finally {
            dolphinContextHandler.resetCurrentContextThreadLocal();
        }
    }
}
