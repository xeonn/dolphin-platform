package org.opendolphin.core.server;

import org.opendolphin.core.BasePresentationModel;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.SwitchPresentationModelCommand;

import java.util.List;
import java.util.logging.Logger;

public class ServerPresentationModel extends BasePresentationModel<ServerAttribute> {

    private static final Logger LOG = Logger.getLogger(ServerPresentationModel.class.getName());

    public static final String AUTO_ID_SUFFIX = "-AUTO-SRV";

    public ServerModelStore modelStore;

    /**
     * @param id if id is null or empty, an auto-generated id will be used
     */
    public ServerPresentationModel(String id, List<ServerAttribute> attributes, ServerModelStore serverModelStore) {
        super((id != null && id.length() > 0) ? id : makeId(serverModelStore), attributes);
        if (id != null && id.endsWith(AUTO_ID_SUFFIX)) {
            LOG.info("Creating a PM with self-provided id \'" + id + "\' even though it ends with a reserved suffix.");
        }

        modelStore = serverModelStore;
    }

    public static String makeId(ServerModelStore serverModelStore) {
        long newId = serverModelStore.pmInstanceCount++;
        return String.valueOf(newId) + AUTO_ID_SUFFIX;
    }

    @Override
    public void syncWith(PresentationModel sourcePresentationModel) {
        super.syncWith(sourcePresentationModel);// this may already trigger some value changes and metadata changes
        modelStore.getCurrentResponse().add(new SwitchPresentationModelCommand(getId(), sourcePresentationModel.getId()));
    }

    public void addAttribute(ServerAttribute attribute) {
        _internal_addAttribute(attribute);
        modelStore.registerAttribute(attribute);
        DefaultServerDolphin.initAt(modelStore.getCurrentResponse(), getId(), attribute.getPropertyName(), attribute.getQualifier(), attribute.getValue(), attribute.getTag());
    }

    public void rebase() {
        for (ServerAttribute attr : getAttributes()) {
            attr.rebase();
        }

    }

    public ServerModelStore getModelStore() {
        return modelStore;
    }
}
