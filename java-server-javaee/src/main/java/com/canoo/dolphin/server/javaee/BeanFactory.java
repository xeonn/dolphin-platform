package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.collections.ListMapper;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.TaskExecutor;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.impl.ServerEventDispatcher;
import com.canoo.dolphin.server.impl.ServerPresentationModelBuilderFactory;
import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;
import org.opendolphin.core.server.ServerDolphin;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;

public class BeanFactory {

    @Produces
    @SessionScoped
    public BeanManager createManager() {
        return DolphinContext.getCurrentContext().getBeanManager();
    }

    @Produces
    @SessionScoped
    public TaskExecutor createTaskExecutor() {
        return DolphinContext.getCurrentContext().getTaskExecutor();
    }

    @Produces
    @ApplicationScoped
    public DolphinEventBus createEventBus() {
        return DolphinEventBusImpl.getInstance();
    }
}
