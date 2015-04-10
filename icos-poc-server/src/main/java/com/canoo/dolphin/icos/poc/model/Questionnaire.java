package com.canoo.dolphin.icos.poc.model;


import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;

@DolphinBean("Questionnaire")
public class Questionnaire {

    private ObservableList<Section> sections;
    public ObservableList<Section> getSections() {
        return sections;
    }

}
