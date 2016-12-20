package org.opendolphin.core.client;

import groovy.lang.Closure;
import org.opendolphin.core.AbstractDolphin;
import org.opendolphin.core.ModelStore;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.comm.ClientConnector;
import org.opendolphin.core.client.comm.OnFinishedDataAdapter;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter;
import org.opendolphin.core.comm.AttributeCreatedNotification;
import org.opendolphin.core.comm.EmptyNotification;
import org.opendolphin.core.comm.NamedCommand;
import org.opendolphin.core.comm.SignalCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The main Dolphin facade on the client side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with client model store and client connector.
 * Threading model: confined to the UI handling thread.
 */
public class ClientDolphin extends AbstractDolphin<ClientAttribute, ClientPresentationModel> {


    private ClientModelStore clientModelStore;

    private ClientConnector clientConnector;

    @Override
    public ModelStore getModelStore() {
        return clientModelStore;
    }

    /**
     * Convenience method for a creating a ClientPresentationModel with initial null values for the attributes
     */
    public ClientPresentationModel presentationModel(String id, List<String> attributeNames) {
        List<ClientAttribute> attributes = new ArrayList<ClientAttribute>();
        for (String name : attributeNames) {
            ((ArrayList<ClientAttribute>) attributes).add(new ClientAttribute(name));
        }

        ClientPresentationModel result = new ClientPresentationModel(id, attributes);
        clientModelStore.add(result);
        return result;
    }

    public ClientPresentationModel presentationModel(String id, Map<String, Object> attributeNamesAndValues) {
        return presentationModel(id, null, attributeNamesAndValues);
    }

    /**
     * groovy-friendly convenience method for a typical case of creating a ClientPresentationModel with initial values
     */
    public ClientPresentationModel presentationModel(String id, String presentationModelType, Map<String, Object> attributeNamesAndValues) {
        return presentationModel(attributeNamesAndValues, id, presentationModelType);
    }

    public ClientPresentationModel presentationModel(Map<String, Object> attributeNamesAndValues, String id) {
        return presentationModel(attributeNamesAndValues, id, null);
    }

    /**
     * groovy-friendly convenience method for a typical case of creating a ClientPresentationModel with initial values
     */
    public ClientPresentationModel presentationModel(Map<String, Object> attributeNamesAndValues, String id, String presentationModelType) {
        List<ClientAttribute> attributes = new ArrayList<ClientAttribute>();
        for (Map.Entry<String, Object> entry : attributeNamesAndValues.entrySet()) {
            ((ArrayList<ClientAttribute>) attributes).add(new ClientAttribute(entry.getKey(), entry.getValue()));
        }

        ClientPresentationModel result = new ClientPresentationModel(id, attributes);
        result.setPresentationModelType(presentationModelType);
        clientModelStore.add(result);
        return result;
    }

    public ClientPresentationModel presentationModel(String id, ClientAttribute... attributes) {
        return presentationModel(id, null, attributes);
    }

    /**
     * both groovy- and java-friendly full-control constructor
     */
    public ClientPresentationModel presentationModel(String id, String presentationModelType, ClientAttribute... attributes) {
        ClientPresentationModel result = new ClientPresentationModel(id, Arrays.asList(attributes));
        result.setPresentationModelType(presentationModelType);
        clientModelStore.add(result);
        return result;
    }

    /**
     * java-friendly convenience method for sending a named command
     */
    public void send(String commandName, OnFinishedHandler onFinished) {
        clientConnector.send(new NamedCommand(commandName), onFinished);
    }

    public void send(String commandName) {
        clientConnector.send(new NamedCommand(commandName), null);
    }

    /**
     * groovy-friendly convenience method for sending a named command that expects only PM responses
     */
    public void send(String commandName, final Closure onFinished) {
        clientConnector.send(new NamedCommand(commandName), new OnFinishedHandlerAdapter() {
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                onFinished.call(presentationModels);
            }

        });
    }

    /**
     * both java- and groovy-friendly convenience method to send an empty command, which will have no
     * presentation models nor data in the callback
     */
    public void sync(final Runnable runnable) {
        clientConnector.send(new EmptyNotification(), new OnFinishedHandlerAdapter() {
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                runnable.run();
            }

        });
    }

    /**
     * groovy-friendly convenience method for sending a named command that expects only data responses
     */
    public void data(String commandName, Closure onFinished) {
        clientConnector.send(new NamedCommand(commandName), OnFinishedDataAdapter.withAction(onFinished));
    }

    /**
     * start of a fluent api: apply source to target. Use for selection changes in master-detail views.
     */
    public ApplyToAble apply(ClientPresentationModel source) {
        return new ApplyToAble(this, source);
    }

    /**
     * Removes the modelToDelete from the client model store,
     * detaches all model store listeners,
     * and notifies the server if successful
     */
    public void delete(ClientPresentationModel modelToDelete) {
        clientModelStore.delete(modelToDelete);
    }

    /**
     * Removes the models of a given type from the model store just like delete()
     * but sends only one notification to the server
     */
    public void deleteAllPresentationModelsOfType(String presentationModelType) {
        clientModelStore.deleteAllPresentationModelsOfType(presentationModelType);
    }

    /**
     * Tags the attribute by
     * adding a new attribute with the given tag and value to the model store
     * inside the given presentation model and for the given property name.
     *
     * @return the created ClientAttribute that carries the tag value
     */
    public ClientAttribute tag(ClientPresentationModel model, String propertyName, Tag tag, Object value) {
        ClientAttribute attribute = new ClientAttribute(propertyName, value, null, tag);
        addAttributeToModel(model, attribute);
        return attribute;
    }

    /**
     * Adds the supplied attribute to the model store for the specified presentation model.
     *
     * @param presentationModel
     * @param attribute
     */
    public void addAttributeToModel(ClientPresentationModel presentationModel, ClientAttribute attribute) {
        presentationModel._internal_addAttribute(attribute);
        clientModelStore.registerAttribute(attribute);
        if (!presentationModel.isClientSideOnly()) {
            clientConnector.send(new AttributeCreatedNotification(presentationModel.getId(), attribute.getId(), attribute.getPropertyName(), attribute.getValue(), attribute.getQualifier(), attribute.getTag()));
        }

    }

    protected ClientAttribute copyAttribute(ClientAttribute sourceAttribute) {
        ClientAttribute result = new ClientAttribute(sourceAttribute.getPropertyName(), sourceAttribute.getBaseValue(), sourceAttribute.getQualifier(), sourceAttribute.getTag());
        result.setValue(sourceAttribute.getValue());
        return result;
    }

    /**
     * Constructs a new {@link ClientPresentationModel} with attributes identical to the source presentation model.
     *
     * @param sourcePM
     * @return the newly constructed ClientPresentationModel
     */
    public ClientPresentationModel copy(ClientPresentationModel sourcePM) {
        List<ClientAttribute> attrs = new ArrayList<ClientAttribute>();
        for (ClientAttribute attribute : sourcePM.getAttributes()) {
            ((ArrayList<ClientAttribute>) attrs).add(copyAttribute(attribute));
        }

        ClientPresentationModel result = new ClientPresentationModel(null, attrs);
        result.setPresentationModelType(sourcePM.getPresentationModelType());
        result.setClientSideOnly(sourcePM.isClientSideOnly());
        clientModelStore.add(result);
        return result;
    }

    public void startPushListening(String pushActionName, String releaseActionName) {
        if (pushActionName == null) {
            return;

        }

        if (releaseActionName == null) {
            return;

        }

        clientConnector.setPushListener(new NamedCommand(pushActionName));
        clientConnector.setReleaseCommand(new SignalCommand(releaseActionName));
        clientConnector.setPushEnabled(true);
        clientConnector.listen();
    }

    public void stopPushListening() {
        clientConnector.setPushEnabled(false);
    }

    public boolean isPushListening() {
        return clientConnector.isPushEnabled();
    }

    public ClientModelStore getClientModelStore() {
        return clientModelStore;
    }

    public ClientConnector getClientConnector() {
        return clientConnector;
    }

    public void setClientModelStore(ClientModelStore clientModelStore) {
        this.clientModelStore = clientModelStore;
    }

    public void setClientConnector(ClientConnector clientConnector) {
        this.clientConnector = clientConnector;
    }

}
