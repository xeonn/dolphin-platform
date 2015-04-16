package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.workflow.server.activiti.ActivitiService;

import javax.inject.Inject;

@DolphinController("ActivitiController")
public class ActivitiController {

    @Inject
    private ActivitiService activitiService;

    @DolphinAction
    public void init() {
        activitiService.setupProcessList();
    }
}
