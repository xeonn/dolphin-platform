package com.canoo.dolphin.client;

import java.net.HttpURLConnection;

public interface HttpURLConnectionResponseHandler {

    void handle(HttpURLConnection response);

}
