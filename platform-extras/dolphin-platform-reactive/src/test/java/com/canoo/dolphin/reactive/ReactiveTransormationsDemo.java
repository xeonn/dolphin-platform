package com.canoo.dolphin.reactive;

import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.dolphin.impl.MockedProperty;
import com.canoo.dolphin.mapping.Property;
import rx.functions.Func1;

import java.util.concurrent.TimeUnit;

/**
 * Created by hendrikebbers on 18.05.16.
 */
public class ReactiveTransormationsDemo {

    public static void main(String... args) throws Exception{

        System.out.println("throttleLast");

        final Property<String> property = new MockedProperty<>();
        property.set("");
        final Property<String> debouncedProperty = ReactiveTransormations.throttleLast(property, 200, TimeUnit.MILLISECONDS);
        debouncedProperty.onChanged(new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                System.out.println(debouncedProperty.get().length());
            }
        });

        for(int i = 0; i < 50; i++) {
            property.set(property.get() + "A");
            Thread.sleep(20);
        }
        Thread.sleep(2000);

        System.out.println("filter");

        final Property<String> property2 = new MockedProperty<>();
        property2.set("");
        final Property<String> debouncedProperty2 = ReactiveTransormations.filter(property2, new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return s.length() % 2 == 0;
            }
        });
        debouncedProperty2.onChanged(new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                System.out.println(debouncedProperty2.get().length());
            }
        });

        for(int i = 0; i < 50; i++) {
            property2.set(property2.get() + "A");
            Thread.sleep(20);
        }
        Thread.sleep(500);
    }

}
