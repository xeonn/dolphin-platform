package com.canoo.dolphin.client;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.ClientConnector;
import org.opendolphin.core.client.comm.CommandAndHandler;
import org.opendolphin.core.client.comm.ICommandBatcher;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.client.comm.UiThreadHandler;
import org.opendolphin.core.comm.AttributeMetadataChangedCommand;
import org.opendolphin.core.comm.CallNamedActionCommand;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.comm.DeleteAllPresentationModelsOfTypeCommand;
import org.opendolphin.core.comm.DeletePresentationModelCommand;
import org.opendolphin.core.comm.InitializeAttributeCommand;
import org.opendolphin.core.comm.NamedCommand;
import org.opendolphin.core.comm.PresentationModelResetedCommand;
import org.opendolphin.core.comm.SavedPresentationModelNotification;
import org.opendolphin.core.comm.SignalCommand;
import org.opendolphin.core.comm.SwitchPresentationModelCommand;
import org.opendolphin.core.comm.ValueChangedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public abstract class AbstractConnector implements ClientConnector {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConnector.class);

    private boolean running = true;

    private Lock communicationLock = new ReentrantLock();

    final Condition waitingCommands = communicationLock.newCondition();

    private final AtomicBoolean waiting = new AtomicBoolean(false);

    private final AtomicBoolean needsRelease = new AtomicBoolean(false);

    private SignalCommand releaseCommand;

    private NamedCommand pushListener;

    private final ICommandBatcher commandBatcher;

    private final ClientDolphin clientDolphin;

    private final UiThreadHandler uiThreadHandler;

    private final ExecutorService baseExecutor = Executors.newSingleThreadExecutor();

    private final ExecutorService communicationExecutor = Executors.newSingleThreadExecutor();

    private final ExecutorService releaseExecutor = Executors.newSingleThreadExecutor();

    private List<CommandAndHandler> commands = new ArrayList<>();

    public AbstractConnector(ICommandBatcher commandBatcher, ClientDolphin clientDolphin, UiThreadHandler uiThreadHandler) {
        this.commandBatcher = commandBatcher;
        this.clientDolphin = clientDolphin;
        this.uiThreadHandler = uiThreadHandler;

        baseExecutor.execute(() -> {
            while (running) {
                processCommunication();
            }
        });
    }

    private void processCommunication() {
        List<CommandAndHandler> toProcess = new ArrayList<>();

        communicationLock.lock();
        try {
            while (commands.isEmpty()) {
                waitingCommands.await();
            }
            toProcess.addAll(commands);
            commands.clear();
        } catch (InterruptedException e) {
            //TODO
            e.printStackTrace();
        } finally {
            communicationLock.unlock();
        }

        List<Command> commands = toProcess.stream().map(t -> t.getCommand()).collect(Collectors.toList());

        if (pushListener != null) {
            commands.add(pushListener);
        }

        LOG.trace("Sending batch of size {0}", commands.size());
        if (LOG.isTraceEnabled()) {
            commands.forEach(c -> LOG.trace("Sending {}", c));
        }

        List<Command> answer = transmit(commands);

        LOG.trace("Received response with {} commands");
        if (LOG.isTraceEnabled()) {
            answer.forEach(c -> LOG.trace("Received {}", c));
        }

        uiThreadHandler.executeInsideUiThread(() -> {
            processResults(answer, toProcess);
        });
    }

    protected abstract List<Command> transmit(List<Command> commands);

    protected void processResults(List<Command> response, List<CommandAndHandler> commandsAndHandlers) {

        response.forEach(c -> handleResponseCommand(c));

        commandsAndHandlers.forEach(t -> {
            OnFinishedHandler callback = t.getHandler();
            if (callback != null) {
                callback.onFinished(Collections.EMPTY_LIST);
            }
        });
    }

    private void handleResponseCommand(Command command) {
        if (command instanceof DeletePresentationModelCommand) {
            DeletePresentationModelCommand serverCommand = (DeletePresentationModelCommand) command;
            ClientPresentationModel model = clientDolphin.findPresentationModelById(serverCommand.getPmId());
            if (model != null) {
                getClientModelStore().delete(model);
            }
        } else if (command instanceof DeleteAllPresentationModelsOfTypeCommand) {
            DeleteAllPresentationModelsOfTypeCommand serverCommand = (DeleteAllPresentationModelsOfTypeCommand) command;
            clientDolphin.deleteAllPresentationModelsOfType(serverCommand.getPmType());
        } else if (command instanceof CreatePresentationModelCommand) {
            CreatePresentationModelCommand serverCommand = (CreatePresentationModelCommand) command;
            if (getClientModelStore().containsPresentationModel(serverCommand.getPmId())) {
                throw new IllegalStateException("There already is a presentation model with id " + serverCommand.getPmId() + " known to the client.");
            }
            List<ClientAttribute> attributes = new ArrayList<>();
            serverCommand.getAttributes().forEach(attr -> {
                ClientAttribute attribute = new ClientAttribute(
                        attr.get("propertyName").toString(),
                        attr.get("value"),
                        Optional.ofNullable(attr.get("qualifier")).map(o -> o.toString()).orElse(null),
                        Optional.ofNullable(attr.get("tag")).map(t -> Tag.tagFor.get(t)).orElse(Tag.VALUE));

                Optional.ofNullable(attr.get("id")).map(i -> i.toString()).filter(i -> i.endsWith("S")).ifPresent(i -> attribute.setId(i));

                attribute.setBaseValue(attr.get("baseValue"));
                attributes.add(attribute);
            });

            ClientPresentationModel model = new ClientPresentationModel(serverCommand.getPmId(), attributes);
            model.setPresentationModelType(serverCommand.getPmType());
            if (serverCommand.isClientSideOnly()) {
                model.setClientSideOnly(true);
            }
            getClientModelStore().add(model);
            clientDolphin.updateQualifiers(model);
        } else if (command instanceof ValueChangedCommand) {
            ValueChangedCommand serverCommand = (ValueChangedCommand) command;
            Attribute attribute = getClientModelStore().findAttributeById(serverCommand.getAttributeId());
            if (attribute == null) {
                LOG.warn("attribute with id {} not found, cannot update old value {} to new value {}", serverCommand.getAttributeId(), serverCommand.getOldValue(), serverCommand.getNewValue());
                return;
            }

            Optional oldVal = Optional.ofNullable(attribute.getValue());
            Optional newVal = Optional.ofNullable(serverCommand.getNewValue());
            if ((!oldVal.isPresent() && newVal.isPresent()) || (oldVal.isPresent() && !newVal.isPresent()) || (oldVal.isPresent() && newVal.isPresent() && !oldVal.get().equals(newVal.get()))) {
                LOG.trace("updating {} with id {} from {} to {}", attribute.getPropertyName(), attribute.getId(), attribute.getValue(), serverCommand.getNewValue());
                attribute.setValue(serverCommand.getNewValue());
            }
        } else if (command instanceof SwitchPresentationModelCommand) {
            SwitchPresentationModelCommand serverCommand = (SwitchPresentationModelCommand) command;
            PresentationModel switchPm = getClientModelStore().findPresentationModelById(serverCommand.getPmId());
            if (switchPm == null) {
                LOG.warn("switch pm with id {} not found, cannot switch", serverCommand.getPmId());
                return;
            }
            PresentationModel sourcePm = getClientModelStore().findPresentationModelById(serverCommand.getSourcePmId());
            if (sourcePm == null) {
                LOG.warn("source pm with id {} not found, cannot switch", serverCommand.getSourcePmId());
                return;
            }
            switchPm.syncWith(sourcePm);
        } else if (command instanceof InitializeAttributeCommand) {
            InitializeAttributeCommand serverCommand = (InitializeAttributeCommand) command;
            ClientAttribute attribute = new ClientAttribute(serverCommand.getPropertyName(), serverCommand.getNewValue(), serverCommand.getQualifier(), serverCommand.getTag());

            // todo: add check for no-value; null is a valid value
            if (serverCommand.getQualifier() != null) {
                List<ClientAttribute> copies = getClientModelStore().findAllAttributesByQualifier(serverCommand.getQualifier());
                if (copies != null && !copies.isEmpty()) {
                    if (null == serverCommand.getNewValue()) {
                        attribute.setValue(copies.get(0).getValue());
                    } else {
                        copies.forEach(a -> {
                            a.setValue(attribute.getValue());
                        });
                    }
                }
            }
            ClientPresentationModel presentationModel = null;
            if (serverCommand.getPmId() != null) {
                presentationModel = getClientModelStore().findPresentationModelById(serverCommand.getPmId());
            }
            // here we could have a pmType conflict and we may want to throw an Exception...
            // if there is no pmId, it is most likely an error and CreatePresentationModelCommand should have been used
            if (presentationModel == null) {
                presentationModel = new ClientPresentationModel(serverCommand.getPmId(), new ArrayList<>());
                presentationModel.setPresentationModelType(serverCommand.getPmType());
                getClientModelStore().add(presentationModel);
            }
            // if we already have the attribute, just update the value
            Attribute existingAtt = presentationModel.getAt(serverCommand.getPropertyName(), serverCommand.getTag());
            if (existingAtt != null) {
                existingAtt.setValue(attribute.getValue());
            } else {
                clientDolphin.addAttributeToModel(presentationModel, attribute);
            }
            clientDolphin.updateQualifiers(presentationModel);
        } else if (command instanceof SavedPresentationModelNotification) {
            SavedPresentationModelNotification serverCommand = (SavedPresentationModelNotification) command;

            if (serverCommand.getPmId() != null) {
                ClientPresentationModel model = getClientModelStore().findPresentationModelById(serverCommand.getPmId());
                if (model == null) {
                    LOG.warn("model with id {} not found, cannot rebase", serverCommand.getPmId());
                    return;
                }
                model.getAttributes().forEach(a -> a.rebase());
            }
        } else if (command instanceof PresentationModelResetedCommand) {
            PresentationModelResetedCommand serverCommand = (PresentationModelResetedCommand) command;
            if (serverCommand.getPmId() != null) {
                ClientPresentationModel model = getClientModelStore().findPresentationModelById(serverCommand.getPmId());
                // reset locally first
                if (model != null) {
                    model.getAttributes().forEach(a -> a.reset());
                }
            }
        } else if (command instanceof AttributeMetadataChangedCommand) {
            AttributeMetadataChangedCommand serverCommand = (AttributeMetadataChangedCommand) command;
            ClientAttribute attribute = getClientModelStore().findAttributeById(serverCommand.getAttributeId());
            if (attribute != null && serverCommand.getMetadataName() != null) {
                if (serverCommand.getMetadataName().equals(Attribute.BASE_VALUE)) {
                    attribute.setBaseValue(serverCommand.getValue());
                } else if (serverCommand.getMetadataName().equals(Attribute.QUALIFIER_PROPERTY)) {
                    attribute.setQualifier(serverCommand.getValue().toString());
                } else if (serverCommand.getMetadataName().equals(Attribute.VALUE)) {
                    attribute.setValue(serverCommand.getValue());
                } else {
                    //TODO
                    LOG.warn("TODO");
                }
            }
        } else if (command instanceof CallNamedActionCommand) {
            CallNamedActionCommand serverCommand = (CallNamedActionCommand) command;
            clientDolphin.send(serverCommand.getActionName());
        } else {
            //TODO
            LOG.warn("TODO");
        }
    }

    @Override
    public void send(final Command command, final OnFinishedHandler callback) {
        communicationLock.lock();
        try {
            CommandAndHandler tuple = new CommandAndHandler();
            tuple.setCommand(command);
            tuple.setHandler(callback);
            commands.add(tuple);
        } finally {
            communicationLock.unlock();
        }

    }

    @Override
    public void send(Command command) {
        send(command, null);
    }

    @Override
    public void setPushListener(NamedCommand pushListener) {
        this.pushListener = pushListener;
    }

    @Override
    public void setReleaseCommand(SignalCommand releaseCommand) {
        this.releaseCommand = releaseCommand;
    }

    public SignalCommand getReleaseCommand() {
        return releaseCommand;
    }

    @Override
    public void setPushEnabled(boolean pushEnabled) {
    }

    @Override
    public boolean isPushEnabled() {
        return true;
    }

    @Override
    public void listen() {
    }

    protected ClientModelStore getClientModelStore() {
        return clientDolphin.getClientModelStore();
    }

}
