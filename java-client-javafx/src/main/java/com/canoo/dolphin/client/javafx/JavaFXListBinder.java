package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.collections.ObservableList;

public interface JavaFXListBinder<S> {

    default Binding to(ObservableList<? extends S> dolphinList) {
        if (dolphinList == null) {
            throw new IllegalArgumentException("dolphinProperty must not be null");
        }
        return to(dolphinList, n -> n);
    }

    <T> Binding to(ObservableList<T> dolphinList, Converter<? super T, ? extends S> converter);

}
