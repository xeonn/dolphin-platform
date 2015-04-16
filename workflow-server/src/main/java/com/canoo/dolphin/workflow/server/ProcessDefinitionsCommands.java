package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.workflow.server.model.ProcessList;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;

import javax.inject.Inject;
import java.util.List;

@DolphinController("ProcessDefinitionCommands")
public class ProcessDefinitionsCommands {

    @Inject
    private BeanManager manager;

    @Inject
    private RepositoryService repositoryService;

    @DolphinAction
    public void fetchAll() {
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();

        final ProcessList processList = manager.create(ProcessList.class);

        for (ProcessDefinition processDefinition : processDefinitions) {
            com.canoo.dolphin.workflow.server.model.ProcessDefinition pD = manager.create(com.canoo.dolphin.workflow.server.model.ProcessDefinition.class);
            pD.setId(processDefinition.getId());
            pD.setName(processDefinition.getName());
            processList.getProcessDefinitions().add(pD);
        }
    }
}
