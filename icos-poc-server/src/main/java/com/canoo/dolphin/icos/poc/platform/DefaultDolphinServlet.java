package com.canoo.dolphin.icos.poc.platform;

import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.server.adapter.DolphinServlet;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;


public class DefaultDolphinServlet extends DolphinServlet {

    private final DolphinCommandRepository dolphinCommandRepository;

    @Inject
    private ApplicationContext applicationContext;

    public DefaultDolphinServlet(DolphinCommandRepository dolphinCommandRepository) {
        this.dolphinCommandRepository = dolphinCommandRepository;
    }

    @Override
    protected void registerApplicationActions(ServerDolphin serverDolphin) {
        dolphinCommandRepository.initCommandsForDolphin(serverDolphin);
    }
}
