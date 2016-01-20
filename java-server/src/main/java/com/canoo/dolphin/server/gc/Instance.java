package com.canoo.dolphin.server.gc;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.impl.IdentitySet;
import com.canoo.dolphin.mapping.Property;

import java.util.ArrayList;
import java.util.List;

public class Instance {

    private Object bean;

    private boolean rootBean;

    private IdentitySet<Property> properties;
    private IdentitySet<ObservableList> lists;

    private List<Reference> references;

    public Instance(Object bean, boolean rootBean, IdentitySet<Property> properties, IdentitySet<ObservableList> lists) {
        this.bean = bean;
        this.rootBean = rootBean;
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

    public boolean isRootBean() {
        return rootBean;
    }

    public boolean isReferencedByRoot() {
        if(rootBean) {
            return true;
        }
        for(Reference reference : references) {
            Instance parent = reference.getParent();
            if(parent.isReferencedByRoot()) {
                return true;
            }
        }
        return false;
    }
}
