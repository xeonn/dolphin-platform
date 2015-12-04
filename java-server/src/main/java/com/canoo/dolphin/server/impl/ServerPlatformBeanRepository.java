package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.DolphinEventHandler;
import com.canoo.dolphin.internal.EventDispatcher;
import org.opendolphin.core.PresentationModel;

public class ServerPlatformBeanRepository {

    private ServerControllerActionCallBean controllerActionCallBean;

    public ServerPlatformBeanRepository(final BeanRepository beanRepository, EventDispatcher dispatcher) {
        dispatcher.addPlatformBeanAddedHandler(new DolphinEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                final String type = model.getPresentationModelType();
                switch (type) {
                    case PlatformConstants.CONTROLLER_ACTION_CALL_BEAN_NAME:
                        controllerActionCallBean = new ServerControllerActionCallBean(beanRepository, model);
                        break;
                }
            }
        });

        dispatcher.addPlatformBeanRemovedHandler(new DolphinEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                final String type = model.getPresentationModelType();
                switch (type) {
                    case PlatformConstants.CONTROLLER_ACTION_CALL_BEAN_NAME:
                        controllerActionCallBean = null;
                        break;
                }
            }
        });
    }

    public ServerControllerActionCallBean getControllerActionCallBean() {
        return controllerActionCallBean;
    }
}
