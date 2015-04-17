package com.canoo.dolphin.workflow.server.model;


import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("Transition")
public class Transition {
    private Property<String> activityId;
    private Property<Activity> source;
    private Property<Activity> target;
    private Property<String> transitionName;
    private Property<String> conditionText;
    private Property<String> description;


    public Activity getSource() {
        return source.get();
    }

    public void setSource(Activity source) {
        this.source.set(source);
    }

    public Activity getTarget() {
        return target.get();
    }

    public void setTarget(Activity target) {
        this.target.set(target);
    }

    public String getTransitionName() {
        return transitionName.get();
    }

    public void setTransitionName(String transitionName) {
        this.transitionName.set(transitionName);
    }

    public String getActivityId() {
        return activityId.get();
    }

    public void setActivityId(String activityId) {
        this.activityId.set(activityId);
    }

    public String getConditionText() {
        return conditionText.get();
    }

    public void setConditionText(String conditionText) {
        this.conditionText.set(conditionText);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }


}
