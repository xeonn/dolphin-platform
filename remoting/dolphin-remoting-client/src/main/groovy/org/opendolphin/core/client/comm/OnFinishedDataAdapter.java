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
