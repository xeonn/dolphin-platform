package com.canoo.dolphin.test.impl;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class HttpClientMock implements HttpClient {

    @Override
    public HttpParams getParams() {
        throw new RuntimeException("Functionality not supported for tests since in memory mode is used!");
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        throw new RuntimeException("Functionality not supported for tests since in memory mode is used!");
    }

    @Override
    public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
        throw new RuntimeException("Functionallity not supported for tests since in memory mode is used!");
    }

    @Override
    public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
        throw new RuntimeException("Functionality not supported for tests since in memory mode is used!");
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
        throw new RuntimeException("Functionallity not supported for tests since in memory mode is used!");
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        throw new RuntimeException("Functionality not supported for tests since in memory mode is used!");
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        throw new RuntimeException("Functionality not supported for tests since in memory mode is used!");
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        throw new RuntimeException("Functionality not supported for tests since in memory mode is used!");
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        throw new RuntimeException("Functionality not supported for tests since in memory mode is used!");
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        throw new RuntimeException("Functionality not supported for tests since in memory mode is used!");
    }
}
