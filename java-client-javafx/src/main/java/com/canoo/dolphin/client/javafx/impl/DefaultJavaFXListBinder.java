package com.canoo.dolphin.client.javafx.impl;

import com.canoo.dolphin.client.javafx.Binding;
import com.canoo.dolphin.client.javafx.Converter;
import com.canoo.dolphin.client.javafx.JavaFXListBinder;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.event.Subscription;
import javafx.collections.ListChangeListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultJavaFXListBinder<S> implements JavaFXListBinder<S> {

    private javafx.collections.ObservableList<S> list;

    private static List<javafx.collections.ObservableList> boundLists = new CopyOnWriteArrayList<>();

    public DefaultJavaFXListBinder(javafx.collections.ObservableList<S> list) {
        if (list == null) {
            throw new IllegalArgumentException("list must not be null");
        }
        this.list = list;
    }

    @Override
    public <T> Binding to(ObservableList<T> dolphinList, Converter<? super T, ? extends S> converter) {
        boundLists.forEach(addedList -> {
            if(addedList == list) {
                throw new UnsupportedOperationException("A JavaFX list can only be bound to one Dolphin Platform list!");
            }
        });
        boundLists.add(list);
        InternalListChangeListener listChangeListener = new InternalListChangeListener(list, converter);
        Subscription subscription = dolphinList.onChanged(listChangeListener);

        dolphinList.forEach(item -> list.add(converter.convert(item)));
        list.clear();

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
}
