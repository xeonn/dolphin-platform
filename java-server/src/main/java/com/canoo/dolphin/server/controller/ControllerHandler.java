package com.canoo.dolphin.server.controller;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.server.container.ContainerManager;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class ControllerHandler {

    private Map<String, Object> controllers;

    private Map<String, Object> models;

    private ContainerManager containerManager;

    private BeanRepository beanRepository;

    private BeanManager beanManager;

    private ServerDolphin dolphin;

    public ControllerHandler(ServerDolphin dolphin, ContainerManager containerManager, BeanRepository beanRepository, BeanManager beanManager) {
        this.dolphin = dolphin;
        this.containerManager = containerManager;
        this.beanRepository = beanRepository;
        this.beanManager = beanManager;
        this.controllers = new HashMap<>();
        this.models = new HashMap<>();
    }

    public String createController(String name) {
        Class<?> controllerClass = controllersClasses.get(name);
        Object instance = containerManager.createManagedController(controllerClass);
        String id = UUID.randomUUID().toString();
        controllers.put(id, instance);

        for (final Method method : ReflectionHelper.getInheritedDeclaredMethods(controllerClass)) {
            if (method.isAnnotationPresent(DolphinAction.class)) {
                final String actionName = getActionName(method);
                final List<String> paramNames = getParamNames(method);
                //TODO: Register action for controller by name
            }
        }

        attachModel(id, instance);

        return id;
    }

    private void attachModel(String controllerId, Object controller) {
        List<Field> allFields = getInheritedDeclaredFields(controller.getClass());

        Field modelField = null;

        for (Field field : allFields) {
            if (field.isAnnotationPresent(DolphinModel.class)) {
                if (modelField != null) {
                    throw new RuntimeException("More than one Model was found for controller " + controller.getClass().getName());
                }
                modelField = field;
            }
        }

        if (modelField != null) {
            Object model = beanManager.create(modelField.getType());
            setPrivileged(modelField, controller, model);
            models.put(controllerId, model);
        }
    }

    public void callAction(String controllerId, String actionName) throws InvocationTargetException, IllegalAccessException {
        Object controller = controllers.get(controllerId);
        Method actionMethod = getActionMethod(controller, actionName);
        List<String> paramNames = getParamNames(actionMethod);
        invokeMethodWithParams(controller, actionMethod, paramNames, dolphin);
    }

    public void destroyController(String id) {
        Object controller = controllers.remove(id);
        containerManager.destroyController(controller);

        Object model = models.get(id);
        if (model != null) {
            beanManager.remove(model);
        }
    }

    private Method getActionMethod(Object controller, String actionName) {
        List<Method> allMethods = getInheritedDeclaredMethods(controller.getClass());
        Method foundMethod = null;
        for (Method method : allMethods) {
            if (method.isAnnotationPresent(DolphinAction.class)) {
                DolphinAction actionAnnotation = method.getAnnotation(DolphinAction.class);
                String currentActionName = method.getName();
                if (actionAnnotation.value() != null && !actionAnnotation.value().trim().isEmpty()) {
                    currentActionName = actionAnnotation.value();
                }
                if (currentActionName.equals(actionName)) {
                    if (foundMethod != null) {
                        throw new RuntimeException("More than one method for action " + actionName + " found in " + controller.getClass());
                    }
                    foundMethod = method;
                }
            }
        }
        return foundMethod;
    }

    private void invokeMethodWithParams(Object instance, Method method, List<String> paramNames, ServerDolphin dolphin) throws InvocationTargetException, IllegalAccessException {
        Object[] args = getParam(dolphin, paramNames);
        ReflectionHelper.invokePrivileged(method, instance, args);
    }

    private List<String> getParamNames(Method method) {
        final List<String> paramNames = new ArrayList<String>();

        for (int i = 0; i < method.getParameterTypes().length; i++) {
            String paramName = Integer.toString(i);
            for (Annotation annotation : method.getParameterAnnotations()[i]) {
                if (annotation.annotationType().equals(Param.class)) {
                    Param param = (Param) annotation;
                    if (param.value() != null && !param.value().isEmpty()) {
                        paramName = param.value();
                    }
                }
            }
            paramNames.add(paramName);
        }
        return paramNames;
    }

    private Object[] getParam(ServerDolphin dolphin, List<String> names) {
        final List<Object> result = new ArrayList<>();
        final List<ServerPresentationModel> presentationModels = dolphin.findAllPresentationModelsByType(DolphinConstants.DOLPHIN_PARAMETER);
        if (!presentationModels.isEmpty()) {
            final ServerPresentationModel parameterModel = presentationModels.get(0);
            for (final String name : names) {
                final Attribute valueAttribute = parameterModel.findAttributeByPropertyNameAndTag(name, Tag.VALUE);
                final Attribute typeAttribute = parameterModel.findAttributeByPropertyNameAndTag(name, Tag.VALUE_TYPE);
                final ClassRepository.FieldType fieldType = DolphinUtils.mapFieldTypeFromDolphin(typeAttribute.getValue());
                result.add(beanRepository.mapDolphinToObject(valueAttribute.getValue(), fieldType));
            }
            dolphin.removeAllPresentationModelsOfType(DolphinConstants.DOLPHIN_PARAMETER);
        }
        return result.toArray(new Object[result.size()]);
    }

    private String getActionName(Method method) {
        String name = method.getName();
        DolphinAction actionAnnotation = method.getAnnotation(DolphinAction.class);
        if (actionAnnotation.value() != null && !actionAnnotation.value().isEmpty()) {
            name = actionAnnotation.value();
        }
        return name;
    }

    private List<Field> getInheritedDeclaredFields(Class<?> type) {
        List<Field> result = new ArrayList<>();
        Class<?> i = type;
        while (i != null && i != Object.class) {
            result.addAll(Arrays.asList(i.getDeclaredFields()));
            i = i.getSuperclass();
        }
        return result;
    }

    private List<Method> getInheritedDeclaredMethods(Class<?> type) {
        List<Method> result = new ArrayList<>();
        Class<?> i = type;
        while (i != null && i != Object.class) {
            result.addAll(Arrays.asList(i.getDeclaredMethods()));
            i = i.getSuperclass();
        }
        return result;
    }

    private void setPrivileged(final Field field, final Object bean,
                               final Object value) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                boolean wasAccessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    field.set(bean, value);
                    return null; // return nothing...
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new IllegalStateException("Cannot set field: "
                            + field, ex);
                } finally {
                    field.setAccessible(wasAccessible);
                }
            }
        });
    }

    private static Map<String, Class> controllersClasses;

    private static boolean initialized = false;

    public static synchronized void init() {
        if (initialized) {
            throw new RuntimeException(ControllerHandler.class.getName() + " already initialized");
        }
        controllersClasses = new HashMap<>();
        Reflections reflections = new Reflections();
        Set<Class<?>> foundControllerClasses = reflections.getTypesAnnotatedWith(DolphinController.class);
        for (Class<?> controllerClass : foundControllerClasses) {
            String name = controllerClass.getName();
            if (controllerClass.getAnnotation(DolphinController.class).value() != null && !controllerClass.getAnnotation(DolphinController.class).value().trim().isEmpty()) {
                name = controllerClass.getAnnotation(DolphinController.class).value();
            }
            controllersClasses.put(name, controllerClass);
        }
        initialized = true;
    }
}
