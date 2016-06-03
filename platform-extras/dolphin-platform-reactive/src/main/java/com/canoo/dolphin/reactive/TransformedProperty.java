package com.canoo.dolphin.reactive;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.mapping.Property;

/**
 * A transformed property is a {@link Property} that can be created by using
 * the methods of {@link ReactiveTransormations}. Since a {@link TransformedProperty} is based on a transformation
 * the value of such a property can not be set manually.
 * @param <T> type of the property
 */
public interface TransformedProperty<T> extends Property<T>, Subscription {

}
