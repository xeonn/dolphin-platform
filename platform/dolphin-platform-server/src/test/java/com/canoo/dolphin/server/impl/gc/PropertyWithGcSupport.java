/**
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
package com.canoo.dolphin.server.impl.gc;

import com.canoo.dolphin.impl.MockedProperty;

/**
 * Created by hendrikebbers on 20.01.16.
 */
public class PropertyWithGcSupport<T> extends MockedProperty<T> {

    private final GarbageCollection garbageCollection;

    public PropertyWithGcSupport(final GarbageCollection garbageCollection) {
        this.garbageCollection = garbageCollection;
    }

    @Override
    public void set(final T value) {
        final T oldValue = get();
        super.set(value);
        garbageCollection.onPropertyValueChanged(this, oldValue, value);
    }
}
