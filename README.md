# Dolphin Platform

This repository contains all java related sources of the Dolphin Platform. Clients for other languages can be found in seperate repositories (JavaScript, AngularJS, Polymer).

The Dolphin Platform is a client-server-framework to create MVP based application for several clients.

For more information visit [our website](http://www.dolphin-platform.io).

## How to use it
You can simply integrate Dolphin Platform in a Spring based application (JavaEE 7 support will come the next weeks). To do so you only need to add our Spring plugin:
```xml
<dependency>
    <groupId>com.canoo.dolphin-platform</groupId>
    <artifactId>dolphin-platform-server-spring</artifactId>
    <version>0.6.0</version>
</dependency>
```

For a JavaFX based client you need to add the following dependency:
```xml
<dependency>
    <groupId>com.canoo.dolphin-platform</groupId>
    <artifactId>dolphin-platform-client-javafx</artifactId>
    <version>0.6.0</version>
</dependency>
```

A complete "Getting started" documentation can be found [here](http://www.dolphin-platform.io).
