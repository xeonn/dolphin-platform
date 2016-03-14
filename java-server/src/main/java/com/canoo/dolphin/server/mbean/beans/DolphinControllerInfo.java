package com.canoo.dolphin.server.mbean.beans;

/**
 * Created by hendrikebbers on 14.03.16.
 */
public class DolphinControllerInfo implements DolphinControllerInfoMBean {

    private Class<?> controllerClass;

    private String id;

    public DolphinControllerInfo(Class<?> controllerClass, String id) {
        this.controllerClass = controllerClass;
        this.id = id;
    }

    public String getControllerClass() {
        return controllerClass.getName();
    }

    public String getId() {
        return id;
    }
}
