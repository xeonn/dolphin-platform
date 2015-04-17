package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.MessageHandler;
import com.canoo.dolphin.workflow.server.activiti.ActivitiService;
import com.canoo.dolphin.workflow.server.model.BaseProcessInstance;
import com.canoo.dolphin.workflow.server.model.ProcessInstance;
import com.canoo.dolphin.workflow.server.model.WorkflowViewModel;

import javax.inject.Inject;

@DolphinController("WorkflowController")
public class WorkflowController {

    private WorkflowViewModel workflowViewModel;

    private MessageHandler messageHandler;

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
        if (messageHandler != null) {
            eventBus.unregisterHandler(messageHandler);
        } else {
            messageHandler = message -> System.out.println("we are in messagehandler and received a message: " + message);
        }
        ProcessInstance processInstance = activitiService.createProcessInstance(baseProcessInstance.getLabel());
        eventBus.registerHandler("processInstance/" + processInstance.getLabel(), messageHandler);
        workflowViewModel.setProcessInstance(processInstance);
    }
}
