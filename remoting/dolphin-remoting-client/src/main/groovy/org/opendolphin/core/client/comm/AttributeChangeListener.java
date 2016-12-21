package org.opendolphin.core.client.comm;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.comm.ChangeAttributeMetadataCommand;
import org.opendolphin.core.comm.ValueChangedCommand;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class AttributeChangeListener implements PropertyChangeListener {

    private ClientModelStore clientModelStore;

    private ClientConnector clientConnector;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Attribute.VALUE)) {
            if (evt.getOldValue() == null && evt.getNewValue() == null || evt.getOldValue() != null && evt.getNewValue() != null && evt.getOldValue().equals(evt.getNewValue())) {
                return;
            }

            if (isSendable(evt)) {
                clientConnector.send(constructValueChangedCommand(evt));
            }

            List<ClientAttribute> attributes = clientModelStore.findAllAttributesByQualifier(((Attribute) evt.getSource()).getQualifier());
            for (ClientAttribute attribute : attributes) {
                attribute.setValue(evt.getNewValue());
            }

        } else {
            // we assume the change is on a metadata property such as qualifier
            if (isSendable(evt)) {
                clientConnector.send(constructChangeAttributeMetadataCommand(evt));
            }
        }
    }

    private boolean isSendable(PropertyChangeEvent evt) {
        PresentationModel pmOfAttribute = ((Attribute) evt.getSource()).getPresentationModel();
        if (pmOfAttribute == null) {
            return true;
        }

        if (pmOfAttribute instanceof ClientPresentationModel && ((ClientPresentationModel) pmOfAttribute).isClientSideOnly()) {
            return false;
        }

        return true;
    }

    private ValueChangedCommand constructValueChangedCommand(PropertyChangeEvent evt) {
        return new ValueChangedCommand(((Attribute) evt.getSource()).getId(), evt.getOldValue(), evt.getNewValue());
    }

    private ChangeAttributeMetadataCommand constructChangeAttributeMetadataCommand(PropertyChangeEvent evt) {
        return new ChangeAttributeMetadataCommand(((Attribute) evt.getSource()).getId(), evt.getPropertyName(), evt.getNewValue());
    }

    public void setClientModelStore(ClientModelStore clientModelStore) {
        this.clientModelStore = clientModelStore;
    }

    public ClientConnector getClientConnector() {
        return clientConnector;
    }

    public void setClientConnector(ClientConnector clientConnector) {
        this.clientConnector = clientConnector;
    }
}
