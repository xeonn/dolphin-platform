package org.opendolphin.core.server;

import org.opendolphin.core.BasePresentationModel;

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

    public void addAttribute(ServerAttribute attribute) {
        _internal_addAttribute(attribute);
        modelStore.registerAttribute(attribute);
        DefaultServerDolphin.initAt(modelStore.getCurrentResponse(), getId(), attribute.getPropertyName(), attribute.getQualifier(), attribute.getValue());
    }

    public ServerModelStore getModelStore() {
        return modelStore;
    }
}
