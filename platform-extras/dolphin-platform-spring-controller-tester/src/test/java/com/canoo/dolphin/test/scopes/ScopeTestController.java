/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
