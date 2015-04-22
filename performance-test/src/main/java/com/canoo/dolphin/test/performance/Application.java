package com.canoo.dolphin.test.performance;

import com.canoo.dolphin.server.spring.DolphinPlatformSpringBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String... args) {
        SpringApplication.run(new Class[]{Application.class, DolphinPlatformSpringBootstrap.class}, args);
    }

}
