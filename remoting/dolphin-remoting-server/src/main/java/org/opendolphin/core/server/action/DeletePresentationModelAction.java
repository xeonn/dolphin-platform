package org.opendolphin.core.server.action;

import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.DeletedPresentationModelNotification;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.List;

public class DeletePresentationModelAction extends DolphinServerAction {

    public void registerIn(ActionRegistry registry) {
        registry.register(DeletedPresentationModelNotification.class, new CommandHandler<DeletedPresentationModelNotification>() {
            @Override
            public void handleCommand(final DeletedPresentationModelNotification command, List response) {
                PresentationModel model = getServerDolphin().getPresentationModel(command.getPmId());

                // Note: we cannot do serverDolphin.remove(model) since that may trigger another DeleteCommand
                // We need to do it silently just like when creating PMs.

                getServerDolphin().getServerModelStore().remove(model);
            }
        });
    }

}
