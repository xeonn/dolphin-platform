package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.server.spring.DolphinPlatformSpringBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@SpringBootApplication
public class Application {

    @Inject
    WorkflowBootStrap workflowBootStrap;

    @PostConstruct
    private void init() {
        workflowBootStrap.onStartup();
    }

    public static void main(String... args) {
        SpringApplication.run(new Class[]{Application.class, DolphinPlatformSpringBootstrap.class}, args);
    }

}
