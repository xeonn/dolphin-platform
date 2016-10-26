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

import groovy.transform.CompileStatic
import groovy.util.logging.Log
import org.opendolphin.core.Attribute
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.comm.AttributeMetadataChangedCommand
import org.opendolphin.core.comm.CallNamedActionCommand
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.comm.DataCommand
import org.opendolphin.core.comm.DeleteAllPresentationModelsOfTypeCommand
import org.opendolphin.core.comm.DeletePresentationModelCommand
import org.opendolphin.core.comm.InitializeAttributeCommand
import org.opendolphin.core.comm.PresentationModelResetedCommand
import org.opendolphin.core.comm.SavedPresentationModelNotification
import org.opendolphin.core.comm.SwitchPresentationModelCommand
import org.opendolphin.core.comm.ValueChangedCommand

@Log
class ClientResponseHandler {

    private final ClientDolphin clientDolphin;

    boolean strictMode = true;

    ClientResponseHandler(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin
    }

    protected ClientModelStore getClientModelStore() {
        clientDolphin.clientModelStore
    }

    public Object dispatchHandle(Command command) {
        handle(command)
    }

    def handle(Command serverCommand) {
        log.severe "C: cannot handle unknown command '$serverCommand'"
    }

    Map handle(DataCommand serverCommand) {
        return serverCommand.data
    }

    ClientPresentationModel handle(DeletePresentationModelCommand serverCommand) {
        ClientPresentationModel model = clientDolphin.findPresentationModelById(serverCommand.pmId)
        if (!model) return null
        clientModelStore.delete(model)
        return model
    }

    ClientPresentationModel handle(DeleteAllPresentationModelsOfTypeCommand serverCommand) {
        clientDolphin.deleteAllPresentationModelsOfType(serverCommand.pmType)
        return null // we cannot really return a single pm here
    }

    @CompileStatic
    ClientPresentationModel handle(CreatePresentationModelCommand serverCommand) {
        if (clientModelStore.containsPresentationModel(serverCommand.pmId)) {
            throw new IllegalStateException("There already is a presentation model with id '$serverCommand.pmId' known to the client.")
        }
        List<ClientAttribute> attributes = []
        for (attr in serverCommand.attributes) {
            ClientAttribute attribute = new ClientAttribute(
                    attr.propertyName.toString(),
                    attr.value,
                    attr.qualifier?.toString(),
                    attr.tag ? Tag.tagFor[(String) attr.tag] : Tag.VALUE)
            if(attr.id?.toString()?.endsWith('S')) {
                attribute.id = attr.id
            }
            attribute.baseValue = attr.baseValue
            attributes << attribute
        }
        ClientPresentationModel model = new ClientPresentationModel(serverCommand.pmId, attributes)
        model.presentationModelType = serverCommand.pmType
        if (serverCommand.clientSideOnly) {
            model.clientSideOnly = true
        }
        clientModelStore.add(model)
        clientDolphin.updateQualifiers(model)
        return model
    }

    ClientPresentationModel handle(ValueChangedCommand serverCommand) {
        Attribute attribute = clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) {
            log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot update old value '$serverCommand.oldValue' to new value '$serverCommand.newValue'"
            return null
        }
        if (attribute.value?.toString() == serverCommand.newValue?.toString()) {
            return null
        }
        if (strictMode && attribute.value?.toString() != serverCommand.oldValue?.toString()) {
            // todo dk: think about sending a RejectCommand here to tell the server about a possible lost update
            log.warning "C: attribute with id '$serverCommand.attributeId' and value '$attribute.value' cannot be set to new value '$serverCommand.newValue' because the change was based on an outdated old value of '$serverCommand.oldValue'."
            return null
        }
        log.info "C: updating '$attribute.propertyName' id '$serverCommand.attributeId' from '$attribute.value' to '$serverCommand.newValue'"
        attribute.value = serverCommand.newValue
        return null // this command is not expected to be sent explicitly, so no pm needs to be returned
    }

    ClientPresentationModel handle(SwitchPresentationModelCommand serverCommand) {
        def switchPm = clientModelStore.findPresentationModelById(serverCommand.pmId)
        if (!switchPm) {
            log.warning "C: switch pm with id '$serverCommand.pmId' not found, cannot switch"
            return null
        }
        def sourcePm = clientModelStore.findPresentationModelById(serverCommand.sourcePmId)
        if (!sourcePm) {
            log.warning "C: source pm with id '$serverCommand.sourcePmId' not found, cannot switch"
            return null
        }
        switchPm.syncWith sourcePm                  // ==  clientDolphin.apply sourcePm to switchPm
        return (ClientPresentationModel) switchPm
    }

    ClientPresentationModel handle(InitializeAttributeCommand serverCommand) {
        def attribute = new ClientAttribute(serverCommand.propertyName, serverCommand.newValue, serverCommand.qualifier, serverCommand.tag)

        // todo: add check for no-value; null is a valid value
        if (serverCommand.qualifier) {
            def copies = clientModelStore.findAllAttributesByQualifier(serverCommand.qualifier)
            if (copies) {
                if (null == serverCommand.newValue) {
                    attribute.value = copies.first()?.value
                } else {
                    copies.each { attr ->
                        attr.value = attribute.value
                    }
                }
            }
        }
        ClientPresentationModel presentationModel = null
        if (serverCommand.pmId) presentationModel = clientModelStore.findPresentationModelById(serverCommand.pmId)
        // here we could have a pmType conflict and we may want to throw an Exception...
        // if there is no pmId, it is most likely an error and CreatePresentationModelCommand should have been used
        if (!presentationModel) {
            presentationModel = new ClientPresentationModel(serverCommand.pmId, [])
            presentationModel.setPresentationModelType(serverCommand.pmType)
            clientModelStore.add(presentationModel)
        }
        // if we already have the attribute, just update the value
        def existingAtt = presentationModel.getAt(serverCommand.propertyName, serverCommand.tag)
        if (existingAtt) {
            existingAtt.value = attribute.value
        } else {
            clientDolphin.addAttributeToModel(presentationModel, attribute)
        }
        clientDolphin.updateQualifiers(presentationModel)
        return presentationModel // todo dk: check and test
    }

    ClientPresentationModel handle(SavedPresentationModelNotification serverCommand) {
        if (!serverCommand.pmId) return null
        ClientPresentationModel model = clientModelStore.findPresentationModelById(serverCommand.pmId)
        if (null == model) {
            log.warning("model with id '$serverCommand.pmId' not found, cannot rebase")
            return null
        }
        model.attributes*.rebase() // rebase sends update command if needed through PCL
        return model
    }

    ClientPresentationModel handle(PresentationModelResetedCommand serverCommand) {
        if (!serverCommand.pmId) return null
        PresentationModel model = clientModelStore.findPresentationModelById(serverCommand.pmId)
        // reset locally first
        if (!model) return null
        model.attributes*.reset()
        return model
    }

    ClientPresentationModel handle(AttributeMetadataChangedCommand serverCommand) {
        ClientAttribute attribute = clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) return null
        attribute[serverCommand.metadataName] = serverCommand.value
        return null
    }

    ClientPresentationModel handle(CallNamedActionCommand serverCommand) {
        clientDolphin.send(serverCommand.actionName)
        return null
    }
}
