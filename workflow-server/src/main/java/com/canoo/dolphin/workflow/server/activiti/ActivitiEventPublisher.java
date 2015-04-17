package com.canoo.dolphin.workflow.server.activiti;

import com.canoo.dolphin.server.event.DolphinEventBus;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Component
public class ActivitiEventPublisher implements ActivitiEventListener {

    @Inject
    private DolphinEventBus eventBus;

    @Override
    public void onEvent(ActivitiEvent event) {
        //for now we only post event which were fired in the context of a process instance
        String processInstanceId = event.getProcessInstanceId();
        if (processInstanceId != null) {
            eventBus.publish("processInstance/" + processInstanceId, event);
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
