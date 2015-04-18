package com.canoo.dolphin.workflow.server.activiti;

public class ProcessInstanceStartedEvent {
    private final String processInstanceId;

    public ProcessInstanceStartedEvent(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }
}
