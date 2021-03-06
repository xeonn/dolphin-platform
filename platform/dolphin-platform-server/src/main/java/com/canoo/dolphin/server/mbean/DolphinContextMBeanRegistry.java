/*
 * Copyright 2015-2017 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.mbean;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.impl.gc.GarbageCollector;
import com.canoo.dolphin.server.mbean.beans.DolphinControllerInfo;
import com.canoo.dolphin.server.mbean.beans.DolphinControllerInfoMBean;
import com.canoo.dolphin.server.mbean.beans.DolphinSessionInfo;
import com.canoo.dolphin.server.mbean.beans.DolphinSessionInfoMBean;
import com.canoo.dolphin.server.mbean.beans.ModelProvider;
import com.canoo.dolphin.util.Assert;

/**
 * Helper method to register MBeans for Dolphin Platform
 */
public class DolphinContextMBeanRegistry {

    private final String dolphinContextId;

    /**
     * Constructor
     * @param dolphinContextId the dolphin context id
     */
    public DolphinContextMBeanRegistry(String dolphinContextId) {
        this.dolphinContextId = Assert.requireNonNull(dolphinContextId, "dolphinContextId");
    }

    /**
     * Register a new dolphin session as a MBean
     * @param session the session
     * @return the subscription for deregistration
     */
    public Subscription registerDolphinContext(DolphinSession session, GarbageCollector garbageCollector) {
        Assert.requireNonNull(session, "session");
        Assert.requireNonNull(garbageCollector, "garbageCollector");
        DolphinSessionInfoMBean mBean = new DolphinSessionInfo(session, garbageCollector);
        return MBeanRegistry.getInstance().register(mBean, new MBeanDescription("com.canoo.dolphin", "DolphinSession", "session"));
    }

    /**
     * Register a new Dolphin Platform controller as a MBean
     * @param controllerClass the controller class
     * @param controllerId the controller id
     * @param modelProvider the model provider
     * @return the subscription for deregistration
     */
    public Subscription registerController(Class<?> controllerClass, String controllerId, ModelProvider modelProvider) {
        Assert.requireNonNull(controllerClass, "controllerClass");
        Assert.requireNonBlank(controllerId, "controllerId");
        Assert.requireNonNull(modelProvider, "modelProvider");
        DolphinControllerInfoMBean mBean = new DolphinControllerInfo(dolphinContextId, controllerClass, controllerId, modelProvider);
        return MBeanRegistry.getInstance().register(mBean, new MBeanDescription("com.canoo.dolphin", controllerClass.getSimpleName(), "controller"));
    }

}
