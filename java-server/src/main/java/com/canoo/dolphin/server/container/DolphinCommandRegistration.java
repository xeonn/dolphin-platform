package com.canoo.dolphin.server.container;

import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.server.impl.DolphinParamRepository;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hendrikebbers on 18.03.15.
 */
public class DolphinCommandRegistration {

    //TODO: Die ganzen Methoden sollten nicht statisch sein. Aktuell noch Hack
    public static <T> void registerAllCommands(final ServerDolphin dolphin, final Class<T> cls, final T managedObject) {
        dolphin.register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {

                for (final Method method : cls.getMethods()) {
                    if (method.isAnnotationPresent(DolphinAction.class)) {
                        final String commandName = getCommandName(cls, method);
                        final List<String> paramNames = getParamNames(method);

                        registry.register(commandName, new CommandHandler() {
                            @Override
                            public void handleCommand(Command command, List response) {
                                try {
                                    invokeMethodWithParams(managedObject, method, paramNames, dolphin);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException("Can't invoke command " + commandName, e);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private static void invokeMethodWithParams(Object instance, Method method, List<String> paramNames, ServerDolphin dolphin) throws InvocationTargetException, IllegalAccessException {
        Object[] args = new Object[paramNames.size()];
        for(int i = 0; i < paramNames.size(); i++) {
            args[i] = getParam(paramNames.get(i), dolphin);
        }
        method.invoke(instance, args);
    }

    private static String getCommandName(Class<?> cls, Method method) {
        return getNameForClass(cls) + getNameSeparator() + getNameForMethod(method);
    }

    private static List<String> getParamNames(Method method) {
        final List<String> paramNames = new ArrayList<String>();

        for(int i = 0; i < method.getTypeParameters().length; i++) {
            TypeVariable<Method> typeVariable = method.getTypeParameters()[i];
            String paramName = typeVariable.getName();
            for(Annotation annotation : method.getParameterAnnotations()[i]) {
                if(annotation.annotationType().equals(Param.class)) {
                    Param param = (Param) annotation;
                    if(param.value() != null && !param.value().isEmpty()) {
                        paramName = param.value();
                    }
                }
            }
            paramNames.add(paramName);
        }
        return paramNames;
    }

    private static Object getParam(String name, ServerDolphin dolphin) {
        //TODO: Aktuell noch Hack.
        DolphinParamRepository paramRepository = new DolphinParamRepository(dolphin);
        return paramRepository.getParam(name);
    }

    private static String getNameSeparator() {
        return ":";
    }

    private static String getNameForClass(Class<?> cls) {
        String name = cls.getName();
        DolphinController controllerAnnotation = cls.getAnnotation(DolphinController.class);
        if (controllerAnnotation.value() != null && !controllerAnnotation.value().isEmpty()) {
            name = controllerAnnotation.value();
        }
        return name;
    }

    private static String getNameForMethod(Method method) {
        String name = method.getName();
        DolphinAction actionAnnotation = method.getAnnotation(DolphinAction.class);
        if (actionAnnotation.value() != null && !actionAnnotation.value().isEmpty()) {
            name = actionAnnotation.value();
        }
        return name;
    }
}
