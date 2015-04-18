package com.canoo.dolphin.workflow.server.activiti;

import com.canoo.dolphin.server.event.DolphinEventBus;
import org.activiti.engine.delegate.event.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
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
        ActivitiEventType type = event.getType();
        switch (type) {
            case ENTITY_CREATED:
                ActivitiEntityEvent activitiEntityEvent = (ActivitiEntityEvent) event;
                Object entity = activitiEntityEvent.getEntity();
                if (entity instanceof ExecutionEntity) {
                    ActivityImpl activity = ((ExecutionEntity) entity).getActivity();
                    if ("startEvent".equals(activity.getProperty("type"))) {
                        eventBus.publish("create", new ProcessInstanceStartedEvent((event.getProcessInstanceId())));
                    }
                }
                break;
            case ACTIVITY_STARTED:
                eventBus.publish("processInstance/" + processInstanceId, new ActivityStartedEvent(((ActivitiActivityEvent) event).getActivityId()));
                return;
            case ACTIVITY_COMPLETED:
                eventBus.publish("processInstance/" + processInstanceId, new ActivityCompletedEvent(((ActivitiActivityEvent) event).getActivityId()));
                return;
            default:
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
