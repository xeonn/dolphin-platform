package com.canoo.dolphin.server.context;

import com.canoo.dolphin.Constants;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.collections.ListMapper;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerHandler;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.impl.ServerEventDispatcher;
import com.canoo.dolphin.server.impl.ServerPresentationModelBuilderFactory;
import com.canoo.dolphin.v2.ControllerActionCallBean;
import com.canoo.dolphin.v2.ControllerDestroyBean;
import com.canoo.dolphin.v2.ControllerRegistryBean;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.JsonCodec;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerConnector;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class DolphinContext {

    private final DefaultServerDolphin dolphin;

    private final BeanRepository beanRepository;

    private final BeanManagerImpl beanManager;

    private final DolphinEventBusImpl eventBus;

    private final ControllerHandler controllerHandler;

    private ContainerManager containerManager;

    private String id;

    private ServletContext servletContext;

    public DolphinContext(ContainerManager containerManager, ServletContext servletContext) {
        this.containerManager = containerManager;
        this.servletContext = servletContext;

        //ID
        id = UUID.randomUUID().toString();

        //Init Open Dolphin
        ServerModelStore modelStore = new ServerModelStore();
        ServerConnector serverConnector = new ServerConnector();
        serverConnector.setCodec(new JsonCodec());
        serverConnector.setServerModelStore(modelStore);
        dolphin = new DefaultServerDolphin(modelStore, serverConnector);
        dolphin.registerDefaultActions();

        //Init BeanRepository
        final ServerDolphin dolphin = getDolphin();
        final EventDispatcher dispatcher = new ServerEventDispatcher(dolphin);
        beanRepository = new BeanRepository(dolphin, dispatcher);

        //Init BeanManager
        final PresentationModelBuilderFactory builderFactory = new ServerPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new BeanBuilder(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        beanManager = new BeanManagerImpl(beanRepository, beanBuilder);

        //Init EventBus
        eventBus = DolphinEventBusImpl.getInstance();

        //Init ControllerHandler
        controllerHandler = new ControllerHandler(containerManager, beanRepository, beanManager);

        //Register Commands
        registerDolphinPlatformDefaultCommands();
    }

    private void registerDolphinPlatformDefaultCommands() {
        dolphin.register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {

                registry.register(Constants.REGISTER_CONTROLLER_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        onRegisterController();
                    }
                });

                registry.register(Constants.DESTROY_CONTROLLER_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        onDestroyController();
                    }
                });

                registry.register(Constants.CALL_CONTROLLER_ACTION_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        onCallControllerAction();
                    }
                });

                registry.register(Constants.POLL_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        onPollEventBus();
                    }
                });

                registry.register(Constants.RELEASE_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        onReleaseEventBus();
                    }
                });
            }
        });
    }

    private void onRegisterController() {
        ControllerRegistryBean bean = beanManager.findAll(ControllerRegistryBean.class).get(0);
        String id = controllerHandler.createController(bean.getControllerName().get());
        bean.getControllerid().set(id);
    }

    private void onDestroyController() {
        ControllerDestroyBean bean = beanManager.findAll(ControllerDestroyBean.class).get(0);
        controllerHandler.destroyController(bean.controlleridProperty().get());
    }

    private void onCallControllerAction() {
        ControllerActionCallBean bean = beanManager.findAll(ControllerActionCallBean.class).get(0);
        controllerHandler.callAction(bean.getControllerid().get(), bean.getActionName().get());
    }

    private void onPollEventBus() {
        try {
            eventBus.longPoll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void onReleaseEventBus() {
        eventBus.release();
    }

    public ServerDolphin getDolphin() {
        return dolphin;
    }

    public BeanManagerImpl getBeanManager() {
        return beanManager;
    }

    public DolphinEventBusImpl getEventBus() {
        return eventBus;
    }

    public ControllerHandler getControllerHandler() {
        return controllerHandler;
    }

    public ContainerManager getContainerManager() {
        return containerManager;
    }

    public BeanRepository getBeanRepository() {
        return beanRepository;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public String getId() {
        return id;
    }

    public String getId(HttpSession session) {
        return id;
    }

    public static DolphinContext getCurrentContext() {
        return DolphinContextHandler.getCurrentContext();
    }

}
