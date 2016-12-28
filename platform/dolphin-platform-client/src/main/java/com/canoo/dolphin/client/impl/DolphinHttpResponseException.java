package com.canoo.dolphin.client.impl;

/**
 * Created by hendrikebbers on 28.12.16.
 */
public class DolphinHttpResponseException extends Exception {

    public DolphinHttpResponseException(int statusCode, String message) {
        super(message + " STATUSCODE: " + statusCode);
    }
}
