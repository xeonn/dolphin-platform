package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.workflow.server.activiti.ActivitiService;
import com.canoo.dolphin.workflow.server.model.BaseProcessInstance;
import com.canoo.dolphin.workflow.server.model.ProcessDefinition;
import com.canoo.dolphin.workflow.server.model.ProcessInstance;
import com.canoo.dolphin.workflow.server.model.WorkflowViewModel;

import javax.inject.Inject;

@DolphinController("WorkflowController")
public class WorkflowController {

    private WorkflowViewModel workflowViewModel;

    private Subscription subscription;

    @Inject
    private ActivitiService activitiService;

    @Inject
    private DolphinEventBus eventBus;

    @DolphinAction
    public void init() {
        workflowViewModel = activitiService.setupWorkflowViewModel();
    }

    @DolphinAction
    public void showProcessInstance(@Param("processInstance") BaseProcessInstance baseProcessInstance) {
        unsubscribeFromOldProcessInstance();
        ProcessInstance processInstance = activitiService.createProcessInstance(baseProcessInstance.getLabel());
        subscribe(processInstance);
        workflowViewModel.setProcessInstance(processInstance);
    }

    @DolphinAction
    public void startProcessInstanceAndShow(@Param("processDefinition") ProcessDefinition processDefinition) {
        ProcessInstance processInstance = activitiService.startProcessInstance(processDefinition);
        processDefinition.getProcessInstances().add(processInstance);
        workflowViewModel.setProcessInstance(processInstance);
    }

    private void subscribe(ProcessInstance processInstance) {
        subscription = eventBus.subscribe("processInstance/" + processInstance.getLabel(), new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("got message: " + message.getData());
            }
        });
    }

    private void unsubscribeFromOldProcessInstance() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }
}
