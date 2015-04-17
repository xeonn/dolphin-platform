package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.container.DolphinCommandManager;
import com.canoo.dolphin.server.event.DolphinEventBusImpl;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.server.adapter.DolphinServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * The default servlet of the dolphin platform. All communication is based on this servlet.
 */
public class DefaultDolphinServlet extends DolphinServlet {

    /**
     * The current command manager (based on used infarstucture - Spring or JavaEE/CDI). The instance will be loaded by SPI
     */
    private final DolphinCommandManager dolphinCommandRepository;

    /**
     * A set that contains all dolphin controllers that will be used as controllers on server site and defines the dolphin commands
     */
    private final Set<Class<?>> dolphinManagedClasses;

    /**
     * Contains the current servlet context
     */
    private static ThreadLocal<ServletContext> servletContext = new ThreadLocal<>();

    /**
     * Contains the current dolphin
     */
    private static ThreadLocal<ServerDolphin> dolphin = new ThreadLocal<>();

    /**
     * Default constructor
     * Loads the implementation (Spring / JavaEE) for all generic interfaces by JavaEE and searches for all controller classes that should be managed by the dolphin platform
     */
    public DefaultDolphinServlet() {
        ServiceLoader<DolphinCommandManager> serviceLoader = ServiceLoader.load(DolphinCommandManager.class);
        Iterator<DolphinCommandManager> serviceIterator = serviceLoader.iterator();
        if (serviceIterator.hasNext()) {
            this.dolphinCommandRepository = serviceIterator.next();
            if (serviceIterator.hasNext()) {
                throw new RuntimeException("More than 1 " + DolphinCommandManager.class + " found!");
            }
        } else {
            throw new RuntimeException("No " + DolphinCommandManager.class + " found!");
        }

        dolphinManagedClasses = DolphinPlatformBootstrap.findAllDolphinBeanClasses();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        servletContext.set(req.getServletContext());
        dolphin.set((ServerDolphin) req.getSession(true).getAttribute(DolphinServlet.class.getName()));

        DolphinEventBusImpl.getInstance().sendEventsForCurrentDolphinSession();

        super.service(req, resp);

        servletContext.remove();
        dolphin.remove();
    }

    /**
     * Returns the current dolphin (based on the current session)
     *
     * @return the dolphin
     */
    public static ServerDolphin getServerDolphin() {
        ServerDolphin serverDolphin = dolphin.get();
        return serverDolphin;
    }

    public static String getDolphinId() {
        return ((ServerModelStore) getServerDolphin().getModelStore()).id + "";
    }

    @Override
    protected void registerApplicationActions(ServerDolphin serverDolphin) {
        dolphin.set(serverDolphin);
        ServletContext context = servletContext.get();
        dolphinCommandRepository.initCommandsForSession(context, serverDolphin, dolphinManagedClasses);
    }

}
