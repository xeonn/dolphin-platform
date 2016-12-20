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
package org.opendolphin.core.server

import org.opendolphin.core.AbstractDolphin
import org.opendolphin.core.Attribute
import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.Tag
import org.opendolphin.core.comm.*
import org.opendolphin.core.server.action.*
import org.opendolphin.core.server.comm.NamedCommandHandler

import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Logger

import static org.opendolphin.StringUtil.isBlank

/**
 * The default implementation of the Dolphin facade on the server side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with server model store and current response.
 * Threading model: confined to a single controller thread.
 */

class DefaultServerDolphin extends AbstractDolphin<ServerAttribute, ServerPresentationModel> implements ServerDolphin {

    private static final Logger LOG = Logger.getLogger(DefaultServerDolphin.class.getName());

    /** the server model store is unique per user session */
    private final ServerModelStore serverModelStore;

    /** the serverConnector is unique per user session */
    private final ServerConnector serverConnector;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    public DefaultServerDolphin(ServerModelStore serverModelStore, ServerConnector serverConnector) {
        this.serverModelStore = serverModelStore;
        this.serverConnector = serverConnector;
        this.serverConnector.serverModelStore = serverModelStore;
    }

    protected DefaultServerDolphin() {
        this(new ServerModelStore(), new ServerConnector());
    }

    @Override
    public ServerModelStore getModelStore() {
        return serverModelStore;
    }

    @Override
    ServerConnector getServerConnector() {
        return serverConnector
    }

    public void registerDefaultActions() {
        if (initialized.getAndSet(true)) {
            LOG.warning("attempt to initialize default actions more than once!");
            return;
        }
        register(new StoreValueChangeAction());
        register(new StoreAttributeAction());
        register(new CreatePresentationModelAction());
        register(new DeletePresentationModelAction());
        register(new DeletedAllPresentationModelsOfTypeAction());
        serverConnector.register(new EmptyAction());
    }

    public void register(DolphinServerAction action) {
        action.setServerDolphin(this);
        serverConnector.register(action);
    }

    /** groovy-friendly convenience method to register a named action */
    public void action(String name, Closure logic) {
        ClosureServerAction serverAction = new ClosureServerAction(name, logic);
        register(serverAction);
    }

    /** java-friendly convenience method to register a named action */
    public void action(String name, NamedCommandHandler namedCommandHandler) {
        NamedServerAction serverAction = new NamedServerAction(name, namedCommandHandler);
        register(serverAction);
    }

    /**
     * Adding the model to the model store and if successful, sending the CreatePresentationModelCommand.
     * @param model the model to be added.
     * @return whether the adding was successful, which implies that also the command has been sent
     */
    @Override
    public boolean add(ServerPresentationModel model) {
        boolean result = super.add(model);
        if (result) {
            serverModelStore.getCurrentResponse().add(CreatePresentationModelCommand.makeFrom(model));
        }
        return result;
    }

    /**
     * Create a presentation model on the server side, add it to the model store, and send a command to
     * the client, advising him to do the same.
     * @throws IllegalArgumentException if a presentation model for this id already exists. No commands are sent in this case.
     */
    public ServerPresentationModel presentationModel(String id, String presentationModelType, DTO dto) {
        List<ServerAttribute> attributes = new ArrayList<>();
        for(Slot slot : dto.getSlots()) {
            ServerAttribute result = new ServerAttribute(slot.getPropertyName(), slot.getBaseValue(), slot.getQualifier(), slot.getTag());
            result.silently(new Runnable() {
                @Override
                void run() {
                    result.setValue(slot.getValue());
                }
            });
            attributes.add(result);
        }
        ServerPresentationModel model = new ServerPresentationModel(id, attributes, serverModelStore);
        model.setPresentationModelType(presentationModelType);
        add(model);
        return model;
    }

    /** @deprecated use {@link #presentationModelCommand(java.util.List, java.lang.String, java.lang.String, org.opendolphin.core.server.DTO)}. You can use the "inline method refactoring". Will be removed in version 1.0!            */
    public static void presentationModel(List<Command> response, String id, String presentationModelType, DTO dto) {
        presentationModelCommand(response, id, presentationModelType, dto);
    }

    /** Convenience method to let the client (!) dolphin create a presentation model as specified by the DTO.
     * The server model store remains untouched until the client has issued the notification.*/
    public
    static void presentationModelCommand(List<Command> response, String id, String presentationModelType, DTO dto) {
        if (response == null) {return;}
        response.add(new CreatePresentationModelCommand(id, presentationModelType, dto.encodable()));
    }

    /** @deprecated use {@link #clientSideModelCommand(java.util.List, java.lang.String, java.lang.String, org.opendolphin.core.server.DTO)}. You can use the "inline method refactoring". Will be removed in version 1.0!            */
    public static void clientSideModel(List<Command> response, String id, String presentationModelType, DTO dto) {
        clientSideModelCommand(response, id, presentationModelType, dto);
    }

    /** Convenience method to let Dolphin create a
     *    <strong> client-side only </strong>
     *  presentation model as specified by the DTO. */
    public
    static void clientSideModelCommand(List<Command> response, String id, String presentationModelType, DTO dto) {
        if (response == null) {
            return;
        }
        response.add(new CreatePresentationModelCommand(id, presentationModelType, dto.encodable(), true));
    }

    /** @deprecated use {@link #rebaseCommand(java.util.List, org.opendolphin.core.server.ServerAttribute)}. You can use the "inline method refactoring". Will be removed in version 1.0!            */
    public static void rebase(List<Command> response, ServerAttribute attribute) {
        rebaseCommand(response, attribute);
    }

    /** Convenience method to let Dolphin rebase the value of an attribute */
    public static void rebaseCommand(List<Command> response, ServerAttribute attribute) {
        if (attribute == null) {
            LOG.severe("Cannot rebase null attribute");
            return;
        }
        response.add(new AttributeMetadataChangedCommand(attribute.getId(), Attribute.BASE_VALUE, attribute.getValue()));
    }

    /** @deprecated use attribute.rebase(). Will be removed in version 1.0!            */
    public static void rebase(List<Command> response, String attributeId) {
        rebaseCommand(response, attributeId);
    }

    /** @deprecated use attribute.rebase(). Will be removed in version 1.0!            */
    public static void rebaseCommand(List<Command> response, String attributeId) {
        throw new UnsupportedOperationException("Direct use of rebaseCommand is no longer supported. Use attribute.rebase()");
    }

    /** Convenience method to let Dolphin remove a presentation model directly on the server and notify the client.*/
    public boolean remove(ServerPresentationModel pm) {
        boolean deleted = serverModelStore.remove(pm);
        if (deleted) {
            DefaultServerDolphin.deleteCommand(serverModelStore.getCurrentResponse(), pm);
        }
        return deleted;
    }

    /** Convenience method to let Dolphin remove all presentation models of a given type directly on the server and notify the client.*/
    public void removeAllPresentationModelsOfType(String type) {
        // todo: [REF] duplicated with DeleteAllPresentationModelsOfTypeAction, could go into ModelStore
        List<ServerPresentationModel> models = new LinkedList(findAllPresentationModelsByType(type)); // work on a copy
        for (ServerPresentationModel model in models) {
            serverModelStore.remove(model);
            // go through the model store to avoid single commands being sent to the client
        }
        DefaultServerDolphin.deleteAllPresentationModelsOfTypeCommand(serverModelStore.getCurrentResponse(), type);
    }

    /** @deprecated use {@link #deleteCommand(java.util.List, org.opendolphin.core.server.ServerPresentationModel)}. You can use the "inline method refactoring". Will be removed in version 1.0!            */
    public static void delete(List<Command> response, ServerPresentationModel pm) {
        deleteCommand(response, pm);
    }

    /** Convenience method to let Dolphin delete a presentation model on the client side */
    public static void deleteCommand(List<Command> response, ServerPresentationModel pm) {
        if (pm == null) {
            LOG.severe("Cannot delete null presentation model");
            return;
        }
        deleteCommand(response, pm.id);
    }

    /** @deprecated use {@link #deleteCommand(java.util.List, java.lang.String)}. You can use the "inline method refactoring". Will be removed in version 1.0!            */
    public static void delete(List<Command> response, String pmId) {
        deleteCommand(response, pmId);
    }

    /** Convenience method to let Dolphin delete a presentation model on the client side */
    public static void deleteCommand(List<Command> response, String pmId) {
        if (response == null || isBlank(pmId)) {
            return;
        }
        response.add(new DeletePresentationModelCommand(pmId));
    }

    /** @deprecated use {@link #deleteAllPresentationModelsOfTypeCommand(java.util.List, java.lang.String)}. You can use the "inline method refactoring". Will be removed in version 1.0!            */
    public static void deleteAllPresentationModelsOfType(List<Command> response, String pmType) {
        deleteAllPresentationModelsOfTypeCommand(response, pmType);
    }

    /** Convenience method to let Dolphin delete all presentation models of a given type on the client side */
    public static void deleteAllPresentationModelsOfTypeCommand(List<Command> response, String pmType) {
        if (response == null || isBlank(pmType)) {
            return;
        }
        response.add(new DeleteAllPresentationModelsOfTypeCommand(pmType));
    }

    /** @deprecated use {@link #resetCommand(java.util.List, org.opendolphin.core.server.ServerPresentationModel)}. You can use the "inline method refactoring". Will be removed in version 1.0!            */
    public static void reset(List<Command> response, ServerPresentationModel pm) {
        resetCommand(response, pm);
    }

    /** Convenience method to let Dolphin reset a presentation model */
    public static void resetCommand(List<Command> response, ServerPresentationModel pm) {
        if (pm == null) {
            LOG.severe("Cannot reset null presentation model");
            return;
        }
        resetCommand(response, pm.getId());
    }

    /** @deprecated use {@link #resetCommand(java.util.List, java.lang.String)}. You can use the "inline method refactoring". Will be removed in version 1.0!            */
    public static void reset(List<Command> response, String pmId) {
        resetCommand(response, pmId);
    }

    /** Convenience method to let Dolphin reset a presentation model */
    public static void resetCommand(List<Command> response, String pmId) {
        if (response == null || isBlank(pmId)) {
            return;
        }
        response.add(new PresentationModelResetedCommand(pmId));
    }

    /** @deprecated use {@link #resetCommand(java.util.List, org.opendolphin.core.server.ServerAttribute)}. You can use the "inline method refactoring". Will be removed in version 1.0!            */
    public static void reset(List<Command> response, ServerAttribute attribute) {
        resetCommand(response, attribute);
    }

    /** Convenience method to let Dolphin reset the value of an attribute */
    public static void resetCommand(List<Command> response, ServerAttribute attribute) {
        if (response == null || attribute == null) {
            LOG.severe("Cannot reset null attribute")
            return;
        }
        response.add(new ValueChangedCommand(attribute.getId(), attribute.getValue(), attribute.getBaseValue()));
    }

    /** @deprecated use {@link #changeValueCommand(java.util.List, org.opendolphin.core.server.ServerAttribute, java.lang.Object)}. You can use the "inline method refactoring". Will be removed in version 1.0!            */
    public static void changeValue(List<Command> response, ServerAttribute attribute, value) {
        changeValueCommand(response, attribute, value);
    }

    /**
     * Convenience method to change an attribute value on the server side.
     * @param response must not be null or the method silently ignores the call
     * @param attribute must not be null
     */
    public static void changeValueCommand(List<Command> response, ServerAttribute attribute, value) {
        if (response == null) {
            return;
        }
        if (attribute == null) {
            LOG.severe("Cannot change value on a null attribute to '" + value);
            return;
        }
        forceChangeValue(value, response, attribute);
    }

    /** @deprecated use {@link #forceChangeValueCommand(java.lang.Object, java.util.List, org.opendolphin.core.server.ServerAttribute)}. You can use the "inline method refactoring". Will be removed in version 1.0!            */
    public static void forceChangeValue(value, List<Command> response, ServerAttribute attribute) {
        forceChangeValueCommand(value, response, attribute);
    }

    /** @deprecated use {@link #changeValueCommand(java.util.List, org.opendolphin.core.server.ServerAttribute, java.lang.Object)}, which enforces the value change by default. Will be removed in version 1.0!            */
    public static void forceChangeValueCommand(Object value, List<Command> response, ServerAttribute attribute) {
        value = BaseAttribute.checkValue(value);
        response.add(new ValueChangedCommand(attribute.getId(), value, attribute.getValue()));
    }

    public
    static void initAt(List<Command> response, String pmId, String propertyName, String qualifier) {
        initAt(response, pmId, propertyName, qualifier, null, Tag.VALUE)
    }

    public
    static void initAt(List<Command> response, String pmId, String propertyName, String qualifier, Object newValue) {
        initAt(response, pmId, propertyName, qualifier, newValue, Tag.VALUE)
    }

    /** Convenience method for the InitializeAttributeCommand */
    public
    static void initAt(List<Command> response, String pmId, String propertyName, String qualifier, Tag tag) {
        initAt(response, pmId, propertyName, qualifier, null, tag)
    }

    /** Convenience method for the InitializeAttributeCommand */
    public
    static void initAt(List<Command> response, String pmId, String propertyName, String qualifier, Object newValue, Tag tag) {
        initAtCommand(response, pmId, propertyName, qualifier, newValue, tag)
    }

    public static void initAtCommand(List<Command> response, String pmId, String propertyName, String qualifier) {
        initAtCommand(response, pmId, propertyName, qualifier, null, Tag.VALUE);
    }

    public
    static void initAtCommand(List<Command> response, String pmId, String propertyName, String qualifier, Object newValue) {
        initAtCommand(response, pmId, propertyName, qualifier, newValue, Tag.VALUE);
    }

    public
    static void initAtCommand(List<Command> response, String pmId, String propertyName, String qualifier, Tag tag) {
        initAtCommand(response, pmId, propertyName, qualifier, null, tag);
    }

    /** Convenience method for the InitializeAttributeCommand */
    public
    static void initAtCommand(List<Command> response, String pmId, String propertyName, String qualifier, Object newValue, Tag tag) {
        if (null == response) {
            return;
        }
        response.add(new InitializeAttributeCommand(pmId, propertyName, qualifier, newValue, tag));
    }

    /** The id of the server dolphin, which is identical to the id of its server model store. */
    public int getId() {
        return serverModelStore.getId();
    }

    ServerModelStore getServerModelStore() {
        return serverModelStore
    }
}
