/**
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
package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.context.DefaultDolphinProvider;
import com.canoo.dolphin.server.context.DolphinContextProvider;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.servlet.DolphinPlatformBootstrap;
import org.opendolphin.core.server.ServerDolphin;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Basic Bootstrap for Spring based application. The bootstrap automatically starts the dolphin platform bootstrap.
 *
 * @author Hendrik Ebbers
 */
@Configuration
public class DolphinPlatformSpringBootstrap implements ServletContextInitializer {


    private DolphinContextProvider dolphinContextProvider;

    public DolphinPlatformSpringBootstrap() {
        this.dolphinContextProvider = new DefaultDolphinProvider();
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        new DolphinPlatformBootstrap().onStartup(servletContext);
    }

    /**
     * Method to create a spring managed {@link com.canoo.dolphin.impl.BeanManagerImpl} instance in session scope.
     * @return the instance
     */
    @Bean
    @ClientScoped
    protected BeanManager createManager() {
        return dolphinContextProvider.getCurrentContext().getBeanManager();
    }

    /**
     * Method to create a spring managed {@link org.opendolphin.core.server.ServerDolphin} instance in session scope.
     * @return the instance
     */
    @Bean
    @ClientScoped
    protected ServerDolphin createDolphin() {
        return dolphinContextProvider.getCurrentContext().getDolphin();
    }

    @Bean
    @ClientScoped
    protected DolphinSession createDolphinSession() {
        return dolphinContextProvider.getCurrentContext().getDolphinSession();
    }


    /**
     * Method to create a spring managed {@link DolphinEventBus} instance in singleton scope.
     * @return the instance
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinEventBus createEventBus() {
        return DolphinEventBusImpl.getInstance();
    }

    @Bean
    public CustomScopeConfigurer createClientScope() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(ClientScope.CLIENT_SCOPE, new ClientScope(dolphinContextProvider));
        return configurer;
    }
}
