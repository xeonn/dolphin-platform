package org.opendolphin.core.client.comm;

import java.util.concurrent.TimeUnit;

/**
 * Created by hendrikebbers on 29.08.16.
 */
public interface DataflowQueue<T> {

    T getVal() throws InterruptedException;

    T getVal(long value,TimeUnit timeUnit) throws InterruptedException;

    void add(T value);

    int length();
}
