package com.canoo.dolphin.workflow.server.model;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("Activity")
public class Activity {

    private Property<String> activitiyId;
    private Property<String> label;
    private Property<String> activityName;
    private Property<String> description;
    private ObservableList<Transition> outgoingTransitions;
    private Property<String> type;

    public String getActivityId() {
        return activitiyId.get();
    }

    public void setActivityId(String id) {
        this.activitiyId.set(id);
    }

    public String getLabel() {
        return label.get();
    }

    public void setLabel(String id) {
        this.label.set(id);
    }

    public String getActivityName() {
        return activityName.get();
    }

    public void setActivityName(String activityName) {
        this.activityName.set(activityName);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public ObservableList<Transition> getOutgoingTransitions() {
        return outgoingTransitions;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }
}
