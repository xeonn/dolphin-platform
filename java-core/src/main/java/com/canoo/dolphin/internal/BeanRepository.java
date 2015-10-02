package com.canoo.dolphin.internal;

import com.canoo.dolphin.event.BeanAddedListener;
import com.canoo.dolphin.event.BeanRemovedListener;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import org.opendolphin.core.PresentationModel;

import java.util.List;

/**
 * Created by hendrikebbers on 25.09.15.
 */
public interface BeanRepository {

    <T> Subscription addOnAddedListener(final Class<T> clazz, final BeanAddedListener<? super T> listener);

    Subscription addOnAddedListener(final BeanAddedListener<Object> listener);

    <T> Subscription addOnRemovedListener(final Class<T> clazz, final BeanRemovedListener<? super T> listener);

    Subscription addOnRemovedListener(final BeanRemovedListener<Object> listener);

    boolean isManaged(Object bean);

    <T> void delete(T bean);

    <T> List<T> findAll(Class<T> beanClass);

    Object getBean(String sourceId);

    String getDolphinId(Object bean);

    Object mapDolphinToObject(Object value, ClassRepositoryImpl.FieldType fieldType);

    void registerBean(Object bean, PresentationModel model, UpdateSource source);

}
