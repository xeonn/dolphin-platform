package com.canoo.dolphin.client.clientscope;

import com.canoo.dolphin.impl.Constants;
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

    private String servletUrl;

    private String clientId;

    private String charset = "UTF-8";

    private DefaultHttpClient httpClient;

    private DefaultHttpClient signalHttpClient;

    private SimpleResponseHandler responseHandler;

    private SimpleResponseHandler signalResponseHandler;

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
            StringEntity entity = new StringEntity(content, charset);
            httpPost.setEntity(entity);

            httpPost.addHeader(Constants.CLIENT_ID_HTTP_HEADER_NAME, clientId);

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
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();

        Header dolphinHeader = response.getFirstHeader(Constants.CLIENT_ID_HTTP_HEADER_NAME);
        if(response != null) {
            clientConnector.setClientId(dolphinHeader.getValue());
        } else {
            throw new DolphinRemotingException("No dolphin id was send from the server!");
        }

        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        String result = entity == null ? null : EntityUtils.toString(entity);
        return result;
    }
}

