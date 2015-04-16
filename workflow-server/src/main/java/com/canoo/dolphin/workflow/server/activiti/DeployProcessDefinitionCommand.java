package com.canoo.dolphin.workflow.server.activiti;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.deploy.DeploymentCache;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;

class DeployProcessDefinitionCommand implements Command<ProcessDefinitionEntity> {

    private String processDefinitionId;

    DeployProcessDefinitionCommand(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public ProcessDefinitionEntity execute(CommandContext commandContext) {
        DeploymentCache<ProcessDefinitionEntity> deploymentCache = Context.getProcessEngineConfiguration().getProcessDefinitionCache();
        if (processDefinitionId != null) {
            return deploymentCache.get(processDefinitionId);
        }
        return null;
    }
}
