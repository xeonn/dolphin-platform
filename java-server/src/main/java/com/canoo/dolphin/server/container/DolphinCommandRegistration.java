package com.canoo.dolphin.server.container;

import com.canoo.dolphin.server.DolphinCommand;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by hendrikebbers on 18.03.15.
 */
public class DolphinCommandRegistration {

    public static void registerAllComands(ServerDolphin dolphin, final Class<?> cls, final Object managedObject) {
        dolphin.register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {

                for (final Method method : cls.getMethods()) {
                    if (method.isAnnotationPresent(DolphinCommand.class)) {
                        final DolphinCommand commandAnnotation = method.getAnnotation(DolphinCommand.class);
                        registry.register(commandAnnotation.value(), new CommandHandler() {
                            @Override
                            public void handleCommand(Command command, List response) {
                                try {
                                    method.invoke(managedObject);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException("Can't invoke command " + commandAnnotation.value(), e);
                                } catch (InvocationTargetException e) {
                                    throw new RuntimeException("Can't invoke command " + commandAnnotation.value(), e);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

}
