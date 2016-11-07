package org.opendolphin.core.client.comm;

import groovy.lang.Closure;
import org.opendolphin.core.client.ClientPresentationModel;

import java.util.List;
import java.util.Map;

/**
 * Convenience class for OnFinishedData
 */
public abstract class OnFinishedDataAdapter implements OnFinishedData {
    @Override
    public void onFinished(List<ClientPresentationModel> presentationModels) {
        // ignore
    }

    public static OnFinishedData withAction(final Closure cl) {
        return new OnFinishedDataAdapter() {
            @Override
            public void onFinishedData(List<Map> data) {
                cl.call(data);
            }

        };
    }

}
