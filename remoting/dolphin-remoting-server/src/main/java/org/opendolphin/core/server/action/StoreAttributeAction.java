package org.opendolphin.core.server.action;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.AttributeCreatedNotification;
import org.opendolphin.core.comm.ChangeAttributeMetadataCommand;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.comm.ActionRegistry;

import java.util.ArrayList;
import java.util.logging.Logger;

public class StoreAttributeAction extends DolphinServerAction {

    private static final Logger LOG = Logger.getLogger(StoreAttributeAction.class.getName());

    public void registerIn(ActionRegistry registry) {
        registry.register(AttributeCreatedNotification.class, new Closure<Object>(this, this) {
            public void doCall(AttributeCreatedNotification command, Object response) {
                ServerModelStore modelStore = getServerDolphin().getServerModelStore();
                Attribute existing = modelStore.findAttributeById(command.getAttributeId());
                if (DefaultGroovyMethods.asBoolean(existing)) {
                    LOG.info("trying to store an already existing attribute: " + command);
                    return;
                }

                ServerAttribute attribute = new ServerAttribute(command.getPropertyName(), command.getNewValue(), command.getQualifier());
                attribute.setId(command.getAttributeId());
                PresentationModel pm = getServerDolphin().getPresentationModel(command.getPmId());
                if (pm == null) {
                    pm = new ServerPresentationModel(command.getPmId(), new ArrayList(), modelStore);
                    modelStore.add(pm);
                }
                ((ServerPresentationModel) pm)._internal_addAttribute(attribute);
                modelStore.registerAttribute(attribute);
            }

        });

        registry.register(ChangeAttributeMetadataCommand.class, new Closure<Object>(this, this) {
            public void doCall(final ChangeAttributeMetadataCommand command, Object response) {
                final Attribute attribute = getServerDolphin().findAttributeById(command.getAttributeId());
                if (!DefaultGroovyMethods.asBoolean(attribute)) {
                    LOG.warning("Cannot find attribute with id '" + command.getAttributeId() + "'. Metadata remains unchanged.");
                    return;
                }

                ((ServerAttribute) attribute).silently(new Runnable() {
                    @Override
                    public void run() {
                        DefaultGroovyMethods.putAt(attribute, command.getMetadataName(), command.getValue());
                    }

                });
            }
        });
    }

}
