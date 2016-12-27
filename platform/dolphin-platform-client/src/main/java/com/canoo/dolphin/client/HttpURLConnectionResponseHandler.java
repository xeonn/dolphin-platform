package com.canoo.dolphin.client;

import java.net.HttpURLConnection;

/**
 * Created by hendrikebbers on 27.12.16.
 */
public interface HttpURLConnectionResponseHandler {

    void handle(HttpURLConnection response);

}
