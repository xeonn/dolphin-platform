package org.opendolphin.core.server.action;

import groovy.lang.Closure;
import org.opendolphin.core.server.comm.ActionRegistry;

/**
 * Groovy-friendly action handling
 */
public class ClosureServerAction extends DolphinServerAction {

    private final String name;

    private final Closure namedCommandHandler;

    public ClosureServerAction(String name, Closure namedCommandHandler) {
        this.name = name;
        this.namedCommandHandler = namedCommandHandler;
    }

    @Override
    public void registerIn(ActionRegistry registry) {
        registry.register(name, namedCommandHandler);
    }

    public final String getName() {
        return name;
    }

    public final Closure getNamedCommandHandler() {
        return namedCommandHandler;
    }

}
