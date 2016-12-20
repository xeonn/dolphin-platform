package org.opendolphin.core.client.comm;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.Command;

import java.io.File;
import java.net.URI;
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

            if (commands.size() == 1 && DefaultGroovyMethods.first(commands).equals(getReleaseCommand())) {// todo dk: ok, this is not nice...
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

    public String uploadFile(File file, URI handler) {
        try {
            HttpPost httpPost = new HttpPost(handler);
            httpPost.setEntity(new FileEntity(file, charset));
            String result = httpClient.execute(httpPost, responseHandler);
            LOG.finest(result);
            return result;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "cannot transmit", ex);
            throw new RuntimeException("Error!", ex);
        }

    }

}
