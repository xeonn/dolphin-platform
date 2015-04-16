package com.canoo.dolphin.server.impl;

import java.awt.*;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BetterBeanInfo implements BeanInfo {
    Set<PropertyDescriptor> propertyDescriptors = new HashSet<>();
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return null;
    }

    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        return new EventSetDescriptor[0];
    }

    @Override
    public int getDefaultEventIndex() {
        return 0;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return propertyDescriptors.toArray(new PropertyDescriptor[propertyDescriptors.size()]);
    }

    public void addPropertyDescriptors(PropertyDescriptor ... descriptors){
        propertyDescriptors.addAll(Arrays.asList(descriptors));
    }

    @Override
    public int getDefaultPropertyIndex() {
        return 0;
    }

    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return new MethodDescriptor[0];
    }

    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        return new BeanInfo[0];
    }

    @Override
    public Image getIcon(int iconKind) {
        return null;
    }


}
