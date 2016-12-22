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
package org.opendolphin.core.client.comm;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.Command;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpClientConnector extends AbstractClientConnector {

    private static final Logger LOG = Logger.getLogger(HttpClientConnector.class.getName());

    private String servletUrl = "http://localhost:8080/dolphin-grails/dolphin/";

    private String charset = "UTF-8";

    private Codec codec;

    private DefaultHttpClient httpClient = new DefaultHttpClient();

    /**
     * A second channel for the sole purpose of sending SignalCommands
     */
    private DefaultHttpClient signalHttpClient = new DefaultHttpClient();

    private SessionAffinityCheckingResponseHandler responseHandler = null;

    private SimpleResponseHandler signalResponseHandler = null;

    public HttpClientConnector(ClientDolphin clientDolphin, String servletUrl) {
        this(clientDolphin, null, servletUrl);
    }

    public HttpClientConnector(ClientDolphin clientDolphin, ICommandBatcher commandBatcher, String servletUrl) {
        super(clientDolphin, commandBatcher);
        this.servletUrl = servletUrl;
        this.responseHandler = new SessionAffinityCheckingResponseHandler();
        this.signalResponseHandler = new SimpleResponseHandler();
    }

    public void setThrowExceptionOnSessionChange(boolean throwExceptionOnSessionChange) {
        this.responseHandler.setThrowExceptionOnSessionChange(throwExceptionOnSessionChange);
    }

    public List<Command> transmit(List<Command> commands) {
        List<Command> result = null;
        try {
            String content = codec.encode(commands);
            HttpPost httpPost = new HttpPost(servletUrl);
            StringEntity entity = new StringEntity(content, charset);
            httpPost.setEntity(entity);

            if (commands.size() == 1 && commands.get(0).equals(getReleaseCommand())) {// todo dk: ok, this is not nice...
                signalHttpClient.execute(httpPost, signalResponseHandler);
            } else {
                String response = httpClient.execute(httpPost, responseHandler);
                CookieStore cookieStore = httpClient.getCookieStore();
                if (cookieStore != null) {
                    signalHttpClient.setCookieStore(cookieStore);
                }

                LOG.finest(response);
                result = codec.decode(response);
            }

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "cannot transmit", ex);
            throw new RuntimeException("Error!", ex);
        }

        return result;
    }
}
