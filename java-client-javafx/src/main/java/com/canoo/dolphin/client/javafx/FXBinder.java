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
public class FXBinder {

    private FXBinder() {
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.DoubleProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static DoubleProperty wrapDoubleProperty(final Property<Double> dolphinProperty) {
        final DoubleProperty property = new SimpleDoubleProperty();
        if (dolphinProperty.get() != null) {
            property.setValue(dolphinProperty.get());
        }

        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.doubleValue());
            } else if (dolphinProperty.get() != newV.doubleValue()) {
                dolphinProperty.set(newV.doubleValue());
            }
        });

        dolphinProperty.onChanged(e -> {
            if (property.getValue() == null && e.getNewValue() != null) {
                property.set(e.getNewValue());
            } else if (property.get() != e.getNewValue().doubleValue()) {
                property.set(e.getNewValue().doubleValue());
            }
        });

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
        if (dolphinProperty.get() != null) {
            property.setValue(dolphinProperty.get());
        }

        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.intValue());
            } else if (dolphinProperty.get().intValue() != newV.intValue()) {
                dolphinProperty.set(newV.intValue());
            }
        });

        dolphinProperty.onChanged(e -> {
            if (property.getValue() == null && e.getNewValue() != null) {
                property.set(e.getNewValue());
            } else if (property.get() != e.getNewValue().intValue()) {
                property.set(e.getNewValue().intValue());
            }
        });

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
        if (dolphinProperty.get() != null) {
            property.setValue(dolphinProperty.get());
        }

        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.floatValue());
            } else if (dolphinProperty.get().floatValue() != newV.floatValue()) {
                dolphinProperty.set(newV.floatValue());
            }
        });

        dolphinProperty.onChanged(e -> {
            if (property.getValue() == null && e.getNewValue() != null) {
                property.set(e.getNewValue());
            } else if (property.get() != e.getNewValue().floatValue()) {
                property.set(e.getNewValue().floatValue());
            }
        });

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
        if (dolphinProperty.get() != null) {
            property.setValue(dolphinProperty.get());
        }

        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.longValue());
            } else if (dolphinProperty.get().longValue() != newV.longValue()) {
                dolphinProperty.set(newV.longValue());
            }
        });

        dolphinProperty.onChanged(e -> {
            if (property.getValue() == null && e.getNewValue() != null) {
                property.set(e.getNewValue());
            } else if (property.get() != e.getNewValue().longValue()) {
                property.set(e.getNewValue().longValue());
            }
        });

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
        if (dolphinProperty.get() != null) {
            property.setValue(dolphinProperty.get());
        }


        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.booleanValue());
            } else if (dolphinProperty.get().booleanValue() != newV.booleanValue()) {
                dolphinProperty.set(newV.booleanValue());
            }
        });

        dolphinProperty.onChanged(e -> {
            if (property.getValue() == null && e.getNewValue() != null) {
                property.set(e.getNewValue());
            } else if (property.get() != e.getNewValue().booleanValue()) {
                property.set(e.getNewValue().booleanValue());
            }
        });

        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.StringProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static StringProperty wrapStringProperty(final Property<String> dolphinProperty) {
        final StringProperty property = new SimpleStringProperty();
        if (dolphinProperty.get() != null) {
            property.setValue(dolphinProperty.get());
        }

        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV);
            } else if (!dolphinProperty.get().equals(newV)) {
                dolphinProperty.set(newV);
            }
        });

        dolphinProperty.onChanged(e -> {
            if (property.getValue() == null && e.getNewValue() != null) {
                property.set(e.getNewValue());
            } else if (!property.get().equals(e.getNewValue())) {
                property.set(e.getNewValue());
            }
        });

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
        if (dolphinProperty.get() != null) {
            property.setValue(dolphinProperty.get());
        }

        property.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV);
            } else if (!dolphinProperty.get().equals(newV)) {
                dolphinProperty.set(newV);
            }
        });

        dolphinProperty.onChanged(e -> {
            if (property.getValue() == null && e.getNewValue() != null) {
                property.set(e.getNewValue());
            } else if (!property.get().equals(e.getNewValue())) {
                property.set(e.getNewValue());
            }
        });

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
