package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.impl.HighlanderBean;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.DolphinEventHandler;
import com.canoo.dolphin.internal.EventDispatcher;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientDolphin;

public class ClientPlatformBeanRepository {

    private final ClientDolphin dolphin;
    private final BeanRepository beanRepository;

    private HighlanderBean highlanderBean;

    public ClientPlatformBeanRepository(ClientDolphin dolphin, BeanRepository beanRepository, EventDispatcher dispatcher) {
        this.dolphin = dolphin;
        this.beanRepository = beanRepository;

        dispatcher.onceHighlanderBeanAddedHandler(new DolphinEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                highlanderBean = new HighlanderBean(beanRepository, model);
            }
        });
    }

    public ClientControllerActionCallBean createControllerActionCallBean(String controllerId, String actionName, Param... params) {
        return new ClientControllerActionCallBean(dolphin, beanRepository, controllerId, actionName, params);
    }

    public HighlanderBean getHighlanderBean() {
        if (highlanderBean == null) {
            throw new IllegalStateException("HighlanderBean was not initialized yet");
        }
        return highlanderBean;
    }
}
