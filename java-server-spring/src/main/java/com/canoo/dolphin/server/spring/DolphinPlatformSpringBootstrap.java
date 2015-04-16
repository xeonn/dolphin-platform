package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;
import com.canoo.dolphin.server.servlet.DolphinPlatformBootstrap;
import org.opendolphin.core.server.ServerDolphin;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

@Configuration
public class DolphinPlatformSpringBootstrap implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        new DolphinPlatformBootstrap().onStartup(servletContext);
    }

    @Bean
    @Scope("session")
    protected BeanManager createManager() {
        ServerDolphin dolphin = DefaultDolphinServlet.getServerDolphin();
        return new BeanManager(new BeanRepository(dolphin, new ClassRepository(dolphin)));
    }

    @Bean
    @Scope("session")
    protected ServerDolphin createDolphin() {
        return DefaultDolphinServlet.getServerDolphin();
    }

}
