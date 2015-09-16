package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextHandler;
import com.canoo.dolphin.server.event.ControllerTask;
import com.canoo.dolphin.server.event.TaskExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hendrikebbers on 15.09.15.
 */
public class TaskExecutorImpl implements TaskExecutor {

    private final Map<Class, List<ControllerTask>> tasks;

    private final Lock tasksMapLock;

    public TaskExecutorImpl() {
        tasks = new HashMap<>();
        tasksMapLock = new ReentrantLock();
    }

    @Override
    public <T> void execute(Class<T> controllerClass, ControllerTask<T> task) {
        for(TaskExecutorImpl taskExecutor : getAll()) {
            taskExecutor.add(controllerClass, task);
        }
        DolphinContext.getCurrentContext().getEventBus().triggerTaskExecution();
    }

    private void add(Class controllerClass, ControllerTask task) {
        tasksMapLock.lock();
        try {
            List<ControllerTask> taskList = tasks.get(controllerClass);
            if(taskList == null) {
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
            for(Class controllerClass : tasks.keySet()) {
                List<ControllerTask> taskList = tasks.remove(controllerClass);
                if(taskList != null && !taskList.isEmpty()) {
                    List<?> controllers = getAllControllersForClass(controllerClass);
                    for(Object controller : controllers) {
                        for(ControllerTask task : taskList) {
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

    @Override
    public <T> void executeForSession(Class<T> controllerClass, ControllerTask<T> task) {
        for(TaskExecutorImpl taskExecutor : getAllInCurrentSession()) {
            taskExecutor.add(controllerClass, task);
        }
        DolphinContext.getCurrentContext().getEventBus().triggerTaskExecution();
    }

    private <T> List<? extends T> getAllControllersForClass(Class<T> controllerClass) {
        return DolphinContext.getCurrentContext().getControllerHandler().getAllControllersThatImplement(controllerClass);
    }

    private List<TaskExecutorImpl> getAllInCurrentSession() {
        List<TaskExecutorImpl> ret = new ArrayList<>();
        for(DolphinContext context : DolphinContextHandler.getAllContextsInSession()) {
            ret.add(context.getTaskExecutor());
        }
        return ret;
    }

    private List<TaskExecutorImpl> getAll() {
        List<TaskExecutorImpl> ret = new ArrayList<>();
        for(DolphinContext context : DolphinContextHandler.getAllContexts()) {
            ret.add(context.getTaskExecutor());
        }
        return ret;
    }
}
