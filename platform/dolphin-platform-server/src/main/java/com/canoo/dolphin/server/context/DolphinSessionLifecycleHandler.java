package com.canoo.dolphin.server.context;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.util.Callback;

/**
 * Created by hendrikebbers on 14.11.16.
 */
public interface DolphinSessionLifecycleHandler {

    Subscription addSessionCreatedListener(Callback<DolphinSession> listener);

    Subscription addSessionDestroyedListener(Callback<DolphinSession> listener);

    void onSessionCreated(final DolphinSession session);

    void onSessionDestroyed(final DolphinSession session);
}
