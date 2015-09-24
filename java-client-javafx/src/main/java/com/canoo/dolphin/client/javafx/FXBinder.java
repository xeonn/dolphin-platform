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

    private static final double EPSILON = 1e-6;

    private FXBinder() {
    }

    public static void bindBidirectional(final DoubleProperty javaFxProperty, final Property<Double> dolphinProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.doubleValue());
            } else if (Math.abs(dolphinProperty.get() - newV.doubleValue()) < EPSILON) {
                dolphinProperty.set(newV.doubleValue());
            }
        });
        bind(javaFxProperty, dolphinProperty);
    }

    public static void bind(final DoubleProperty javaFxProperty, final Property<Double> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if (javaFxProperty.getValue() == null && e.getNewValue() != null) {
                javaFxProperty.set(e.getNewValue());
            } else if (Math.abs(dolphinProperty.get() - e.getNewValue().doubleValue()) < EPSILON) {
                javaFxProperty.set(e.getNewValue().doubleValue());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<Double> dolphinProperty, final ReadOnlyDoubleProperty javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.doubleValue());
            } else if (dolphinProperty.get() != newV.doubleValue()) {
                dolphinProperty.set(newV.doubleValue());
            }
        });
        dolphinProperty.set(javaFxProperty.get());
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.DoubleProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static DoubleProperty wrapDoubleProperty(final Property<Double> dolphinProperty) {
        final DoubleProperty property = new SimpleDoubleProperty();
        bindBidirectional(property, dolphinProperty);
        return property;
    }

    public static void bindBidirectional(final IntegerProperty javaFxProperty, final Property<Integer> dolphinProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.intValue());
            } else if (dolphinProperty.get() != newV) {
                dolphinProperty.set(newV.intValue());
            }
        });
        bind(javaFxProperty, dolphinProperty);
    }

    public static void bind(final IntegerProperty javaFxProperty, final Property<Integer> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if (javaFxProperty.getValue() == null && e.getNewValue() != null) {
                javaFxProperty.set(e.getNewValue());
            } else if (javaFxProperty.get() != e.getNewValue().intValue()) {
                javaFxProperty.set(e.getNewValue());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<Integer> dolphinProperty, final ReadOnlyIntegerProperty javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.intValue());
            } else if (dolphinProperty.get().intValue() != newV.intValue()) {
                dolphinProperty.set(newV.intValue());
            }
        });
        dolphinProperty.set(javaFxProperty.get());
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.IntegerProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static IntegerProperty wrapIntProperty(final Property<Integer> dolphinProperty) {
        final IntegerProperty property = new SimpleIntegerProperty();
        bindBidirectional(property, dolphinProperty);
        return property;
    }

    public static void bindBidirectional(final FloatProperty javaFxProperty, final Property<Float> dolphinProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.floatValue());
            } else if (dolphinProperty.get() != newV.floatValue()) {
                dolphinProperty.set(newV.floatValue());
            }
        });
        bind(javaFxProperty, dolphinProperty);
    }

    public static void bind(final FloatProperty javaFxProperty, final Property<Float> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if (javaFxProperty.getValue() == null && e.getNewValue() != null) {
                javaFxProperty.set(e.getNewValue());
            } else if (dolphinProperty.get() != e.getNewValue().doubleValue()) {
                javaFxProperty.set(e.getNewValue());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<Float> dolphinProperty, final ReadOnlyFloatProperty javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.floatValue());
            } else if (dolphinProperty.get() != newV.doubleValue()) {
                dolphinProperty.set(newV.floatValue());
            }
        });
        dolphinProperty.set(javaFxProperty.get());
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.FloatProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static FloatProperty wrapFloatProperty(final Property<Float> dolphinProperty) {
        final FloatProperty property = new SimpleFloatProperty();
        bindBidirectional(property, dolphinProperty);
        return property;
    }

    public static void bindBidirectional(final LongProperty javaFxProperty, final Property<Long> dolphinProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.longValue());
            } else if (dolphinProperty.get() != newV) {
                dolphinProperty.set(newV.longValue());
            }
        });
        bind(javaFxProperty, dolphinProperty);
    }

    public static void bind(final LongProperty javaFxProperty, final Property<Long> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if (javaFxProperty.getValue() == null && e.getNewValue() != null) {
                javaFxProperty.set(e.getNewValue());
            } else if (javaFxProperty.get() != e.getNewValue().longValue()) {
                javaFxProperty.set(e.getNewValue());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<Long> dolphinProperty, final ReadOnlyLongProperty javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV.longValue());
            } else if (dolphinProperty.get().longValue() != newV.longValue()) {
                dolphinProperty.set(newV.longValue());
            }
        });
        dolphinProperty.set(javaFxProperty.get());
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.LongProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static LongProperty wrapLongProperty(final Property<Long> dolphinProperty) {
        final LongProperty property = new SimpleLongProperty();
        bindBidirectional(property, dolphinProperty);
        return property;
    }

    public static void bindBidirectional(final BooleanProperty javaFxProperty, final Property<Boolean> dolphinProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV);
            } else if (dolphinProperty.get() != newV) {
                dolphinProperty.set(newV);
            }
        });
        bind(javaFxProperty, dolphinProperty);
    }

    public static void bind(final BooleanProperty javaFxProperty, final Property<Boolean> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if (javaFxProperty.getValue() == null && e.getNewValue() != null) {
                javaFxProperty.set(e.getNewValue());
            } else if (javaFxProperty.get() != e.getNewValue()) {
                javaFxProperty.set(e.getNewValue());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<Boolean> dolphinProperty, final ReadOnlyBooleanProperty javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV);
            } else if (dolphinProperty.get() != newV) {
                dolphinProperty.set(newV);
            }
        });
        dolphinProperty.set(javaFxProperty.get());
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.BooleanProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static BooleanProperty wrapBooleanProperty(final Property<Boolean> dolphinProperty) {
        final BooleanProperty property = new SimpleBooleanProperty();
        bindBidirectional(property, dolphinProperty);
        return property;
    }

    public static void bindBidirectional(final StringProperty javaFxProperty, final Property<String> dolphinProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV);
            } else if (dolphinProperty.get() != newV) {
                dolphinProperty.set(newV);
            }
        });
        bind(javaFxProperty, dolphinProperty);
    }

    public static void bind(final StringProperty javaFxProperty, final Property<String> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if (javaFxProperty.getValue() == null && e.getNewValue() != null) {
                javaFxProperty.set(e.getNewValue());
            } else if (javaFxProperty.get() != e.getNewValue()) {
                javaFxProperty.set(e.getNewValue());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<String> dolphinProperty, final ReadOnlyStringProperty javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV);
            } else if (dolphinProperty.get() != newV) {
                dolphinProperty.set(newV);
            }
        });
        dolphinProperty.set(javaFxProperty.get());
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.StringProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static StringProperty wrapStringProperty(final Property<String> dolphinProperty) {
        StringProperty property = new SimpleStringProperty();
        bindBidirectional(property, dolphinProperty);
        return property;
    }

    public static <T> void bindBidirectional(final ObjectProperty<T> javaFxProperty, final Property<T> dolphinProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV);
            } else if (dolphinProperty.get() != newV) {
                dolphinProperty.set(newV);
            }
        });
        bind(javaFxProperty, dolphinProperty);
    }

    public static <T> void bind(final ObjectProperty<T> javaFxProperty, final Property<T> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if (javaFxProperty.getValue() == null && e.getNewValue() != null) {
                javaFxProperty.set(e.getNewValue());
            } else if (javaFxProperty.get() != e.getNewValue()) {
                javaFxProperty.set(e.getNewValue());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static <T> void bind(final Property<T> dolphinProperty, final ReadOnlyObjectProperty<T> javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if (dolphinProperty.get() == null && newV != null) {
                dolphinProperty.set(newV);
            } else if (dolphinProperty.get() != newV) {
                dolphinProperty.set(newV);
            }
        });
        dolphinProperty.set(javaFxProperty.get());
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.ObjectProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static <T> ObjectProperty<T> wrapObjectProperty(final Property<T> dolphinProperty) {
        final ObjectProperty<T> property = new SimpleObjectProperty<>();
        bindBidirectional(property, dolphinProperty);
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
