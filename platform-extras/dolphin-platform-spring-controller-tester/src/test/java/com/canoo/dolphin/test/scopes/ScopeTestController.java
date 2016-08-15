package com.canoo.dolphin.test.scopes;

import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@DolphinController("ScopeTestController")
public class ScopeTestController {

    @Inject
    private RequestService requestService;

    @Inject
    private ClientService clientService;

    @Inject
    private SessionService sessionService;

    @Inject
    private SingletonService singletonService;

    @DolphinModel
    private ScopeModel model;

    @PostConstruct
    public void init() {
        model.requestServiceIdProperty().set(requestService.getId());
        model.clientServiceIdProperty().set(clientService.getId());
        model.sessionServiceIdProperty().set(sessionService.getId());
        model.singletonServiceIdProperty().set(singletonService.getId());
    }
}
