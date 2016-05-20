package com.canoo.dolphin.test;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Topic;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@DolphinController("TestController")
public class TestController {

    @Inject
    private BeanManager beanManager;

    @Inject
    private DolphinEventBus eventBus;

    @DolphinModel
    private TestModel model;

    private static Topic<String> TEST_TOPIC = Topic.create();

    @PostConstruct
    public void init() {
        eventBus.subscribe(TEST_TOPIC, e -> {
            model.setValue(e.getData());
        });
    }

    @DolphinAction("sendEvent")
    public void sendEvent() {
        eventBus.publish(TEST_TOPIC, "changed by eventBus!");
    }

    @DolphinAction("action")
    public void doSomeAction() {
        model.setValue("Hello Dolphin Test");
    }

    @DolphinAction("addToList")
    public void addToList() {
        model.getItems().add("Hallo");
    }

    @DolphinAction("addBeanToList")
    public void addBeanToList() {
        TestSubModel bean = beanManager.create(TestSubModel.class);
        bean.setValue("I'm a subbean");
        model.getInternModels().add(bean);
    }
}
