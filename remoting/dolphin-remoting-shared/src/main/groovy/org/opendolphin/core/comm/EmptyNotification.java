package org.opendolphin.core.comm;

/**
 * A notification that does nothing on the server.
 * It is only used to hook into the communication at a known point
 * such that the onFinished handler can be executed
 * in the expected sequence.
 */
public class EmptyNotification extends Command {
}
