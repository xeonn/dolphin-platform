package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.context.DolphinSessionProvider;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.servlet.DolphinPlatformBootstrap;
import org.opendolphin.core.server.ServerDolphin;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Provides all Dolphin Platform Beans and Scopes for CDI
 */
@Configuration
public class SpringBeanFactory {

    /**
     * Method to create a spring managed {@link com.canoo.dolphin.impl.BeanManagerImpl} instance in client scope.
     * @return the instance
     */
    @Bean
    @ClientScoped
    protected BeanManager createManager() {
        return DolphinPlatformBootstrap.getInstance().getCurrentContext().getBeanManager();
    }

    /**
     * Method to create a spring managed {@link org.opendolphin.core.server.ServerDolphin} instance in client scope.
     * @return the instance
     */
    @Bean
    @ClientScoped
    protected ServerDolphin createDolphin() {
        return DolphinPlatformBootstrap.getInstance().getCurrentContext().getDolphin();
    }

    @Bean
    @ClientScoped
    protected DolphinSession createDolphinSession() {
        return DolphinPlatformBootstrap.getInstance().getCurrentContext().getCurrentDolphinSession();
    }


    /**
     * Method to create a spring managed {@link DolphinEventBus} instance in singleton scope.
     * @return the instance
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinEventBus createEventBus() {
        return DolphinPlatformBootstrap.getInstance().getContextHandler().getDolphinEventBus();
    }

    @Bean
    public CustomScopeConfigurer createClientScope() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(ClientScope.CLIENT_SCOPE, new ClientScope(new DolphinSessionProvider() {
            @Override
            public DolphinSession getCurrentDolphinSession() {
                return DolphinPlatformBootstrap.getInstance().getCurrentContext().getCurrentDolphinSession();
            }
        }));
        return configurer;
    }
}
