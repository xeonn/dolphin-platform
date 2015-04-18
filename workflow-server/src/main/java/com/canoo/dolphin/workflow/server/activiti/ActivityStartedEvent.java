package com.canoo.dolphin.workflow.server.activiti;

public class ActivityStartedEvent {
    private final String activityId;

    public ActivityStartedEvent(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityId() {
        return activityId;
    }
}
