package org.opendolphin.core.server.action;

import groovy.lang.Closure;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.DeletedPresentationModelNotification;
import org.opendolphin.core.server.comm.ActionRegistry;

public class DeletePresentationModelAction extends DolphinServerAction {
    public void registerIn(ActionRegistry registry) {
        registry.register(DeletedPresentationModelNotification.class, new Closure<Boolean>(this, this) {
            public Boolean doCall(DeletedPresentationModelNotification command, Object response) {
                PresentationModel model = getServerDolphin().getAt(command.getPmId());

                // Note: we cannot do serverDolphin.remove(model) since that may trigger another DeleteCommand
                // We need to do it silently just like when creating PMs.

                return getServerDolphin().getServerModelStore().remove(model);

            }

        });
    }

}
