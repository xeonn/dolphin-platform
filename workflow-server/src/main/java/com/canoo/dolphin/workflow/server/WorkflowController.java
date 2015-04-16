package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.workflow.server.activiti.ActivitiService;
import com.canoo.dolphin.workflow.server.model.BaseProcessInstance;

import javax.inject.Inject;

@DolphinController("WorkflowController")
public class WorkflowController {

    @Inject
    private ActivitiService activitiService;

    @DolphinAction
    public void init() {
        activitiService.setupWorkflowViewModel();
    }

    @DolphinAction
    public void showProcessInstance(@Param("processInstance")BaseProcessInstance processInstance) {
        activitiService.showProcessInstance(processInstance.getLabel());

    }
}
