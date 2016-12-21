package org.opendolphin.core.server;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.BaseAttribute;
import org.opendolphin.core.comm.AttributeMetadataChangedCommand;

import java.util.List;

public class ServerAttribute extends BaseAttribute {


    private boolean notifyClient = true;

    public ServerAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue);
    }

    public ServerAttribute(String propertyName, Object baseValue, String qualifier) {
        super(propertyName, baseValue, qualifier);
    }

    @Override
    public ServerPresentationModel getPresentationModel() {
        return (ServerPresentationModel) super.getPresentationModel();
    }

    @Override
    public void setValue(final Object newValue) {
        if (notifyClient) {
            DefaultServerDolphin.changeValueCommand(getPresentationModel().getModelStore().getCurrentResponse(), this, newValue);
        }

        super.setValue(newValue);
        // on the server side, we have no listener on the model store to care for the distribution of
        // baseValue changes to all attributes of the same qualifier so we must care for that ourselves

        if (getQualifier() == null) {
            return;

        }

        if (getPresentationModel() == null) {
            return;

        }
        // we may not know the pm, yet
        for (ServerAttribute sameQualified : (List<ServerAttribute>)getPresentationModel().getModelStore().findAllAttributesByQualifier(getQualifier())) {
            if (DefaultGroovyMethods.is(sameQualified, this)) {
                continue;
            }

            if (newValue == null && sameQualified.getValue() != null || newValue != null && sameQualified.getValue() == null || !newValue.equals(sameQualified.getValue())) {
                sameQualified.setValue(newValue);
            }
        }
    }

    @Override
    public void setQualifier(String value) {
        super.setQualifier(value);
        if (notifyClient) {
            getPresentationModel().getModelStore().getCurrentResponse().add(new AttributeMetadataChangedCommand(getId(), Attribute.QUALIFIER_PROPERTY, value));
        }

    }

    public String getOrigin() {
        return "S";
    }

    /**
     * Do the applyChange without creating commands that are sent to the client
     */
    public void silently(Runnable applyChange) {
        boolean temp = notifyClient;
        notifyClient = false;
        applyChange.run();
        notifyClient = temp;
    }

    /**
     * Do the applyChange with enforced creation of commands that are sent to the client
     */
    protected void verbosely(Runnable applyChange) {
        boolean temp = notifyClient;
        notifyClient = true;
        applyChange.run();
        notifyClient = temp;
    }

    /**
     * Overriding the standard behavior of PCLs such that firing is enforced to be done
     * verbosely. This is safe since on the server side PCLs are never used for the control
     * of the client notification as part of the OpenDolphin infrastructure
     * (as opposed to the java client).
     * That is: all remaining PCLs are application specific and _must_ be called verbosely.
     */
    @Override
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        verbosely(new Runnable() {
            @Override
            public void run() {
                ServerAttribute.super.firePropertyChange(propertyName, oldValue, newValue);
            }

        });
    }

}
