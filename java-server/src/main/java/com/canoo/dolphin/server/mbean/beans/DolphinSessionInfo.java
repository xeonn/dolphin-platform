package com.canoo.dolphin.server.mbean.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hendrikebbers on 15.03.16.
 */
public class DolphinSessionInfo implements DolphinSessionInfoMBean {

    private String dolphinSessionId;

    public DolphinSessionInfo(String dolphinSessionId) {
        this.dolphinSessionId = dolphinSessionId;
    }

    @Override
    public String getDolphinSessionId() {
        return dolphinSessionId;
    }

    @Override
    public Set<String> getAttributesNames() {
        Set<String> dummySet = new HashSet<>();
        dummySet.add("Currently");
        dummySet.add("Not");
        dummySet.add("Supported");
        return dummySet;
    }

    @Override
    public Object getAttribute(String name) {
        return "Currently not supported";
    }
}
