package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.HttpURLConnectionFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DefaultHttpURLConnectionFactory implements HttpURLConnectionFactory {
    @Override
    public HttpURLConnection create(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        if(connection instanceof HttpURLConnection) {
            return (HttpURLConnection) connection;
        }
        throw new IOException("URL do not provide a HttpURLConnection!");
    }
}
