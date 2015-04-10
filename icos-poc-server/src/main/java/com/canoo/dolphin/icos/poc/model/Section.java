package com.canoo.dolphin.icos.poc.model;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("Section")
public class Section {

    private Property<String> label;
    public String getLabel() {
        return label.get();
    }
    public void setLabel(String value) {
        label.set(value);
    }

    private ObservableList<Question> questions;
    public ObservableList<Question> getQuestions() {
        return questions;
    }

}
