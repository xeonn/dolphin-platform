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
package org.opendolphin.core.client.comm

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.StackTraceUtils
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.comm.*

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Level
import java.util.logging.Logger

public abstract class AbstractClientConnector implements ClientConnector {

    private static final Logger LOG = Logger.getLogger(AbstractClientConnector.class.getName());

    Codec codec;

    UiThreadHandler uiThreadHandler; // must be set from the outside - toolkit specific

    ExecutorService backgroundExecutor = Executors.newCachedThreadPool();

    ExceptionHandler onException;

    ClientResponseHandler responseHandler;

    protected final ClientDolphin clientDolphin;

    protected final ICommandBatcher commandBatcher;

    /** The named command that waits for pushes on the server side */
    NamedCommand pushListener = null;

    /** The signal command that publishes a "release" event on the respective bus */
    SignalCommand releaseCommand = null;

    /** whether listening for push events should be done at all. */
    protected boolean pushEnabled = false;

    /** whether we currently wait for push events (internal state) and may need to release */
    protected boolean waiting = false;


    public AbstractClientConnector(ClientDolphin clientDolphin) {
        this(clientDolphin, null);
    }

    public AbstractClientConnector(ClientDolphin clientDolphin, ICommandBatcher commandBatcher) {
        this.clientDolphin = clientDolphin;
        this.commandBatcher = commandBatcher ?: new CommandBatcher();
        this.responseHandler = new ClientResponseHandler(clientDolphin);

        // see https://issues.apache.org/jira/browse/GROOVY-7233 and https://issues.apache.org/jira/browse/GROOVY-5438
        def log = LOG;
        onException = new ExceptionHandler() {

            @Override
            void handle(Throwable e) {
                log.log(Level.SEVERE, "onException reached, rethrowing in UI Thread, consider setting AbstractClientConnector.onException", e);
                if (uiThreadHandler) {
                    uiThreadHandler.executeInsideUiThread(new Runnable() {
                        @Override
                        void run() {
                            throw e;
                        }
                    });
                } else {
                    log.log(Level.SEVERE, "UI Thread not defined...", e);
                }
            }
        };
        startCommandProcessing();
    }

    protected void startCommandProcessing() {
        // see https://issues.apache.org/jira/browse/GROOVY-7233 and https://issues.apache.org/jira/browse/GROOVY-5438
        def log = LOG;
        backgroundExecutor.execute(new Runnable() {
            @Override
            void run() {
                while (true) {
                    doExceptionSafe(new Runnable() {
                        @Override
                        void run() {
                            List<CommandAndHandler> toProcess = commandBatcher.getWaitingBatches().getVal();
                            List<Command> commands = new ArrayList<>();
                            for (CommandAndHandler c : toProcess) {
                                commands.add(c.command);
                            }
                            if (log.isLoggable(Level.INFO)) {
                                log.info("C: sending batch of size " + commands.size());
                                for (command in commands) {
                                    log.info("C:           -> " + command);
                                }
                            }
                            List<Command> answer = transmit(commands);
                            doSafelyInsideUiThread(new Runnable() {
                                @Override
                                void run() {
                                    processResults(answer, toProcess);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    protected ClientModelStore getClientModelStore() {
        return clientDolphin.clientModelStore;
    }

    abstract List<Command> transmit(List<Command> commands)

    @CompileStatic
    public void send(Command command, OnFinishedHandler callback = null) {
        // we have some change so regardless of the batching we may have to release a push
        if (command != pushListener) {
            release();
        }
        // we are inside the UI thread and events calls come in strict order as received by the UI toolkit
        commandBatcher.batch(new CommandAndHandler(command: command, handler: callback));
    }

    @CompileStatic
    void processResults(List<Command> response, List<CommandAndHandler> commandsAndHandlers) {
        def me = this;
        // see http://jira.codehaus.org/browse/GROOVY-6946
        def commands = response?.id;
        LOG.info("C: server responded with ${response?.size()} command(s): ${commands}");

        List<ClientPresentationModel> touchedPresentationModels = new LinkedList<ClientPresentationModel>();
        List<Map> touchedDataMaps = new LinkedList<Map>();
        for (Command serverCommand in response) {
            def touched = me.dispatchHandle(serverCommand);
            if (touched && touched instanceof ClientPresentationModel) {
                touchedPresentationModels << (ClientPresentationModel) touched;
            } else if (touched && touched instanceof Map) {
                touchedDataMaps << (Map) touched;
            }
        }
        def callback = commandsAndHandlers.first().handler; // there can only be one relevant handler anyway
        // added != null check instead of using simple Groovy truth because of NPE through GROOVY-7709
        if (callback != null) {
            callback.onFinished((List<ClientPresentationModel>) touchedPresentationModels.unique {
                ((ClientPresentationModel) it).id
            });
            if (callback instanceof OnFinishedData) {
                callback.onFinishedData(touchedDataMaps);
            }
        }
    }

    Object dispatchHandle(Command command) {
        handle(command);
    }

    @CompileStatic
    private void doExceptionSafe(Runnable processing, Runnable atLeast = null) {
        try {
            processing.run();
        } catch (Exception e) {
            StackTraceUtils.deepSanitize(e);
            onException.handle(e);
        } finally {
            if (atLeast != null) {
                atLeast.run();
            }
        }
    }

    @CompileStatic
    private void doSafelyInsideUiThread(Runnable whatToDo) {
        // see https://issues.apache.org/jira/browse/GROOVY-7233 and https://issues.apache.org/jira/browse/GROOVY-5438
        def log = LOG;
        doExceptionSafe(new Runnable() {
            @Override
            void run() {
                if (uiThreadHandler) {
                    uiThreadHandler.executeInsideUiThread(whatToDo);
                } else {
                    log.warning("please provide howToProcessInsideUI handler");
                    whatToDo.run();
                }
            }
        });
    }

    //////////////////////////////// push support ////////////////////////////////////////

    /** listens for the pushListener to return. The pushListener must be set and pushEnabled must be true. */
    public void listen() {
        if (!pushEnabled) {
            return; // allow the loop to end
        }
        if (waiting) {
            return; // avoid second call while already waiting (?) -> two different push actions not supported
        }
        waiting = true;
        send(pushListener, new OnFinishedHandlerAdapter() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                // we do nothing here nor do we register a special handler.
                // The server may have sent commands, though, even CallNamedActionCommand.
                waiting = false;
                listen(); // not a real recursion; is added to event queue
            }
        })
    }

    /** Release the current push listener, which blocks the sending queue.
     *  Does nothing in case that the push listener is not active.
     * */
    protected void release() {
        if (!waiting) {
            return;      // there is no point in releasing if we do not wait. Avoid excessive releasing.
        }
        waiting = false; // release is under way
        backgroundExecutor.execute(new Runnable() {
            @Override
            void run() {
                transmit(Collections.<Command> singletonList(releaseCommand));
            }
        });
    }

    @Override
    public void setPushEnabled(boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    @Override
    public boolean isPushEnabled() {
        return this.pushEnabled;
    }

    public def handle(Command serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    public Map handle(DataCommand serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    public ClientPresentationModel handle(DeletePresentationModelCommand serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    public ClientPresentationModel handle(DeleteAllPresentationModelsOfTypeCommand serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    @CompileStatic
    public ClientPresentationModel handle(CreatePresentationModelCommand serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    public ClientPresentationModel handle(ValueChangedCommand serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    public ClientPresentationModel handle(SwitchPresentationModelCommand serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    public ClientPresentationModel handle(InitializeAttributeCommand serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    public ClientPresentationModel handle(SavedPresentationModelNotification serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    public ClientPresentationModel handle(PresentationModelResetedCommand serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    public ClientPresentationModel handle(AttributeMetadataChangedCommand serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    public ClientPresentationModel handle(CallNamedActionCommand serverCommand) {
        return responseHandler.handle(serverCommand);
    }

    public boolean getStrictMode() {
        return this.responseHandler.strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.responseHandler.strictMode = strictMode;
    }
}
