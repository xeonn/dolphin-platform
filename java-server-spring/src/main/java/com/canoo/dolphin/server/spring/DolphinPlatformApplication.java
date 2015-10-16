package com.canoo.dolphin.server.spring;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Annotation can be used to annotate a Spring based Dolphin Platform annotation.
 * This annotation does mainly the same as {@link SpringBootApplication} but adds the
 * Dolphin Platform bootstrap.
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
@SpringBootApplication
@Import(DolphinPlatformSpringBootstrap.class)
public @interface DolphinPlatformApplication {
}
