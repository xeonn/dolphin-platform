package com.canoo.dolphin.client.javafx;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public interface BidirectionalConverter<T, U>  extends Converter<T, U> {

    T convertBack(U value);
}
