package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.DolphinEventBusImpl;
import com.canoo.dolphin.server.impl.BeanManagerImpl;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;
import org.opendolphin.core.server.ServerDolphin;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;

public class BeanFactory {

    @Produces
    @SessionScoped
    public BeanManager createManager() {
        ServerDolphin dolphin = DefaultDolphinServlet.getServerDolphin();
        BeanManagerImpl manager = new BeanManagerImpl(new BeanRepository(dolphin, new ClassRepository(dolphin)));
        return manager;
    }

    @Produces
    @ApplicationScoped
    public DolphinEventBus createEventBus() {
        return DolphinEventBusImpl.getInstance();
    }
}
