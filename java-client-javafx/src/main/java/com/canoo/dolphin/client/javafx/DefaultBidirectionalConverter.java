/*
 * Copyright 2015 Canoo Engineering AG.
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
package com.canoo.dolphin.client.javafx;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class DefaultBidirectionalConverter<T, U> implements BidirectionalConverter<T, U> {

    private Converter<T, U> converter;

    private Converter<U, T> backConverter;

    public DefaultBidirectionalConverter(Converter<T, U> converter, Converter<U, T> backConverter) {
        if(converter == null) {
            throw new IllegalArgumentException("converter must not be null");
        }
        if(backConverter == null) {
            throw new IllegalArgumentException("backConverter must not be null");
        }
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
