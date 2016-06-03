package com.canoo.dolphin.reactive;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.util.Assert;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

/**
 * Helper class that creates a {@link TransformedProperty} for a regular {@link Property}. A transformed property brings
 * some first reactive features to the dolphin platform by providing reactive transformations like throttleLast or
 * debounce
 */
public class ReactiveTransormations {

    /**
     * Provides a {@link TransformedProperty} that is "throttleLast" transformation of the given {@link Property}.
     * @param property the property
     * @param timeout timeout for the "throttleLast" transformation
     * @param unit time unit for the "throttleLast" transformation
     * @param <T> type of the property
     * @return the transformed property
     */
    public static <T> TransformedProperty<T> throttleLast(Property<T> property, long timeout, TimeUnit unit) {
        Assert.requireNonNull(property, "property");
        Assert.requireNonNull(unit, "unit");

        final PublishSubject<T> reactiveObservable = PublishSubject.create();

        Subscription basicSubscription = property.onChanged(new ValueChangeListener<T>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends T> evt) {
                reactiveObservable.onNext(evt.getNewValue());
            }
        });

        TransformedPropertyImpl result = new TransformedPropertyImpl<>(basicSubscription);

        Observable<T> transformedObservable = reactiveObservable.throttleLast(timeout, unit);
        transformedObservable.subscribe(result);

        reactiveObservable.onNext(property.get());

        return result;
    }

    /**
     * Provides a {@link TransformedProperty} that is "debounce" transformation of the given {@link Property}.
     * @param property the property
     * @param timeout timeout for the "debounce" transformation
     * @param unit time unit for the "debounce" transformation
     * @param <T> type of the property
     * @return the transformed property
     */
    public static <T> TransformedProperty<T> debounce(Property<T> property, long timeout, TimeUnit unit) {
        Assert.requireNonNull(property, "property");
        Assert.requireNonNull(unit, "unit");

        final PublishSubject<T> reactiveObservable = PublishSubject.create();

        Subscription basicSubscription = property.onChanged(new ValueChangeListener<T>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends T> evt) {
                reactiveObservable.onNext(evt.getNewValue());
            }
        });

        TransformedPropertyImpl result = new TransformedPropertyImpl<>(basicSubscription);

        Observable<T> transformedObservable = reactiveObservable.debounce(timeout, unit);
        transformedObservable.subscribe(result);

        reactiveObservable.onNext(property.get());

        return result;
    }

    /**
     * Provides a {@link TransformedProperty} that is "map" transformation of the given {@link Property}.
     * @param property the property
     * @param mapFunction the function that does the mapping for the transformation
     * @param <T> type of the property
     * @return the transformed property
     */
    public static <T, U> TransformedProperty<U> map(Property<T> property, Func1<T, U> mapFunction) {
        Assert.requireNonNull(property, "property");
        Assert.requireNonNull(mapFunction, "mapFunction");

        final PublishSubject<T> reactiveObservable = PublishSubject.create();

        Subscription basicSubscription = property.onChanged(new ValueChangeListener<T>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends T> evt) {
                reactiveObservable.onNext(evt.getNewValue());
            }
        });

        TransformedPropertyImpl result = new TransformedPropertyImpl<>(basicSubscription);

        Observable<U> transformedObservable = reactiveObservable.map(mapFunction);
        transformedObservable.subscribe(result);

        reactiveObservable.onNext(property.get());

        return result;
    }

    /**
     * Provides a {@link TransformedProperty} that is "filter" transformation of the given {@link Property}.
     * @param property the property
     * @param filterFunction the map that does the filtering for the transformation
     * @param <T> type of the property
     * @return the transformed property
     */
    public static <T> TransformedProperty<T> filter(Property<T> property, Func1<T, Boolean> filterFunction) {
        Assert.requireNonNull(property, "property");
        Assert.requireNonNull(filterFunction, "filterFunction");

        final PublishSubject<T> reactiveObservable = PublishSubject.create();

        Subscription basicSubscription = property.onChanged(new ValueChangeListener<T>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends T> evt) {
                reactiveObservable.onNext(evt.getNewValue());
            }
        });

        TransformedPropertyImpl result = new TransformedPropertyImpl<>(basicSubscription);

        Observable<T> transformedObservable = reactiveObservable.filter(filterFunction);
        transformedObservable.subscribe(result);

        reactiveObservable.onNext(property.get());

        return result;
    }

}
