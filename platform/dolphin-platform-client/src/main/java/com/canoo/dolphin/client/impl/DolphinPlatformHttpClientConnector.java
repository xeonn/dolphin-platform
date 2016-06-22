/*
 * Copyright 2015-2016 Canoo Engineering AG.
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
package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.AbstractConnector;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.DolphinRemotingException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.comm.CommandBatcher;
import org.opendolphin.core.client.comm.UiThreadHandler;
import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to sync the unique client scope id of the current dolphin
 */
public class DolphinPlatformHttpClientConnector extends AbstractConnector {

    private static final String CHARSET = "UTF-8";

    private final String servletUrl;

    private final HttpClient httpClient;

    private final IdBasedResponseHandler responseHandler;

    private final ForwardableCallback<DolphinRemotingException> remotingErrorHandler;

    private String clientId;

    private Codec codec;


    public DolphinPlatformHttpClientConnector(ClientDolphin clientDolphin, HttpClient httpClient, CommandBatcher commandBatcher, String servletUrl, ForwardableCallback<DolphinRemotingException> remotingErrorHandler, UiThreadHandler uiThreadHandler) {
        super(commandBatcher, clientDolphin, uiThreadHandler);
        this.servletUrl = Assert.requireNonNull(servletUrl, "servletUrl");
        this.remotingErrorHandler = Assert.requireNonNull(remotingErrorHandler, "remotingErrorHandler");

        this.httpClient = Assert.requireNonNull(httpClient, "httpClient");

        this.responseHandler = new IdBasedResponseHandler(this);
        //setStrictMode(false);
    }

    public List<Command> transmit(List<Command> commands) {
        List<Command> result = new ArrayList<>();
        try {
            String content = getCodec().encode(commands);
            HttpPost httpPost = new HttpPost(servletUrl);
            StringEntity entity = new StringEntity(content, CHARSET);
            httpPost.setEntity(entity);
            if(clientId != null) {
                httpPost.addHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, clientId);
            }
            if (commands.size() == 1 && commands.get(0) == getReleaseCommand()) {
                httpClient.execute(httpPost, responseHandler);
            } else {
                String response = httpClient.execute(httpPost, responseHandler);
                result = getCodec().decode(response);
            }
        } catch (Exception e) {
            DolphinRemotingException dolphinRemotingException = new DolphinRemotingException("Error in remoting layer", e);
            remotingErrorHandler.call(dolphinRemotingException);
            throw dolphinRemotingException;
        }
        return result;
    }

    public String getClientId() {
        return clientId;
    }

    protected void setClientId(String clientId) {
        if (this.clientId != null && !this.clientId.equals(clientId)) {
            throw new DolphinRemotingException("Error: client id conflict!");
        }
        this.clientId = clientId;
    }

    public Codec getCodec() {
        return codec;
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }
}



