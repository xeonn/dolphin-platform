package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.AbstractPlatformBeanRepository;
import com.canoo.dolphin.impl.ControllerActionCallBean;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;

public class ServerPlatformBeanRepository extends AbstractPlatformBeanRepository {

    public ServerPlatformBeanRepository(PresentationModelBuilderFactory builderFactory) {
        setControllerActionCallBean(new ControllerActionCallBean(builderFactory.createBuilder()));
    }

}
