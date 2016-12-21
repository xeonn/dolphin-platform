package org.opendolphin.core.server.action;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.AttributeCreatedNotification;
import org.opendolphin.core.comm.ChangeAttributeMetadataCommand;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StoreAttributeAction extends DolphinServerAction {

    private static final Logger LOG = Logger.getLogger(StoreAttributeAction.class.getName());

    public void registerIn(ActionRegistry registry) {
        registry.register(AttributeCreatedNotification.class, new CommandHandler<AttributeCreatedNotification>() {
            @Override
            public void handleCommand(AttributeCreatedNotification command, List response) {
                ServerModelStore modelStore = getServerDolphin().getServerModelStore();
                Attribute existing = modelStore.findAttributeById(command.getAttributeId());
                if (existing != null) {
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

        registry.register(ChangeAttributeMetadataCommand.class, new CommandHandler<ChangeAttributeMetadataCommand>() {
            @Override
            public void handleCommand(final ChangeAttributeMetadataCommand command, List response) {
                final Attribute attribute = getServerDolphin().findAttributeById(command.getAttributeId());
                if (attribute == null) {
                    LOG.warning("Cannot find attribute with id '" + command.getAttributeId() + "'. Metadata remains unchanged.");
                    return;
                }

                ((ServerAttribute) attribute).silently(new Runnable() {
                    @Override
                    public void run() {
                        if(command.getMetadataName().equals(Attribute.VALUE)) {
                            attribute.setValue(command.getValue());
                        } else if(command.getMetadataName().equals(Attribute.QUALIFIER_PROPERTY)) {
                            if(command.getValue() == null) {
                                ((ServerAttribute) attribute).setQualifier(null);
                            } else {
                                ((ServerAttribute) attribute).setQualifier(command.getValue().toString());
                            }
                        } else {
                            throw new RuntimeException("Metadata type wrong!");
                        }
                    }

                });
            }
        });
    }

}
