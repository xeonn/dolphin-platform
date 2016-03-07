package com.canoo.dolphin.server.context;

import javax.servlet.ServletContext;

/**
 * Created by hendrikebbers on 11.03.16.
 */
public interface DolphinContextHandlerFactory {

    DolphinContextHandler create(ServletContext servletContext);

}
