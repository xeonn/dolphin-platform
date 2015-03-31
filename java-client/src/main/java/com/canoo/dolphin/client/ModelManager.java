package com.canoo.dolphin.client;

import com.canoo.dolphin.mapping.util.ParamConstants;
import org.opendolphin.core.ModelStoreEvent;
import org.opendolphin.core.ModelStoreListener;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hendrikebbers on 31.03.15.
 */
public class ModelManager {

    private ClientDolphin dolphin;

    public ModelManager(ClientDolphin dolphin) {
        this.dolphin = dolphin;
        this.dolphin.addModelStoreListener(new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                //TODO
            }
        });
    }

    public <T> void addModelCreationListener(Class<T> modelClass, ModelCreationListener<T> listener) {
        //TODO
    }

    public <T> void removeModelCreationListener(Class<T> modelClass, ModelCreationListener<T> listener) {
        //TODO
    }

    public void addModelDeletedCallback(Object managedBean, Callback callback) {}

    public void removeModelDeletedCallback(Object managedBean, Callback callback) {}

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
