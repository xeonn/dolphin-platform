//package com.canoo.dolphin.server.proxy;
//
//import com.canoo.dolphin.BeanManager;
//import com.canoo.dolphin.impl.BeanBuilder;
//import com.canoo.dolphin.impl.BeanRepository;
//import com.canoo.dolphin.impl.ClassRepository;
//import com.canoo.dolphin.impl.DolphinConstants;
//import com.canoo.dolphin.impl.ReflectionHelper;
//import com.canoo.dolphin.impl.collections.ListMapper;
//import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
//import com.canoo.dolphin.server.util.SimpleTestModel;
//import org.opendolphin.core.Attribute;
//import org.opendolphin.core.server.ServerDolphin;
//import org.opendolphin.core.server.ServerPresentationModel;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//
//import javax.swing.*;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//import static org.testng.Assert.*;
//
///**
// * Created by hendrikebbers on 15.04.15.
// */
//public class ProxyTests extends AbstractDolphinBasedTest {
//
//    private TestCarModel car;
//    private BeanManager manager;
//    private ServerDolphin dolphin;
//    private BeanRepository beanRepository;
//
//    @BeforeMethod
//    public void setUp() throws Exception {
//        dolphin = createServerDolphin();
//        beanRepository = new BeanRepository(dolphin);
//        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository, builderFactory);
//        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository, builderFactory);
//        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper, builderFactory);
//        manager = new BeanManagerImpl(beanRepository, beanBuilder);
//    }
//
//    @Test
//    public void testProxyInstanceCreation_not_initialized() {
//        car = manager.create(TestCarModel.class);
//        assertNotNull(car);
//
//        assertEquals(null, car.getBrandNameProperty().get());
//    }
//
//    @Test
//    public void testProxyInstanceCreation() {
//        car = manager.create(TestCarModel.class);
//        assertNotNull(car);
//
//        String expectedValue = "a String";
//
//        car.getBrandNameProperty().set(expectedValue);
//        assertEquals(expectedValue, car.getBrandNameProperty().get());
//
//        assertCorrectPM("brandName", expectedValue, TestCarModel.class.getName(), 5);
//    }
//
//    @Test(expectedExceptions = IllegalArgumentException.class)
//    public void testProxyInstance_OnlySetManagedBeans() {
//        TestMixedModel testMixedModel = manager.create(TestMixedModel.class);
//
//        testMixedModel.setSimpleModel(new SimpleTestModel());
//    }
//
//    @Test(expectedExceptions = IllegalArgumentException.class)
//    public void testProxyInstance_OnlyAddManagedBeansToList() {
//        TestMixedModel testMixedModel = manager.create(TestMixedModel.class);
//
//        testMixedModel.getTestModels().add(new SimpleTestModel());
//    }
//
//    @Test(expectedExceptions = IllegalArgumentException.class)
//    public void testProxyInstance_OnlySetManagedBeansToList() {
//        TestMixedModel testMixedModel = manager.create(TestMixedModel.class);
//
//        testMixedModel.getTestModels().set(0,new SimpleTestModel());
//    }
//
//
//    @Test
//    public void proxyInstance_Inheritance() {
//        TestCarManufacturer manufacturer = manager.create(TestCarManufacturer.class);
//
//        String expectedValue = "a Name";
//
//        manufacturer.setName(expectedValue);
//
//        assertThat(manufacturer.getName(), is(expectedValue));
//
//        assertCorrectPM("name", expectedValue, TestCarManufacturer.class.getName(), 3);
//    }
//
//    private void assertCorrectPM(String attributeName, String attributeValue, String modelClass, int expectedAttributeSize) {
//        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(modelClass);
//        assertThat(dolphinModels, hasSize(1));
//
//        ServerPresentationModel dolphinModel = dolphinModels.get(0);
//
//        List<Attribute> attributes = dolphinModel.getAttributes();
//        assertThat(attributes, hasSize(expectedAttributeSize));
//
//        Attribute textAttribute = dolphinModel.getAt(attributeName);
//        assertThat(textAttribute.getValue().toString(), is(attributeValue));
//    }
//
//    @Test
//    public void proxyInstanceWithSetter() {
//        car = manager.create(TestCarModel.class);
//        assertNotNull(car);
//
//        car.setYear(2015);
//
//        assertThat(car.getYear(), is(2015));
//
//        assertCorrectPM("year", "2015", TestCarModel.class.getName(), 5);
//    }
//
//    @Test
//    public void proxyInstance_List_Primitive() {
//        car = manager.create(TestCarModel.class);
//
//        car.getTripKilometerCounters().addAll(Arrays.asList(1, 3));
//
//        assertThat(car.getTripKilometerCounters(), contains(1,3));
//
//        List<ServerPresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER);
//        assertThat(changes, hasSize(2));
//        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_SERVER), empty());
//        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_SERVER), empty());
//    }
//
//    @Test
//    public void proxyInstance_List_Objects() {
//        car = manager.create(TestCarModel.class);
//        TestCarColor blue = manager.create(TestCarColor.class);
//        blue.setColorName("blue");
//        TestCarColor red = manager.create(TestCarColor.class);
//        red.setColorName("red");
//
//        car.getCarColors().addAll(Arrays.asList(blue, red));
//
//        assertThat(car.getCarColors(), contains(blue,red));
//
//        List<ServerPresentationModel> testCarModels = dolphin.findAllPresentationModelsByType(TestCarModel.class.getName());
//        assertThat(testCarModels, hasSize(1));
//
//        List<ServerPresentationModel> colorModels = dolphin.findAllPresentationModelsByType(TestCarColor.class.getName());
//        assertThat(colorModels, hasSize(2));
//
//        List < ServerPresentationModel > changes = dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER);
//        assertThat(changes, hasSize(2));
//        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_SERVER), empty());
//        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_SERVER), empty());
//    }
//
//
//    @Test
//    public void proxyInstanceWithAggregation() {
//        car = manager.create(TestCarModel.class);
//        assertNotNull(car);
//
//        TestCarManufacturer carManufacturer = manager.create(TestCarManufacturer.class);
//
//        carManufacturer.setName("name");
//        carManufacturer.setCapital("capital");
//        carManufacturer.setNation("schwiiz");
//
//        car.setCarManufacturer(carManufacturer);
//
//        TestCarManufacturer manufacturer = car.getCarManufacturer();
//        assertThat(manufacturer, is(carManufacturer));
//        assertThat(manufacturer.getName(),is("name"));
//        assertThat(manufacturer.getCapital(),is("capital"));
//        assertThat(manufacturer.getNation(),is("schwiiz"));
//
//        List<ServerPresentationModel> carModels = dolphin.findAllPresentationModelsByType(TestCarModel.class.getName());
//        assertThat(carModels, hasSize(1));
//        List<ServerPresentationModel> manufacturerModels = dolphin.findAllPresentationModelsByType(TestCarManufacturer.class.getName());
//        assertThat(manufacturerModels, hasSize(1));
//    }
//
//    @Test
//    public void testMixedModels() throws Exception {
//        SimpleTestModel simpleTestModel1 = manager.create(SimpleTestModel.class);
//        SimpleTestModel simpleTestModel2 = manager.create(SimpleTestModel.class);
//
//        TestMixedModel testMixedModel = manager.create(TestMixedModel.class);
//
//        testMixedModel.getTestModels().addAll(Arrays.asList(simpleTestModel1, simpleTestModel2));
//
//        List<ServerPresentationModel> mixedModels = dolphin.findAllPresentationModelsByType(TestMixedModel.class.getName());
//        assertThat(mixedModels, hasSize(1));
//        List<ServerPresentationModel>  simpleTestModels = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
//        assertThat(simpleTestModels, hasSize(2));
//    }
//
//    @Test
//    public void proxyInstanceWithSomeInterface() {
//        car = manager.create(TestCarModel.class);
//        assertNotNull(car);
//
//        Action action = manager.create(Action.class);
//        action.setEnabled(true);
//
//        assertCorrectPM("enabled", "true", Action.class.getName(), 1);
//    }
//
//    @Test
//    public void testIsProxyType() throws Exception {
//        TestCarModel testCarModel = manager.create(TestCarModel.class);
//
//        assertFalse(ReflectionHelper.isProxyInstance("No Proxy"));
//        assertTrue(ReflectionHelper.isProxyInstance(testCarModel));
//
//        SimpleTestModel simpleTestModel = manager.create(SimpleTestModel.class);
//        assertFalse(ReflectionHelper.isProxyInstance(simpleTestModel));
//    }
//
//}
