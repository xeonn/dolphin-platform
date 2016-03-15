package com.canoo.dolphin.server.mbean.beans;

import java.util.Set;

/**
 * Created by hendrikebbers on 14.03.16.
 */
public interface DolphinSessionInfoMBean {

    String getDolphinSessionId();

    Set<String> getAttributesNames();

    Object getAttribute(String name);
}
