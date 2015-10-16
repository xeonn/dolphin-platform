package com.canoo.dolphin.client.javafx;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public interface BidirectionalConverter<T, U>  extends Converter<T, U> {

    T convertBack(U value);

    default BidirectionalConverter<U, T> invert() {
        final BidirectionalConverter<T, U> converter = this;
        return new BidirectionalConverter<U, T>() {
            @Override
            public U convertBack(T value) {
                return converter.convert(value);
            }

            @Override
            public T convert(U value) {
                return converter.convertBack(value);
            }
        };
    }
}
