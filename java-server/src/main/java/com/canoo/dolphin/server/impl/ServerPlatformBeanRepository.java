package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.HighlanderBean;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.DolphinEventHandler;
import com.canoo.dolphin.internal.EventDispatcher;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;

public class ServerPlatformBeanRepository {

    private ServerControllerActionCallBean controllerActionCallBean;

    private final HighlanderBean highlanderBean;

    public ServerPlatformBeanRepository(ServerDolphin dolphin, final BeanRepository beanRepository, EventDispatcher dispatcher) {
        dispatcher.addControllerActionCallBeanAddedHandler(new DolphinEventHandler() {
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

        dispatcher.addControllerActionCallBeanRemovedHandler(new DolphinEventHandler() {
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

        highlanderBean = new HighlanderBean(beanRepository, new ServerPresentationModelBuilder(dolphin));
    }

    public ServerControllerActionCallBean getControllerActionCallBean() {
        return controllerActionCallBean;
    }

    public HighlanderBean getHighlanderBean() {
        return highlanderBean;
    }
}
