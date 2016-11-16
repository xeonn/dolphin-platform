package org.opendolphin.core.client;

import org.opendolphin.core.BasePresentationModel;

import java.util.List;

public final class ClientPresentationModel extends BasePresentationModel<ClientAttribute> {
    public ClientPresentationModel(List<ClientAttribute> attributes) {
        this(null, attributes);
    }

    /**
     * @param id if id is null or empty, an auto-generated id will be used
     */
    public ClientPresentationModel(String id, List<ClientAttribute> attributes) {
        super(createUniqueId(id), attributes);
        if (id != null && id.endsWith(AUTO_ID_SUFFIX)) {
            throw new IllegalArgumentException("presentation model with self-provided id \'" + id + "\' may not end with suffix \'" + AUTO_ID_SUFFIX + "\' since that is reserved.");
        }
    }

    private static String createUniqueId(String id) {
        return (id != null && id.length() > 0) ? id : "" + instanceCount++ + AUTO_ID_SUFFIX;
    }

    public boolean getClientSideOnly() {
        return clientSideOnly;
    }

    public boolean isClientSideOnly() {
        return clientSideOnly;
    }

    public void setClientSideOnly(boolean clientSideOnly) {
        this.clientSideOnly = clientSideOnly;
    }

    public static final String AUTO_ID_SUFFIX = "-AUTO-CLT";
    private boolean clientSideOnly = false;
    private static long instanceCount = 0;
}
