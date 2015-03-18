package com.canoo.dolphin.server.javaee;

import org.apache.deltaspike.core.util.bean.BaseImmutableBean;
import org.opendolphin.core.server.ServerDolphin;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import java.beans.Introspector;
import java.util.Collections;

/**
 * Created by hendrikebbers on 18.03.15.
 */
public class ServerDolphinBean extends BaseImmutableBean<ServerDolphin> {

    public ServerDolphinBean() {
        super(ServerDolphin.class, createDefaultBeanName(), Collections.EMPTY_SET, SessionScoped.class, Collections.EMPTY_SET, Collections.EMPTY_SET, false, false, Collections.EMPTY_SET, null);
    }

    private static String createDefaultBeanName() {
        return Introspector.decapitalize(ServerDolphin.class.getSimpleName());
    }

    @Override
    public ServerDolphin create(CreationalContext<ServerDolphin> creationalContext) {
        ServerDolphin dolphin = null;
        creationalContext.push(dolphin);
        return dolphin;
    }

    @Override
    public void destroy(ServerDolphin instance, CreationalContext<ServerDolphin> creationalContext) {
        creationalContext.release();
    }
}
