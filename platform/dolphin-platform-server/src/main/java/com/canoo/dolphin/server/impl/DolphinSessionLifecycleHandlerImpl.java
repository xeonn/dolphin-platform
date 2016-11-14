package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.context.DolphinContextException;
import com.canoo.dolphin.server.context.DolphinSessionLifecycleHandler;
import com.canoo.dolphin.util.Callback;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DolphinSessionLifecycleHandlerImpl implements DolphinSessionLifecycleHandler {

    private final List<Callback<DolphinSession>> onCreateCallbacks = new CopyOnWriteArrayList<>();

    private final List<Callback<DolphinSession>> onDestroyCallbacks = new CopyOnWriteArrayList<>();

    @Override
    public Subscription addSessionCreatedListener(final Callback<DolphinSession> listener) {
        onCreateCallbacks.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                onCreateCallbacks.remove(listener);
            }
        };
    }

    @Override
    public Subscription addSessionDestroyedListener(final Callback<DolphinSession> listener) {
        onDestroyCallbacks.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                onDestroyCallbacks.remove(listener);
            }
        };
    }

    @Override
    public void onSessionCreated(final DolphinSession session) {
        for (Callback<DolphinSession> listener : onCreateCallbacks) {
            try {
                listener.call(session);
            } catch (Exception e) {
                throw new DolphinContextException("Error while handling onSessionCreated listener", e);
            }
        }
    }

    @Override
    public void onSessionDestroyed(final DolphinSession session) {
        for (Callback<DolphinSession> listener : onDestroyCallbacks) {
            try {
                listener.call(session);
            } catch (Exception e) {
                throw new DolphinContextException("Error while handling onSessionDestroyed listener", e);
            }
        }
    }
}
