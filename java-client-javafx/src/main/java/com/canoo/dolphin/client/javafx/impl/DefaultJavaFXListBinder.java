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
package com.canoo.dolphin.client.javafx.impl;

import com.canoo.dolphin.client.javafx.Binding;
import com.canoo.dolphin.client.javafx.Converter;
import com.canoo.dolphin.client.javafx.JavaFXListBinder;
import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.event.Subscription;
import javafx.collections.ListChangeListener;

import java.util.IdentityHashMap;

public class DefaultJavaFXListBinder<S> implements JavaFXListBinder<S> {

    private javafx.collections.ObservableList<S> list;

    private static IdentityHashMap<javafx.collections.ObservableList, javafx.collections.ObservableList> boundLists = new IdentityHashMap<>();

    public DefaultJavaFXListBinder(javafx.collections.ObservableList<S> list) {
        if (list == null) {
            throw new IllegalArgumentException("list must not be null");
        }
        this.list = list;
    }

    @Override
    public <T> Binding to(ObservableList<T> dolphinList, Converter<? super T, ? extends S> converter) {
        if(boundLists.containsKey(list)) {
            throw new UnsupportedOperationException("A JavaFX list can only be bound to one Dolphin Platform list!");
        };

        boundLists.put(list, list);
        InternalListChangeListener listChangeListener = new InternalListChangeListener(list, converter);
        Subscription subscription = dolphinList.onChanged(listChangeListener);

        list.clear();
        dolphinList.forEach(item -> list.add(converter.convert(item)));

        ListChangeListener readOnlyListener = c -> {
            if (!listChangeListener.isOnChange()) {
                throw new UnsupportedOperationException("A JavaFX list that is bound to a dolphin list can only be modified by the binding!");
            }
        };
        list.addListener(readOnlyListener);


        return () -> {
            subscription.unsubscribe();
            list.removeListener(readOnlyListener);
            boundLists.remove(list);
        };
    }

    private class InternalListChangeListener<S, T> implements com.canoo.dolphin.collections.ListChangeListener<T> {

        private final javafx.collections.ObservableList<S> javaFXList;

        private final Converter<? super T, ? extends S> converter;

        private boolean onChange;

        public InternalListChangeListener(javafx.collections.ObservableList<S> javaFXList, Converter<? super T, ? extends S> converter) {
            this.converter = converter;
            this.javaFXList = javaFXList;
            onChange = false;
        }

        @Override
        public void listChanged(ListChangeEvent<? extends T> e) {
            onChange = true;
            try {
                for (ListChangeEvent.Change<? extends T> c : e.getChanges()) {
                    if (c.isRemoved()) {
                        final int index = c.getFrom();
                        javaFXList.remove(index, index + c.getRemovedElements().size());
                    } else if (c.isAdded()) {
                        for (int i = c.getFrom(); i < c.getTo(); i++) {
                            javaFXList.add(i, converter.convert(e.getSource().get(i)));
                        }
                    } else if (c.isReplaced()) {
                        for (int i = c.getFrom(); i < c.getTo(); i++) {
                            javaFXList.set(i, converter.convert(e.getSource().get(i)));
                        }
                    }
                }
            } finally {
                onChange = false;
            }
        }

        public boolean isOnChange() {
            return onChange;
        }
    }
}
