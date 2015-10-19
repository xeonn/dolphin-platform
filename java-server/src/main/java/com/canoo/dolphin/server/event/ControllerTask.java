/*
 * Copyright 2015 Canoo Engineering AG.
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
package com.canoo.dolphin.server.event;

/**
 * A functional interface that is used to call a specific task for a controller by using the
 * {@link com.canoo.dolphin.server.event.TaskExecutor}. Since the server part of Dolphin Platform depends on Java 7 we
 * can't use the Java 8 Consumer interface here.
 * @param <T> Controller Type
 */
public interface ControllerTask<T> {

    /**
     * The specified action will be called for the given controller
     * @param controller the controller
     */
    void run(T controller);
}
