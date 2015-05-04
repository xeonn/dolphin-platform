package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.impl.BeanBuilder;
import com.canoo.dolphin.server.impl.BeanManagerImpl;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.impl.collections.ListMapper;
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
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        DefaultDolphinServlet.addToSession(beanRepository);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        return new BeanManagerImpl(beanRepository, beanBuilder);
    }

    @Produces
    @ApplicationScoped
    public DolphinEventBus createEventBus() {
        return DolphinEventBusImpl.getInstance();
    }
}
