package org.opendolphin.core.client.comm;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.NamedCommand;
import org.opendolphin.core.comm.SignalCommand;

public interface ClientConnector {

    void send(Command command, OnFinishedHandler callback);

    void send(Command command);

    void setPushListener(NamedCommand pushListener);

    void setReleaseCommand(SignalCommand releaseCommand);

    void setPushEnabled(boolean pushEnabled);

    boolean isPushEnabled();

    void listen();
}
