package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.Callback;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by hendrikebbers on 20.05.16.
 */
public class ForwardableCallback<T> implements Callback<T> {

    private List<Callback<T>> registeredCallbacks = new CopyOnWriteArrayList<>();

    public Subscription register(Callback<T> callback) {
        Assert.requireNonNull(callback, "callback");
        registeredCallbacks.add(callback);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                registeredCallbacks.remove(callback);
            }
        };
    }

    @Override
    public void call(T t) {
        for(Callback<T> callback : registeredCallbacks) {
            callback.call(t);
        }
    }
}
