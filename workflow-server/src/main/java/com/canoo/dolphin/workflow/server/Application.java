package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.workflow.server.platform.DolphinBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void start(String... args) {
        SpringApplication.run(new Class[]{Application.class, DolphinBootstrap.class}, args);
    }

    public static void main(String[] args) throws Exception {
        start(args);
    }
}
