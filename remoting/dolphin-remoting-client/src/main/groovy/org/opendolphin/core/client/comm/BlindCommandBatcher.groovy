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

import org.opendolphin.core.comm.GetPresentationModelCommand
import org.opendolphin.core.comm.ValueChangedCommand

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Level
import java.util.logging.Logger

/**
 * A command batcher that puts all commands in one packet that
 * have no onFinished handler attached (blind commands), which is the typical case
 * for value change and create presentation model commands
 * when synchronizing back to the server.
 */

class BlindCommandBatcher extends CommandBatcher {

    private static final Logger LOG = Logger.getLogger(BlindCommandBatcher.class.getName());

    ExecutorService executorService = Executors.newCachedThreadPool();

    LinkedList<CommandAndHandler> commandsAndHandlers = new LinkedList<>();

    Lock commandsAndHandlersLock = new ReentrantLock();

    /** Time allowed to fill the queue before a batch is assembled */
    long deferMillis = 10;

    /** Must be > 0*/
    int maxBatchSize = 100;

    /** when attribute x changes its value from 0 to 1 and then from 1 to 2, merge this into one change from 0 to 2 */
    boolean mergeValueChanges = false;

    protected final AtomicBoolean inProcess = new AtomicBoolean(false); // whether we started to batch up commands

    protected final AtomicBoolean deferralNeeded = new AtomicBoolean(false);
    // whether we need to give commands the opportunity to enter the queue

    protected boolean shallWeEvenTryToMerge = false; // do not even try if there is no value change cmd in the batch

    protected final int MAX_GET_PM_CMD_CACHE_SIZE = 200;

    protected LinkedList<CommandAndHandler> cacheGetPmCmds = new LinkedList();

    @Override
    boolean isEmpty() {
        return waitingBatches.length() < 1;
    }

    @Override
    void batch(CommandAndHandler commandWithHandler) {
        LOG.log(Level.FINEST, "batching " + commandWithHandler.getCommand() + " with" + (commandWithHandler.getHandler() ? '' : 'out') + " handler");

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

    // the only command we can safely drop is a GetPmCmd where a second one for the same
    // pmId is already in the batch with the exact same onFinished handler or no handler at all
    protected boolean canBeDropped(CommandAndHandler commandWithHandler) {
        if (!(commandWithHandler.getCommand() instanceof GetPresentationModelCommand)) {
            return false;
        }
        String pmId = ((GetPresentationModelCommand) commandWithHandler.command).getPmId();
        OnFinishedHandler handler = commandWithHandler.getHandler();

        boolean found = false;
        for (CommandAndHandler commandAndHandler : cacheGetPmCmds) {
            if (((GetPresentationModelCommand) commandAndHandler.getCommand()).getPmId().equals(pmId) &&
                    ((handler == null) || handler.is(commandAndHandler.handler))) {
                found = true;
            }
        }

        if (!found) {
            cacheGetPmCmds.push(commandWithHandler) // front adding makes lookup faster
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
            void run() {
                int count = maxBatchSize;        // never wait for more than those
                while (deferralNeeded.get() && count > 0) {
                    count--;
                    deferralNeeded.set(false);
                    sleep(deferMillis);
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
            void run() {
                commandsAndHandlersLock.lock();
                try {
                    CommandAndHandler last = batchBlinds(commandsAndHandlers); // always send leading blinds first
                    if (last != null) {
                        // we do have a trailing command with handler and batch it separately
                        waitingBatches.add(Arrays.asList(last));
                    }
                    if (!commandsAndHandlers.isEmpty()) {
                        processBatch();
                        // this is not so much like recursion, more like a trampoline
                    }
                } finally {
                    commandsAndHandlersLock.unlock();
                }
            }
        });
    }

    protected CommandAndHandler batchBlinds(List<CommandAndHandler> queue) {
        if (queue.isEmpty()) {
            return;
        }
        List<CommandAndHandler> blindCommands = new LinkedList();
        int counter = maxBatchSize;
        // we have to check again, since new ones may have arrived since last check
        CommandAndHandler val = take(queue);
        shallWeEvenTryToMerge = false;
        while (counter-- && val?.isBatchable()) {      // we do have a blind
            addToBlindsOrMerge(blindCommands, val);
            val = counter ? take(queue) : null;
        }
        LOG.log(Level.FINEST, "batching " + blindCommands.size() + " blinds");
        if (blindCommands) {
            waitingBatches.add(blindCommands);
        }
        return val; // may be null or a cwh that has a handler
    }

    protected void addToBlindsOrMerge(List<CommandAndHandler> blindCommands, CommandAndHandler val) {
        if (!wasMerged(blindCommands, val)) {
            blindCommands.add(val);
            if (val.command in ValueChangedCommand) {
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
        if (blindCommands.empty) {
            return false;
        }
        if (!val.command) {
            return false;
        }
        if (!(val.command in ValueChangedCommand)) {
            return false;
        }
        ValueChangedCommand valCmd = (ValueChangedCommand) val.command;

        shallWeEvenTryToMerge = true;

        def mergeable = blindCommands.find { CommandAndHandler cah ->           // this has O(n*n) and can become costly
            cah.command != null &&
                    cah.command instanceof ValueChangedCommand &&
                    ((ValueChangedCommand) cah.command).attributeId == valCmd.attributeId &&
                    ((ValueChangedCommand) cah.command).newValue == valCmd.oldValue
        }
        if (!mergeable) {
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
}
