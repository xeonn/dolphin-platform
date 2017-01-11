package com.canoo.dolphin.client.impl;

public class DolphinHttpResponseException extends Exception {

    public DolphinHttpResponseException(int statusCode, String message) {
        super(message + " STATUSCODE: " + statusCode);
    }
}
