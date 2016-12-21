/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opendolphin.core.client.comm

import org.opendolphin.core.Attribute
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.comm.*

import java.util.logging.Logger

class ClientResponseHandler {

    private static final Logger LOG = Logger.getLogger(ClientResponseHandler.class.getName());

    private final ClientDolphin clientDolphin;

    private boolean strictMode = true;

    ClientResponseHandler(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin
    }

    protected ClientModelStore getClientModelStore() {
        clientDolphin.clientModelStore
    }

    public Object dispatchHandle(Command command) {
        if (command instanceof DataCommand) {
            return handleDataCommand(command);
        } else if (command instanceof DeletePresentationModelCommand) {
            return handleDeletePresentationModelCommand(command);
        } else if (command instanceof CreatePresentationModelCommand) {
            return handleCreatePresentationModelCommand(command);
        } else if (command instanceof ValueChangedCommand) {
            return handleValueChangedCommand(command);
        } else if (command instanceof InitializeAttributeCommand) {
            return handleInitializeAttributeCommand(command);
        } else if (command instanceof AttributeMetadataChangedCommand) {
            return handleAttributeMetadataChangedCommand(command);
        } else if (command instanceof CallNamedActionCommand) {
            return handleCallNamedActionCommand(command);
        } else {
            handleUnknownCommand(command);
            return null;
        }
    }

    private void handleUnknownCommand(Command serverCommand) {
        LOG.severe("C: cannot handle unknown command '" + serverCommand + "'");
    }

    private Map handleDataCommand(DataCommand serverCommand) {
        return serverCommand.getData();
    }

    private ClientPresentationModel handleDeletePresentationModelCommand(DeletePresentationModelCommand serverCommand) {
        ClientPresentationModel model = clientDolphin.getPresentationModel(serverCommand.getPmId())
        if (model == null) {
            return null;
        }
        clientModelStore.delete(model);
        return model;
    }

    private ClientPresentationModel handleCreatePresentationModelCommand(CreatePresentationModelCommand serverCommand) {
        if (clientModelStore.containsPresentationModel(serverCommand.getPmId())) {
            throw new IllegalStateException("There already is a presentation model with id '" + serverCommand.pmId + "' known to the client.");
        }
        List<ClientAttribute> attributes = new ArrayList<>();
        for (Map<String, Object> attr in serverCommand.getAttributes()) {

            Object propertyName = attr.get("propertyName");
            Object value = attr.get("value");
            Object qualifier = attr.get("qualifier");
            Object id = attr.get("id");


            ClientAttribute attribute = new ClientAttribute(propertyName, value, qualifier);
            if (id != null && id.toString().endsWith("S")) {
                attribute.setId(id.toString());
            }

            attributes.add(attribute);
        }
        ClientPresentationModel model = new ClientPresentationModel(serverCommand.getPmId(), attributes)
        model.setPresentationModelType(serverCommand.getPmType());
        if (serverCommand.isClientSideOnly()) {
            model.setClientSideOnly(true);
        }
        clientModelStore.add(model)
        clientDolphin.updateQualifiers(model)
        return model;
    }

    private ClientPresentationModel handleValueChangedCommand(ValueChangedCommand serverCommand) {
        Attribute attribute = clientModelStore.findAttributeById(serverCommand.getAttributeId());
        if (attribute == null) {
            LOG.warning("C: attribute with id '" + serverCommand.getAttributeId() + "' not found, cannot update old value '" + serverCommand.getOldValue() + "' to new value '" + serverCommand.getNewValue() + "'");
            return null;
        }
        if (attribute.getValue() == null && serverCommand.getNewValue() == null || (attribute.getValue() != null && serverCommand.getNewValue() != null && attribute.getValue().equals(serverCommand.getNewValue()))) {
            return null;
        }

        if (strictMode && ((attribute.getValue() == null && serverCommand.getOldValue() != null) || (attribute.getValue() != null && serverCommand.getOldValue() == null) || (attribute.getValue() != null && !attribute.getValue().equals(serverCommand.getOldValue())))) {
            // todo dk: think about sending a RejectCommand here to tell the server about a possible lost update
            LOG.warning("C: attribute with id '" + serverCommand.getAttributeId() + "' and value '" + attribute.getValue() + "' cannot be set to new value '" + serverCommand.getNewValue() + "' because the change was based on an outdated old value of '" + serverCommand.getOldValue() + "'.");
            return null;
        }
        LOG.info("C: updating '" + attribute.getPropertyName() + "' id '" + serverCommand.getAttributeId() + "' from '" + attribute.getValue() + "' to '" + serverCommand.getNewValue() + "'");
        attribute.setValue(serverCommand.getNewValue());
        return null; // this command is not expected to be sent explicitly, so no pm needs to be returned
    }

    private ClientPresentationModel handleInitializeAttributeCommand(InitializeAttributeCommand serverCommand) {
        ClientAttribute attribute = new ClientAttribute(serverCommand.getPropertyName(), serverCommand.getNewValue(), serverCommand.getQualifier());

        // todo: add check for no-value; null is a valid value
        if (serverCommand.getQualifier() != null) {
            List<ClientAttribute> copies = clientModelStore.findAllAttributesByQualifier(serverCommand.getQualifier());
            if (copies != null && !copies.isEmpty()) {
                if (serverCommand.getNewValue() == null) {
                    attribute.setValue(copies.get(0).getValue());
                } else {
                    for (ClientAttribute attr : copies) {
                        attr.setValue(attribute.getValue());
                    }
                }
            }
        }
        ClientPresentationModel presentationModel = null;
        if (serverCommand.getPmId() != null) {
            presentationModel = clientModelStore.findPresentationModelById(serverCommand.getPmId());
        }
        // here we could have a pmType conflict and we may want to throw an Exception...
        // if there is no pmId, it is most likely an error and CreatePresentationModelCommand should have been used
        if (presentationModel == null) {
            presentationModel = new ClientPresentationModel(serverCommand.pmId, Collections.emptyList());
            presentationModel.setPresentationModelType(serverCommand.getPmType());
            clientModelStore.add(presentationModel);
        }
        // if we already have the attribute, just update the value
        Attribute existingAtt = presentationModel.getAttribute(serverCommand.getPropertyName());
        if (existingAtt != null) {
            existingAtt.setValue(attribute.getValue());
        } else {
            clientDolphin.addAttributeToModel(presentationModel, attribute);
        }
        clientDolphin.updateQualifiers(presentationModel);
        return presentationModel; // todo dk: check and test
    }

    private ClientPresentationModel handleAttributeMetadataChangedCommand(AttributeMetadataChangedCommand serverCommand) {
        ClientAttribute attribute = clientModelStore.findAttributeById(serverCommand.getAttributeId());
        if (!attribute) {
            return null;
        }
        if(serverCommand.getMetadataName() != null && serverCommand.getMetadataName().equals(Attribute.VALUE)) {
            attribute.setValue(serverCommand.getValue());
        }
        if(serverCommand.getMetadataName() != null && serverCommand.getMetadataName().equals(Attribute.QUALIFIER_PROPERTY)) {
            attribute.setQualifier(serverCommand.getValue());
        }
        return null;
    }

    private ClientPresentationModel handleCallNamedActionCommand(CallNamedActionCommand serverCommand) {
        clientDolphin.send(serverCommand.getActionName());
        return null;
    }

    boolean getStrictMode() {
        return strictMode
    }

    void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode
    }
}
