package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextHandler;
import com.canoo.dolphin.server.event.ControllerTask;
import com.canoo.dolphin.server.event.TaskExecutor;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
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
