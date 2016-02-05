package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.impl.ClientContextImpl;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.container.ModelInjector;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.spring.DolphinPlatformSpringBootstrap;
import com.canoo.dolphin.server.spring.SpringModelInjector;
import com.canoo.dolphin.test.future.ControllerFactory;
import com.canoo.dolphin.test.future.ControllerFactoryImpl;
import org.opendolphin.core.comm.DefaultInMemoryConfig;
import org.opendolphin.core.server.ServerModelStore;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Configuration
@Import(DolphinPlatformSpringBootstrap.class)
public class DolphinPlatformSpringTestBootstrap {

    @Inject
    private ServletContext servletContext;

    @Resource
    private WebApplicationContext webApplicationContext;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ControllerFactory createControllerFactory(ClientContext clientContext, DolphinContext serverContext) {
        return new ControllerFactoryImpl(clientContext, serverContext.getControllerHandler());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ClientContext createClientContext(DefaultInMemoryConfig config, DolphinContext serverContext) throws ExecutionException, InterruptedException {
        ControllerRepository.init();

        config.getServerDolphin().registerDefaultActions();
        ServerModelStore store = config.getServerDolphin().getServerModelStore();
        try {
            ReflectionHelper.setPrivileged(ServerModelStore.class.getDeclaredField("currentResponse"), store, new ArrayList<>());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return new ClientContextImpl(config.getClientDolphin());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DefaultInMemoryConfig createInMemoryConfig() throws ExecutionException, InterruptedException {
        return new DefaultInMemoryConfig();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DolphinContext createDolphinContext(DefaultInMemoryConfig config) {
        ContainerManager containerManager = new ContainerManager() {

            @Override
            public void init(ServletContext servletContext) {
                DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) webApplicationContext.getAutowireCapableBeanFactory();
                beanFactory.addBeanPostProcessor(SpringModelInjector.getInstance());
            }

            @Override
            public <T> T createManagedController(Class<T> controllerClass, ModelInjector modelInjector) {
                if(controllerClass == null) {
                    throw new IllegalArgumentException("controllerClass must not be null!");
                }
                if(modelInjector == null) {
                    throw new IllegalArgumentException("modelInjector must not be null!");
                }
                // SpringBeanAutowiringSupport kann man auch nutzen
                AutowireCapableBeanFactory beanFactory = webApplicationContext.getAutowireCapableBeanFactory();
                SpringModelInjector.getInstance().prepare(controllerClass, modelInjector);
                return beanFactory.createBean(controllerClass);
            }

            @Override
            public void destroyController(Object instance, Class controllerClass) {
                if(instance == null) {
                    throw new IllegalArgumentException("instance must not be null!");
                }
                webApplicationContext.getAutowireCapableBeanFactory().destroyBean(instance);
            }
        };
        containerManager.init(servletContext);
        return new DolphinContext(containerManager, new TestDolphinFactory(config));
    }

}
