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
package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextHandler;
import com.canoo.dolphin.server.event.ControllerTask;
import com.canoo.dolphin.server.event.TaskExecutor;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class TaskExecutorImpl implements TaskExecutor {

    private static TaskExecutorImpl Instance = new TaskExecutorImpl();

    private TaskExecutorImpl() {
    }

    @Override
    public <T> void execute(Class<T> controllerClass, ControllerTask<T> task) {
        for(DolphinContextTaskExecutor taskExecutor : getAll()) {
            taskExecutor.add(controllerClass, task);
        }
        DolphinEventBusImpl.getInstance().triggerTaskExecution();
    }

    @Override
    public <T> void executeForSession(Class<T> controllerClass, ControllerTask<T> task) {
        for(DolphinContextTaskExecutor taskExecutor : getAllInCurrentSession()) {
            taskExecutor.add(controllerClass, task);
        }
        DolphinEventBusImpl.getInstance().triggerTaskExecution();
    }

    private List<DolphinContextTaskExecutor> getAllInCurrentSession() {
        List<DolphinContextTaskExecutor> ret = new ArrayList<>();
        for(DolphinContext context : DolphinContextHandler.getAllContextsInSession()) {
            ret.add(context.getTaskExecutor());
        }
        return ret;
    }

    private List<DolphinContextTaskExecutor> getAll() {
        List<DolphinContextTaskExecutor> ret = new ArrayList<>();
        for(DolphinContext context : DolphinContextHandler.getAllContexts()) {
            ret.add(context.getTaskExecutor());
        }
        return ret;
    }

    public static TaskExecutorImpl getInstance() {
        return Instance;
    }
}
