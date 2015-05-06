package com.canoo.dolphin.workflow.server.activiti;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;

@Component
public class StartProcessService {

    private final static String HIRE_PROCESS = "hireProcess";

    @Inject
    private RuntimeService runtimeService;

    public ProcessInstance startProcessByKey(String key) {
        switch (key) {
            case HIRE_PROCESS:
                HashMap<String, Object> processVariables = new HashMap<>();
                processVariables.put("applicantName", "Dolphin Workflow1");
                processVariables.put("phoneNumber", "02476/346341");
                return runtimeService.startProcessInstanceByKey("hireProcess", processVariables);
            default:
                return runtimeService.startProcessInstanceByKey(key);
        }
    }

    public ProcessInstance startProcessById(String id) {
        if (id != null && id.startsWith(HIRE_PROCESS)) {
            HashMap<String, Object> processVariables = new HashMap<>();
            processVariables.put("applicantName", "Dolphin Workflow1");
            processVariables.put("phoneNumber", "02476/346341");
            return runtimeService.startProcessInstanceById(id, processVariables);
        }
        return runtimeService.startProcessInstanceById(id);
    }
}
