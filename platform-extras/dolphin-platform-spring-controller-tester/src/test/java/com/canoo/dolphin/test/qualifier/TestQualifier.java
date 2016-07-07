package com.canoo.dolphin.test.qualifier;

import com.canoo.dolphin.test.ControllerUnderTest;
import com.canoo.dolphin.test.SpringTestNGControllerTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.canoo.dolphin.test.qualifier.QualifierTestConstants.*;
import static com.canoo.dolphin.test.qualifier.QualifierTestConstants.QUALIFIER_CONTROLLER_NAME;
import static org.testng.Assert.assertEquals;

public class TestQualifier extends SpringTestNGControllerTest {

    private ControllerUnderTest<QualifierTestModel> controller;

    @BeforeMethod
    protected void init() {
        controller = createController(QUALIFIER_CONTROLLER_NAME);
    }


    @AfterMethod
    protected void destroy() {
        controller.destroy();
    }

    @Test
    public void testQualifier1() {
        //given:
        QualifierTestSubModelOne subModelOne = controller.getModel().subModelOneProperty().get();
        QualifierTestSubModelTwo subModelTwo = controller.getModel().subModelTwoProperty().get();

        //when:
        subModelOne.booleanProperty().set(true);
        subModelOne.stringProperty().set("Test1");
        subModelOne.integerProperty().set(42);

        controller.invoke(DUMMY_ACTION);

        //then:
        assertEquals(subModelOne.booleanProperty().get(), Boolean.TRUE);
        assertEquals(subModelOne.stringProperty().get(), "Test1");
        assertEquals(subModelOne.integerProperty().get(), new Integer(42));

        assertEquals(subModelTwo.booleanProperty().get(), Boolean.TRUE);
        assertEquals(subModelTwo.stringProperty().get(), "Test1");
        assertEquals(subModelTwo.integerProperty().get(), new Integer(42));
    }

    @Test
    public void testQualifier2() {
        //given:
        QualifierTestSubModelOne subModelOne = controller.getModel().subModelOneProperty().get();
        QualifierTestSubModelTwo subModelTwo = controller.getModel().subModelTwoProperty().get();

        //when:
        subModelTwo.booleanProperty().set(true);
        subModelTwo.stringProperty().set("Test1");
        subModelTwo.integerProperty().set(42);

        controller.invoke(DUMMY_ACTION);

        //then:
        assertEquals(subModelOne.booleanProperty().get(), Boolean.TRUE);
        assertEquals(subModelOne.stringProperty().get(), "Test1");
        assertEquals(subModelOne.integerProperty().get(), new Integer(42));

        assertEquals(subModelTwo.booleanProperty().get(), Boolean.TRUE);
        assertEquals(subModelTwo.stringProperty().get(), "Test1");
        assertEquals(subModelTwo.integerProperty().get(), new Integer(42));
    }

    @Test
    public void testQualifierUnbind() {
        //given:
        QualifierTestSubModelOne subModelOne = controller.getModel().subModelOneProperty().get();
        QualifierTestSubModelTwo subModelTwo = controller.getModel().subModelTwoProperty().get();

        //when:
        subModelOne.booleanProperty().set(true);
        subModelOne.stringProperty().set("Test1");
        subModelOne.integerProperty().set(42);

        controller.invoke(UNBIND_ACTION);

        subModelOne.booleanProperty().set(false);
        subModelOne.stringProperty().set("Test2");
        subModelOne.integerProperty().set(44);

        //then:
        assertEquals(subModelOne.booleanProperty().get(), Boolean.FALSE);
        assertEquals(subModelOne.stringProperty().get(), "Test2");
        assertEquals(subModelOne.integerProperty().get(), new Integer(44));

        assertEquals(subModelTwo.booleanProperty().get(), Boolean.TRUE);
        assertEquals(subModelTwo.stringProperty().get(), "Test1");
        assertEquals(subModelTwo.integerProperty().get(), new Integer(42));
    }

    @Test
    public void testQualifierNotBound() {
        //given:
        QualifierTestSubModelOne subModelOne = controller.getModel().subModelOneProperty().get();
        QualifierTestSubModelTwo subModelTwo = controller.getModel().subModelTwoProperty().get();

        //when:
        controller.invoke(UNBIND_ACTION);

        subModelOne.booleanProperty().set(true);
        subModelOne.stringProperty().set("Test1");
        subModelOne.integerProperty().set(42);

        controller.invoke(DUMMY_ACTION);

        //then:
        assertEquals(subModelOne.booleanProperty().get(), Boolean.TRUE);
        assertEquals(subModelOne.stringProperty().get(), "Test1");
        assertEquals(subModelOne.integerProperty().get(), new Integer(42));

        assertEquals(subModelTwo.booleanProperty().get(), null);
        assertEquals(subModelTwo.stringProperty().get(), null);
        assertEquals(subModelTwo.integerProperty().get(), null);
    }

    @Test
    public void testQualifierRebind() {
        //given:
        QualifierTestSubModelOne subModelOne = controller.getModel().subModelOneProperty().get();
        QualifierTestSubModelTwo subModelTwo = controller.getModel().subModelTwoProperty().get();

        //when:
        controller.invoke(UNBIND_ACTION);

        subModelOne.booleanProperty().set(true);
        subModelOne.stringProperty().set("Test1");
        subModelOne.integerProperty().set(42);

        controller.invoke(BIND_ACTION);

        //then:
        assertEquals(subModelOne.booleanProperty().get(), Boolean.TRUE);
        assertEquals(subModelOne.stringProperty().get(), "Test1");
        assertEquals(subModelOne.integerProperty().get(), new Integer(42));

        assertEquals(subModelTwo.booleanProperty().get(), null);
        assertEquals(subModelTwo.stringProperty().get(), null);
        assertEquals(subModelTwo.integerProperty().get(), null);
    }

    @Test
    public void testQualifierChangeAfterRebind() {
        //given:
        QualifierTestSubModelOne subModelOne = controller.getModel().subModelOneProperty().get();
        QualifierTestSubModelTwo subModelTwo = controller.getModel().subModelTwoProperty().get();

        //when:
        controller.invoke(UNBIND_ACTION);

        subModelOne.booleanProperty().set(true);
        subModelOne.stringProperty().set("Test1");
        subModelOne.integerProperty().set(42);

        controller.invoke(BIND_ACTION);

        subModelTwo.booleanProperty().set(true);
        subModelTwo.stringProperty().set("Test1");
        subModelTwo.integerProperty().set(42);

        //then:
        assertEquals(subModelTwo.booleanProperty().get(), Boolean.TRUE);
        assertEquals(subModelTwo.stringProperty().get(), "Test1");
        assertEquals(subModelTwo.integerProperty().get(), new Integer(42));

        assertEquals(subModelTwo.booleanProperty().get(), Boolean.TRUE);
        assertEquals(subModelTwo.stringProperty().get(), "Test1");
        assertEquals(subModelTwo.integerProperty().get(), new Integer(42));
    }

}

