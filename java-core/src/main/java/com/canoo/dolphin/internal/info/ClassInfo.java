package com.canoo.dolphin.internal.info;

import com.canoo.dolphin.impl.DolphinUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClassInfo {
    private final Class<?> beanClass;
    private final String modelType;
    private final Map<String, PropertyInfo> propertyInfoMap;
    private final Map<String, PropertyInfo> observableListInfoMap;

    public ClassInfo(Class<?> beanClass, Collection<PropertyInfo> propertyInfos, Collection<PropertyInfo> observableListInfos) {
        this.beanClass = beanClass;
        modelType = DolphinUtils.getDolphinPresentationModelTypeForClass(beanClass);

        final Map<String, PropertyInfo> localPropertyInfoMap = new HashMap<>();
        for (final PropertyInfo propertyInfo : propertyInfos) {
            localPropertyInfoMap.put(propertyInfo.getAttributeName(), propertyInfo);
        }
        propertyInfoMap = Collections.unmodifiableMap(localPropertyInfoMap);

        final Map<String, PropertyInfo> localObservableListInfoMap = new HashMap<>();
        for (final PropertyInfo observableListInfo : observableListInfos) {
            localObservableListInfoMap.put(observableListInfo.getAttributeName(), observableListInfo);
        }
        observableListInfoMap = Collections.unmodifiableMap(localObservableListInfoMap);
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getModelType() {
        return modelType;
    }

    public PropertyInfo getPropertyInfo(String attributeName) {
        return propertyInfoMap.get(attributeName);
    }

    public PropertyInfo getObservableListInfo(String attributeName) {
        return observableListInfoMap.get(attributeName);
    }

    public void forEachProperty(PropertyIterator iterator) {
        for (final PropertyInfo propertyInfo : propertyInfoMap.values()) {
            iterator.call(propertyInfo);
        }
    }

    public void forEachObservableList(PropertyIterator iterator) {
        for (final PropertyInfo observableListInfo : observableListInfoMap.values()) {
            iterator.call(observableListInfo);
        }
    }

    public interface PropertyIterator {
        void call(PropertyInfo propertyInfo);
    }
}
