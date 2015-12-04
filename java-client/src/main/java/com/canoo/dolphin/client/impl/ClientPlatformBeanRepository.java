package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.impl.AbstractPlatformBeanRepository;
import com.canoo.dolphin.impl.ControllerActionCallBean;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.internal.DolphinEventHandler;
import com.canoo.dolphin.internal.EventDispatcher;
import org.opendolphin.core.PresentationModel;

public class ClientPlatformBeanRepository extends AbstractPlatformBeanRepository {

    public ClientPlatformBeanRepository(EventDispatcher dispatcher) {
        dispatcher.addPlatformBeanAddedHandler(new DolphinEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                final String type = model.getPresentationModelType();
                switch (type) {
                    case PlatformConstants.CONTROLLER_ACTION_CALL_BEAN_NAME:
                        setControllerActionCallBean(new ControllerActionCallBean(model));
                        break;
                }
            }
        });
    }

}
