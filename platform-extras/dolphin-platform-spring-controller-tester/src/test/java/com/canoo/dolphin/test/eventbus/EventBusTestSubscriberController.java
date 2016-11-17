package com.canoo.dolphin.test.eventbus;

import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@DolphinController(EventBusTestConstants.EVENT_BUS_SUBSCIBER_CONTROLLER_NAME)
public class EventBusTestSubscriberController {

    @DolphinModel
    private EventBusTestModel model;

    @Autowired
    private DolphinEventBus eventBus;

    @PostConstruct
    public void init() {
        eventBus.subscribe(EventBusTestConstants.TEST_TOPIC, new MessageListener<String>() {
            @Override
            public void onMessage(Message<String> message) {
                model.valueProperty().set(message.getData());
            }
        });
    }
}