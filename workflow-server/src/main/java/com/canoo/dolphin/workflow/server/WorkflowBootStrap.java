package com.canoo.dolphin.workflow.server;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;

@Component
public class WorkflowBootStrap {

    @Inject
    private RuntimeService runtimeService;

    public void onStartup() {
        HashMap<String, Object> processVariables = new HashMap<>();
        processVariables.put("applicantName", "Dolphin Workflow");
        processVariables.put("phoneNumber", "02476/34634");
        ProcessInstance hireProcess = runtimeService.startProcessInstanceByKey("hireProcess", processVariables);
        System.out.println(hireProcess.getProcessInstanceId());
    }
}
