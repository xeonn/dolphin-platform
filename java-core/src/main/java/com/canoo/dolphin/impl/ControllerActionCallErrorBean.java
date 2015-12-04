/*
 * Copyright 2015 Canoo Engineering AG.
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
package com.canoo.dolphin.impl;

import com.canoo.dolphin.internal.PresentationModelBuilder;
import org.opendolphin.StringUtil;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;

public class ControllerActionCallErrorBean {

    private static final String CONTROLLER_ID = "controllerId";
    private static final String ACTION_NAME = "actionName";

    private final Attribute controllerId;
    private final Attribute actionName;

    public ControllerActionCallErrorBean(PresentationModelBuilder builder) {
        this(builder.withType(PlatformConstants.CONTROLLER_ACTION_CALL_ERROR_BEAN_NAME)
                .withAttribute(CONTROLLER_ID)
                .withAttribute(ACTION_NAME)
                .create()
        );
    }

    public ControllerActionCallErrorBean(PresentationModel pm) {
        controllerId = pm.findAttributeByPropertyName(CONTROLLER_ID);
        actionName   = pm.findAttributeByPropertyName(ACTION_NAME);
    }

    public String getControllerId() {
        return (String) controllerId.getValue();
    }

    public void setControllerId(String controllerId) {
        if (StringUtil.isBlank(controllerId)) {
            throw new IllegalArgumentException("ControllerId cannot be empty");
        }
        this.controllerId.setValue(controllerId);
    }

    public String getActionName() {
        return (String) actionName.getValue();
    }

    public void setActionName(String actionName) {
        if (StringUtil.isBlank(actionName)) {
            throw new IllegalArgumentException("ActionName cannot be empty");
        }
        this.actionName.setValue(actionName);
    }
}
