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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by hendrikebbers on 20.12.16.
 */
public class SessionAffinityCheckingResponseHandler implements ResponseHandler<String> {

    private static final Logger LOG = Logger.getLogger(SessionAffinityCheckingResponseHandler.class.getName());

    private boolean throwExceptionOnSessionChange = true;

    private String lastSessionId = null;

    @Override
    public String handleResponse(HttpResponse response) throws HttpResponseException, IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            //EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        String result = entity == null ? null : EntityUtils.toString(entity);
        String sessionID = null;
        if(response != null && response.getFirstHeader("Set-Cookie") != null) {
            sessionID = response.getFirstHeader("Set-Cookie").getValue();
        }
        if (sessionID == null) {
            return result;
        }
        if (null == lastSessionId) {
            lastSessionId = sessionID;
        } else {
            String msg;
            if (sessionID != lastSessionId) {
                msg = "Http session must not change but did. Old: " + lastSessionId + ", new: " + sessionID + ".\nFull response: " + response;
                LOG.severe(msg);
                if (throwExceptionOnSessionChange) {
                    throw new IOException(msg);
                }
            }
        }
        return result;
    }

    public void setThrowExceptionOnSessionChange(boolean throwExceptionOnSessionChange) {
        this.throwExceptionOnSessionChange = throwExceptionOnSessionChange;
    }
}
