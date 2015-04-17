package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.container.DolphinCommandManager;
import com.canoo.dolphin.server.container.DolphinCommandRegistration;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.server.adapter.DolphinServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * The default servlet of the dolphin platform. All communication is based on this servlet.
 */
public class DefaultDolphinServlet extends DolphinServlet {

    /**
     * this constant is already defined in DolphinServlet. It should maybe public there.
     */
    private static final String DOLPHIN_ATTRIBUTE_ID = DolphinServlet.class.getName();

    /**
     * The current command manager (based on used infarstucture - Spring or JavaEE/CDI). The instance will be loaded by SPI
     */
    private final DolphinCommandManager dolphinCommandRepository;

    /**
     * A set that contains all dolphin controllers that will be used as controllers on server site and defines the dolphin commands
     */
    private final Set<Class<?>> dolphinManagedClasses;

    private static ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();

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

        dolphinManagedClasses = DolphinCommandRegistration.findAllDolphinBeanClasses();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        request.set(req);
        super.service(req, resp);
        request.remove();
    }

    /**
     * Returns the current dolphin (based on the current session)
     *
     * @return the dolphin
     */
    public static ServerDolphin getServerDolphin() {
        return getServerDolphin(getSession());
    }

    private static HttpSession getSession() {
        return request.get().getSession();
    }

    @Override
    protected void registerApplicationActions(ServerDolphin serverDolphin) {
        //this is done by DolphinServlet after! calling this method.
        getSession().setAttribute(DOLPHIN_ATTRIBUTE_ID, serverDolphin);
        dolphinCommandRepository.initCommandsForSession(getServletContext(), serverDolphin, dolphinManagedClasses);
    }

    public static void addToSession(Object o) {
        getSession().setAttribute(o.getClass().getName(), o);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFromSession(Class<T> cls) {
        return (T) getSession().getAttribute(cls.getName());
    }

    public static String getDolphinId() {
        return getDolphinId(getSession());
    }

    public static String getDolphinId(HttpSession session) {
        return ((ServerModelStore) getServerDolphin(session).getModelStore()).id + "";
    }

    private static ServerDolphin getServerDolphin(HttpSession session) {
        return (ServerDolphin) session.getAttribute(DOLPHIN_ATTRIBUTE_ID);
    }

}
