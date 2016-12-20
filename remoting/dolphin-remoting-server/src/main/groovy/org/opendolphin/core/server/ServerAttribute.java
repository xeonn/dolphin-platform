package org.opendolphin.core.server;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.BaseAttribute;
import org.opendolphin.core.Tag;
import org.opendolphin.core.comm.AttributeMetadataChangedCommand;

import java.util.List;

public class ServerAttribute extends BaseAttribute {


    private boolean notifyClient = true;

    public ServerAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue);
    }

    public ServerAttribute(String propertyName, Object baseValue, String qualifier, Tag tag) {
        super(propertyName, baseValue, qualifier, tag);
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
        forAllQualified(new Closure<Object>(this, this) {
            public void doCall(ServerAttribute attribute) {
                if (newValue == null && attribute.getValue() != null || newValue != null && attribute.getValue() == null || !newValue.equals(attribute.getValue())) {
                    attribute.setValue(newValue);
                }

            }

        });
    }

    @Override
    public void setBaseValue(final Object value) {
        if (notifyClient) {
            getPresentationModel().getModelStore().getCurrentResponse().add(new AttributeMetadataChangedCommand(getId(), Attribute.BASE_VALUE, value));
        }

        super.setBaseValue(value);
        // on the server side, we have no listener on the model store to care for the distribution of
        // baseValue changes to all attributes of the same qualifier so we must care for that ourselves
        forAllQualified(new Closure<Object>(this, this) {
            public void doCall(ServerAttribute attribute) {
                if (value == null && attribute.getBaseValue() != null || value != null && attribute.getBaseValue() == null || !value.equals(attribute.getBaseValue())) {
                    attribute.setBaseValue(value);
                }

            }

        });

    }

    protected void forAllQualified(Closure yield) {
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

            yield.call(sameQualified);
        }

    }

    @Override
    public void setQualifier(String value) {
        super.setQualifier(value);
        if (notifyClient) {
            getPresentationModel().getModelStore().getCurrentResponse().add(new AttributeMetadataChangedCommand(getId(), Attribute.QUALIFIER_PROPERTY, value));
        }

    }

    @Override
    public void reset() {
        super.reset();
        if (notifyClient) {
            DefaultServerDolphin.resetCommand(getPresentationModel().getModelStore().getCurrentResponse(), this);
        }

    }

    /**
     * Rebasing on the server side must set the base value to the current value as seen on the server side.
     * This will send a command to the client that instructs him to also set his base value to the exact
     * same (server-side) value.
     * NB: This is subtly different from just calling "rebase" on the client side since the attribute value
     * on the client side may have changed due to user input or value change listeners to a state that the
     * server has not yet seen.
     */
    @Override
    public void rebase() {// todo dk: delete before 1.0 final
        super.rebase();
        // we are no longer sending RebaseCommand
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
