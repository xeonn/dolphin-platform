package com.canoo.dolphin.icos.poc.platform;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
public class DolphinCommandRepository {

    @Value("${dolphin.commands.basePackage:com.canoo}")
    private String mainPackage;

    @Inject
    private ApplicationContext applicationContext;

    private List<Class<AbstractDolphinCommand>> commandClasses;

    @PostConstruct
    private void init() {
        //Alle DolphinCommands suchen
        Reflections reflections = new Reflections(mainPackage);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(DolphinCommand.class);
        commandClasses = new ArrayList<>();
        annotated.stream().filter(new Predicate<Class<?>>() {
            @Override
            public boolean test(Class<?> cls) {
                return AbstractDolphinCommand.class.isAssignableFrom(cls);
            }
        }).forEach(new Consumer<Class<?>>() {
            @Override
            public void accept(Class<?> cls) {
                commandClasses.add((Class<AbstractDolphinCommand>) cls);
            }
        });
    }

    public void initCommandsForDolphin(final ServerDolphin dolphin) {
        dolphin.register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {
                //Alle registrieren
                for (Class<AbstractDolphinCommand> commandClass : commandClasses) {
                    final AbstractDolphinCommand handler = applicationContext.getAutowireCapableBeanFactory().createBean(commandClass);
                    handler.setDolphin(dolphin);

                    DolphinCommand commandAnnotation = handler.getClass().getAnnotation(DolphinCommand.class);
                    registry.register(commandAnnotation.value(), new CommandHandler() {
                        @Override
                        public void handleCommand(Command command, List list) {
                            try {
                                handler.action();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public List<Class<AbstractDolphinCommand>> getCommandClasses() {
        return commandClasses;
    }
}
