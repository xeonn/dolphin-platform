package com.canoo.dolphin.workflow.server.activiti;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.workflow.server.model.Activity;
import com.canoo.dolphin.workflow.server.model.BaseProcessInstance;
import com.canoo.dolphin.workflow.server.model.ProcessDefinition;
import com.canoo.dolphin.workflow.server.model.ProcessInstance;
import com.canoo.dolphin.workflow.server.model.ProcessList;
import com.canoo.dolphin.workflow.server.model.WorkflowViewModel;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class ActivitiService {

    @Inject
    private RuntimeService runtimeService;

    @Inject
    private ManagementService managementService;

    @Inject
    private BeanManager manager;

    @Inject
    private RepositoryService repositoryService;

    public void setupWorkflowViewModel() {
        WorkflowViewModel workflowViewModel = manager.create(WorkflowViewModel.class);
        workflowViewModel.setProcessList(setupProcessList());
    }

    public ProcessInstance getProcessInstance(String processInstanceId) {
        List<org.activiti.engine.runtime.ProcessInstance> list = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).list();
        if (list.isEmpty()) {
            return null;
        } else {
            return map(list.get(0));
        }
    }

    private ProcessDefinition map(org.activiti.engine.repository.ProcessDefinition processDefinition) {
        ProcessDefinition mappedInstance = manager.create(ProcessDefinition.class);
        mappedInstance.setId(processDefinition.getId());
        mappedInstance.setName(processDefinition.getName());

        List<org.activiti.engine.runtime.ProcessInstance> instances = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinition.getId()).list();
        for (org.activiti.engine.runtime.ProcessInstance instance : instances) {
            mappedInstance.getProcessInstances().add(mapLight(instance));
        }
        return mappedInstance;
    }

    private ProcessInstance map(org.activiti.engine.runtime.ProcessInstance processInstance) {
        List<Activity> activities = createActivities(processInstance.getProcessDefinitionId());
        ProcessInstance mappedInstance = manager.create(ProcessInstance.class);
        mappedInstance.setId(processInstance.getId());
        mappedInstance.getActivities().addAll(activities);
        mappedInstance.setStartActivity(activities.get(0));
        return mappedInstance;
    }

    private BaseProcessInstance mapLight(org.activiti.engine.runtime.ProcessInstance processInstance) {
        BaseProcessInstance mappedInstance = manager.create(BaseProcessInstance.class);
        mappedInstance.setId(processInstance.getId());
        return mappedInstance;
    }

    private List<Activity> createActivities(String processDefinitionId) {
        ProcessDefinitionEntity processDefinitionEntity = managementService.executeCommand(new DeployProcessDefinitionCommand(processDefinitionId));
        List<ActivityImpl> initialActivityStack = processDefinitionEntity.getInitialActivityStack();
        return createActivityList(initialActivityStack);
    }

    private List<Activity> createActivityList(List<ActivityImpl> activities) {
        List<Activity> result = new ArrayList<>();
        for (ActivityImpl activityImpl : activities) {
            Activity activity = manager.create(Activity.class);
            activity.setId(activityImpl.getId());
        }
        //TODO map outgoing activities ...
        return result;
    }

    private ProcessList setupProcessList() {
        final List<org.activiti.engine.repository.ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
        final ProcessList processList = manager.create(ProcessList.class);
        processList.getProcessDefinitions().addAll(processDefinitions.parallelStream().map(this::map).collect(Collectors.toList()));
        return processList;
    }
}
