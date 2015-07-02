package com.canoo.dolphin.todo.pm;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class ToDoItem {

    private Property<String> text;
    private Property<Boolean> completed;

    public String getText() {
        return text.get();
    }
    public void setText(String text) {
        this.text.set(text);
    }
    public Property<String> getTextProperty() {
        return text;
    }

    public boolean isCompleted() {
        return Boolean.TRUE == completed.get();
    }
    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }
    public Property<Boolean> getCompletedProperty() {
        return completed;
    }

    @Override
    public String toString() {
        return text.get();
    }
}
