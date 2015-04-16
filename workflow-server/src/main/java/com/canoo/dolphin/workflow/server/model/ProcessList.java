package com.canoo.dolphin.workflow.server.model;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;

@DolphinBean("ProcessList")
public class ProcessList {

    private ObservableList<ProcessDefinition> processDefinitions;

    public ObservableList<ProcessDefinition> getProcessDefinitions() {
        return processDefinitions;
    }

}
