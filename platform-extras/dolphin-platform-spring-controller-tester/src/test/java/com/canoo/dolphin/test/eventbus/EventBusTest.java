package com.canoo.dolphin.test.eventbus;

import com.canoo.dolphin.test.ControllerUnderTest;
import com.canoo.dolphin.test.SpringTestNGControllerTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EventBusTest extends SpringTestNGControllerTest {

    private ControllerUnderTest<EventBusTestModel> publisher;

    private ControllerUnderTest<EventBusTestModel> subscriber;

    @BeforeMethod
    protected void init() {
        publisher = createController(EventBusTestConstants.EVENT_BUS_PUBLISHER_CONTROLLER_NAME);
        subscriber = createController(EventBusTestConstants.EVENT_BUS_SUBSCIBER_CONTROLLER_NAME);
    }


    @AfterMethod
    protected void destroy() {
        publisher.destroy();
        subscriber.destroy();
    }

    @Test
    public void testValuesNotEqualByDefault() {
        //given:
        publisher.getModel().valueProperty().set("A");
        subscriber.getModel().valueProperty().set("B");

        //then:
        Assert.assertFalse(publisher.getModel().valueProperty().get().equals(subscriber.getModel().valueProperty().get()));
    }

    @Test
    public void testValuesEqualsAfterEvent() {
        //given:
        publisher.getModel().valueProperty().set("A");

        //when:
        publisher.invoke(EventBusTestConstants.CALL_ACTION);

        //then:
        Assert.assertTrue(publisher.getModel().valueProperty().get().equals(subscriber.getModel().valueProperty().get()));
        Assert.assertEquals(publisher.getModel().valueProperty().get(), "A");
        Assert.assertEquals(subscriber.getModel().valueProperty().get(), "A");
    }

}