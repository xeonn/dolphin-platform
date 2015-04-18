package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.workflow.server.activiti.ActivitiEventPublisher;
import com.canoo.dolphin.workflow.server.activiti.StartProcessService;
import org.activiti.engine.RuntimeService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class WorkflowBootStrap {

    @Inject
    private RuntimeService runtimeService;

    @Inject
    private ActivitiEventPublisher eventPublisher;

    @Inject
    private StartProcessService startProcessService;

    public void onStartup() {
        //eventPublisher receives events from activiti and publishes them to dolphin
        runtimeService.addEventListener(eventPublisher);

        //start one example process
        startProcessService.startProcessByKey("hireProcess");
    }

}
