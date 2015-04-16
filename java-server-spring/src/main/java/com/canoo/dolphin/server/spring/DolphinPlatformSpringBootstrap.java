package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.server.servlet.DolphinPlatformBootstrap;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Configuration
public class DolphinPlatformSpringBootstrap implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        new DolphinPlatformBootstrap().onStartup(servletContext);
    }
}
