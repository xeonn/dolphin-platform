package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.impl.BeanBuilder;
import com.canoo.dolphin.impl.BeanRepository;
import com.canoo.dolphin.impl.ClassRepository;
import com.canoo.dolphin.impl.DolphinUtils;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.collections.ListMapper;
import com.canoo.dolphin.server.proxy.TestCarModel;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import org.opendolphin.core.server.ServerDolphin;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.beans.BeanInfo;
import java.beans.Introspector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DolphinUtilsTest extends AbstractDolphinBasedTest {

    private BeanManager manager;
    private ServerDolphin dolphin;
    private BeanRepository beanRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        dolphin = createServerDolphin();
        BeanRepository beanRepository = new BeanRepository(dolphin);
        final PresentationModelBuilderFactory builderFactory = new ServerPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository, builderFactory);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper, builderFactory);
        manager = new BeanManager(beanRepository, beanBuilder);
    }

    @Test
    public void testGetDolphinAttributePropertyNameForMethod() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(TestCarModel.class);
        String nameForMethod = DolphinUtils.getDolphinAttributeName(info.getPropertyDescriptors()[0]);

        assertThat(nameForMethod, is("brandName"));
    }

//    @Test
//    public void testGetBeanInfoWithHierarchy_interface() throws Exception {
//        BeanInfo beanInfo = DolphinUtils.getBeanInfo(TestCarManufacturer.class);
//
//        assertThat(beanInfo.getPropertyDescriptors().length, is(3));
//    }

//    @Test
//    public void testGetBeanInfo_forClass() throws Exception {
//        BeanInfo beanInfo = DolphinUtils.getBeanInfo(SimpleAnnotatedTestModel.class);
//
//        assertThat(beanInfo.getPropertyDescriptors().length, is(1));
//    }

//    @Test(expectedExceptions = IllegalArgumentException.class,expectedExceptionsMessageRegExp = "Getter for property brandName should end with \"Property\"")
//    public void testInvalidPropertyName() throws Exception {
//        DolphinUtils.getBeanInfo(TestInvalidPropertyInterface.class);
//    }

//    @Test(expectedExceptions = IllegalArgumentException.class,expectedExceptionsMessageRegExp = "Collections should not be set, method: setABC")
//    public void testInvalidSetterForCollection() throws Exception {
//        DolphinUtils.getBeanInfo(TestNotSetCollection.class);
//    }

//    @Test(expectedExceptions = IllegalArgumentException.class,expectedExceptionsMessageRegExp = "Collections must be subtypes of List, method: something")
//    public void testInvalidCollectionType() throws Exception {
//        DolphinUtils.getBeanInfo(TestInvalidCollectionType.class);
//    }

//    @Test
//    public void testGetProperty_Interface() throws Exception {
//        TestCarModel carModel = manager.create(TestCarModel.class);
//        carModel.setYear(2);
//        Property<Integer> year = DolphinUtils.getProperty(carModel, "year");
//        assertNotNull(year);
//
//        assertThat(year.get(), is(2));
//
//    }
}