package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.Callback;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

/**
 * Created by hendrikebbers on 31.05.16.
 */
public class DefaultDolphinContextFactory implements DolphinContextFactory {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DefaultDolphinContextFactory.class);

    private final ControllerRepository controllerRepository;

    private final OpenDolphinFactory dolphinFactory;

    private final ContainerManager containerManager;

    private final DolphinEventBusImpl dolphinEventBus;

    public DefaultDolphinContextFactory(ContainerManager containerManager, DolphinEventBusImpl dolphinEventBus) {
        this.containerManager = containerManager;
        this.dolphinEventBus = dolphinEventBus;
        this.controllerRepository = new ControllerRepository();
        this.dolphinFactory = new DefaultOpenDolphinFactory();
    }

    @Override
    public DolphinContext create(final HttpSession httpSession, DolphinSessionListenerProvider dolphinSessionListenerProvider) {

        final Callback<DolphinContext> preDestroyCallback = new Callback<DolphinContext>() {
            @Override
            public void call(DolphinContext dolphinContext) {
                Assert.requireNonNull(dolphinContext, "dolphinContext");
                for(DolphinSessionListener listener : dolphinSessionListenerProvider.getAllListeners()) {
                    listener.sessionDestroyed(dolphinContext.getCurrentDolphinSession());
                }
            }
        };

        final Callback<DolphinContext> onDestroyCallback = new Callback<DolphinContext>() {
            @Override
            public void call(DolphinContext dolphinContext) {
                Assert.requireNonNull(dolphinContext, "dolphinContext");
                LOG.trace("Destroying DolphinContext {} in http session {}", dolphinContext.getId(), httpSession.getId());
                DolphinContextUtils.removeFromSession(httpSession, dolphinContext);
            }
        };
        return new DolphinContext(containerManager, controllerRepository, dolphinFactory, dolphinEventBus, preDestroyCallback, onDestroyCallback);
    }
}
