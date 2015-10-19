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
package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.event.ControllerTask;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class DolphinContextTaskExecutor {

    @GuardedBy("tasksMapLock")
    private final Map<Class, List<ControllerTask>> tasks;

    private final Lock tasksMapLock;

    public DolphinContextTaskExecutor() {
        tasks = new HashMap<>();
        tasksMapLock = new ReentrantLock();
    }

    public void add(Class controllerClass, ControllerTask task) {
        tasksMapLock.lock();
        try {
            List<ControllerTask> taskList = tasks.get(controllerClass);
            if (taskList == null) {
                taskList = new ArrayList<>();
                tasks.put(controllerClass, taskList);
            }
            taskList.add(task);
        } finally {
            tasksMapLock.unlock();
        }
    }

    public boolean execute() {
        boolean executedTask = false;
        tasksMapLock.lock();
        try {
            for (Class controllerClass : tasks.keySet()) {
                List<ControllerTask> taskList = tasks.remove(controllerClass);
                if (taskList != null && !taskList.isEmpty()) {
                    List<?> controllers = getAllControllersForClass(controllerClass);
                    for (Object controller : controllers) {
                        for (ControllerTask task : taskList) {
                            try {
                                executedTask = true;
                                task.run(controller);
                            } catch (Exception e) {
                                //TODO: ExceptionHandler
                                System.out.println("Error in calling task!");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } finally {
            tasksMapLock.unlock();
        }
        return executedTask;
    }

    private <T> List<? extends T> getAllControllersForClass(Class<T> controllerClass) {
        return DolphinContext.getCurrentContext().getControllerHandler().getAllControllersThatImplement(controllerClass);
    }

}
