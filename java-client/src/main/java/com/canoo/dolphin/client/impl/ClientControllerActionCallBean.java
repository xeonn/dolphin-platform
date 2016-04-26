/**
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
package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.impl.AbstractControllerActionCallBean;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.internal.PresentationModelBuilder;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientDolphin;

public class ClientControllerActionCallBean extends AbstractControllerActionCallBean {

    private final Dolphin dolphin;
    private PresentationModel pm;

    public ClientControllerActionCallBean(ClientDolphin dolphin, Converters converters, String controllerId, String actionName, Param... params) {
        this.dolphin = dolphin;

        final PresentationModelBuilder builder = new ClientPresentationModelBuilder(dolphin);
        builder.withType(PlatformConstants.CONTROLLER_ACTION_CALL_BEAN_NAME)
                .withAttribute(CONTROLLER_ID, controllerId)
                .withAttribute(ACTION_NAME, actionName)
                .withAttribute(ERROR_CODE);

        for (final Param param : params) {
            final Object value = param.getValue();
            final Object dolphinValue = converters.getConverter(value.getClass()).convertToDolphin(value);
            final String paramName = PARAM_PREFIX + param.getName();
            builder.withAttribute(paramName, dolphinValue);
        }

        this.pm = builder.create();
    }

    public boolean isError() {
        if (pm == null) {
            throw new IllegalStateException("ClientControllerActionCallBean was already unregistered");
        }
        return Boolean.TRUE.equals(pm.findAttributeByPropertyName(ERROR_CODE).getValue());
    }

    @SuppressWarnings("unchecked")
    public void unregister() {
        if (pm != null) {
            dolphin.remove(pm);
            pm = null;
        }
    }

}
