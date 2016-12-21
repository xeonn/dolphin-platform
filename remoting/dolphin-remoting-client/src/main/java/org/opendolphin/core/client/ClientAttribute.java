package org.opendolphin.core.client;

import org.opendolphin.core.BaseAttribute;

/**
 * A client side (remote) ClientAttribute is considered a remote representation of a ServerAttribute.
 * Changes to a remote ClientAttribute are sent to the server. This happens by using a dedicated
 * One can bind against a ClientAttribute in two ways
 * a) as a PropertyChangeListener
 * b) through the valueProperty() method for JavaFx
 */
public class ClientAttribute extends BaseAttribute {

    public ClientAttribute(String propertyName, Object initialValue, String qualifier) {
        super(propertyName, initialValue, qualifier);
    }

    public ClientAttribute(String propertyName, Object initialValue) {
        this(propertyName, initialValue, null);
    }

    public String getOrigin() {
        return "C";
    }

}
