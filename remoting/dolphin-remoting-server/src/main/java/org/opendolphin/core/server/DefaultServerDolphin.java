package org.opendolphin.core.server;

import org.opendolphin.StringUtil;
import org.opendolphin.core.AbstractDolphin;
import org.opendolphin.core.BaseAttribute;
import org.opendolphin.core.Tag;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.comm.DeletePresentationModelCommand;
import org.opendolphin.core.comm.InitializeAttributeCommand;
import org.opendolphin.core.comm.ValueChangedCommand;
import org.opendolphin.core.server.action.CreatePresentationModelAction;
import org.opendolphin.core.server.action.DeletePresentationModelAction;
import org.opendolphin.core.server.action.DeletedAllPresentationModelsOfTypeAction;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.action.EmptyAction;
import org.opendolphin.core.server.action.StoreAttributeAction;
import org.opendolphin.core.server.action.StoreValueChangeAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * The default implementation of the Dolphin facade on the server side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with server model store and current response.
 * Threading model: confined to a single controller thread.
 */
public class DefaultServerDolphin extends AbstractDolphin<ServerAttribute, ServerPresentationModel> implements ServerDolphin {

    private static final Logger LOG = Logger.getLogger(DefaultServerDolphin.class.getName());

    /**
     * the server model store is unique per user session
     */
    private final ServerModelStore serverModelStore;

    /**
     * the serverConnector is unique per user session
     */
    private final ServerConnector serverConnector;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    public DefaultServerDolphin(ServerModelStore serverModelStore, ServerConnector serverConnector) {
        this.serverModelStore = serverModelStore;
        this.serverConnector = serverConnector;
        this.serverConnector.setServerModelStore(serverModelStore);
    }

    protected DefaultServerDolphin() {
        this(new ServerModelStore(), new ServerConnector());
    }

    @Override
    public ServerModelStore getModelStore() {
        return serverModelStore;
    }

    @Override
    public ServerConnector getServerConnector() {
        return serverConnector;
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

    /**
     * Adding the model to the model store and if successful, sending the CreatePresentationModelCommand.
     *
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
     *
     * @throws IllegalArgumentException if a presentation model for this id already exists. No commands are sent in this case.
     */
    public ServerPresentationModel presentationModel(String id, String presentationModelType, DTO dto) {
        List<ServerAttribute> attributes = new ArrayList<ServerAttribute>();
        for (final Slot slot : dto.getSlots()) {
            final ServerAttribute result = new ServerAttribute(slot.getPropertyName(), slot.getBaseValue(), slot.getQualifier(), slot.getTag());
            result.silently(new Runnable() {
                @Override
                public void run() {
                    result.setValue(slot.getValue());
                }

            });
            ((ArrayList<ServerAttribute>) attributes).add(result);
        }
        ServerPresentationModel model = new ServerPresentationModel(id, attributes, serverModelStore);
        model.setPresentationModelType(presentationModelType);
        add(model);
        return model;
    }

    /**
     * Convenience method to let the client (!) dolphin create a presentation model as specified by the DTO.
     * The server model store remains untouched until the client has issued the notification.
     */
    public static void presentationModelCommand(List<Command> response, String id, String presentationModelType, DTO dto) {
        if (response == null) {
            return;
        }
        response.add(new CreatePresentationModelCommand(id, presentationModelType, dto.encodable()));
    }

    /**
     * Convenience method to let Dolphin remove a presentation model directly on the server and notify the client.
     */
    public boolean remove(ServerPresentationModel pm) {
        boolean deleted = serverModelStore.remove(pm);
        if (deleted) {
            DefaultServerDolphin.deleteCommand(serverModelStore.getCurrentResponse(), pm);
        }
        return deleted;
    }

    /**
     * Convenience method to let Dolphin delete a presentation model on the client side
     */
    public static void deleteCommand(List<Command> response, ServerPresentationModel pm) {
        if (pm == null) {
            LOG.severe("Cannot delete null presentation model");
            return;
        }
        deleteCommand(response, pm.getId());
    }

    /**
     * Convenience method to let Dolphin delete a presentation model on the client side
     */
    public static void deleteCommand(List<Command> response, String pmId) {
        if (response == null || StringUtil.isBlank(pmId)) {
            return;
        }
        response.add(new DeletePresentationModelCommand(pmId));
    }

    /**
     * Convenience method to let Dolphin reset the value of an attribute
     */
    public static void resetCommand(List<Command> response, ServerAttribute attribute) {
        if (response == null || attribute == null) {
            LOG.severe("Cannot reset null attribute");
            return;
        }
        response.add(new ValueChangedCommand(attribute.getId(), attribute.getValue(), attribute.getBaseValue()));
    }

    /**
     * Convenience method to change an attribute value on the server side.
     *
     * @param response  must not be null or the method silently ignores the call
     * @param attribute must not be null
     */
    public static void changeValueCommand(List<Command> response, ServerAttribute attribute, Object value) {
        if (response == null) {
            return;
        }
        if (attribute == null) {
            LOG.severe("Cannot change value on a null attribute to '" + value);
            return;
        }
        forceChangeValue(value, response, attribute);
    }

    /**
     * @deprecated use {@link #forceChangeValueCommand(Object, List, ServerAttribute)}. You can use the "inline method refactoring". Will be removed in version 1.0!
     */
    public static void forceChangeValue(Object value, List<Command> response, ServerAttribute attribute) {
        forceChangeValueCommand(value, response, attribute);
    }

    /**
     * @deprecated use {@link #changeValueCommand(List, ServerAttribute, Object)}, which enforces the value change by default. Will be removed in version 1.0!
     */
    public static void forceChangeValueCommand(Object value, List<Command> response, ServerAttribute attribute) {
        response.add(new ValueChangedCommand(attribute.getId(), attribute.getValue(), BaseAttribute.checkValue(value)));
    }

    /**
     * Convenience method for the InitializeAttributeCommand
     */
    public static void initAt(List<Command> response, String pmId, String propertyName, String qualifier, Object newValue, Tag tag) {
        initAtCommand(response, pmId, propertyName, qualifier, newValue, tag);
    }

    /**
     * Convenience method for the InitializeAttributeCommand
     */
    public static void initAtCommand(List<Command> response, String pmId, String propertyName, String qualifier, Object newValue, Tag tag) {
        if (null == response) {
            return;
        }
        response.add(new InitializeAttributeCommand(pmId, propertyName, qualifier, newValue, tag));
    }

    public ServerModelStore getServerModelStore() {
        return serverModelStore;
    }
}
