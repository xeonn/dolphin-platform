package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.container.ModelInjector;
import com.canoo.dolphin.server.context.DolphinContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringContainerManager implements ContainerManager {

    @Override
    public <T> T createManagedController(final Class<T> controllerClass, final ModelInjector modelInjector) {
        ApplicationContext context = getContext();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();

        beanFactory.addBeanPostProcessor(new InstantiationAwareBeanPostProcessorAdapter() {
            @Override
            public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
                modelInjector.inject(bean);
                return true;
            }
        });
        return beanFactory.createBean(controllerClass);
    }

    @Override
    public void destroyController(Object instance) {
        ApplicationContext context = getContext();
        context.getAutowireCapableBeanFactory().destroyBean(instance);
    }

    /**
     * Returns the Spring {@link org.springframework.context.ApplicationContext} for the current {@link javax.servlet.ServletContext}
     *
     * @return the spring context
     */
    private ApplicationContext getContext() {
        return WebApplicationContextUtils.getWebApplicationContext(DolphinContext.getCurrentContext().getServletContext());
    }
}
