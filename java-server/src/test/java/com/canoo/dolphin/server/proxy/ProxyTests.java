package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.impl.DolphinConstants;
import com.canoo.dolphin.server.impl.collections.ListMapper;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.empty;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by hendrikebbers on 15.04.15.
 */
public class ProxyTests extends AbstractDolphinBasedTest {

    private TestCarModel car;
    private ModelProxyFactory factory;
    private ServerDolphin dolphin;

    @BeforeMethod
    public void setUp() throws Exception {
        dolphin = createServerDolphin();
        ClassRepository classRepository = new ClassRepository(dolphin);
        BeanRepository beanRepository = new BeanRepository(dolphin, classRepository);

        beanRepository.setListMapper(new ListMapper(dolphin, classRepository, beanRepository));
        factory = new ModelProxyFactory(dolphin, beanRepository);

    }

    @Test
    public void testProxyInstanceCreation_not_initialized() {
        car = factory.create(TestCarModel.class);
        assertNotNull(car);

        assertEquals(null, car.getBrandNameProperty().get());
    }

    @Test
    public void testProxyInstanceCreation() {
        car = factory.create(TestCarModel.class);
        assertNotNull(car);

        String expectedValue = "a String";

        car.getBrandNameProperty().set(expectedValue);
        assertEquals(expectedValue, car.getBrandNameProperty().get());


        assertCorrectPM("brandName", expectedValue, TestCarModel.class.getName(), 4);
    }

    @Test
    public void proxyInstance_Inheritance() {
        TestCarManufacturer manufacturer = factory.create(TestCarManufacturer.class);

        String expectedValue = "a Name";

        manufacturer.setName(expectedValue);

        assertThat(manufacturer.getName(), is(expectedValue));

        assertCorrectPM("name", expectedValue, TestCarManufacturer.class.getName(), 3);
    }

    private void assertCorrectPM(String attributeName, String attributeValue, String modelClass, int expectedAttributeSize) {
        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(modelClass);
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, hasSize(expectedAttributeSize));

        Attribute textAttribute = dolphinModel.getAt(attributeName);
        assertThat(textAttribute.getValue().toString(), is(attributeValue));
    }

    @Test
    public void proxyInstanceWithSetter() {
        car = factory.create(TestCarModel.class);
        assertNotNull(car);

        car.setYear(2015);

        assertThat(car.getYear(), is(2015));

        assertCorrectPM("year", "2015", TestCarModel.class.getName(), 4);
    }

    @Test
    public void proxyInstance_List_Primitive() {
        car = factory.create(TestCarModel.class);

        car.getTripKilometerCounters().addAll(Arrays.asList(1, 3));

        assertThat(car.getTripKilometerCounters(), contains(1,3));

        List<ServerPresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER);
        assertThat(changes, hasSize(2));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_SERVER), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_SERVER), empty());
    }

    @Test
    public void proxyInstanceWithAggregation() {
        car = factory.create(TestCarModel.class);
        assertNotNull(car);

        TestCarManufacturer carManufacturer = factory.create(TestCarManufacturer.class);

        carManufacturer.setName("name");
        carManufacturer.setCapital("capital");
        carManufacturer.setNation("schwiiz");

        car.setCarManufacturer(carManufacturer);

        TestCarManufacturer manufacturer = car.getCarManufacturer();
        assertThat(manufacturer, is(carManufacturer));
        assertThat(manufacturer.getName(),is("name"));
        assertThat(manufacturer.getCapital(),is("capital"));
        assertThat(manufacturer.getNation(),is("schwiiz"));

        List<ServerPresentationModel> carModels = dolphin.findAllPresentationModelsByType(TestCarModel.class.getName());
        assertThat(carModels, hasSize(1));
        List<ServerPresentationModel> manufacturerModels = dolphin.findAllPresentationModelsByType(TestCarManufacturer.class.getName());
        assertThat(manufacturerModels, hasSize(1));

    }

    @Test
    public void proxyInstanceWithSomeInterface() {
        car = factory.create(TestCarModel.class);
        assertNotNull(car);

        Action action = factory.create(Action.class);
        action.setEnabled(true);

        assertCorrectPM("enabled", "true", Action.class.getName(), 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,expectedExceptionsMessageRegExp = "Getter for property brandName should end with \"Property\"")
    public void testInvalidPropertyName() throws Exception {
        factory.create(TestInvalidPropertyInterface.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,expectedExceptionsMessageRegExp = "Collections should not be set, method: setABC")
    public void testInvalidSetterForCollection() throws Exception {
        factory.create(TestNotSetCollection.class);
    }
}
