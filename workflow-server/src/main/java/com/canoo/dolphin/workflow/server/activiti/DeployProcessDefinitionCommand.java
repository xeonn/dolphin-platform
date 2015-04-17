package com.canoo.dolphin.workflow.server.activiti;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;

class DeployProcessDefinitionCommand implements Command<ProcessDefinitionEntity> {

    private String processDefinitionId;

    DeployProcessDefinitionCommand(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public ProcessDefinitionEntity execute(CommandContext commandContext) {
        if (processDefinitionId == null) {
            return null;
        }
        return commandContext.getProcessEngineConfiguration()
                .getDeploymentManager().findDeployedProcessDefinitionById(processDefinitionId);
    }
}
