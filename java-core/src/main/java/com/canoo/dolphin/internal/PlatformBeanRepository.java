package com.canoo.dolphin.internal;

import com.canoo.dolphin.impl.ControllerActionCallBean;
import com.canoo.dolphin.impl.ControllerActionCallErrorBean;

public interface PlatformBeanRepository {

    ControllerActionCallBean getControllerActionCallBean();

    ControllerActionCallErrorBean getControllerActionCallErrorBean();
}
