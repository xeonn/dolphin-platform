package com.canoo.dolphin.samples.processmonitor;

import com.canoo.dolphin.todo.server.BackgroundTaskRunner;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class BackgroundTaskRunnerImpl implements BackgroundTaskRunner {

    private List<Runnable> myTasks = new CopyOnWriteArrayList<>();

    @Schedule(second="*/1", minute="*",hour="*", persistent=false)
    public void update(){
        while (!myTasks.isEmpty()) {
            Runnable task = myTasks.remove(0);
            task.run();
        }
    }

    @Override
    public void setTask(Runnable task) {
        myTasks.add(task);
    }
}
