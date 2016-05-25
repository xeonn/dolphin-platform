package com.canoo.dolphin.client.javafx.stage;

import com.canoo.dolphin.client.javafx.AbstractViewBinder;
import com.canoo.dolphin.util.Assert;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A JavaFX {@link Stage} that contains the view of a {@link AbstractViewBinder} and will automatically call
 * {@link AbstractViewBinder#destroy()} when the stage becomes hidden.
 * @param <M> type of the model
 */
public class DolphinStage<M> extends Stage {

    /**
     * Constructor
     * @param viewBinder the viewBinder
     */
    public DolphinStage(final AbstractViewBinder<M> viewBinder) {
        Assert.requireNonNull(viewBinder, "viewBinder");
        DolphinWindowUtils.destroyOnClose(this, viewBinder);
        setScene(new Scene(viewBinder.getParent()));
    }
}
