package com.canoo.dolphin.icos.poc;

import com.canoo.dolphin.icos.poc.platform.DolphinBootstrap;
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
