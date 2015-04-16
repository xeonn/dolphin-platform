package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.impl.collections.ListMapper;
import com.canoo.dolphin.workflow.server.platform.AbstractDolphinCommand;
import com.canoo.dolphin.workflow.server.platform.DolphinCommand;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.opendolphin.core.server.ServerDolphin;

import javax.inject.Inject;
import java.util.List;

@DolphinCommand("FetchProcessDefinitionsCommand")
public class FetchProcessDefinitionsCommand extends AbstractDolphinCommand {

    @Inject
    private RepositoryService repositoryService;

    @Override
    public void action() {
        final ServerDolphin serverDolphin = getDolphin();
        final ClassRepository classRepository = new ClassRepository(serverDolphin);
        final BeanRepository beanRepository = new BeanRepository(serverDolphin, classRepository);
        beanRepository.setListMapper(new ListMapper(serverDolphin, classRepository, beanRepository));
        final BeanManager manager = new BeanManager(beanRepository);

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();

        for (ProcessDefinition processDefinition : processDefinitions) {
            com.canoo.dolphin.workflow.server.model.ProcessDefinition pD = manager.create(com.canoo.dolphin.workflow.server.model.ProcessDefinition.class);
            pD.setId(processDefinition.getId());
            pD.setName(processDefinition.getName());
        }
    }
}
