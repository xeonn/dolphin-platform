package com.canoo.dolphin.workflow.server.activiti;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.workflow.server.model.Activity;
import com.canoo.dolphin.workflow.server.model.ProcessInstance;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("request")
public class ActivitiService {

    @Inject
    private RuntimeService runtimeService;

    @Inject
    private ManagementService managementService;

    @Inject
    private BeanManager manager;

    public ProcessInstance getProcessInstance(String processInstanceId) {
        List<org.activiti.engine.runtime.ProcessInstance> list = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).list();
        if (list.isEmpty()) {
            return null;
        } else {
            return map(list.get(0));
        }
    }

    private ProcessInstance map(org.activiti.engine.runtime.ProcessInstance processInstance) {
        List<Activity> activities = findActivities(processInstance.getProcessDefinitionId());
        ProcessInstance mappedInstance = manager.create(ProcessInstance.class);
        mappedInstance.setId(processInstance.getId());
        mappedInstance.getActivities().addAll(activities);
        mappedInstance.setStartActivity(activities.get(0));
        return mappedInstance;
    }

    public List<Activity> findActivities(String processDefinitionId) {
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
        return result;
    }
}
