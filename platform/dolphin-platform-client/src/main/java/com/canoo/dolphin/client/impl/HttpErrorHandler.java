package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.util.DolphinRemotingException;

/**
 * Created by hendrikebbers on 20.05.16.
 */
public class HttpErrorHandler {

    private final ForwardableCallback<DolphinRemotingException> httpExceptionCallback;

    private final ForwardableCallback<DolphinRemotingException> sessionLostExceptionCallback;

    private final ForwardableCallback<DolphinRemotingException> connectionRefusedCallback;

    private final ForwardableCallback<DolphinRemotingException> remotingExceptionCallback;

    public HttpErrorHandler() {
        this.httpExceptionCallback = new ForwardableCallback<>();
        this.sessionLostExceptionCallback = new ForwardableCallback<>();
        this.connectionRefusedCallback = new ForwardableCallback<>();
        this.remotingExceptionCallback = new ForwardableCallback<>();
    }

    public ForwardableCallback<DolphinRemotingException> getHttpExceptionCallback() {
        return httpExceptionCallback;
    }

    public ForwardableCallback<DolphinRemotingException> getSessionLostExceptionCallback() {
        return sessionLostExceptionCallback;
    }

    public ForwardableCallback<DolphinRemotingException> getConnectionRefusedCallback() {
        return connectionRefusedCallback;
    }
}
