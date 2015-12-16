package com.canoo.dolphin.test;

import com.canoo.dolphin.test.impl.DolphinPlatformSpringTestBootstrap;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@SpringApplicationConfiguration(classes = DolphinPlatformSpringTestBootstrap.class)
public abstract class AbstractSpringTest extends AbstractTestNGSpringContextTests {}

