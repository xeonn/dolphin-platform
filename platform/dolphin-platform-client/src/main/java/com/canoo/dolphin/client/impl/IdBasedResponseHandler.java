package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.DolphinSessionException;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.util.Assert;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by hendrikebbers on 03.06.16.
 */
public class IdBasedResponseHandler implements ResponseHandler<String> {

    private final DolphinPlatformHttpClientConnector clientConnector;

    private String lastSessionId = null;

    IdBasedResponseHandler(DolphinPlatformHttpClientConnector clientConnector) {
        this.clientConnector = Assert.requireNonNull(clientConnector, "clientConnector");
    }

    @Override
    public String handleResponse(HttpResponse response) throws IOException {
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity entity = response.getEntity();

        if (statusLine.getStatusCode() == 408) {
            EntityUtils.consume(entity);
            throw new DolphinSessionException("Server can not handle Dolphin Client ID");
        }

        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }

        try {
            final Header dolphinHeader = response.getFirstHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME);
            clientConnector.setClientId(dolphinHeader.getValue());
        } catch (Exception e) {
            throw new DolphinSessionException("Error in handling Dolphin Client ID", e);
        }

        String sessionID = null;
        Header cookieHeader = response.getFirstHeader("Set-Cookie");
        if (cookieHeader != null) {
            sessionID = cookieHeader.getValue();
            if (lastSessionId != null) {
                throw new DolphinSessionException("Http session must not change but did. Old: " + lastSessionId + ", new: " + sessionID);
            }
            lastSessionId = sessionID;
        }
        return entity == null ? null : EntityUtils.toString(entity);
    }
}