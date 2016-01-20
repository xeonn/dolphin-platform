package com.canoo.dolphin.server.gc;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.impl.IdentitySet;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.mapping.Property;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class GarbageCollection {

    private IdentitySet<Instance> removeOnGC;

    private Map<Object, Instance> allInstances;

    private Map<Property, Instance> propertyToParent;

    private Map<ObservableList, Instance> listToParent;

    private GarbageCollectionCallback onRemove;

    private static final List<Class<? extends Serializable>> BASIC_TYPES = Arrays.asList(String.class, Number.class, Boolean.class);

    public GarbageCollection(GarbageCollectionCallback onRemove) {
        this.onRemove = onRemove;
        removeOnGC = new IdentitySet<>();
        allInstances = new IdentityHashMap<>();
        propertyToParent = new IdentityHashMap<>();
        listToParent = new IdentityHashMap<>();
    }

    public synchronized void onBeanCreated(Object bean, boolean rootBean) {
        if (allInstances.containsKey(bean)) {
            throw new RuntimeException("Bean instance is already managed!");
        }

        IdentitySet<Property> properties = getAllProperties(bean);
        IdentitySet<ObservableList> lists = getAllLists(bean);
        Instance instance = new Instance(bean, properties, lists);
        allInstances.put(bean, instance);
        for(Property property : properties) {
            propertyToParent.put(property, instance);
        }
        for(ObservableList list : lists) {
            listToParent.put(list, instance);
        }

        if (!rootBean) {
            //Until the bean isn't referenced in another bean it will be removed at gc
            addToGC(instance);
        }
    }

    public synchronized void onPropertyValueChanged(Property property, Object oldValue, Object newValue) {
        if (oldValue != null && !isBasicType(oldValue.getClass())) {
            Instance instance = getInstance(oldValue);
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
                    addToGC(instance);
                }
            }
        }
        if (newValue != null && !isBasicType(newValue.getClass())) {
            Instance instance = getInstance(newValue);
            Reference reference = new PropertyReference(propertyToParent.get(property), property, instance);
            if(reference.hasCircularDependency()) {
                throw new CircularDependencyException("Circular dependency detected!");
            }
            instance.getReferences().add(reference);
            removeFromGC(instance);
        }
    }

    public synchronized void onAddedToList(ObservableList list, Object value) {
        if (value != null && !isBasicType(value.getClass())) {
            Instance instance = getInstance(value);
            Reference reference = new ListReference(listToParent.get(list), list, instance);
            if(reference.hasCircularDependency()) {
                throw new CircularDependencyException("Circular dependency detected!");
            }
            instance.getReferences().add(reference);
            removeFromGC(instance);
        }
    }

    public synchronized void onRemovedFromList(ObservableList list, Object value) {
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
                    addToGC(instance);
                }
            }
        }
    }

    public synchronized void gc() {
        onRemove.onRemove(removeOnGC);
        for(Instance removedInstance : removeOnGC) {
            for(Property property : removedInstance.getProperties()) {
                propertyToParent.remove(property);
            }
            for(ObservableList list : removedInstance.getLists()) {
                listToParent.remove(list);
            }
            allInstances.remove(removedInstance);
        }
        removeOnGC.clear();
    }

    private void addToGC(Instance instance) {
        removeOnGC.add(instance);

        Object bean = instance.getBean();

        for(Property property : instance.getProperties()) {
            Object value = property.get();
            if(value != null && !isBasicType(value.getClass())) {
                Instance childInstance = getInstance(value);
                if(childInstance.getReferences().size() == 1) {
                    addToGC(childInstance);
                }
            }
        }
        for(ObservableList list : instance.getLists()) {
            for(Object value : list) {
                if (value != null && !isBasicType(value.getClass())) {
                    Instance childInstance = getInstance(value);
                    if(childInstance.getReferences().size() == 1) {
                        addToGC(childInstance);
                    }
                }
            }
        }
    }

    private void removeFromGC(Instance instance) {
        boolean removed = removeOnGC.remove(instance);

        if(removed) {
            Object bean = instance.getBean();

            for(Property property : instance.getProperties()) {
                Object value = property.get();
                if (value != null && !isBasicType(value.getClass())) {
                    Instance childInstance = getInstance(value);
                    removeFromGC(childInstance);
                }
            }
            for(ObservableList list : instance.getLists()) {
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
        List<Field> fields = ReflectionHelper.getInheritedDeclaredFields(bean.getClass());
        for(Field field : fields) {
            if(Property.class.isAssignableFrom(field.getType())) {
                ret.add((Property) ReflectionHelper.getPrivileged(field, bean));
            }
        }
        return ret;
    }

    private IdentitySet<ObservableList> getAllLists(Object bean) {
        IdentitySet<ObservableList> ret = new IdentitySet<>();
        List<Field> fields = ReflectionHelper.getInheritedDeclaredFields(bean.getClass());
        for(Field field : fields) {
            if(ObservableList.class.isAssignableFrom(field.getType())) {
                ret.add((ObservableList) ReflectionHelper.getPrivileged(field, bean));
            }
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
        for (Class<? extends Serializable> basicCls : BASIC_TYPES) {
            if (basicCls.isAssignableFrom(cls)) {
                return true;
            }
        }
        return false;
    }

}
