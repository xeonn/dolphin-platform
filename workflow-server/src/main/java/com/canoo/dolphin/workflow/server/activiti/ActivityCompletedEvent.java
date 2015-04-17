package com.canoo.dolphin.workflow.server.activiti;

public class ActivityCompletedEvent {
    private final String activityId;

    public ActivityCompletedEvent(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityId() {
        return activityId;
    }
}
