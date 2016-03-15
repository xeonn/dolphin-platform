package com.canoo.dolphin.server.mbean.beans;

/**
 * Created by hendrikebbers on 14.03.16.
 */
public interface DolphinControllerInfoMBean {

    String getDolphinSessionId();

    String getId();

    String getControllerClass();

    String dumpModel();

}
