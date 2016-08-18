package com.canoo.dolphin.impl;

/**
 * Created by hendrikebbers on 14.07.16.
 */
public class BeanDefinitionException extends RuntimeException {

    private static final long serialVersionUID = -723698312668668310L;

    public BeanDefinitionException() {
    }

    public BeanDefinitionException(Class<?> notValidBeanClass) {
        throw new BeanDefinitionException("Class " + beanClass + " is not a valid Dolphin Platform bean class!", e);
    }

    public BeanDefinitionException(String message) {
        super(message);
    }

    public BeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanDefinitionException(Throwable cause) {
        super(cause);
    }
}
