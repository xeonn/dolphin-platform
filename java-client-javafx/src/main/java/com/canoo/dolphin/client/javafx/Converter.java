package com.canoo.dolphin.client.javafx;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public interface Converter<T, U> {

    U convert(T value);

}
