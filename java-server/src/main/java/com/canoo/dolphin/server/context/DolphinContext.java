package com.canoo.dolphin.server.context;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.impl.Constants;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.collections.ListMapper;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerHandler;
import com.canoo.dolphin.server.controller.InvokeActionException;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.event.impl.TaskExecutorImpl;
import com.canoo.dolphin.server.impl.ServerEventDispatcher;
import com.canoo.dolphin.server.impl.ServerPresentationModelBuilderFactory;
import com.canoo.dolphin.impl.ControllerActionCallBean;
import com.canoo.dolphin.impl.ControllerDestroyBean;
import com.canoo.dolphin.impl.ControllerRegistryBean;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
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

    private TaskExecutorImpl taskExecutor;

    public DolphinContext(ContainerManager containerManager, ServletContext servletContext) {
        this.containerManager = containerManager;
        this.servletContext = servletContext;

        //ID
        id = UUID.randomUUID().toString();

        //Init Open Dolphin
        final ServerModelStore modelStore = new ServerModelStore();
        final ServerConnector serverConnector = new ServerConnector();
        serverConnector.setCodec(new JsonCodec());
        serverConnector.setServerModelStore(modelStore);
        dolphin = new DefaultServerDolphin(modelStore, serverConnector);
        dolphin.registerDefaultActions();

        //Init BeanRepository
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
        controllerHandler = new ControllerHandler(dolphin, containerManager, beanRepository, beanManager);

        //Init TaskExecutor
        taskExecutor = new TaskExecutorImpl();

        //Register commands
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
                        try {
                            onInvokeControllerAction();
                        } catch (Exception e) {
                            //TODO: ExceptionHandler
                            throw new RuntimeException(e);
                        }
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

                registry.register(Constants.INIT_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        //New Client
                        beanManager.create(ControllerRegistryBean.class);
                        beanManager.create(ControllerDestroyBean.class);
                        beanManager.create(ControllerActionCallBean.class);
                    }
                });
            }
        });
    }

    private void onRegisterController() {
        ControllerRegistryBean bean = beanManager.findAll(ControllerRegistryBean.class).get(0);
        String controllerId = controllerHandler.createController(bean.getControllerName());
        bean.setControllerid(controllerId);
        Object model = controllerHandler.getControllerModel(controllerId);
        if(model != null) {
            bean.setModelId(beanRepository.getDolphinId(model));
        }
    }

    private void onDestroyController() {
        ControllerDestroyBean bean = beanManager.findAll(ControllerDestroyBean.class).get(0);
        controllerHandler.destroyController(bean.getControllerid());
    }

    private void onInvokeControllerAction() throws InvokeActionException {
        ControllerActionCallBean bean = beanManager.findAll(ControllerActionCallBean.class).get(0);
        controllerHandler.invokeAction(bean.getControllerid(), bean.getActionName());
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

    public BeanManager getBeanManager() {
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

    public TaskExecutorImpl getTaskExecutor() {
        return taskExecutor;
    }

    public String getId() {
        return id;
    }

    public static DolphinContext getCurrentContext() {
        return DolphinContextHandler.getCurrentContext();
    }

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        resp.setHeader(Constants.CLIENT_ID_HTTP_HEADER_NAME, id);

        //copied from DolphinServlet
        StringBuilder requestJson = new StringBuilder();
        String line = null;
        while((line =req.getReader().readLine())!=null){
            requestJson.append(line).append("\n");
        }
        List<Command> commands = dolphin.getServerConnector().getCodec().decode(requestJson.toString());
        List<Command> results = new LinkedList<>();
        for (Command command : commands) {
            results.addAll(dolphin.getServerConnector().receive(command));
        }
        String jsonResponse = dolphin.getServerConnector().getCodec().encode(results);
        resp.getOutputStream().print(jsonResponse);
    }
}
