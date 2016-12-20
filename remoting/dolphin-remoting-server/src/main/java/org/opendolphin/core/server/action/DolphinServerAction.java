package org.opendolphin.core.server.action;

import org.opendolphin.core.Tag;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerAttribute;

import java.util.List;

/**
 * Common superclass for all actions that need access to
 * the ServerDolphin, e.g. to work with the server model store.
 */
public abstract class DolphinServerAction implements ServerAction {

    private DefaultServerDolphin serverDolphin;

    private List<Command> dolphinResponse;

    public void presentationModel(String id, String presentationModelType, DTO dto) {
        DefaultServerDolphin.presentationModelCommand(dolphinResponse, id, presentationModelType, dto);
    }

    public void changeValue(ServerAttribute attribute, String value) {
        DefaultServerDolphin.changeValueCommand(dolphinResponse, attribute, value);
    }

    /**
     * Convenience method for the InitializeAttributeCommand
     */
    public void initAt(String pmId, String propertyName, String qualifier, Object newValue, Tag tag) {
        DefaultServerDolphin.initAt(dolphinResponse, pmId, propertyName, qualifier, newValue, tag);
    }

    /**
     * Convenience method for the InitializeAttributeCommand
     */
    public void initAt(String pmId, String propertyName, String qualifier, Object newValue) {
        initAt(pmId, propertyName, qualifier, newValue, Tag.VALUE);
    }

    /**
     * Convenience method for the InitializeAttributeCommand
     */
    public void initAt(String pmId, String propertyName, String qualifier) {
        initAt(pmId, propertyName, qualifier, null, Tag.VALUE);
    }

    public DefaultServerDolphin getServerDolphin() {
        return serverDolphin;
    }

    public void setServerDolphin(DefaultServerDolphin serverDolphin) {
        this.serverDolphin = serverDolphin;
    }

    public List<Command> getDolphinResponse() {
        return dolphinResponse;
    }

    public void setDolphinResponse(List<Command> dolphinResponse) {
        this.dolphinResponse = dolphinResponse;
    }

}
