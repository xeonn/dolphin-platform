package com.canoo.dolphin.workflow.server.model;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("ProcessDefinition")
public class ProcessDefinition {

    private Property<String> label;
    private Property<String> name;
    private ObservableList<BaseProcessInstance> processInstances;

    public ObservableList<BaseProcessInstance> getProcessInstances() {
        return processInstances;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public String getLabel() {
        return label.get();
    }

    public void setLabel(String value) {
        label.set(value);
    }
}
