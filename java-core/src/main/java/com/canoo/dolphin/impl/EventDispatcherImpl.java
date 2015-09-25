package com.canoo.dolphin.impl;

import com.canoo.dolphin.internal.DolphinEventHandler;
import com.canoo.dolphin.internal.EventDispatcher;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.ModelStoreEvent;
import org.opendolphin.core.PresentationModel;

import java.util.ArrayList;
import java.util.List;

public abstract class EventDispatcherImpl implements EventDispatcher {

    private final List<DolphinEventHandler> modelAddedHandlers = new ArrayList<>(1);
    private final List<DolphinEventHandler> modelRemovedHandlers = new ArrayList<>(1);
    private final List<DolphinEventHandler> listElementsAddHandlers = new ArrayList<>(1);
    private final List<DolphinEventHandler> listElementsDelHandlers = new ArrayList<>(1);
    private final List<DolphinEventHandler> listElementsSetHandlers = new ArrayList<>(1);

    public EventDispatcherImpl(Dolphin dolphin) {
        dolphin.addModelStoreListener(this);
    }

    public void addAddedHandler(DolphinEventHandler handler) {
        modelAddedHandlers.add(handler);
    }

    public void addRemovedHandler(DolphinEventHandler handler) {
        modelRemovedHandlers.add(handler);
    }

    public void addListElementAddHandler(DolphinEventHandler handler) {
        listElementsAddHandlers.add(handler);
    }

    public void addListElementDelHandler(DolphinEventHandler handler) {
        listElementsDelHandlers.add(handler);
    }

    public void addListElementSetHandler(DolphinEventHandler handler) {
        listElementsSetHandlers.add(handler);
    }

    @Override
    public void modelStoreChanged(ModelStoreEvent event) {
        final PresentationModel model = event.getPresentationModel();
        if (!isLocalChange(model)) {
            if (ModelStoreEvent.Type.ADDED == event.getType()) {
                onAddedHandler(model);
            } else if (ModelStoreEvent.Type.REMOVED == event.getType()) {
                onRemovedHandler(model);
            }
        }
    }

    private void onAddedHandler(PresentationModel model) {
        final String type = model.getPresentationModelType();
        switch (type) {
            case DolphinConstants.DOLPHIN_BEAN:
            case DolphinConstants.DOLPHIN_PARAMETER:
                // ignore
                break;
            case DolphinConstants.LIST_ADD:
                for (final DolphinEventHandler handler : listElementsAddHandlers) {
                    handler.onEvent(model);
                }
                break;
            case DolphinConstants.LIST_DEL:
                for (final DolphinEventHandler handler : listElementsDelHandlers) {
                    handler.onEvent(model);
                }
                break;
            case DolphinConstants.LIST_SET:
                for (final DolphinEventHandler handler : listElementsSetHandlers) {
                    handler.onEvent(model);
                }
                break;
            default:
                for (final DolphinEventHandler handler : modelAddedHandlers) {
                    handler.onEvent(model);
                }
                break;
        }
    }

    private void onRemovedHandler(PresentationModel model) {
        final String type = model.getPresentationModelType();
        switch (type) {
            case DolphinConstants.DOLPHIN_BEAN:
            case DolphinConstants.DOLPHIN_PARAMETER:
            case DolphinConstants.LIST_ADD:
            case DolphinConstants.LIST_DEL:
            case DolphinConstants.LIST_SET:
                // ignore
                break;
            default:
                for (final DolphinEventHandler handler : modelRemovedHandlers) {
                    handler.onEvent(model);
                }
                break;
        }
    }

    private boolean isLocalChange(PresentationModel model) {
        final Object value = model.findAttributeByPropertyName(DolphinConstants.SOURCE_SYSTEM).getValue();
        return getLocalSystemIdentifier().equals(value);
    }

    protected abstract String getLocalSystemIdentifier();
}
