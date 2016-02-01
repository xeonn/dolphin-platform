package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.collections.ObservableList;
import static com.canoo.dolphin.util.Assert.*;

public interface JavaFXListBinder<S> {

    default Binding to(ObservableList<? extends S> dolphinList) {
        requireNonNull(dolphinList, "dolphinList");
        return to(dolphinList, n -> n);
    }

    <T> Binding to(ObservableList<T> dolphinList, Converter<? super T, ? extends S> converter);

}
