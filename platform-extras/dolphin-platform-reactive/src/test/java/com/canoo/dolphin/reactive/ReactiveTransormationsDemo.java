package com.canoo.dolphin.reactive;

import com.canoo.dolphin.impl.MockedProperty;
import com.canoo.dolphin.mapping.Property;

import java.util.concurrent.TimeUnit;

/**
 * Created by hendrikebbers on 18.05.16.
 */
public class ReactiveTransormationsDemo {

    public static void main(String... args) throws Exception{
        Property<String> property = new MockedProperty<>();
        property.set("");
        Property<String> debouncedProperty = ReactiveTransormations.throttleLast(property, 200, TimeUnit.MILLISECONDS);
        debouncedProperty.onChanged(e -> System.out.println(debouncedProperty.get().length()));

        for(int i = 0; i < 50; i++) {
            property.set(property.get() + "A");
            Thread.sleep(20);
        }
        Thread.sleep(100);
    }

}
