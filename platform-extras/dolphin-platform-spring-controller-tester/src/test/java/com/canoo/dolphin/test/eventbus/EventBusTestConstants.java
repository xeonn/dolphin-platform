package com.canoo.dolphin.test.eventbus;

import com.canoo.dolphin.server.event.Topic;

/**
 * Created by hendrikebbers on 02.11.16.
 */
public interface EventBusTestConstants {
    String EVENT_BUS_SUBSCIBER_CONTROLLER_NAME = "EventBusSubscriberController";
    String EVENT_BUS_PUBLISHER_CONTROLLER_NAME = "EventBusPublisherController";
    String CALL_ACTION = "call";
    Topic<String> TEST_TOPIC = Topic.create();
}
