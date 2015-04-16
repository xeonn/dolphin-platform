package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.servlet.DolphinPlatformBootstrap;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public class DolphinPlatformJavaeeBootstrap implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        new DolphinPlatformBootstrap().onStartup(ctx);
    }
}
