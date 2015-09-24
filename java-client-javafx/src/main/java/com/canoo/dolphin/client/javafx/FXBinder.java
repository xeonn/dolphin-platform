package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.property.*;
import javafx.beans.value.*;
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
        bind(javaFxProperty, dolphinProperty);
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.getValue());
            } else if (Math.abs(dolphinProperty.get().doubleValue() - javaFxProperty.getValue().doubleValue()) > EPSILON) {
                dolphinProperty.set(javaFxProperty.getValue());
            }
        });
    }

    public static void bind(final DoubleProperty javaFxProperty, final Property<Double> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                javaFxProperty.set(dolphinProperty.get());
            } else if (Math.abs(dolphinProperty.get().doubleValue() - javaFxProperty.getValue().doubleValue()) > EPSILON) {
                javaFxProperty.set(dolphinProperty.get());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<Double> dolphinProperty, final ObservableDoubleValue javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.get());
            } else if (Math.abs(dolphinProperty.get().doubleValue() - javaFxProperty.getValue().doubleValue()) > EPSILON) {
                dolphinProperty.set(javaFxProperty.get());
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

    public static void bindBidirectional(final FloatProperty javaFxProperty, final Property<Float> dolphinProperty) {
        bind(javaFxProperty, dolphinProperty);
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.getValue());
            } else if (Math.abs(dolphinProperty.get().floatValue() - javaFxProperty.getValue().floatValue()) > EPSILON) {
                dolphinProperty.set(javaFxProperty.getValue());
            }
        });
    }

    public static void bind(final FloatProperty javaFxProperty, final Property<Float> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                javaFxProperty.set(dolphinProperty.get());
            } else if (Math.abs(dolphinProperty.get().floatValue() - javaFxProperty.getValue().floatValue()) > EPSILON) {
                javaFxProperty.set(dolphinProperty.get());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<Float> dolphinProperty, final ObservableFloatValue javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.get());
            } else if (Math.abs(dolphinProperty.get().floatValue() - javaFxProperty.getValue().floatValue()) > EPSILON) {
                dolphinProperty.set(javaFxProperty.get());
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

    public static void bindBidirectional(final IntegerProperty javaFxProperty, final Property<Integer> dolphinProperty) {
        bind(javaFxProperty, dolphinProperty);
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.getValue());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                dolphinProperty.set(javaFxProperty.getValue());
            }
        });
    }

    public static void bind(final IntegerProperty javaFxProperty, final Property<Integer> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                javaFxProperty.set(dolphinProperty.get());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                javaFxProperty.set(dolphinProperty.get());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<Integer> dolphinProperty, final ObservableIntegerValue javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.get());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                dolphinProperty.set(javaFxProperty.get());
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



    public static void bindBidirectional(final LongProperty javaFxProperty, final Property<Long> dolphinProperty) {
        bind(javaFxProperty, dolphinProperty);
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.getValue());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                dolphinProperty.set(javaFxProperty.getValue());
            }
        });
    }

    public static void bind(final LongProperty javaFxProperty, final Property<Long> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                javaFxProperty.set(dolphinProperty.get());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                javaFxProperty.set(dolphinProperty.get());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<Long> dolphinProperty, final ObservableLongValue javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.get());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                dolphinProperty.set(javaFxProperty.get());
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
        bind(javaFxProperty, dolphinProperty);
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.getValue());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                dolphinProperty.set(javaFxProperty.getValue());
            }
        });
    }

    public static void bind(final BooleanProperty javaFxProperty, final Property<Boolean> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                javaFxProperty.set(dolphinProperty.get());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                javaFxProperty.set(dolphinProperty.get());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<Boolean> dolphinProperty, final ObservableBooleanValue javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.getValue());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                dolphinProperty.set(javaFxProperty.getValue());
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
        bind(javaFxProperty, dolphinProperty);
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.getValue());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                dolphinProperty.set(javaFxProperty.getValue());
            }
        });
    }

    public static void bind(final StringProperty javaFxProperty, final Property<String> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                javaFxProperty.set(dolphinProperty.get());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                javaFxProperty.set(dolphinProperty.get());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static void bind(final Property<String> dolphinProperty, final ObservableStringValue javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.getValue());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                dolphinProperty.set(javaFxProperty.getValue());
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
        bind(javaFxProperty, dolphinProperty);
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.getValue());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                dolphinProperty.set(javaFxProperty.getValue());
            }
        });
    }

    public static <T> void bind(final ObjectProperty<T> javaFxProperty, final Property<T> dolphinProperty) {
        dolphinProperty.onChanged(e -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                javaFxProperty.set(dolphinProperty.get());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                javaFxProperty.set(dolphinProperty.get());
            }
        });
        javaFxProperty.setValue(dolphinProperty.get());
    }

    public static <T> void bind(final Property<T> dolphinProperty, final ObservableObjectValue<T> javaFxProperty) {
        javaFxProperty.addListener((obs, oldV, newV) -> {
            if(dolphinProperty.get() == null && javaFxProperty.getValue() == null) {
                //Do nothing
            } else if(dolphinProperty.get() == null || javaFxProperty.getValue() == null) {
                dolphinProperty.set(javaFxProperty.getValue());
            } else if (!dolphinProperty.get().equals(javaFxProperty.getValue())) {
                dolphinProperty.set(javaFxProperty.getValue());
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
