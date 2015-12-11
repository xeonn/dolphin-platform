package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.impl.InternalAttributesBean;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.DolphinEventHandler;
import com.canoo.dolphin.internal.EventDispatcher;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientDolphin;

public class ClientPlatformBeanRepository {

    private final ClientDolphin dolphin;
    private final BeanRepository beanRepository;

    private InternalAttributesBean internalAttributesBean;

    public ClientPlatformBeanRepository(ClientDolphin dolphin, BeanRepository beanRepository, EventDispatcher dispatcher) {
        this.dolphin = dolphin;
        this.beanRepository = beanRepository;

        dispatcher.onceInternalAttributesBeanAddedHandler(new DolphinEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                internalAttributesBean = new InternalAttributesBean(beanRepository, model);
            }
        });
    }

    public ClientControllerActionCallBean createControllerActionCallBean(String controllerId, String actionName, Param... params) {
        return new ClientControllerActionCallBean(dolphin, beanRepository, controllerId, actionName, params);
    }

    public InternalAttributesBean getInternalAttributesBean() {
        if (internalAttributesBean == null) {
            throw new IllegalStateException("InternalAttributesBean was not initialized yet");
        }
        return internalAttributesBean;
    }
}
