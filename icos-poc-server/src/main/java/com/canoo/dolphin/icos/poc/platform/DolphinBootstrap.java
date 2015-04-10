package com.canoo.dolphin.icos.poc.platform;

import org.opendolphin.server.adapter.InvalidationServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

@Configuration
@ComponentScan
public class DolphinBootstrap implements ServletContextInitializer {

    @Value("${dolphin.servletName:dolphin}")
    private String dolphinServletName;

    @Value("${dolphin.servletMapping:/dolphin}")
    private String dolphinServletMapping;

    @Value("${dolphin.invalidationServletName:invalidate}")
    private String dolphinInvalidationServletName;

    @Value("${dolphin.invalidationSMapping:/dolphininvalidate}")
    private String dolphinInvalidationServletMapping;

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        DolphinCommandRepository repository = applicationContext.getBean(DolphinCommandRepository.class);
        servletContext.addServlet(dolphinServletName, new DefaultDolphinServlet(repository)).addMapping(dolphinServletMapping);
        servletContext.addServlet(dolphinInvalidationServletName, InvalidationServlet.class).addMapping(dolphinInvalidationServletMapping);


        servletContext.addFilter("dolphinCrossSiteFilter", CrossSiteOriginFilter.class).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),true,"/*");
    }

}
