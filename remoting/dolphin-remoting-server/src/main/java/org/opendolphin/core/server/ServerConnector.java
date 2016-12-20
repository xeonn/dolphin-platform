package org.opendolphin.core.server;

import groovy.util.logging.Log;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.StackTraceUtils;
import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.SignalCommand;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.action.ServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
public class ServerConnector {
    /**
     * doesn't fail on missing commands
     **/
    public List<Command> receive(Command command) {
        LOG.info("S:     received " + command);
        List<Command> response = new LinkedList();// collecting parameter pattern

        if (!(command instanceof SignalCommand)) {// signal commands must not update thread-confined state
            for (DolphinServerAction it : dolphinServerActions) {
                it.setDolphinResponse(response);// todo: can be deleted as soon as all action refer to the SMS
            }

            serverModelStore.setCurrentResponse(response);
        }


        List<CommandHandler> actions = registry.getAt(command.getId());
        if (!DefaultGroovyMethods.asBoolean(actions)) {
            LOG.warning("S: there is no server action registered for received command: " + String.valueOf(command) + ", known commands are " + String.valueOf(registry.getActions().keySet()));
            return response;
        }

        // copying the list of actions allows an Action to unregister itself
        // avoiding ConcurrentModificationException to be thrown by the loop
        List<CommandHandler> actionsCopy = new ArrayList<CommandHandler>();
        ((ArrayList<CommandHandler>) actionsCopy).addAll(actions);
        try {
            for (CommandHandler action : actionsCopy) {
                action.handleCommand(command, response);
            }

        } catch (Exception exception) {
            StackTraceUtils.deepSanitize(exception);
            LOG.log(Level.SEVERE, "S: an error ocurred while processing " + command, exception);
            throw exception;
        }

        return response;
    }

    public void register(ServerAction action) {
        if (action instanceof DolphinServerAction) {
            // static type checker complains if no explicit cast
            dolphinServerActions.add((DolphinServerAction) action);
        }
        action.registerIn(registry);
    }

    private static final Logger LOG = Logger.getLogger(ServerConnector.class.getName());
    private Codec codec;
    private ServerModelStore serverModelStore;
    private ActionRegistry registry = new ActionRegistry();
    private List<DolphinServerAction> dolphinServerActions = new ArrayList<DolphinServerAction>();

    public Codec getCodec() {
        return codec;
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }

    public ServerModelStore getServerModelStore() {
        return serverModelStore;
    }

    public void setServerModelStore(ServerModelStore serverModelStore) {
        this.serverModelStore = serverModelStore;
    }

    public ActionRegistry getRegistry() {
        return registry;
    }

    /**
     * Hack that is used in old groovy unit test
     * @param logLevel
     */
    public void setLogLevel(Level logLevel) {
        LOG.setLevel(logLevel);
    }
}
