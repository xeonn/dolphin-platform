package com.canoo.dolphin.client.javafx;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class DefaultBidirectionalConverter<T, U> implements BidirectionalConverter<T, U> {

    private Converter<T, U> converter;

    private Converter<U, T> backConverter;

    public DefaultBidirectionalConverter(Converter<T, U> converter, Converter<U, T> backConverter) {
        this.converter = converter;
        this.backConverter = backConverter;
    }

    @Override
    public T convertBack(U value) {
        return backConverter.convert(value);
    }

    @Override
    public U convert(T value) {
        return converter.convert(value);
    }
}
