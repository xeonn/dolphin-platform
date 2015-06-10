package com.canoo.dolphin.impl;

import org.opendolphin.core.Dolphin;
import org.opendolphin.core.ModelStoreEvent;
import org.opendolphin.core.ModelStoreListener;
import org.opendolphin.core.PresentationModel;

import java.util.ArrayList;
import java.util.List;

public abstract class EventDispatcher implements ModelStoreListener {

    interface ModelAddedHandler {
        void onModelAdded(PresentationModel model);
    }
    interface ModelRemovedHandler {
        void onModelRemoved(PresentationModel model);
    }

    private final List<ModelAddedHandler> modelAddedHandlers = new ArrayList<>(1);
    private final List<ModelRemovedHandler> modelRemovedHandlers = new ArrayList<>(1);

    public EventDispatcher(Dolphin dolphin) {
        dolphin.addModelStoreListener(this);
    }

    void addAddedHandler(ModelAddedHandler handler) {
        modelAddedHandlers.add(handler);
    }

    void addRemovedHandler(ModelRemovedHandler handler) {
        modelRemovedHandlers.add(handler);
    }

    @Override
    public void modelStoreChanged(ModelStoreEvent event) {
        final PresentationModel model = event.getPresentationModel();
        if (ModelStoreEvent.Type.ADDED == event.getType()) {
            onAddedHandler(model);
        } else if (ModelStoreEvent.Type.REMOVED == event.getType()) {
            onRemovedHandler(model);
        }
    }

    private void onAddedHandler(PresentationModel model) {
        final String type = model.getPresentationModelType();
        switch (type) {
            case DolphinConstants.DOLPHIN_BEAN:
            case DolphinConstants.DOLPHIN_PARAMETER:
            case DolphinConstants.ADD_FROM_SERVER:
            case DolphinConstants.DEL_FROM_SERVER:
            case DolphinConstants.SET_FROM_SERVER:
            case DolphinConstants.ADD_FROM_CLIENT:
            case DolphinConstants.DEL_FROM_CLIENT:
            case DolphinConstants.SET_FROM_CLIENT:
                // ignore
                return;
            default:
                if (!isLocalChange(model)) {
                    for (final ModelAddedHandler handler : modelAddedHandlers) {
                        handler.onModelAdded(model);
                    }
                }
        }
    }

    private void onRemovedHandler(PresentationModel model) {
        final String type = model.getPresentationModelType();
        switch (type) {
            case DolphinConstants.DOLPHIN_BEAN:
            case DolphinConstants.DOLPHIN_PARAMETER:
            case DolphinConstants.ADD_FROM_SERVER:
            case DolphinConstants.DEL_FROM_SERVER:
            case DolphinConstants.SET_FROM_SERVER:
            case DolphinConstants.ADD_FROM_CLIENT:
            case DolphinConstants.DEL_FROM_CLIENT:
            case DolphinConstants.SET_FROM_CLIENT:
                // ignore
                return;
            default:
                if (!isLocalChange(model)) {
                    for (final ModelRemovedHandler handler : modelRemovedHandlers) {
                        handler.onModelRemoved(model);
                    }
                }
        }
    }

    private boolean isLocalChange(PresentationModel model) {
        final Object value = model.findAttributeByPropertyName(DolphinConstants.SOURCE_SYSTEM).getValue();
        return getLocalSystemIdentifier().equals(value);
    }

    protected abstract String getLocalSystemIdentifier();
}
