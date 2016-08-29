package org.opendolphin.core.client.comm;

/**
 * Created by hendrikebbers on 29.08.16.
 */
public interface DataflowQueue<T> {

    T getVal() throws InterruptedException;
}
