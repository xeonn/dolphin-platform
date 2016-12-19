package org.opendolphin.core.server.action;

import groovy.lang.Closure;
import org.opendolphin.core.ModelStore;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.PresentationModelResetedCommand;
import org.opendolphin.core.comm.ResetPresentationModelCommand;
import org.opendolphin.core.server.comm.ActionRegistry;

import java.util.List;

public class ResetPresentationModelAction implements ServerAction {
    public ResetPresentationModelAction(ModelStore modelStore) {
        this.modelStore = modelStore;
    }

    public void registerIn(ActionRegistry registry) {
        registry.register(ResetPresentationModelCommand.class, new Closure<Object>(this, this) {
            public void doCall(ResetPresentationModelCommand command, List<Command> response) {
                PresentationModel model = modelStore.findPresentationModelById(command.getPmId());
                // todo: trigger application specific persistence
                // todo: deal with potential persistence errors
                response.add(doWithPresentationModel(model));
            }

        });
    }

    public Command doWithPresentationModel(PresentationModel model) {
        PresentationModelResetedCommand command = new PresentationModelResetedCommand();
        command.setPmId(model.getId());
        return command;
    }

    private final ModelStore modelStore;
}
