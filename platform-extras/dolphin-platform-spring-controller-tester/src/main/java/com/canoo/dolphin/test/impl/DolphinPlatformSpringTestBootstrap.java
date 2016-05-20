package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.impl.ClientContextImpl;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.container.ModelInjector;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.spring.SpringModelInjector;
import com.canoo.dolphin.test.ControllerTestException;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.comm.DefaultInMemoryConfig;
import org.opendolphin.core.server.ServerModelStore;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Configuration
public class DolphinPlatformSpringTestBootstrap {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ClientContext createClientContext(DefaultInMemoryConfig config, DolphinContext serverContext) throws ExecutionException, InterruptedException {
        ControllerRepository.init();

        config.getServerDolphin().registerDefaultActions();
        ServerModelStore store = config.getServerDolphin().getServerModelStore();
        try {
            ReflectionHelper.setPrivileged(ServerModelStore.class.getDeclaredField("currentResponse"), store, new ArrayList<>());
        } catch (NoSuchFieldException e) {
            throw new ControllerTestException(e);
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
    public DolphinContext createDolphinContext(DefaultInMemoryConfig config, final WebApplicationContext webApplicationContext) {
        ContainerManager containerManager = new ContainerManager() {

            @Override
            public void init(ServletContext servletContext) {
            }

            @Override
            public <T> T createManagedController(Class<T> controllerClass, ModelInjector modelInjector) {
                Assert.requireNonNull(controllerClass, "controllerClass");
                Assert.requireNonNull(modelInjector, "modelInjector");

                // SpringBeanAutowiringSupport kann man auch nutzen
                AutowireCapableBeanFactory beanFactory = webApplicationContext.getAutowireCapableBeanFactory();
                SpringModelInjector.getInstance().prepare(controllerClass, modelInjector);
                return beanFactory.createBean(controllerClass);
            }

            @Override
            public void destroyController(Object instance, Class controllerClass) {
                Assert.requireNonNull(instance, "instance");
                webApplicationContext.getAutowireCapableBeanFactory().destroyBean(instance);
            }
        };

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) webApplicationContext.getAutowireCapableBeanFactory();
        beanFactory.addBeanPostProcessor(SpringModelInjector.getInstance());

        return new DolphinContext(containerManager, new TestDolphinFactory(config));
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected BeanManager createManager(DolphinContext dolphinContext) {
        return dolphinContext.getBeanManager();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinEventBus createEventBus() {
        return DolphinEventBusImpl.getInstance();
    }

}
