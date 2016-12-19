package org.opendolphin.core.server.action;

import groovy.lang.Closure;
import org.opendolphin.core.comm.DeletedAllPresentationModelsOfTypeNotification;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.comm.ActionRegistry;

import java.util.LinkedList;
import java.util.List;

public class DeletedAllPresentationModelsOfTypeAction extends DolphinServerAction {
    public void registerIn(ActionRegistry registry) {
        registry.register(DeletedAllPresentationModelsOfTypeNotification.class, new Closure<Void>(this, this) {
            public void doCall(DeletedAllPresentationModelsOfTypeNotification command, Object response) {
                List<ServerPresentationModel> models = new LinkedList(getServerDolphin().findAllPresentationModelsByType(command.getPmType()));// work on a copy
                for (ServerPresentationModel model : models) {
                    getServerDolphin().getModelStore().remove(model);// go through the model store to avoid commands being sent to the client
                }

            }

        });
    }

}
