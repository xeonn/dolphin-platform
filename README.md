# Dolphin Platform

This repository contains all Java related sources of the Dolphin Platform. Clients for other languages can be found in seperate repositories (JavaScript, AngularJS, Polymer).

![Dolphin Platform Logo](http://www.guigarage.com/wordpress/wp-content/uploads/2015/10/logo.png)

The Dolphin Platform is a framework that implements the presentation model pattern and provides a modern way to create enterprise applications. The Platform provides several client implementations that all can be used in combination with a general sever API.

![Several clients](http://i2.wp.com/www.guigarage.com/wordpress/wp-content/uploads/2015/09/clients.png)


By doing so you can create enterprise application with a single server and several desktop, web and mobile client implementations. Here the Dolphin Platforms define a mechanism to automatically snchronize models between the server and the client.

![Model sync](http://i0.wp.com/www.guigarage.com/wordpress/wp-content/uploads/2015/09/pm1.png)


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

A complete "Getting started" documentation can be found [here](http://www.dolphin-platform.io/documentation/getting-started.html).
