package com.canoo.dolphin.workflow.server.model;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("ProcessInstance")
public class ProcessInstance extends BaseProcessInstance {

    private Property<Activity> startActivity;
    private ObservableList<Activity> activities;

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
