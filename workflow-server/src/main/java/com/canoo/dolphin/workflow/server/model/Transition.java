package com.canoo.dolphin.workflow.server.model;


import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("Transition")
public class Transition {
    private Property<Activity> source;
    private Property<Activity> target;
    private Property<String> label;

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

    public String getLabel() {
        return label.get();
    }

    public void setLabel(String label) {
        this.label.set(label);
    }
}
