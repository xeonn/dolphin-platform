package com.canoo.dolphin.server.mbean.beans;

import java.lang.ref.WeakReference;

/**
 * Created by hendrikebbers on 14.03.16.
 */
public class DolphinControllerInfo implements DolphinControllerInfoMBean {

    private Class<?> controllerClass;

    private String id;

    private String dolphinSessionId;

    private WeakReference<ModelProvider> weakModelProvider;

    public DolphinControllerInfo(String dolphinSessionId, Class<?> controllerClass, String id, ModelProvider modelProvider) {
        this.controllerClass = controllerClass;
        this.dolphinSessionId = dolphinSessionId;
        this.id = id;
        this.weakModelProvider = new WeakReference<ModelProvider>(modelProvider);
    }

    public String getControllerClass() {
        return controllerClass.getName();
    }

    @Override
    public String dumpModel() {
        ModelProvider provider = weakModelProvider.get();
        if(provider != null) {
            Object model = provider.getModel();
            if(model != null) {
                return ModelJsonSerializer.toJson(model).toString();
            }
        }
        return null;
    }

    @Override
    public String getDolphinSessionId() {
        return dolphinSessionId;
    }

    public String getId() {
        return id;
    }
}
