package com.canoo.dolphin.client.javafx.stage;

import com.canoo.dolphin.client.javafx.AbstractViewBinder;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.util.Assert;
import javafx.event.EventHandler;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Class that provides some helper methods.
 */
public class DolphinWindowUtils {

    /**
     * The method will register an event handler to the window that will automatically call the {@link AbstractViewBinder#destroy()}
     * method when the windows becomes hidden.
     * @param window the window
     * @param viewBinder the view binder
     * @param <M> the model type
     * @return a subscription to unsubsribe / deregister the handler.
     */
    public static <M> Subscription destroyOnClose(final Window window, final AbstractViewBinder<M> viewBinder) {
        Assert.requireNonNull(window, "window");
        Assert.requireNonNull(viewBinder, "viewBinder");
        final EventHandler<WindowEvent> handler = e -> viewBinder.destroy();
        window.addEventFilter(WindowEvent.WINDOW_HIDDEN, handler);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                window.removeEventFilter(WindowEvent.WINDOW_HIDDEN, handler);
            }
        };
    }

}
