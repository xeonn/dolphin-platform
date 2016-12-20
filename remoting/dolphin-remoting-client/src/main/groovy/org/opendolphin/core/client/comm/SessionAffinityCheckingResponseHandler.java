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
            EntityUtils.consume(entity);
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

    public boolean isThrowExceptionOnSessionChange() {
        return throwExceptionOnSessionChange;
    }

    public void setThrowExceptionOnSessionChange(boolean throwExceptionOnSessionChange) {
        this.throwExceptionOnSessionChange = throwExceptionOnSessionChange;
    }
}
