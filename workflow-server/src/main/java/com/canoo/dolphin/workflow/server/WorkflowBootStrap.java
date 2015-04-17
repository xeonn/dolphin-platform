package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.workflow.server.activiti.ActivitiEventPublisher;
import org.activiti.engine.RuntimeService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;

@Component
public class WorkflowBootStrap {

    @Inject
    private RuntimeService runtimeService;

    @Inject
    private ActivitiEventPublisher eventPublisher;

    public void onStartup() {
        //eventPublisher receives events from activiti and publishes them to dolphin
        runtimeService.addEventListener(eventPublisher);

        startSomeExampleProcesses();

    }

    private void startSomeExampleProcesses() {
        HashMap<String, Object> processVariables = new HashMap<>();
        processVariables.put("applicantName", "Dolphin Workflow1");
        processVariables.put("phoneNumber", "02476/346341");
        runtimeService.startProcessInstanceByKey("hireProcess", processVariables);

        processVariables.put("applicantName", "Dolphin Workflow2");
        processVariables.put("phoneNumber", "02476/346342");
        runtimeService.startProcessInstanceByKey("hireProcess", processVariables);


        processVariables.put("applicantName", "Dolphin Workflow3");
        processVariables.put("phoneNumber", "02476/346343");
        runtimeService.startProcessInstanceByKey("hireProcess", processVariables);
    }
}
