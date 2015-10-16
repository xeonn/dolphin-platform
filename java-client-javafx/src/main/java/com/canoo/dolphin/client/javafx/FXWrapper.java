package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class FXWrapper {


    private FXWrapper() {
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.DoubleProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static DoubleProperty wrapDoubleProperty(final Property<Double> dolphinProperty) {
        final DoubleProperty property = new SimpleDoubleProperty();
        FXBinder.bind(property).bidirectionalToNumeric(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.FloatProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static FloatProperty wrapFloatProperty(final Property<Float> dolphinProperty) {
        final FloatProperty property = new SimpleFloatProperty();
        FXBinder.bind(property).bidirectionalToNumeric(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.IntegerProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static IntegerProperty wrapIntProperty(final Property<Integer> dolphinProperty) {
        final IntegerProperty property = new SimpleIntegerProperty();
        FXBinder.bind(property).bidirectionalToNumeric(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.LongProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static LongProperty wrapLongProperty(final Property<Long> dolphinProperty) {
        final LongProperty property = new SimpleLongProperty();
        FXBinder.bind(property).bidirectionalToNumeric(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.BooleanProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static BooleanProperty wrapBooleanProperty(final Property<Boolean> dolphinProperty) {
        final BooleanProperty property = new SimpleBooleanProperty();
        FXBinder.bind(property).bidirectionalTo(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.StringProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static StringProperty wrapStringProperty(final Property<String> dolphinProperty) {
        StringProperty property = new SimpleStringProperty();
        FXBinder.bind(property).bidirectionalTo(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.ObjectProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static <T> ObjectProperty<T> wrapObjectProperty(final Property<T> dolphinProperty) {
        final ObjectProperty<T> property = new SimpleObjectProperty<>();
        FXBinder.bind(property).bidirectionalTo(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.collections.ObservableList} wrapper for a dolphin platform list
     *
     * @param dolphinList the dolphin platform list
     * @param <T>         type of the list content
     * @return the JavaFX list
     */
    public static <T> ObservableList<T> wrapList(com.canoo.dolphin.collections.ObservableList<T> dolphinList) {
        final ObservableList<T> list = FXCollections.observableArrayList(dolphinList);

        list.addListener((ListChangeListener<T>) c -> {
            if (listenToFx) {
                listenToDolphin = false;
                while (c.next()) {
                    if (c.wasAdded() || c.wasRemoved() || c.wasReplaced()) {
                        for (T removed : c.getRemoved()) {
                            dolphinList.remove(removed);
                        }
                        for (T added : c.getAddedSubList()) {
                            dolphinList.add(list.indexOf(added), added);
                        }
                    }
                }
                listenToDolphin = true;
            }
        });

        dolphinList.onChanged(e -> {
            if (listenToDolphin) {
                listenToFx = false;
                for (ListChangeEvent.Change<? extends T> c : e.getChanges()) {
                    if (c.isAdded()) {
                        for (int i = c.getFrom(); i <= c.getTo() - 1; i++) {
                            list.add(i, dolphinList.get(i));
                        }
                    } else if (c.isRemoved()) {
                        list.remove(c.getFrom(), c.getTo());
                    }
                }
                listenToFx = true;
            }
        });

        return list;
    }

    //TODO: HACK
    private static boolean listenToFx = true;

    //TODO: HACK
    private static boolean listenToDolphin = true;
}
