package com.canoo.dolphin.client.javafx.stage;

import com.canoo.dolphin.client.javafx.AbstractViewBinder;
import com.canoo.dolphin.util.Assert;
import javafx.scene.Scene;
import javafx.stage.Window;

/**
 * A JavaFX {@link Window} that contains the view of a {@link AbstractViewBinder} and will automatically call
 * {@link AbstractViewBinder#destroy()} when the stage becomes hidden.
 * @param <M> type of the model
 */
public class DolphinWindow<M> extends Window {

    /**
     * Constructor
     * @param viewBinder the viewBinder
     */
    public DolphinWindow(final AbstractViewBinder<M> viewBinder) {
        Assert.requireNonNull(viewBinder, "viewBinder");
        DolphinWindowUtils.destroyOnClose(this, viewBinder);
        setScene(new Scene(viewBinder.getParent()));
    }
}

