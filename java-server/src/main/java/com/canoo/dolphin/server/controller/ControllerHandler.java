package com.canoo.dolphin.server.controller;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.impl.DolphinUtils;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.container.ModelInjector;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

public class ControllerHandler {

    private final Map<String, Object> controllers = new HashMap<>();

    private final Map<String, Object> models = new HashMap<>();

    private final ContainerManager containerManager;

    private final BeanRepository beanRepository;

    private final BeanManager beanManager;

    private final ServerDolphin dolphin;

    public ControllerHandler(ServerDolphin dolphin, ContainerManager containerManager, BeanRepository beanRepository, BeanManager beanManager) {
        this.dolphin = dolphin;
        this.containerManager = containerManager;
        this.beanRepository = beanRepository;
        this.beanManager = beanManager;
    }

    public Object getController(String id) {
        return controllers.get(id);
    }

    public Object getControllerModel(String id) {
        return models.get(id);
    }

    public String createController(String name) {
        Class<?> controllerClass = ControllerRepository.getControllerClassForName(name);

        final String id = UUID.randomUUID().toString();
        Object instance = containerManager.createManagedController(controllerClass, new ModelInjector() {
            @Override
            public void inject(Object controller) {
                attachModel(id, controller);
            }
        });
        controllers.put(id, instance);

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

    public void invokeAction(String controllerId, String actionName) throws InvokeActionException {
        try {
            Object controller = controllers.get(controllerId);
            Method actionMethod = getActionMethod(controller, actionName);
            List<String> paramNames = getParamNames(actionMethod);
            invokeMethodWithParams(controller, actionMethod, paramNames, dolphin);
        } catch (Exception e) {
            throw new InvokeActionException(e);
        }
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
        final List<String> paramNames = new ArrayList<>();

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
        final List<ServerPresentationModel> presentationModels = dolphin.findAllPresentationModelsByType(PlatformConstants.DOLPHIN_PARAMETER);
        if (!presentationModels.isEmpty()) {
            final ServerPresentationModel parameterModel = presentationModels.get(0);
            for (final String name : names) {
                final Attribute valueAttribute = parameterModel.findAttributeByPropertyNameAndTag(name, Tag.VALUE);
                final Attribute typeAttribute = parameterModel.findAttributeByPropertyNameAndTag(name, Tag.VALUE_TYPE);
                final ClassRepositoryImpl.FieldType fieldType = DolphinUtils.mapFieldTypeFromDolphin(typeAttribute.getValue());
                result.add(beanRepository.mapDolphinToObject(valueAttribute.getValue(), fieldType));
            }
            dolphin.removeAllPresentationModelsOfType(PlatformConstants.DOLPHIN_PARAMETER);
        }
        return result.toArray(new Object[result.size()]);
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

    @SuppressWarnings("unchecked")
    public <T> List<? extends T> getAllControllersThatImplement(Class<T> cls) {
        final List<T> ret = new ArrayList<>();
        for (Object controller : controllers.values()) {
            if (cls.isAssignableFrom(controller.getClass())) {
                ret.add((T) controller);
            }
        }
        return ret;
    }


}
