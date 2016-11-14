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

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.DolphinRemotingException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.core.client.comm.BlindCommandBatcher;
import org.opendolphin.core.client.comm.OnFinishedDataAdapter;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.client.comm.UiThreadHandler;
import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.NamedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is used to sync the unique client scope id of the current dolphin
 */
public class DolphinPlatformHttpClientConnector extends AbstractClientConnector {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformHttpClientConnector.class);

    private static final String CHARSET = "UTF-8";

    private final URL servletUrl;

    private final HttpClient httpClient;

    private final IdBasedResponseHandler responseHandler;

    private final ForwardableCallback<DolphinRemotingException> remotingErrorHandler;

    private final Codec codec;

    private String clientId;

    private boolean gc;

    private AtomicBoolean gcSend = new AtomicBoolean(false);

    public DolphinPlatformHttpClientConnector(ClientDolphin clientDolphin, Codec codec, HttpClient httpClient, URL servletUrl, ForwardableCallback<DolphinRemotingException> remotingErrorHandler, UiThreadHandler uiThreadHandler) {
        super(clientDolphin, new BlindCommandBatcher());
        setUiThreadHandler(uiThreadHandler);
        this.servletUrl = Assert.requireNonNull(servletUrl, "servletUrl");
        this.codec = Assert.requireNonNull(codec, "codec");
        this.remotingErrorHandler = Assert.requireNonNull(remotingErrorHandler, "remotingErrorHandler");
        this.httpClient = Assert.requireNonNull(httpClient, "httpClient");
        this.responseHandler = new IdBasedResponseHandler(this);
    }

    @Override
    public void send(Command command, OnFinishedHandler callback) {
        super.send(command, callback);
        if(gc && !gcSend.get()) {
            gcSend.set(true);
            getUiThreadHandler().executeInsideUiThread(new Runnable() {
                @Override
                public void run() {
                    send(new NamedCommand(PlatformConstants.GARBAGE_COLLECTION_COMMAND_NAME), new OnFinishedDataAdapter() {
                        @Override
                        public void onFinishedData(List<Map> data) {
                            gcSend.set(false);
                        }
                    });
                }
            });
        }
    }

    public List<Command> transmit(List<Command> commands) {
        Assert.requireNonNull(commands, "commands");
        List<Command> result = new ArrayList<>();

        if(LOG.isTraceEnabled()) {
            StringBuilder commandsString = new StringBuilder();
            Iterator<Command> commandIterator = commands.iterator();
            while (commandIterator.hasNext()) {
                commandsString.append(commandIterator.next().getId());
                if(commandIterator.hasNext()) {
                    commandsString.append(", ");
                }
            }
            LOG.trace("Sending {} commands to the server: {}", commands.size(), commandsString.toString());
        }

        try {
            String content = codec.encode(commands);
            HttpPost httpPost = new HttpPost(servletUrl.toString());
            StringEntity entity = new StringEntity(content, CHARSET);
            httpPost.setEntity(entity);
            if(clientId != null) {
                httpPost.addHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, clientId);
            }
            if (commands.size() == 1 && commands.get(0) == getReleaseCommand()) {
                httpClient.execute(httpPost, responseHandler);
            } else {
                String response = httpClient.execute(httpPost, responseHandler);
                result = codec.decode(response);
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

    public boolean isGc() {
        return gc;
    }

    public void setGc(boolean gc) {
        this.gc = gc;
    }
}



