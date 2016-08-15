package com.canoo.dolphin.test.scopes;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class ScopeModel {

    private Property<String> requestServiceId;

    private Property<String> clientServiceId;

    private Property<String> sessionServiceId;

    private Property<String> singletonServiceId;

    public Property<String> requestServiceIdProperty() {
        return requestServiceId;
    }

    public Property<String> clientServiceIdProperty() {
        return clientServiceId;
    }

    public Property<String>sessionServiceIdProperty() {
        return sessionServiceId;
    }

    public Property<String> singletonServiceIdProperty() {
        return singletonServiceId;
    }
}
