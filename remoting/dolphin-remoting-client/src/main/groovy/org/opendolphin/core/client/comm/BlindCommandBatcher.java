package org.opendolphin.core.client.comm;

import groovy.lang.Closure;
import groovy.transform.CompileStatic;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyStaticMethods;
import org.opendolphin.core.comm.GetPresentationModelCommand;
import org.opendolphin.core.comm.ValueChangedCommand;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A command batcher that puts all commands in one packet that
 * have no onFinished handler attached (blind commands), which is the typical case
 * for value change and create presentation model commands
 * when synchronizing back to the server.
 */
@CompileStatic
public class BlindCommandBatcher extends CommandBatcher {
    @Override
    public boolean isEmpty() {
        return getWaitingBatches().length() < 1;
    }

    @Override
    public void batch(CommandAndHandler commandWithHandler) {
        LOG.log(Level.FINEST, "batching " + commandWithHandler.getCommand() + " with" + (DefaultGroovyMethods.asBoolean(commandWithHandler.getHandler()) ? "" : "out") + " handler");

        if (canBeDropped(commandWithHandler)) {
            LOG.log(Level.FINEST, "dropping duplicate GetPresentationModelCommand");
            return;

        }


        commandsAndHandlersLock.lock();
        try {
            commandsAndHandlers.add(commandWithHandler);
        } finally {
            commandsAndHandlersLock.unlock();
        }


        if (commandWithHandler.isBatchable()) {
            deferralNeeded.set(true);
            if (inProcess.get()) {
                return;

            }

            processDeferred();
        } else {
            processBatch();
        }

    }

    protected boolean canBeDropped(CommandAndHandler commandWithHandler) {
        if (!(commandWithHandler.getCommand() instanceof GetPresentationModelCommand)) {
            return false;
        }

        String pmId = ((GetPresentationModelCommand) commandWithHandler.getCommand()).getPmId();
        OnFinishedHandler handler = commandWithHandler.getHandler();

        boolean found = false;
        for (CommandAndHandler commandAndHandler : cacheGetPmCmds) {
            if (((GetPresentationModelCommand) commandAndHandler.getCommand()).getPmId().equals(pmId) && ((handler == null) || DefaultGroovyMethods.is(handler, commandAndHandler.getHandler()))) {
                found = true;
            }

        }


        if (!found) {
            cacheGetPmCmds.push(commandWithHandler);// front adding makes lookup faster
            if (cacheGetPmCmds.size() > MAX_GET_PM_CMD_CACHE_SIZE) {
                cacheGetPmCmds.removeLast();
            }

        }

        return found;
    }

    protected void processDeferred() {
        inProcess.set(true);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int count = getMaxBatchSize();// never wait for more than those
                while (deferralNeeded.get() && count > 0) {
                    count = count--;
                    deferralNeeded.set(false);
                    DefaultGroovyStaticMethods.sleep(null, getDeferMillis());
                    // while we sleep, new requests may have arrived that request deferral
                }

                processBatch();
                inProcess.set(false);
            }

        });
    }

    protected void processBatch() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                getCommandsAndHandlersLock().lock();
                try {
                    CommandAndHandler last = batchBlinds(getCommandsAndHandlers());// always send leading blinds first
                    if (last != null) {
                        // we do have a trailing command with handler and batch it separately
                        getWaitingBatches().add(Arrays.asList(last));
                    }

                    if (!getCommandsAndHandlers().isEmpty()) {
                        processBatch();
                        // this is not so much like recursion, more like a trampoline
                    }

                } finally {
                    getCommandsAndHandlersLock().unlock();
                }

            }

        });
    }

    protected CommandAndHandler batchBlinds(List<CommandAndHandler> queue) {
        if (queue.isEmpty()) {
            return null;
        }

        List<CommandAndHandler> blindCommands = new LinkedList();
        int counter = maxBatchSize;
        // we have to check again, since new ones may have arrived since last check
        CommandAndHandler val = take(queue);
        shallWeEvenTryToMerge = false;
        while (val != null && val.isBatchable() && counter-- > 0) {// we do have a blind
            addToBlindsOrMerge(blindCommands, val);
            val = DefaultGroovyMethods.asBoolean(counter) ? take(queue) : null;
        }

        LOG.log(Level.FINEST, "batching " + blindCommands.size() + " blinds");
        if (DefaultGroovyMethods.asBoolean(blindCommands)) {
            getWaitingBatches().add(blindCommands);
        }
        return val;// may be null or a cwh that has a handler
    }

    protected void addToBlindsOrMerge(List<CommandAndHandler> blindCommands, CommandAndHandler val) {
        if (!wasMerged(blindCommands, val)) {
            blindCommands.add(val);
            if (val.getCommand() instanceof ValueChangedCommand) {
                shallWeEvenTryToMerge = true;
            }

        }

    }

    protected boolean wasMerged(List<CommandAndHandler> blindCommands, CommandAndHandler val) {
        if (!mergeValueChanges) {
            return false;
        }

        if (!shallWeEvenTryToMerge) {
            return false;
        }

        if (blindCommands.isEmpty()) {
            return false;
        }

        if (!DefaultGroovyMethods.asBoolean(val.getCommand())) {
            return false;
        }

        if (!(val.getCommand() instanceof ValueChangedCommand)) {
            return false;
        }

        final ValueChangedCommand valCmd = (ValueChangedCommand) val.getCommand();

        shallWeEvenTryToMerge = true;

        CommandAndHandler mergeable = DefaultGroovyMethods.find(blindCommands, new Closure<Boolean>(this, this) {
            public Boolean doCall(CommandAndHandler cah) {// this has O(n*n) and can become costly
                return cah.getCommand() != null && cah.getCommand() instanceof ValueChangedCommand && ((ValueChangedCommand) cah.getCommand()).getAttributeId().equals(valCmd.getAttributeId()) && ((ValueChangedCommand) cah.getCommand()).getNewValue().equals(valCmd.getOldValue());
            }

        });
        if (!DefaultGroovyMethods.asBoolean(mergeable)) {
            return false;
        }


        ValueChangedCommand mergeableCmd = (ValueChangedCommand) mergeable.getCommand();
        LOG.log(Level.FINEST, "merging value changed command for attribute " + mergeableCmd.getAttributeId() + " with new values " + mergeableCmd.getNewValue() + " -> " + valCmd.getNewValue());
        mergeableCmd.setNewValue(valCmd.getNewValue());

        return true;
    }

    protected CommandAndHandler take(List<CommandAndHandler> intern) {
        if (intern.isEmpty()) {
            return null;
        }

        return intern.remove(0);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public LinkedList<CommandAndHandler> getCommandsAndHandlers() {
        return commandsAndHandlers;
    }

    public void setCommandsAndHandlers(LinkedList<CommandAndHandler> commandsAndHandlers) {
        this.commandsAndHandlers = commandsAndHandlers;
    }

    public Lock getCommandsAndHandlersLock() {
        return commandsAndHandlersLock;
    }

    public void setCommandsAndHandlersLock(Lock commandsAndHandlersLock) {
        this.commandsAndHandlersLock = commandsAndHandlersLock;
    }

    public long getDeferMillis() {
        return deferMillis;
    }

    public void setDeferMillis(long deferMillis) {
        this.deferMillis = deferMillis;
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public boolean getMergeValueChanges() {
        return mergeValueChanges;
    }

    public boolean isMergeValueChanges() {
        return mergeValueChanges;
    }

    public void setMergeValueChanges(boolean mergeValueChanges) {
        this.mergeValueChanges = mergeValueChanges;
    }

    private static final Logger LOG = Logger.getLogger(BlindCommandBatcher.class.getName());
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private LinkedList<CommandAndHandler> commandsAndHandlers = new LinkedList<CommandAndHandler>();
    private Lock commandsAndHandlersLock = new ReentrantLock();
    /**
     * Time allowed to fill the queue before a batch is assembled
     */
    private long deferMillis = 10;
    /**
     * Must be > 0
     */
    private int maxBatchSize = 100;
    /**
     * when attribute x changes its value from 0 to 1 and then from 1 to 2, merge this into one change from 0 to 2
     */
    private boolean mergeValueChanges = false;
    protected final AtomicBoolean inProcess = new AtomicBoolean(false);
    protected final AtomicBoolean deferralNeeded = new AtomicBoolean(false);
    protected boolean shallWeEvenTryToMerge = false;
    protected final int MAX_GET_PM_CMD_CACHE_SIZE = 200;
    protected LinkedList<CommandAndHandler> cacheGetPmCmds = new LinkedList();
}
