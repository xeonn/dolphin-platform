package com.canoo.dolphin.client;

import org.opendolphin.core.ModelStoreEvent;
import org.opendolphin.core.ModelStoreListener;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.*;

/**
 * Created by hendrikebbers on 31.03.15.
 */
public class ModelManager {

    private ClientDolphin dolphin;

    private Map<String, ?> managedObjects;

    private Map<Class<?>, List<ModelCreationListener<?>>> onCreationListener;

    private Map<? super Object, List<Callback>> onDeleteCallbacks;

    public ModelManager(ClientDolphin dolphin) {
        this.dolphin = dolphin;
        managedObjects = new HashMap<>();
        onDeleteCallbacks = new HashMap<>();
        onCreationListener = new HashMap<>();
        this.dolphin.addModelStoreListener(new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                if(event.getType().equals(ModelStoreEvent.Type.REMOVED)) {
                    if(managedObjects.containsKey(event.getPresentationModel().getId())) {
                        Object deleted = managedObjects.get(event.getPresentationModel().getId());
                        if(onDeleteCallbacks.containsKey(deleted)) {
                            List<Callback> callbacks = onDeleteCallbacks.get(deleted);
                            for(Callback callback : callbacks) {
                                callback.call();
                            }
                        }
                        managedObjects.remove(event.getPresentationModel().getId());
                        onDeleteCallbacks.remove(deleted);
                    }
                } else {
                    Class<?> beanClass = getBeanClassForPresentationModel((ClientPresentationModel) event.getPresentationModel());
                    if(beanClass != null) {
                        if (onCreationListener.containsKey(beanClass)) {
                            try {
                                Object bean = createBeanForPresentationModel((ClientPresentationModel) event.getPresentationModel(), beanClass);
                                for(ModelCreationListener listener : onCreationListener.get(beanClass)) {
                                    listener.modelCreated(bean);
                                }
                            } catch (IllegalAccessException | InstantiationException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        });
    }

    public <T> void addModelCreationListener(Class<T> modelClass, ModelCreationListener<T> listener) {
        if(onCreationListener.get(modelClass) == null) {
            onCreationListener.put(modelClass, new ArrayList<ModelCreationListener<?>>());
        }
        onCreationListener.get(modelClass).add(listener);
    }

    public <T> void removeModelCreationListener(Class<T> modelClass, ModelCreationListener<T> listener) {
        if(onCreationListener.get(modelClass) != null) {
            onCreationListener.get(modelClass).remove(listener);
        }
    }

    private Class<?> getBeanClassForPresentationModel(ClientPresentationModel model) {
        //TODO
        return null;
    }

    private <T> T createBeanForPresentationModel(ClientPresentationModel model, Class<T> beanClass) throws IllegalAccessException, InstantiationException {
        T instance = beanClass.newInstance();

        //TODO: Inject properties

        return instance;
    }

    public void addModelDeletedCallback(Object managedBean, Callback callback) {
        if(onDeleteCallbacks.get(managedBean) == null) {
            onDeleteCallbacks.put(managedBean, new ArrayList<Callback>());
        }
        onDeleteCallbacks.get(managedBean).add(callback);
    }

    public void removeModelDeletedCallback(Object managedBean, Callback callback) {
        if(onDeleteCallbacks.get(managedBean) != null) {
            onDeleteCallbacks.get(managedBean).remove(callback);
        }
    }

    public void callAction(String actionName, ActionParam... params) {
        callAction(actionName, null, params);
    }

    public void callAction(String actionName, final Callback callback, ActionParam... params) {
        for(ActionParam param : params) {
            createParamAsPresentationModel(param);
        }
        dolphin.send(actionName, new OnFinishedHandler() {
            @Override
            public void onFinished(List<ClientPresentationModel> clientPresentationModels) {
                callback.call();
            }

            @Override
            public void onFinishedData(List<Map> maps) {}
        });
    }

    private void createParamAsPresentationModel(ActionParam param) {
        ClientAttribute nameAttribute = new ClientAttribute(ParamConstants.NAME_ATTRIBUTE, param.getName());
        ClientAttribute valueAttribute = new ClientAttribute(ParamConstants.VALUE_ATTRIBUTE, param.getValue());
        dolphin.presentationModel(UUID.randomUUID().toString(), ParamConstants.PM_TYPE, nameAttribute, valueAttribute);
    }
}
