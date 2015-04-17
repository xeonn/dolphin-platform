package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.workflow.server.activiti.ActivitiService;
import com.canoo.dolphin.workflow.server.model.BaseProcessInstance;
import com.canoo.dolphin.workflow.server.model.ProcessDefinition;
import com.canoo.dolphin.workflow.server.model.ProcessInstance;
import com.canoo.dolphin.workflow.server.model.WorkflowViewModel;

import javax.inject.Inject;

@DolphinController("WorkflowController")
public class WorkflowController {

    private WorkflowViewModel workflowViewModel;

    @Inject
    private ActivitiService activitiService;

    @Inject
    private WorkflowSubscriptionService workflowSubscriptionService;


    @DolphinAction
    public void init() {
        workflowViewModel = activitiService.createWorkflowViewModel();
    }

    @DolphinAction
    public void showProcessInstance(@Param("processInstance") BaseProcessInstance baseProcessInstance) {
        workflowSubscriptionService.unsubscribe();
        ProcessInstance processInstance = activitiService.findProcessInstance(baseProcessInstance);
        workflowSubscriptionService.subscribe(processInstance);
        workflowViewModel.setProcessInstance(processInstance);
    }

    @DolphinAction
    public void startProcessInstanceAndShow(@Param("processDefinition") ProcessDefinition processDefinition) {
        ProcessInstance processInstance = activitiService.startProcessInstance(processDefinition);
        processDefinition.getProcessInstances().add(processInstance);
        workflowViewModel.setProcessInstance(processInstance);
    }
}
