/*
 * Copyright 2015 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.client.clientscope;

import com.canoo.dolphin.impl.PlatformConstants;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.comm.ClientConnector;
import org.opendolphin.core.client.comm.CommandBatcher;
import org.opendolphin.core.comm.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to sync the unique client scope id of the current dolphin
 */
public class DolphinPlatformHttpClientConnector extends ClientConnector {

    private static final String CHARSET = "UTF-8";

    private final String servletUrl;

    private final DefaultHttpClient httpClient;

    private final DefaultHttpClient signalHttpClient;

    private final SimpleResponseHandler responseHandler;

    private final SimpleResponseHandler signalResponseHandler;

    private String clientId;

    public DolphinPlatformHttpClientConnector(ClientDolphin clientDolphin, String servletUrl) {
        this(clientDolphin, null, servletUrl);
    }

    public DolphinPlatformHttpClientConnector(ClientDolphin clientDolphin, CommandBatcher commandBatcher, String servletUrl) {
        super(clientDolphin, commandBatcher);
        this.servletUrl = servletUrl;

        httpClient = new DefaultHttpClient();
        signalHttpClient = new DefaultHttpClient();

        this.responseHandler = new SimpleResponseHandler(this);
        this.signalResponseHandler = new SimpleResponseHandler(this);
    }

    public List<Command> transmit(List<Command> commands) {
        List<Command> result = new ArrayList<>();
        try {
            String content = getCodec().encode(commands);
            HttpPost httpPost = new HttpPost(servletUrl);
            StringEntity entity = new StringEntity(content, CHARSET);
            httpPost.setEntity(entity);

            httpPost.addHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, clientId);

            if (commands.size() == 1 && commands.get(0) == getReleaseCommand()) {
                signalHttpClient.execute(httpPost, signalResponseHandler);
            } else {
                String response = httpClient.execute(httpPost, responseHandler);
                result = getCodec().decode(response);
            }
        } catch (Exception e) {
            throw new DolphinRemotingException("Error in remoting layer", e);
        }
        return result;
    }

    public String getClientId() {
        return clientId;
    }

    protected void setClientId(String clientId) {
        if(this.clientId != null && !this.clientId.equals(clientId)) {
            throw new DolphinRemotingException("Error: client id conflict!");
        }
        this.clientId = clientId;
    }
}

class SimpleResponseHandler implements ResponseHandler<String> {

    private DolphinPlatformHttpClientConnector clientConnector;

    SimpleResponseHandler(DolphinPlatformHttpClientConnector clientConnector) {
        this.clientConnector = clientConnector;
    }

    @Override
    public String handleResponse(HttpResponse response) throws IOException {
        if (response == null) {
            throw new DolphinRemotingException("No dolphin id was send from the server!");
        }

        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity entity = response.getEntity();

        final Header dolphinHeader = response.getFirstHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME);
        clientConnector.setClientId(dolphinHeader.getValue());

        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        return entity == null ? null : EntityUtils.toString(entity);
    }
}

