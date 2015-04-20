package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Method to create JavaFX property wrappers for dolphin platform properties
 */
public class JavaFXBinder {

    /**
     * Create a JavaFX {@link javafx.beans.property.DoubleProperty} as a wrapper for a dolphin platform property
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static DoubleProperty wrapDoubleProperty(final Property<Double> dolphinProperty) {
        final DoubleProperty property = new SimpleDoubleProperty(dolphinProperty.get());

        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get().doubleValue() != newV.doubleValue()) {
                dolphinProperty.set(newV.doubleValue());
            }
        });

        dolphinProperty.subscribeToValueChanges(e -> {
            if (property.get() != e.getNewValue().doubleValue()) {
                property.set(e.getNewValue().doubleValue());
            }
        });

        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.IntegerProperty} as a wrapper for a dolphin platform property
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static IntegerProperty wrapIntProperty(final Property<Integer> dolphinProperty) {
        final IntegerProperty property = new SimpleIntegerProperty(dolphinProperty.get());

        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get().intValue() != newV.intValue()) {
                dolphinProperty.set(newV.intValue());
            }
        });

        dolphinProperty.subscribeToValueChanges(e -> {
            if (property.get() != e.getNewValue().intValue()) {
                property.set(e.getNewValue().intValue());
            }
        });

        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.FloatProperty} as a wrapper for a dolphin platform property
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static FloatProperty wrapFloatProperty(final Property<Float> dolphinProperty) {
        final FloatProperty property = new SimpleFloatProperty(dolphinProperty.get());

        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get().floatValue() != newV.floatValue()) {
                dolphinProperty.set(newV.floatValue());
            }
        });

        dolphinProperty.subscribeToValueChanges(e -> {
            if (property.get() != e.getNewValue().floatValue()) {
                property.set(e.getNewValue().floatValue());
            }
        });

        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.LongProperty} as a wrapper for a dolphin platform property
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static LongProperty wrapLongProperty(final Property<Long> dolphinProperty) {
        final LongProperty property = new SimpleLongProperty(dolphinProperty.get());

        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get().longValue() != newV.longValue()) {
                dolphinProperty.set(newV.longValue());
            }
        });

        dolphinProperty.subscribeToValueChanges(e -> {
            if (property.get() != e.getNewValue().longValue()) {
                property.set(e.getNewValue().longValue());
            }
        });

        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.BooleanProperty} as a wrapper for a dolphin platform property
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static BooleanProperty wrapBooleanProperty(final Property<Boolean> dolphinProperty) {
        final BooleanProperty property = new SimpleBooleanProperty(dolphinProperty.get());

        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get().booleanValue() != newV.booleanValue()) {
                dolphinProperty.set(newV.booleanValue());
            }
        });

        dolphinProperty.subscribeToValueChanges(e -> {
            if (property.get() != e.getNewValue().booleanValue()) {
                property.set(e.getNewValue().booleanValue());
            }
        });

        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.StringProperty} as a wrapper for a dolphin platform property
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static StringProperty wrapStringProperty(final Property<String> dolphinProperty) {
        final StringProperty property = new SimpleStringProperty(dolphinProperty.get());

        property.addListener((obs, oldV, newV) -> {
            if (!dolphinProperty.get().equals(newV)) {
                dolphinProperty.set(newV);
            }
        });

        dolphinProperty.subscribeToValueChanges(e -> {
            if (!property.get().equals(e.getNewValue())) {
                property.set(e.getNewValue());
            }
        });

        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.ObjectProperty} as a wrapper for a dolphin platform property
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static <T> ObjectProperty<T> wrapObjectProperty(final Property<T> dolphinProperty) {
        final ObjectProperty<T> property = new SimpleObjectProperty<>(dolphinProperty.get());

        property.addListener((obs, oldV, newV) -> {
            if (!dolphinProperty.get().equals(newV)) {
                dolphinProperty.set(newV);
            }
        });

        dolphinProperty.subscribeToValueChanges(e -> {
            if (!property.get().equals(e.getNewValue())) {
                property.set(e.getNewValue());
            }
        });

        return property;
    }

    /**
     * Create a JavaFX {@link javafx.collections.ObservableList} wrapper for a dolphin platform list
     * @param dolphinList the dolphin platform list
     * @param <T> type of the list content
     * @return the JavaFX list
     */
    public static <T> ObservableList<T> wrapList(com.canoo.dolphin.collections.ObservableList<T> dolphinList) {
        final ObservableList<T> list = FXCollections.observableArrayList(dolphinList);

        com.canoo.dolphin.collections.ListChangeListener<T> dolphinListener = null;

        list.addListener((ListChangeListener<T>)c -> {
            if(listenToFx) {
                listenToDolphin = false;
                while (c.next()) {
                    if (c.wasPermutated()) {
                        //TODO
                    } else if (c.wasUpdated()) {
                        //TODO
                    } else {
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

        dolphinList.subscribeToListChanges(e -> {
            if (listenToDolphin) {
                listenToFx = false;
                for (ListChangeEvent.Change<? extends T> c : e.getChanges()) {
                    if (c.isAdded()) {
                        for (int i = c.getFrom(); i <= c.getTo(); i++) {
                            list.add(i, dolphinList.get(i));
                        }
                    } else if (c.isRemoved()) {
                        list.remove(c.getFrom(), c.getTo());
                    } else {
                        //TODO
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
