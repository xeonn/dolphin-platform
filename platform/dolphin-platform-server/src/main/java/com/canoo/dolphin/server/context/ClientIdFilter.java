package com.canoo.dolphin.server.context;

import com.canoo.dolphin.impl.PlatformConstants;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by hendrikebbers on 31.05.16.
 */
public class ClientIdFilter implements Filter {

    private static final ThreadLocal<String> currentClientId = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> newClient = new ThreadLocal<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //Nothing to do here
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        newClient.set(false);
        currentClientId.set(null);
        try {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            HttpServletResponse servletResponse = (HttpServletResponse) response;

            String clientId = servletRequest.getHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME);
            if (clientId == null || clientId.trim().isEmpty()) {
                clientId = UUID.randomUUID().toString();
                newClient.set(true);
            }
            currentClientId.set(clientId);

            servletResponse.setHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, clientId);
            servletResponse.setHeader("Content-Type", "application/json");
            servletResponse.setCharacterEncoding("UTF-8");

            chain.doFilter(request, response);
        } finally {
            currentClientId.set(null);
            newClient.set(false);
        }
    }

    @Override
    public void destroy() {
    }

    public static String getCurrentClientId() {
        return currentClientId.get();
    }

    public static boolean isNewClient() {
        return newClient.get();
    }
}
