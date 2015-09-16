package com.canoo.dolphin.impl;

import com.canoo.dolphin.event.BeanAddedListener;
import com.canoo.dolphin.event.BeanRemovedListener;
import com.canoo.dolphin.event.Subscription;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@code BeanRepository} keeps a list of all registered Dolphin Beans and the mapping between OpenDolphin IDs and
 * the associated Dolphin Bean.
 *
 * A new bean needs to be registered with the {@link #registerBean(Object, PresentationModel, UpdateSource)} method and can be deleted
 * with the {@link #delete(Object)} method.
 */
// TODO mapDolphinToObject() does not really fit here, we should probably move it to Converters, but first we need to fix scopes
public class BeanRepository {

    enum UpdateSource {SELF, OTHER}

    private final Map<Object, PresentationModel> objectPmToDolphinPm = new HashMap<>();
    private final Map<String, Object> dolphinIdToObjectPm = new HashMap<>();
    private final Dolphin dolphin;
    private final Multimap<Class<?>, BeanAddedListener<?>> beanAddedListenerMap = ArrayListMultimap.create();
    private List<BeanAddedListener<Object>> anyBeanAddedListeners = new ArrayList<>();
    private final Multimap<Class<?>, BeanRemovedListener<?>> beanRemovedListenerMap = ArrayListMultimap.create();
    private List<BeanRemovedListener<Object>> anyBeanRemovedListeners = new ArrayList<>();

    public BeanRepository(Dolphin dolphin, EventDispatcher dispatcher) {
        this.dolphin = dolphin;

        dispatcher.addRemovedHandler(new EventDispatcher.DolphinEventHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public void onEvent(PresentationModel model) {
                final Object bean = dolphinIdToObjectPm.remove(model.getId());
                if (bean != null) {
                    objectPmToDolphinPm.remove(bean);
                    for (final BeanRemovedListener beanRemovedListener : beanRemovedListenerMap.get(bean.getClass())) {
                        beanRemovedListener.beanDestructed(bean);
                    }
                    for (final BeanRemovedListener beanRemovedListener : anyBeanRemovedListeners) {
                        beanRemovedListener.beanDestructed(bean);
                    }
                }
            }
        });
    }

    <T> Subscription addOnAddedListener(final Class<T> clazz, final BeanAddedListener<? super T> listener) {
        beanAddedListenerMap.put(clazz, listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                beanAddedListenerMap.remove(clazz, listener);
            }
        };
    }

    Subscription addOnAddedListener(final BeanAddedListener<Object> listener) {
        anyBeanAddedListeners.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                anyBeanAddedListeners.remove(listener);
            }
        };
    }

    <T> Subscription addOnRemovedListener(final Class<T> clazz, final BeanRemovedListener<? super T> listener) {
        beanRemovedListenerMap.put(clazz, listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                beanRemovedListenerMap.remove(clazz, listener);
            }
        };
    }

    Subscription addOnRemovedListener(final BeanRemovedListener<Object> listener) {
        anyBeanRemovedListeners.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                anyBeanRemovedListeners.remove(listener);
            }
        };
    }

    public boolean isManaged(Object bean) {
        return objectPmToDolphinPm.containsKey(bean);
    }

    public <T> void delete(T bean) {
        final PresentationModel model = objectPmToDolphinPm.remove(bean);
        if (model != null) {
            dolphinIdToObjectPm.remove(model.getId());
            dolphin.remove(model);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Class<T> beanClass) {
        final List<T> result = new ArrayList<>();
        final List<PresentationModel> presentationModels = dolphin.findAllPresentationModelsByType(DolphinUtils.getDolphinPresentationModelTypeForClass(beanClass));
        for (PresentationModel model : presentationModels) {
            result.add((T) dolphinIdToObjectPm.get(model.getId()));
        }
        return result;
    }

    public Object getBean(String sourceId) {
        return dolphinIdToObjectPm.get(sourceId);
    }

    public String getDolphinId(Object bean) {
        try {
            return objectPmToDolphinPm.get(bean).getId();
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("Only managed Dolphin Beans can be used.", ex);
        }
    }

    public Object mapDolphinToObject(Object value, ClassRepository.FieldType fieldType) {
        return fieldType == ClassRepository.FieldType.DOLPHIN_BEAN? dolphinIdToObjectPm.get(value) : value;
    }

    @SuppressWarnings("unchecked")
    void registerBean(Object bean, PresentationModel model, UpdateSource source) {
        objectPmToDolphinPm.put(bean, model);
        dolphinIdToObjectPm.put(model.getId(), bean);

        if (source == UpdateSource.OTHER) {
            for (final BeanAddedListener beanAddedListener : beanAddedListenerMap.get(bean.getClass())) {
                beanAddedListener.beanCreated(bean);
            }
            for (final BeanAddedListener beanAddedListener : anyBeanAddedListeners) {
                beanAddedListener.beanCreated(bean);
            }
        }
    }
}
