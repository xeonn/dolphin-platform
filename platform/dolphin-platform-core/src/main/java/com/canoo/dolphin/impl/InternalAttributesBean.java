/*
 * Copyright 2015-2016 Canoo Engineering AG.
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

import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.PresentationModelBuilder;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;

public class InternalAttributesBean {

    private static final String CONTROLLER_NAME = "controllerName";
    private static final String CONTROLLER_ID = "controllerId";
    private static final String MODEL = "model";

    private final BeanRepository beanRepository;
    private final Attribute controllerName;
    private final Attribute controllerId;
    private final Attribute model;

    public InternalAttributesBean(final BeanRepository beanRepository, final PresentationModel pm) {
        this.beanRepository = Assert.requireNonNull(beanRepository, "beanRepository");
        Assert.requireNonNull(pm, "pm");
        controllerName = pm.getAttribute(CONTROLLER_NAME);
        controllerId = pm.getAttribute(CONTROLLER_ID);
        model = pm.getAttribute(MODEL);
    }

    public InternalAttributesBean(final BeanRepository beanRepository, final PresentationModelBuilder builder) {
        this(
            beanRepository,
                Assert.requireNonNull(builder, "builder").withType(PlatformConstants.INTERNAL_ATTRIBUTES_BEAN_NAME)
                .withAttribute(CONTROLLER_NAME)
                .withAttribute(CONTROLLER_ID)
                .withAttribute(MODEL)
                .create()
        );
    }

    public String getControllerName() {
        return (String) controllerName.getValue();
    }

    public void setControllerName(String controllerName) {
        this.controllerName.setValue(controllerName);
    }

    public String getControllerId() {
        return (String) controllerId.getValue();
    }

    public void setControllerId(String controllerId) {
        this.controllerId.setValue(controllerId);
    }

    public <T> T getModel() {
        if(model.getValue() == null) {
            throw new RuntimeException("Dolphin Platform internal error: No model defined");
        }
        return (T) beanRepository.getBean(model.getValue().toString());
    }

    public void setModel(Object model) {
        this.model.setValue(beanRepository.getDolphinId(model));
    }
}
