package com.canoo.dolphin.icos.poc.model;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("Question")
public class Question {

    public static enum Type {RADIO_GROUP}

    private Property<String> label;
    public String getLabel() {
        return label.get();
    }
    public void setLabel(String value) {
        label.set(value);
    }

    private Property<Type> type;
    public Type getType() {
        return type.get();
    }
    public void setType(Type value) {
        type.set(value);
    }

    private Property<Boolean> visible;
    public boolean getVisible() {
        return visible.get();
    }
    public void setVisible(boolean value) {
        visible.set(value);
    }

    private Property<String> value;
    public String getValue() {
        return value.get();
    }
    public void setValue(String value) {
        this.value.set(value);
    }
    public Property<String> getValueProperty() {
        return value;
    }

    private ObservableList<Option> options;
    public ObservableList<Option> getOptions() {
        return options;
    }
}
