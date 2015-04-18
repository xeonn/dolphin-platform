package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.workflow.server.activiti.ActivitiService;
import com.canoo.dolphin.workflow.server.activiti.ProcessInstanceStartedEvent;
import com.canoo.dolphin.workflow.server.model.BaseProcessInstance;
import com.canoo.dolphin.workflow.server.model.ProcessDefinition;
import com.canoo.dolphin.workflow.server.model.ProcessInstance;
import com.canoo.dolphin.workflow.server.model.WorkflowViewModel;

import javax.inject.Inject;

@DolphinController("WorkflowController")
public class WorkflowController {

    private WorkflowViewModel workflowViewModel;
    private Subscription subscription;
    private Subscription createSubscription;

    @Inject
    private ActivitiService activitiService;

    @Inject
    private DolphinEventBus eventBus;


    @DolphinAction
    public void init() {
        workflowViewModel = activitiService.createWorkflowViewModel();
        subscribe();
    }

    @DolphinAction
    public void showProcessInstance(@Param("processInstance") BaseProcessInstance baseProcessInstance) {
        unsubscribe();
        ProcessInstance processInstance = activitiService.findProcessInstance(baseProcessInstance);
        subscribe(processInstance);
        workflowViewModel.setProcessInstance(processInstance);
    }

    @DolphinAction
    public void startProcessInstanceAndShow(@Param("processDefinition") ProcessDefinition processDefinition) {
        ProcessInstance processInstance = activitiService.startProcessInstance(processDefinition);
        processDefinition.getProcessInstances().add(processInstance);
        workflowViewModel.setProcessInstance(processInstance);
    }

    public void subscribe() {
        createSubscription = eventBus.subscribe("create", new MessageListener() {
            @Override
            public void onMessage(Message message) {
                final Object data = message.getData();
                if (data instanceof ProcessInstanceStartedEvent) {
                    final ProcessInstanceStartedEvent event = (ProcessInstanceStartedEvent) data;
                    final String definitionId = event.getProcessDefinitionId();
                    final String instanceId = event.getProcessInstanceId();
                    workflowViewModel.getProcessList().getProcessDefinitions();
                    for (ProcessDefinition processDefinition : workflowViewModel.getProcessList().getProcessDefinitions()) {
                        if (processDefinition.getLabel().equals(definitionId)) {
                            for (BaseProcessInstance processInstance : processDefinition.getProcessInstances()) {
                                if (processInstance.getLabel().equals(instanceId)) {
                                    return;
                                }
                            }
                            processDefinition.getProcessInstances().add(activitiService.findBaseProcessInstance(instanceId));
                            return;
                        }
                    }
                } else {
                    System.out.println("got message: " + message.getData());
                }
            }
        });
    }

    public void subscribe(ProcessInstance processInstance) {
        subscription = eventBus.subscribe("processInstance/" + processInstance.getLabel(), new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("got message: " + message.getData());
            }
        });
    }

    public void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }
}
