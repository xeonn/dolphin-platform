package com.canoo.dolphin.workflow.server.activiti;

public class ProcessInstanceStartedEvent {
    private final String processDefinitionId;
    private final String processInstanceId;

    public ProcessInstanceStartedEvent(String processDefinitionId, String processInstanceId) {
        this.processDefinitionId = processDefinitionId;
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }
}
