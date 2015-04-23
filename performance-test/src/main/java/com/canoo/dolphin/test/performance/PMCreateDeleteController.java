package com.canoo.dolphin.test.performance;

import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import org.opendolphin.core.ModelStoreEvent;
import org.opendolphin.core.ModelStoreListener;
import org.opendolphin.core.server.ServerDolphin;

import javax.inject.Inject;

@DolphinController("PMCreateDeleteController")
public class PMCreateDeleteController {

    private static final String PM_CREATE_DELETE_MODEL_TYPE = "PM_CREATE_DELETE";

    @Inject
    private ServerDolphin dolphin;

    private ModelStoreListener listener;

    @DolphinAction
    public void setUp() {
        if (listener == null) {
            listener = new ModelStoreListener() {
                @Override
                public void modelStoreChanged(ModelStoreEvent event) {
                    if (event.getType() == ModelStoreEvent.Type.ADDED) {
                        dolphin.removeAllPresentationModelsOfType(PM_CREATE_DELETE_MODEL_TYPE);
                    }
                }
            };
        }

        dolphin.getModelStore().addModelStoreListener(PM_CREATE_DELETE_MODEL_TYPE, listener);
    }

    @DolphinAction
    public void tearDown() {
        if (listener != null) {
            dolphin.getModelStore().removeModelStoreListener(listener);
        }
    }

}
