package com.canoo.dolphin.server;

/**
 * Defines a Dolphin Platform Session. For each client (each client context instance) one {@link DolphinSession}
 * will be created on the server.
 */
public interface DolphinSession {

    /**
     * Binds the given object to this Dolphin Platform session, using the given name.
     * @param name the name
     * @param value the object
     */
    void setAttribute(String name, Object value);

    /**
     * Returns the object that is bound to this Dolphin Platform session with the given name or null.
     * @param name the name
     * @param <T> type of the object
     * @return the object or null
     */
    <T> T getAttribute(String name);

    /**
     * Removes the object bound with the given name from
     * this Dolphin Platform session.
     * @param name the name
     */
    void removeAttribute(String name);

    /**
     * Invalidates the Dolphin Platform session.
     */
    void invalidate();

    /**
     * Returns the unique id of this Dolphin Platform session.
     * @return the id
     */
    String getId();
}
