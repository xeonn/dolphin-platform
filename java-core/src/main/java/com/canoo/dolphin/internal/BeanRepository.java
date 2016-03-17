/**
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
