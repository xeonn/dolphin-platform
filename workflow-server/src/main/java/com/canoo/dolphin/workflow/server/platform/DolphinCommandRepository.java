package com.canoo.dolphin.workflow.server.platform;

import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        annotated.stream().filter(AbstractDolphinCommand.class::isAssignableFrom).forEach(c -> commandClasses.add((Class<AbstractDolphinCommand>) c));
    }

    public void initCommandsForDolphin(ServerDolphin dolphin) {
        dolphin.register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {
                //Alle registrieren
                for (Class<AbstractDolphinCommand> commandClass : commandClasses) {
                    AbstractDolphinCommand handler = applicationContext.getAutowireCapableBeanFactory().createBean(commandClass);
                    handler.setDolphin(dolphin);

                    DolphinCommand commandAnnotation = handler.getClass().getAnnotation(DolphinCommand.class);
                    registry.register(commandAnnotation.value(), (command, list) -> {
                        try {
                            handler.action();
                        } catch (Exception e) {
                            e.printStackTrace();
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
