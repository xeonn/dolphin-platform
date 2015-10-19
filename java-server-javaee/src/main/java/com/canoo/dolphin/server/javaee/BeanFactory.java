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
package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.TaskExecutor;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.event.impl.TaskExecutorImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;

public class BeanFactory {

    @Produces
    @SessionScoped
    public BeanManager createManager() {
        return DolphinContext.getCurrentContext().getBeanManager();
    }

    @Produces
    @ApplicationScoped
    public TaskExecutor createTaskExecutor() {
        return TaskExecutorImpl.getInstance();
    }

    @Produces
    @ApplicationScoped
    public DolphinEventBus createEventBus() {
        return DolphinEventBusImpl.getInstance();
    }
}
