package com.canoo.dolphin.workflow.server;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.workflow.server.model.ProcessInstance;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("session")
public class WorkflowSubscriptionService {
    @Inject
    private DolphinEventBus eventBus;

    private Subscription subscription;

    public void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    public void subscribe(ProcessInstance processInstance) {
        subscription = eventBus.subscribe("processInstance/" + processInstance.getLabel(), new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("got message: " + message.getData());
            }
        });
    }
}
