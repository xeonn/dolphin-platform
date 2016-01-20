package com.canoo.dolphin.server.gc;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.impl.IdentitySet;
import com.canoo.dolphin.mapping.Property;

import java.util.ArrayList;
import java.util.List;

public class Instance {

    private Object bean;

    private IdentitySet<Property> properties;
    private IdentitySet<ObservableList> lists;

    private List<Reference> references;

    public Instance(Object bean, IdentitySet<Property> properties, IdentitySet<ObservableList> lists) {
        this.bean = bean;
        this.properties = properties;
        this.lists = lists;
        references = new ArrayList<>();
    }

    public Object getBean() {
        return bean;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public IdentitySet<Property> getProperties() {
        return properties;
    }

    public IdentitySet<ObservableList> getLists() {
        return lists;
    }
}
