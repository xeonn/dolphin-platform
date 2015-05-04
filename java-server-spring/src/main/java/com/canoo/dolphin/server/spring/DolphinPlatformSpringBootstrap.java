package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.impl.BeanBuilder;
import com.canoo.dolphin.server.impl.BeanManagerImpl;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.impl.collections.ListMapper;
import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;
import com.canoo.dolphin.server.servlet.DolphinPlatformBootstrap;
import org.opendolphin.core.server.ServerDolphin;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Basic Bootstrap for Spring based application. The boostrap automatically starts the dolphin platform bootstrap.
 */
@Configuration
public class DolphinPlatformSpringBootstrap implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        new DolphinPlatformBootstrap().onStartup(servletContext);
    }

    /**
     * Method to create a spring managed {@link BeanManager} instance in session scope.
     * @return the instance
     */
    @Bean
    @Scope("session")
    protected BeanManager createManager() {
        ServerDolphin dolphin = DefaultDolphinServlet.getServerDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        DefaultDolphinServlet.addToSession(beanRepository);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        return new BeanManagerImpl(beanRepository, beanBuilder);

    }

    /**
     * Method to create a spring managed {@link org.opendolphin.core.server.ServerDolphin} instance in session scope.
     * @return the instance
     */
    @Bean
    @Scope("session")
    protected ServerDolphin createDolphin() {
        return DefaultDolphinServlet.getServerDolphin();
    }

    @Bean
    @Scope("singleton")
    protected DolphinEventBus createEventBus() {
        return DolphinEventBusImpl.getInstance();
    }

}
