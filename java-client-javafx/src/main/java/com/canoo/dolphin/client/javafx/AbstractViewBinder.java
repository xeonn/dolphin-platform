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
package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerActionException;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.Param;
import javafx.beans.property.*;
import org.opendolphin.StringUtil;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractViewBinder<M> {

    private ControllerProxy<M> controllerProxy;

    private ReadOnlyBooleanWrapper actionInProcess;

    private ReadOnlyObjectWrapper<M> model;

    private ReadOnlyObjectWrapper<Throwable> initializationException;

    private ReadOnlyObjectWrapper<ControllerActionException> invocationException;

    public AbstractViewBinder(ClientContext clientContext, String controllerName) {
        if(clientContext == null) {
            throw new IllegalArgumentException("clientContext must not be null");
        }
        if(StringUtil.isBlank(controllerName)) {
            throw new IllegalArgumentException("controllerName must not be null");
        }
        model = new ReadOnlyObjectWrapper<>();
        actionInProcess = new ReadOnlyBooleanWrapper(false);
        initializationException = new ReadOnlyObjectWrapper<>();
        invocationException = new ReadOnlyObjectWrapper<>();
        clientContext.<M>createController(controllerName).whenComplete((c, e) -> {
            if (e != null) {
                initializationException.set(e);
            } else {
                controllerProxy = c;
                model.set(c.getModel());
                init();
            }
        });
    }

    protected abstract void init();

    public CompletableFuture<Void> destroy() {
        CompletableFuture<Void> ret;
        if (controllerProxy != null) {
            ret = controllerProxy.destroy();
            controllerProxy = null;
        } else {
            ret = new CompletableFuture<>();
            ret.complete(null);
        }
        return ret;
    }

    protected void invoke(String actionName, Param... params) {
        if(StringUtil.isBlank(actionName)) {
            throw new IllegalArgumentException("actionName must not be null");
        }
        actionInProcess.set(true);
        controllerProxy.invoke(actionName, params).whenComplete((v, e) -> {
            try {
                if (e != null) {
                    invocationException.set(new ControllerActionException(e));
                }
            } finally {
                actionInProcess.set(false);
            }
        });
    }

    public boolean isActionInProcess() {
        return actionInProcess.get();
    }

    public ReadOnlyBooleanProperty actionInProcessProperty() {
        return actionInProcess.getReadOnlyProperty();
    }

    public M getModel() {
        return model.get();
    }

    public ReadOnlyObjectProperty<M> modelProperty() {
        return model.getReadOnlyProperty();
    }

    public ControllerActionException getInvocationException() {
        return invocationException.get();
    }

    public ReadOnlyObjectProperty<ControllerActionException> invocationExceptionProperty() {
        return invocationException.getReadOnlyProperty();
    }

    public Throwable getInitializationException() {
        return initializationException.get();
    }

    public ReadOnlyObjectProperty<Throwable> initializationExceptionProperty() {
        return initializationException.getReadOnlyProperty();
    }
}
