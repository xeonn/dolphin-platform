package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.impl.collections.ListMapper;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import org.opendolphin.core.server.ServerDolphin;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
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

        car.getBrandNameProperty().set("a String");
        assertEquals("a String", car.getBrandNameProperty().get());
    }

    @Test
    public void proxyInstance_Inheritance() {
        TestCarManufacturer manufacturer = factory.create(TestCarManufacturer.class);

        manufacturer.setName("a Name");

        assertThat(manufacturer.getName(), is("a Name"));
    }


    @Test
    public void proxyInstanceWithSetter() {
        car = factory.create(TestCarModel.class);
        assertNotNull(car);

        car.setYear(2015);

        assertThat(car.getYear(), is(2015));
    }

    @Test
    public void proxyInstance_List_Primitive() {
        car = factory.create(TestCarModel.class);

        car.getTripKilometerCounters().addAll(Arrays.asList(1, 3));

        assertThat(car.getTripKilometerCounters(), contains(1,3));
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
