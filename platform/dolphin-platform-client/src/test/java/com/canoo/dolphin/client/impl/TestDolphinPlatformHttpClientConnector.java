package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.DummyUiThreadHandler;
import com.canoo.dolphin.util.DolphinRemotingException;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.comm.JsonCodec;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TestDolphinPlatformHttpClientConnector {

    @Test
    public void testSimpleCall() {
        HttpClient httpClient = new DefaultHttpClient() {

            @Override
            public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
                return (T) "[{\"pmId\":\"p1\",\"clientSideOnly\":false,\"id\":\"CreatePresentationModel\",\"attributes\":[],\"pmType\":null,\"className\":\"org.opendolphin.core.comm.CreatePresentationModelCommand\"}]";
            }
        };
        DolphinPlatformHttpClientConnector connector = new DolphinPlatformHttpClientConnector(new ClientDolphin(), new JsonCodec(), httpClient, "noUrl", new ForwardableCallback<>(), new DummyUiThreadHandler());

        CreatePresentationModelCommand command = new CreatePresentationModelCommand();
        command.setPmId("p1");
        List<Command> result = connector.transmit(Collections.singletonList(command));

        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.get(0) instanceof CreatePresentationModelCommand);
        Assert.assertEquals(((CreatePresentationModelCommand)result.get(0)).getPmId(), "p1");
    }

    @Test(expectedExceptions = DolphinRemotingException.class)
    public void testBadResponse() {
        final CountDownLatch httpWasCalled = new CountDownLatch(1);

        HttpClient httpClient = new DefaultHttpClient() {

            @Override
            public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
                StatusLine statusLine = new StatusLine() {
                    @Override
                    public ProtocolVersion getProtocolVersion() {
                        return new ProtocolVersion("Dummy-Protocol", 1, 1);
                    }

                    @Override
                    public int getStatusCode() {
                        return 500;
                    }

                    @Override
                    public String getReasonPhrase() {
                        return "Internal Server Error";
                    }
                };
                StringEntity entity = new StringEntity("failed");
                HttpResponse response = new BasicHttpResponse(statusLine);
                response.setEntity(entity);
                responseHandler.handleResponse(response);
                httpWasCalled.countDown();
                return (T) "[]";
            }
        };
        DolphinPlatformHttpClientConnector connector = new DolphinPlatformHttpClientConnector(new ClientDolphin(), new JsonCodec(), httpClient, "noUrl", new ForwardableCallback<>(), new DummyUiThreadHandler());

        connector.transmit(Collections.singletonList(new Command()));
    }

    @Test(expectedExceptions = DolphinRemotingException.class)
    public void testCallWithException() {
        DolphinPlatformHttpClientConnector connector = new DolphinPlatformHttpClientConnector(new ClientDolphin(), new JsonCodec(), new DefaultHttpClient(), "noUrl", new ForwardableCallback<>(), new DummyUiThreadHandler());
        connector.transmit(Collections.singletonList(new Command()));
    }

}
