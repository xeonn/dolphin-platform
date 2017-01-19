/*
 * Copyright 2015-2017 Canoo Engineering AG.
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
package com.canoo.dolphin.integration;

import com.canoo.dolphin.client.ClientConfiguration;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ClientContextFactory;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.client.impl.HttpStatus;
import com.canoo.dolphin.util.DolphinRemotingException;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import org.opendolphin.core.client.comm.UiThreadHandler;

public class AbstractIntegrationTest {

    public final static String ENDPOINTS_DATAPROVIDER = "endpoints";

    protected void waitUntilServerIsUp(String host, long time, TimeUnit timeUnit) throws TimeoutException {
        long startTime = System.currentTimeMillis();
        long waitMillis = timeUnit.toMillis(time);
        boolean connected = false;
        while (!connected) {
            if(System.currentTimeMillis() > startTime + waitMillis) {
                throw new TimeoutException("Server " + host + " is still down after " + waitMillis + " ms");
            }
            try {
                URL healthUrl = new URL(host + "/health");
                URLConnection connection = healthUrl.openConnection();
                if(connection instanceof HttpURLConnection) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
                    httpURLConnection.connect();
                    if(httpURLConnection.getResponseCode() == HttpStatus.HTTP_OK) {
                        connected = true;
                    }
                } else {
                    throw new IOException("URL " + healthUrl + " do not provide a HttpURLConnection!");
                }
            } catch (Exception e) {
                //
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected <T> ControllerProxy<T> createController(ClientContext clientContext, String controllerName) {
        try {
            return (ControllerProxy<T>) clientContext.createController(controllerName).get(5_000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new DolphinRemotingException("Can not create controller " + controllerName, e);
        }
    }

    protected ClientContext createClientContext(String endpoint) {
        try {
            waitUntilServerIsUp(endpoint, 5, TimeUnit.MINUTES);
            ClientConfiguration configuration = new ClientConfiguration(new URL(endpoint + "/dolphin"), new UiThreadHandler() {
                @Override
                public void executeInsideUiThread(Runnable r) {
                    r.run();
                }
            });
            configuration.setDolphinLogLevel(Level.FINE);
            configuration.setConnectionTimeout(10_000L);
            return ClientContextFactory.connect(configuration).get(configuration.getConnectionTimeout(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new DolphinRemotingException("Can not create client context for endpoint " + endpoint, e);
        }
    }

    protected void invoke(ControllerProxy<?> controllerProxy, String actionName, String endpoint, Param... params) {
        try {
            controllerProxy.invoke(actionName, params).get(5_000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new DolphinRemotingException("Can not handle action " + actionName + " for endpoint " + endpoint, e);
        }
    }

    protected void destroy(ControllerProxy<?> controllerProxy, String endpoint) {
        try {
            controllerProxy.destroy().get(5_000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new DolphinRemotingException("Can not destroy controller for endpoint " + endpoint, e);
        }
    }

    protected void sleep(long time, TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(time));
        } catch (Exception e) {
            throw new DolphinRemotingException("Can not sleep :(", e);
        }
    }

    @DataProvider(name = ENDPOINTS_DATAPROVIDER, parallel = true)
    public Object[][] getEndpoints() {
        return new String[][]{{"Payara", "http://localhost:8081/todo-app"},
                {"TomEE", "http://localhost:8082/todo-app"},
                {"Wildfly", "http://localhost:8083/todo-app"}//,
                //{"Spring-Boot", "http://localhost:8084/todo-app"}
        };
    }
}
