package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.servlet.DolphinPlatformBootstrap;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

public class ServerDolphinExtension implements Extension {

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
        afterBeanDiscovery.addBean(new ServerDolphinBean());

        for(Class dolphinBeanClass : DolphinPlatformBootstrap.findAllDolphinBeanClasses()) {
            afterBeanDiscovery.addBean(CdiDolphinCommandManager.getBean(beanManager, dolphinBeanClass));
        }
    }

}
