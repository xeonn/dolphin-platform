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
package com.canoo.dolphin.client;

/**
 * Exception that is thrown when the invocation of a Dolphin Platform action in the server controller throwed
 * an exception. This exception will be thrown on the client.
 */
public class ControllerActionException extends Exception {

    /**
     * constructor
     */
    public ControllerActionException() {
    }

    /**
     * constructor
     * @param message error message
     */
    public ControllerActionException(String message) {
        super(message);
    }

    /**
     * constructor
     * @param message error message
     * @param cause the cause
     */
    public ControllerActionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * constructor
     * @param cause the cause
     */
    public ControllerActionException(Throwable cause) {
        super(cause);
    }
}
