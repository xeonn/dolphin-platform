package com.canoo.dolphin.server;

public interface DolphinSession {

    void setAttribute(String name, Object value);

    <T> T getAttribute(String name);

    void removeAttribute(String name);

    void invalidate();

    String getId();
}
