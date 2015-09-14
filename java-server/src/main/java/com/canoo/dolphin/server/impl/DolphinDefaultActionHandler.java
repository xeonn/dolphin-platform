package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.Constants;
import com.canoo.dolphin.server.controller.ControllerHandler;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.v2.ControllerActionCallBean;
import com.canoo.dolphin.v2.ControllerDestroyBean;
import com.canoo.dolphin.v2.ControllerRegistryBean;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.List;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class DolphinDefaultActionHandler {

    public void registerApplicationActions(final DefaultServerDolphin serverDolphin, final ControllerHandler controllerHandler, final BeanManager beanManager, final DolphinEventBusImpl eventBus) {

        serverDolphin.register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {

                registry.register(Constants.REGISTER_CONTROLLER_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        ControllerRegistryBean bean = beanManager.findAll(ControllerRegistryBean.class).get(0);
                        String id = controllerHandler.createController(bean.getControllerName().get());
                        bean.getControllerid().set(id);
                    }
                });

                registry.register(Constants.DESTROY_CONTROLLER_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        ControllerDestroyBean bean = beanManager.findAll(ControllerDestroyBean.class).get(0);
                        controllerHandler.destroyController(bean.controlleridProperty().get());
                    }
                });

                registry.register(Constants.CALL_CONTROLLER_ACTION_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        ControllerActionCallBean bean = beanManager.findAll(ControllerActionCallBean.class).get(0);
                        controllerHandler.callAction(bean.getControllerid().get(), bean.getActionName().get());
                    }
                });

                registry.register(Constants.POLL_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        try {
                            eventBus.longPoll();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });

                registry.register(Constants.RELEASE_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        eventBus.release();
                    }
                });
            }
        });

    }
}
