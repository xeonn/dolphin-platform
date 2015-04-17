package com.canoo.dolphin.workflow.server.activiti;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;

@Singleton
@Component
public class StartProcessService {

    private final static String HIRE_PROCESS = "hireProcess";

    @Inject
    private RuntimeService runtimeService;

    public ProcessInstance startProcess(String key) {
        switch (key) {
            case HIRE_PROCESS:
                HashMap<String, Object> processVariables = new HashMap<>();
                processVariables.put("applicantName", "Dolphin Workflow1");
                processVariables.put("phoneNumber", "02476/346341");
                return runtimeService.startProcessInstanceByKey("hireProcess", processVariables);
            default:
                System.out.println("process not found. key: " + key);
                return null;
        }
    }
}
