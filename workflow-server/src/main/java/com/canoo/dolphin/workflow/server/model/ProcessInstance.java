package com.canoo.dolphin.workflow.server.model;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.Property;

public class ProcessInstance extends BaseProcessInstance {
    private Property<ProcessDefinition> processDefinition;

    private Property<Activity> startActivity;
    private ObservableList<Activity> activities;

    public ProcessDefinition getProcessDefinition() {
        return processDefinition.get();
    }

    public void setProcessDefinition(ProcessDefinition processDefinition) {
        this.processDefinition.set(processDefinition);
    }

    public Activity getStartActivity() {
        return startActivity.get();
    }

    public void setStartActivity(Activity startActivity) {
        this.startActivity.set(startActivity);
    }

    public ObservableList<Activity> getActivities() {
        return activities;
    }
}
