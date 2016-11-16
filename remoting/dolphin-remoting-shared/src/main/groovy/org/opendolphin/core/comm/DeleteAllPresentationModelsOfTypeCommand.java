package org.opendolphin.core.comm;

/**
 * Same structure as DeletedAllPresentationModelsOfTypeNotification but for the purpose of
 * sending the command from server to client.
 */
public class DeleteAllPresentationModelsOfTypeCommand extends DeletedAllPresentationModelsOfTypeNotification {

    public DeleteAllPresentationModelsOfTypeCommand(String pmType) {
        super(pmType);
    }

    public DeleteAllPresentationModelsOfTypeCommand() {
    }
}
