package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.ClientConfiguration;
import javafx.application.Platform;
import org.opendolphin.StringUtil;

/**
 * Created by hendrikebbers on 15.09.15.
 */
public class JavaFXConfiguration extends ClientConfiguration {

    public JavaFXConfiguration(String serverEndpoint) {
        super(serverEndpoint, r -> Platform.runLater(r));
    }

    public static JavaFXConfiguration create(String serverEndpoint) {
        return new JavaFXConfiguration(serverEndpoint);
    }
}
