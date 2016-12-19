package org.opendolphin.core.server.action;

import groovy.lang.Closure;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.comm.ActionRegistry;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CreatePresentationModelAction extends DolphinServerAction {

    private static final Logger LOG = Logger.getLogger(CreatePresentationModelAction.class.getName());

    public void registerIn(ActionRegistry registry) {
        registry.register(CreatePresentationModelCommand.class, new Closure<Object>(this, this) {
            public void doCall(CreatePresentationModelCommand command, Object response) {
                createPresentationModel(command, getServerDolphin());// closure wrapper for correct scoping and extracted method for static compilation
            }
        });
    }

    private static void createPresentationModel(CreatePresentationModelCommand command, DefaultServerDolphin serverDolphin) {
        if (serverDolphin.getAt(command.getPmId()) != null) {
            LOG.info("Ignoring create PM '" + command.getPmId() + "' since it is already in the model store.");
            return;
        }

        if (command.getPmId().endsWith(ServerPresentationModel.AUTO_ID_SUFFIX)) {
            LOG.info("Creating the PM '" + command.getPmId() + "' with reserved server-auto-suffix.");
        }

        List<ServerAttribute> attributes = new LinkedList();
        for (Map<String, Object> attr : command.getAttributes()) {
            ServerAttribute attribute = new ServerAttribute((String) attr.get("propertyName"), attr.get("value"), (String) attr.get("qualifier"), Tag.tagFor((String) attr.get("tag")));
            attribute.setId((String) attr.get("id"));
            attributes.add(attribute);
        }

        PresentationModel model = new ServerPresentationModel(command.getPmId(), attributes, serverDolphin.getServerModelStore());
        ((ServerPresentationModel) model).setPresentationModelType(command.getPmType());
        if (serverDolphin.getServerModelStore().containsPresentationModel(model.getId())) {
            LOG.info("There already is a PM with id " + model.getId() + ". Create PM ignored.");
        } else {
            serverDolphin.getServerModelStore().add(model);
        }
    }

}
