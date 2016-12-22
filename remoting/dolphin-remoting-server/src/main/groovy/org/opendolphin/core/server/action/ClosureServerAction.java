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
