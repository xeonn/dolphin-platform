package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.DolphinEventHandler;
import com.canoo.dolphin.internal.EventDispatcher;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientDolphin;

public class ClientPlatformBeanRepository {

    private final ClientDolphin dolphin;
    private final BeanRepository beanRepository;

    public ClientPlatformBeanRepository(ClientDolphin dolphin, BeanRepository beanRepository, EventDispatcher dispatcher) {
        this.dolphin = dolphin;
        this.beanRepository = beanRepository;

        dispatcher.addPlatformBeanAddedHandler(new DolphinEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                final String type = model.getPresentationModelType();
                switch (type) {
                }
            }
        });
    }

    public ClientControllerActionCallBean createControllerActionCallBean(String controllerId, String actionName, Param... params) {
        return new ClientControllerActionCallBean(dolphin, beanRepository, controllerId, actionName, params);
    }
}
