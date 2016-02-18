package com.canoo.dolphin.client.impl;

import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DolphinCommandHandler {

    private final ClientDolphin clientDolphin;

    public DolphinCommandHandler(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
    }

    public synchronized CompletableFuture<Void> invokeDolphinCommand(String command) {
        final CompletableFuture<Void> result = new CompletableFuture<>();
        clientDolphin.send(command, new OnFinishedHandler() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                result.complete(null);
            }

            @Override
            public void onFinishedData(List<Map> data) {
                //Unused....
            }
        });
        return result;
    }
}
