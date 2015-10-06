package com.canoo.dolphin.event;

/**
 * Defines a function interface that is used to handle a unsubscription or unregistration procedure.
 * Whenever you register for example a handler or listener in the Dolphin Platform API you will get a
 * {@link Subscription} instance as return value of the methods that does the registration. The {@link Subscription}
 * instance can be used to unregister / unsibscribe the registration by just calling the {@link #unsubscribe()} method.
 *
 * Example:
 *
 * <blockquote>
 * <pre>
 *     //Add a change handler to a property
 *     Subscription subscription = myProperty.onChange(e -> System.out.println("value changed"));
 *
 *     //Remove the change handler
 *     subscription.unsubscribe();
 * </pre>
 * </blockquote>
 *
 */
public interface Subscription {

    /**
     * Unsusbscribe / unregister the handling that is defined by the {@link Subscription} instance.
     */
    void unsubscribe();
}
