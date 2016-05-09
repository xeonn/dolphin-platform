/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.impl.gc;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.DolphinUtils;
import com.canoo.dolphin.impl.IdentitySet;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.impl.UnstableFeatureFlags;
import com.canoo.dolphin.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * The garbage collection for Dolphin Platform models. Whenever a new Dolphin bean {@link com.canoo.dolphin.mapping.DolphinBean}
 * has been created or the hierarchy in a Dolphin model changes the GC will check if the mutated models are still
 * referenced by a root model. In this case a root model is a model as it's defined as a model for a MVC group in
 * Dolphin Platform (see {@link com.canoo.dolphin.server.DolphinModel}).
 */
public class GarbageCollection {

    private final IdentityHashMap<Instance, Object> removeOnGC;

    private final IdentityHashMap<Object, Instance> allInstances;

    private final IdentityHashMap<Property, Instance> propertyToParent;

    private final IdentityHashMap<ObservableList, Instance> listToParent;

    private final IdentityHashMap<Class, List<Field>> propertyFieldCache;

    private final IdentityHashMap<Class, List<Field>> listFieldCache;

    private final GarbageCollectionCallback onRemoveCallback;

    private long gcCalls = 0;

    private long removedBeansCount = 0;

    /**
     * Constructor
     * @param onRemoveCallback callback that will be called for each garbage collection call.
     */
    public GarbageCollection(GarbageCollectionCallback onRemoveCallback) {
        this.onRemoveCallback = Assert.requireNonNull(onRemoveCallback, "onRemoveCallback");
        removeOnGC = new IdentityHashMap<>();
        allInstances = new IdentityHashMap<>();
        propertyToParent = new IdentityHashMap<>();
        listToParent = new IdentityHashMap<>();
        propertyFieldCache = new IdentityHashMap<>();
        listFieldCache = new IdentityHashMap<>();
    }

    /**
     * This method must be called for each new Dolphin bean (see {@link com.canoo.dolphin.mapping.DolphinBean}).
     * Normally beans are created by {@link com.canoo.dolphin.BeanManager#create(Class)}
     * @param bean the bean that was created
     * @param rootBean if this is true the bean is handled as a root bean. This bean don't need a reference.
     */
    public synchronized void onBeanCreated(Object bean, boolean rootBean) {
        if(!UnstableFeatureFlags.isUseGc()) {
            return;
        }
        Assert.requireNonNull(bean, "bean");
        if (allInstances.containsKey(bean)) {
            throw new IllegalArgumentException("Bean instance is already managed!");
        }

        IdentitySet<Property> properties = getAllProperties(bean);
        IdentitySet<ObservableList> lists = getAllLists(bean);
        Instance instance = new Instance(bean, rootBean, properties, lists);
        allInstances.put(bean, instance);
        for (Property property : properties) {
            propertyToParent.put(property, instance);
        }
        for (ObservableList list : lists) {
            listToParent.put(list, instance);
        }

        if (!rootBean) {
            //Until the bean isn't referenced in another bean it will be removed at gc
            addToGC(instance, bean);
        }
    }

    public synchronized void onBeanRemoved(Object bean) {
        if(!UnstableFeatureFlags.isUseGc()) {
            return;
        }
        Assert.requireNonNull(bean, "bean");
        if (!allInstances.containsKey(bean)) {
            throw new IllegalArgumentException("Bean is not managed by GC");
        }

        Instance instance = allInstances.remove(bean);
        removeOnGC.remove(instance);

        IdentitySet<Property> properties = getAllProperties(bean);
        IdentitySet<ObservableList> lists = getAllLists(bean);

        for (Property property : properties) {
            propertyToParent.remove(property);
            removeReferenceAndCheckForGC(property, property.get());
        }

        for (ObservableList list : lists) {
            listToParent.remove(list);
            for(Object item : list) {
                removeReferenceAndCheckForGC(list, item);
            }
        }
    }

    /**
     * This method must be called for each value change of a {@link Property}
     * @param property the property
     * @param oldValue the old value
     * @param newValue the new value
     */
    public synchronized void onPropertyValueChanged(Property property, Object oldValue, Object newValue) {
        if(!UnstableFeatureFlags.isUseGc()) {
            return;
        }
        removeReferenceAndCheckForGC(property, oldValue);

        if (newValue != null && !isBasicType(newValue.getClass())) {
            Instance instance = getInstance(newValue);
            Reference reference = new PropertyReference(propertyToParent.get(property), property, instance);
            if (reference.hasCircularReference()) {
                throw new CircularDependencyException("Circular dependency detected!");
            }
            instance.getReferences().add(reference);
            removeFromGC(instance);
        }
    }

    /**
     * This method must be called for each item that is added to a {@link ObservableList} that is part of a Dolphin bean (see {@link com.canoo.dolphin.mapping.DolphinBean})
     * @param list the list
     * @param value the added item
     */
    public synchronized void onAddedToList(ObservableList list, Object value) {
        if(!UnstableFeatureFlags.isUseGc()) {
            return;
        }
        if (value != null && !isBasicType(value.getClass())) {
            Instance instance = getInstance(value);
            Reference reference = new ListReference(listToParent.get(list), list, instance);
            if (reference.hasCircularReference()) {
                throw new CircularDependencyException("Circular dependency detected!");
            }
            instance.getReferences().add(reference);
            removeFromGC(instance);
        }
    }

    /**
     * This method must be called for each item that is removed to a {@link ObservableList} that is part of a Dolphin bean (see {@link com.canoo.dolphin.mapping.DolphinBean})
     * @param list the list
     * @param value the removed item
     */
    public synchronized void onRemovedFromList(ObservableList list, Object value) {
        if(!UnstableFeatureFlags.isUseGc()) {
            return;
        }
        removeReferenceAndCheckForGC(list, value);
    }

    /**
     * Calling this method triggers the garbage collection. For all dolphin beans (see {@link com.canoo.dolphin.mapping.DolphinBean}) that
     * are not referenced by a root bean (see {@link com.canoo.dolphin.server.DolphinModel}) the defined {@link GarbageCollectionCallback} (see constructor)
     * will be called.
     */
    public synchronized void gc() {
        if(!UnstableFeatureFlags.isUseGc()) {
            return;
        }
        onRemoveCallback.onRemove(removeOnGC.keySet());
        for (Instance removedInstance : removeOnGC.keySet()) {
            for (Property property : removedInstance.getProperties()) {
                propertyToParent.remove(property);
            }
            for (ObservableList list : removedInstance.getLists()) {
                listToParent.remove(list);
            }
            Object value = removeOnGC.get(removedInstance);
            allInstances.remove(value);
        }
        removedBeansCount = removedBeansCount + removeOnGC.size();
        removeOnGC.clear();
        gcCalls = gcCalls + 1;
    }

    public synchronized int getManagedInstancesCount() {
        return allInstances.size();
    }

    private void removeReferenceAndCheckForGC(ObservableList list, Object value) {
        Assert.requireNonNull(list, "list");
        if (value != null && !isBasicType(value.getClass())) {
            Instance instance = getInstance(value);
            Reference toRemove = null;
            for (Reference reference : instance.getReferences()) {
                if (reference instanceof ListReference) {
                    if (list == ((ListReference) reference).getList()) {
                        toRemove = reference;
                        break;
                    }
                }
            }
            if (toRemove == null) {
                throw new RuntimeException("REFERENCE NOT FOUND! ERROR IN GC!!");
            } else {
                instance.getReferences().remove(toRemove);
                if (instance.getReferences().isEmpty()) {
                    addToGC(instance, value);
                }
            }
        }
    }

    private void removeReferenceAndCheckForGC(Property property, Object value) {
        Assert.requireNonNull(property, "property");
        if(value != null && !isBasicType(value.getClass())) {
            Instance instance = getInstance(value);
            Reference toRemove = null;
            for (Reference reference : instance.getReferences()) {
                if (reference instanceof PropertyReference) {
                    if (property == ((PropertyReference) reference).getProperty()) {
                        toRemove = reference;
                        break;
                    }
                }
            }
            if (toRemove == null) {
                throw new RuntimeException("REFERENCE NOT FOUND! ERROR IN GC!!");
            } else {
                instance.getReferences().remove(toRemove);
                if (instance.getReferences().isEmpty()) {
                    addToGC(instance, value);
                }
            }
        }
    }

    private void addToGC(Instance instance, Object value) {
        removeOnGC.put(instance, value);

        for (Property property : instance.getProperties()) {
            Object propertyValue = property.get();
            if (propertyValue != null && !isBasicType(propertyValue.getClass())) {
                Instance childInstance = getInstance(propertyValue);
                if (!childInstance.isReferencedByRoot()) {
                    addToGC(childInstance, propertyValue);
                }
            }
        }
        for (ObservableList list : instance.getLists()) {
            for (Object listValue : list) {
                if (listValue != null && !isBasicType(listValue.getClass())) {
                    Instance childInstance = getInstance(listValue);
                    if (!childInstance.isReferencedByRoot()) {
                        addToGC(childInstance, listValue);
                    }
                }
            }
        }
    }

    private void removeFromGC(Instance instance) {
        Object removed = removeOnGC.remove(instance);

        if (removed != null) {
            for (Property property : instance.getProperties()) {
                Object value = property.get();
                if (value != null && !isBasicType(value.getClass())) {
                    Instance childInstance = getInstance(value);
                    removeFromGC(childInstance);
                }
            }
            for (ObservableList list : instance.getLists()) {
                for (Object value : list) {
                    if (value != null && !isBasicType(value.getClass())) {
                        Instance childInstance = getInstance(value);
                        removeFromGC(childInstance);
                    }
                }
            }
        }
    }

    private IdentitySet<Property> getAllProperties(Object bean) {
        IdentitySet<Property> ret = new IdentitySet<>();

        List<Field> fields = propertyFieldCache.get(bean.getClass());
        if (fields == null) {
            fields = new ArrayList<>();
            for (Field field : ReflectionHelper.getInheritedDeclaredFields(bean.getClass())) {
                if (Property.class.isAssignableFrom(field.getType())) {
                    fields.add(field);
                }
            }
            propertyFieldCache.put(bean.getClass(), fields);
        }
        for (Field field : fields) {
            ret.add((Property) ReflectionHelper.getPrivileged(field, bean));
        }
        return ret;
    }

    private IdentitySet<ObservableList> getAllLists(Object bean) {
        IdentitySet<ObservableList> ret = new IdentitySet<>();

        List<Field> fields = listFieldCache.get(bean.getClass());
        if (fields == null) {
            fields = new ArrayList<>();
            for (Field field : ReflectionHelper.getInheritedDeclaredFields(bean.getClass())) {
                if (ObservableList.class.isAssignableFrom(field.getType())) {
                    fields.add(field);
                }
            }
            listFieldCache.put(bean.getClass(), fields);
        }
        for (Field field : fields) {
                ret.add((ObservableList) ReflectionHelper.getPrivileged(field, bean));
        }
        return ret;
    }

    private Instance getInstance(Object bean) {
        Instance instance = allInstances.get(bean);
        if (instance == null) {
            throw new IllegalArgumentException("Can't find reference for " + bean);
        }
        return instance;
    }

    private boolean isBasicType(Class cls) {
        ClassRepositoryImpl.FieldType fieldType = DolphinUtils.getFieldType(cls);
        if(fieldType.equals(ClassRepositoryImpl.FieldType.DOLPHIN_BEAN)) {
            return false;
        }
        return true;
    }

    public long getGcCalls() {
        return gcCalls;
    }

    public long getRemovedBeansCount() {
        return removedBeansCount;
    }
}
