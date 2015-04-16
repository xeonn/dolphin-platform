package com.canoo.dolphin.workflow.server.model;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("WorkflowViewModel")
public class WorkflowViewModel {

    private Property<ProcessList> processList;
    private Property<ProcessInstance> processInstance;

    public ProcessList getProcessList() {
        return processList.get();
    }
    public void setProcessList(ProcessList processList) {
        this.processList.set(processList);
    }

    public ProcessInstance getProcessInstance() {
        return processInstance.get();
    }
    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance.set(processInstance);
    }

}
