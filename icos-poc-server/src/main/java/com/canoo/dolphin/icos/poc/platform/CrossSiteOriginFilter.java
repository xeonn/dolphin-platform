package com.canoo.dolphin.icos.poc.platform;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CrossSiteOriginFilter implements Filter {

    @Override
    public void init(FilterConfig inFilterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest inServletRequest, ServletResponse inServletResponse, FilterChain inFilterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) inServletRequest;
        HttpServletResponse resp = (HttpServletResponse) inServletResponse;

        String clientOrigin = req.getHeader("origin");
        resp.setHeader("Access-Control-Allow-Origin", clientOrigin);
        resp.setHeader("Access-Control-Allow-Methods", "*");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Max-Age", "86400");

        inFilterChain.doFilter(inServletRequest, inServletResponse);
    }

    @Override
    public void destroy() {}
}
