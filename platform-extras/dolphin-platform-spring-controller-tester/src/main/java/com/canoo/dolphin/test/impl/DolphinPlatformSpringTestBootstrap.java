/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.impl.codec.OptimizedJsonCodec;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextProvider;
import com.canoo.dolphin.server.context.DolphinSessionProvider;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.spring.ClientScope;
import com.canoo.dolphin.test.ControllerTestException;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.server.ServerModelStore;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Configuration
public class DolphinPlatformSpringTestBootstrap {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DolphinTestContext createServerContext(TestInMemoryConfiguration config, WebApplicationContext context) throws ExecutionException, InterruptedException {
        Assert.requireNonNull(config, "config");
        Assert.requireNonNull(context, "context");
        ControllerRepository controllerRepository = new ControllerRepository();
        TestSpringContainerManager containerManager = new TestSpringContainerManager(context);
        containerManager.init(context.getServletContext());
        DolphinContextProviderMock dolphinContextProviderMock = new DolphinContextProviderMock();
        DolphinEventBusImpl dolphinEventBus = new DolphinEventBusImpl(dolphinContextProviderMock);
        DolphinTestContext dolphinContext = new DolphinTestContext(containerManager, controllerRepository, config, dolphinEventBus);
        dolphinContextProviderMock.setCurrentContext(dolphinContext);
        return dolphinContext;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TestInMemoryConfiguration createInMemoryConfig() throws ExecutionException, InterruptedException {
        TestInMemoryConfiguration config = new TestInMemoryConfiguration();
        config.getServerDolphin().registerDefaultActions();
        ServerModelStore store = config.getServerDolphin().getServerModelStore();
        try {
            ReflectionHelper.setPrivileged(ServerModelStore.class.getDeclaredField("currentResponse"), store, new ArrayList<>());
        } catch (NoSuchFieldException e) {
            throw new ControllerTestException(e);
        }
        config.getServerDolphin().getServerConnector().setCodec(new OptimizedJsonCodec());
        config.getClientDolphin().getClientConnector().setCodec(new OptimizedJsonCodec());
        return config;
    }

    /**
     * Method to create a spring managed {@link com.canoo.dolphin.impl.BeanManagerImpl} instance in client scope.
     *
     * @return the instance
     */
    @Bean(name = "beanManager")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected BeanManager createManager(DolphinTestContext context) {
        Assert.requireNonNull(context, "context");
        return context.getBeanManager();
    }


    @Bean(name = "dolphinSession")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinSession createDolphinSession(DolphinTestContext context) {
        Assert.requireNonNull(context, "context");
        return context.getCurrentDolphinSession();
    }


    /**
     * Method to create a spring managed {@link DolphinEventBus} instance in singleton scope.
     *
     * @return the instance
     */
    @Bean(name = "dolphinEventBus")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinEventBus createEventBus(DolphinTestContext context) {
        Assert.requireNonNull(context, "context");
        return context.getDolphinEventBus();
    }

    @Bean(name = "customScopeConfigurer")
    public static CustomScopeConfigurer createClientScope(final DolphinTestContext context) {
        Assert.requireNonNull(context, "context");
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(ClientScope.CLIENT_SCOPE, new ClientScope(new DolphinSessionProvider() {
            @Override
            public DolphinSession getCurrentDolphinSession() {
                return context.getCurrentDolphinSession();
            }
        }));
        return configurer;
    }

    private class DolphinContextProviderMock implements DolphinContextProvider {

        DolphinContext currentContext;

        public void setCurrentContext(DolphinContext currentContext) {
            this.currentContext = currentContext;
        }

        @Override
        public DolphinContext getCurrentContext() {
            return currentContext;
        }

        @Override
        public DolphinSession getCurrentDolphinSession() {
            return getCurrentContext().getCurrentDolphinSession();
        }
    }

}
