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

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class CommandBatcher implements ICommandBatcher {

    private final DataflowQueue<List<CommandAndHandler>> waitingBatches;

    private List<CommandAndHandler> internalQueue = new LinkedList<>();

    private final Lock queueLock = new ReentrantLock();

    CommandBatcher() {
        this.waitingBatches = new DataflowQueue<List<CommandAndHandler>>() {

            @Override
            List<CommandAndHandler> getVal() throws InterruptedException {
                queueLock.lock();
                try {
                    List<CommandAndHandler> ret = new ArrayList<>();
                    ret.addAll(internalQueue);
                    internalQueue.clear();
                    return ret
                } finally {
                    queueLock.unlock()
                }
            }

            @Override
            void add(List<CommandAndHandler> value) {
                queueLock.lock();
                try {
                    internalQueue.addAll(value);
                } finally {
                    queueLock.unlock()
                }
            }

            @Override
            int length() {
                queueLock.lock();
                try {
                    return internalQueue.size();
                } finally {
                    queueLock.unlock()
                }
            }
        };
    }

    void batch(CommandAndHandler commandAndHandler) {
        queueLock.lock();
        try {
            internalQueue.add(commandAndHandler);
        } finally {
            queueLock.unlock()
        }
    }

    boolean isEmpty() {
        queueLock.lock();
        try {
            return internalQueue.isEmpty();
        } finally {
            queueLock.unlock()
        }
    }

    DataflowQueue<List<CommandAndHandler>> getWaitingBatches() {
        return waitingBatches
    }
}
