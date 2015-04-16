package com.canoo.dolphin.workflow.server.model;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("ProcessList")
public class ProcessList {

    private ObservableList<ProcessDefinition> processDefinitions;

    private Property<BaseProcessInstance> processInstance;

    public ObservableList<ProcessDefinition> getProcessDefinitions() {
        return processDefinitions;
    }

    public BaseProcessInstance getProcessInstance() {
        return processInstance.get();
    }

    public Property<BaseProcessInstance> getProcessInstanceProperty() {
        return processInstance;
    }
}
