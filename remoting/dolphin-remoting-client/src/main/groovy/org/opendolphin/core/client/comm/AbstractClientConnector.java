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
package org.opendolphin.core.client.comm;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.StackTraceUtils;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.NamedCommand;
import org.opendolphin.core.comm.SignalCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractClientConnector implements ClientConnector {

    private static final Logger LOG = Logger.getLogger(AbstractClientConnector.class.getName());

    private UiThreadHandler uiThreadHandler;

    private final ExecutorService backgroundExecutor = Executors.newCachedThreadPool();

    private ExceptionHandler onException;

    private final ClientResponseHandler responseHandler;

    private final ClientDolphin clientDolphin;

    private final ICommandBatcher commandBatcher;

    /**
     * The named command that waits for pushes on the server side
     */
    private NamedCommand pushListener = null;

    /**
     * The signal command that publishes a "release" event on the respective bus
     */
    private SignalCommand releaseCommand = null;

    /**
     * whether listening for push events should be done at all.
     */
    protected boolean pushEnabled = false;

    /**
     * whether we currently wait for push events (internal state) and may need to release
     */
    protected boolean waiting = false;

    public AbstractClientConnector(ClientDolphin clientDolphin) {
        this(clientDolphin, null);
    }

    public AbstractClientConnector(ClientDolphin clientDolphin, ICommandBatcher commandBatcher) {
        this.clientDolphin = clientDolphin;
        this.commandBatcher = commandBatcher != null ? commandBatcher : new CommandBatcher();
        this.responseHandler = new ClientResponseHandler(clientDolphin);
        onException = new ExceptionHandler() {
            @Override
            public void handle(final Throwable e) {
                LOG.log(Level.SEVERE, "onException reached, rethrowing in UI Thread, consider setting AbstractClientConnector.onException", e);
                if (uiThreadHandler != null) {
                    uiThreadHandler.executeInsideUiThread(new Runnable() {
                        @Override
                        public void run() {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    LOG.log(Level.SEVERE, "UI Thread not defined...", e);
                }
            }
        };
        startCommandProcessing();
    }

    private void startCommandProcessing() {
        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    doExceptionSafe(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final List<CommandAndHandler> toProcess = commandBatcher.getWaitingBatches().getVal();
                                List<Command> commands = new ArrayList<>();
                                for (CommandAndHandler c : toProcess) {
                                    commands.add(c.getCommand());
                                }
                                if (LOG.isLoggable(Level.INFO)) {
                                    LOG.info("C: sending batch of size " + ((ArrayList<Command>) commands).size());
                                    for (Command command : commands) {
                                        LOG.info("C:           -> " + command);
                                    }
                                }
                                final List<Command> answer = transmit(commands);
                                doSafelyInsideUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        processResults(answer, toProcess);
                                    }

                                });
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }
        });
    }

    protected abstract List<Command> transmit(List<Command> commands);

    public void send(Command command, OnFinishedHandler callback) {
        // we have some change so regardless of the batching we may have to release a push
        if (!command.equals(pushListener)) {
            release();
        }
        // we are inside the UI thread and events calls come in strict order as received by the UI toolkit
        CommandAndHandler handler = new CommandAndHandler();
        handler.setCommand(command);
        handler.setHandler(callback);
        commandBatcher.batch(handler);
    }

    public void send(Command command) {
        send(command, null);
    }

    protected void processResults(final List<Command> response, List<CommandAndHandler> commandsAndHandlers) {
        // see http://jira.codehaus.org/browse/GROOVY-6946
        if(LOG.isLoggable(Level.INFO)) {
            final List<String> commands = new ArrayList<>();
            if (response != null) {
                for (Command c : response) {
                    commands.add(c.getId());
                }
            }
            LOG.info("C: server responded with " + response.size() + " command(s): " + commands);
        }

        List<ClientPresentationModel> touchedPresentationModels = new LinkedList<>();
        List<Map> touchedDataMaps = new LinkedList<Map>();
        for (Command serverCommand : response) {
            Object touched = dispatchHandle(serverCommand);
            if (touched != null && touched instanceof ClientPresentationModel) {
                DefaultGroovyMethods.leftShift(touchedPresentationModels, (ClientPresentationModel) touched);
            } else if (touched != null && touched instanceof Map) {
                DefaultGroovyMethods.leftShift(touchedDataMaps, (Map) touched);
            }

        }
        OnFinishedHandler callback = DefaultGroovyMethods.first(commandsAndHandlers).getHandler();// there can only be one relevant handler anyway
        // added != null check instead of using simple Groovy truth because of NPE through GROOVY-7709
        if (callback != null) {
            callback.onFinished((List<ClientPresentationModel>) DefaultGroovyMethods.unique(touchedPresentationModels, new Closure<String>(this, this) {
                public String doCall(ClientPresentationModel it) {
                    return ((ClientPresentationModel) it).getId();
                }

                public String doCall() {
                    return doCall(null);
                }

            }));
            if (callback instanceof OnFinishedData) {
                ((OnFinishedData) callback).onFinishedData(touchedDataMaps);
            }
        }
    }

    public Object dispatchHandle(Command command) {
        return responseHandler.dispatchHandle(command);
    }

    private void doExceptionSafe(Runnable processing, Runnable atLeast) {
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

    private void doExceptionSafe(Runnable processing) {
        doExceptionSafe(processing, null);
    }

    private void doSafelyInsideUiThread(final Runnable whatToDo) {
        doExceptionSafe(new Runnable() {
            @Override
            public void run() {
                if (uiThreadHandler != null) {
                    uiThreadHandler.executeInsideUiThread(whatToDo);
                } else {
                    LOG.warning("please provide howToProcessInsideUI handler");
                    whatToDo.run();
                }
            }
        });
    }

    /**
     * listens for the pushListener to return. The pushListener must be set and pushEnabled must be true.
     */
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
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                // we do nothing here nor do we register a special handler.
                // The server may have sent commands, though, even CallNamedActionCommand.
                waiting = false;
                listen();// not a real recursion; is added to event queue
            }
        });
    }

    /**
     * Release the current push listener, which blocks the sending queue.
     * Does nothing in case that the push listener is not active.
     */
    protected void release() {
        if (!waiting) {
            return; // there is no point in releasing if we do not wait. Avoid excessive releasing.
        }

        waiting = false;// release is under way
        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                transmit(Collections.singletonList(getReleaseCommand()));
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

    public boolean getStrictMode() {
        return this.responseHandler.isStrictMode();
    }

    public void setStrictMode(boolean strictMode) {
        this.responseHandler.setStrictMode(strictMode);
    }

    public UiThreadHandler getUiThreadHandler() {
        return uiThreadHandler;
    }

    public void setUiThreadHandler(UiThreadHandler uiThreadHandler) {
        this.uiThreadHandler = uiThreadHandler;
    }

    public ExceptionHandler getOnException() {
        return onException;
    }

    public void setOnException(ExceptionHandler onException) {
        this.onException = onException;
    }

    public ClientResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public NamedCommand getPushListener() {
        return pushListener;
    }

    public void setPushListener(NamedCommand pushListener) {
        this.pushListener = pushListener;
    }

    public SignalCommand getReleaseCommand() {
        return releaseCommand;
    }

    public void setReleaseCommand(SignalCommand releaseCommand) {
        this.releaseCommand = releaseCommand;
    }

    public ClientDolphin getClientDolphin() {
        return clientDolphin;
    }

    protected ClientModelStore getClientModelStore() {
        return clientDolphin.getClientModelStore();
    }
}