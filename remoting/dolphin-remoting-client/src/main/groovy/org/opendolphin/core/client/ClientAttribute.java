package org.opendolphin.core.client;

import groovy.transform.CompileStatic;
import org.opendolphin.core.BaseAttribute;
import org.opendolphin.core.Tag;

import java.util.Map;

/**
 * A client side (remote) ClientAttribute is considered a remote representation of a ServerAttribute.
 * Changes to a remote ClientAttribute are sent to the server. This happens by using a dedicated
 * One can bind against a ClientAttribute in two ways
 * a) as a PropertyChangeListener
 * b) through the valueProperty() method for JavaFx
 */
@CompileStatic
public class ClientAttribute extends BaseAttribute {
    /**
     * @deprecated you should not create Client Attributes without initial values
     */
    @Deprecated
    public ClientAttribute(String propertyName) {
        this(propertyName, null);
    }

    public ClientAttribute(String propertyName, Object initialValue, String qualifier, Tag tag) {
        super(propertyName, initialValue, qualifier, tag);
    }

    public ClientAttribute(String propertyName, Object initialValue, String qualifier) {
        this(propertyName, initialValue, qualifier, Tag.VALUE);
    }

    public ClientAttribute(String propertyName, Object initialValue) {
        this(propertyName, initialValue, null, Tag.VALUE);
    }

    /**
     * @deprecated too much dependent on key names and doesn't allow setting the tag
     */
    public ClientAttribute(Map props) {
        this(props.get("propertyName").toString(), props.get("initialValue"));
        this.setQualifier(props.get("qualifier").toString());
    }

    public String getOrigin() {
        return "C";
    }

}
