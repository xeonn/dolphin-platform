package com.canoo.dolphin.server.binding.impl;

import com.canoo.dolphin.binding.Binding;
import com.canoo.dolphin.binding.BindingException;
import com.canoo.dolphin.impl.PropertyImpl;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.binding.Qualifier;
import com.canoo.dolphin.server.binding.QualifierBinder;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.server.ServerAttribute;

import java.lang.reflect.Field;

public class QualifierBinderImpl implements QualifierBinder {

    public <T> Binding bind(final Property<T> property, final Qualifier<T> qualifier) {
        Assert.requireNonNull(property, "property");
        Assert.requireNonNull(qualifier, "qualifier");

        if(property instanceof PropertyImpl) {
            try {
                final PropertyImpl p = (PropertyImpl) property;

                final Field attributeField = ReflectionHelper.getInheritedDeclaredField(PropertyImpl.class, "attribute");
                final ServerAttribute attribute = (ServerAttribute) ReflectionHelper.getPrivileged(attributeField, p);
                if(attribute == null) {
                    throw new NullPointerException("attribute == null");
                }
                attribute.setQualifier(qualifier.getIdentifier());
                return new Binding() {
                    @Override
                    public void unbind() {
                        attribute.setQualifier(null);
                    }
                };
            } catch (Exception e) {
                throw new BindingException("Can not bind this property!", e);
            }
        } else {
            throw new BindingException("Can not bind this property!");
        }
    }

}
