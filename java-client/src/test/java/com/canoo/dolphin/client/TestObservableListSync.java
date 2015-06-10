package com.canoo.dolphin.client;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.impl.ClientPresentationModelBuilderFactory;
import com.canoo.dolphin.client.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.client.util.ListReferenceModel;
import com.canoo.dolphin.client.util.SimpleTestModel;
import com.canoo.dolphin.impl.ClassRepository;
import com.canoo.dolphin.impl.DolphinConstants;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import mockit.Mocked;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestObservableListSync extends AbstractDolphinBasedTest {

    //////////////////////////////////////////////////////////////
    // Adding, removing, and replacing all element types as user
    //////////////////////////////////////////////////////////////
    @Test
    public void addingObjectElementAsUser_shouldAddElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        model.getObjectList().add(object);

        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "objectList")));
        assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class),  is((Object) objectModel.getId())));
    }

    @Test
    public void addingPrimitiveElementAsUser_shouldAddElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        model.getPrimitiveList().add(value);

        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "primitiveList")));
        assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class),  is((Object) value)));
    }

    @Test
    public void deletingObjectElementAsUser_shouldDeleteElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);

        model.getObjectList().add(object);
        model.getObjectList().remove(0);

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), hasSize(1));
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "objectList")));
        assertThat(change.getAt("from").getValue(),      allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAt("to").getValue(),        allOf(instanceOf(Integer.class), is((Object) 1)));
    }

    @Test
    public void deletingPrimitiveElementAsUser_shouldDeleteElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add("Hello");
        model.getPrimitiveList().remove(0);

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), hasSize(1));
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "primitiveList")));
        assertThat(change.getAt("from").getValue(),      allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAt("to").getValue(),        allOf(instanceOf(Integer.class), is((Object) 1)));
    }

    @Test
    public void replaceObjectElementAsUser_shouldReplaceElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final PresentationModel newObjectModel = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);

        model.getObjectList().add(oldObject);
        model.getObjectList().set(0, newObject);

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT);
        assertThat(changes, hasSize(1));

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class), is((Object) newObjectModel.getId())));
    }

    @Test
    public void replacePrimitiveElementAsUser_shouldReplaceElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye World";

        model.getPrimitiveList().add("Hello World");
        model.getPrimitiveList().set(0, newValue);

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT);
        assertThat(changes, hasSize(1));

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class), is((Object) newValue)));
    }



    //////////////////////////////////////////////////////////////
    // Adding elements at different positions as user
    //////////////////////////////////////////////////////////////
    @Test
    public void addingSingleElementInBeginningAsUser_shouldAddElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().add(0, newElement);

        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class), is((Object) newElement)));
    }

    @Test
    public void addingMultipleElementInBeginningAsUser_shouldAddElements(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[] {"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().addAll(0, Arrays.asList(newElement));

        final List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT);
        assertThat(changes, hasSize(3));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        for (int i = 0, n = changes.size(); i < n; i++) {
            final PresentationModel change = changes.get(i);
            assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
            assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "primitiveList")));
            assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) i)));
            assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class),  is((Object) newElement[i])));
        }
    }

    @Test
    public void addingSingleElementInMiddleAsUser_shouldAddElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().add(1, newElement);

        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class), is((Object) newElement)));
    }

    @Test
    public void addingMultipleElementInMiddleAsUser_shouldAddElements(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[] {"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().addAll(1, Arrays.asList(newElement));

        final List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT);
        assertThat(changes, hasSize(3));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        for (int i = 0, n = changes.size(); i < n; i++) {
            final PresentationModel change = changes.get(i);
            assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
            assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "primitiveList")));
            assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) (i + 1))));
            assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class),  is((Object) newElement[i])));
        }
    }

    @Test
    public void addingSingleElementAtEndAsUser_shouldAddElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().add(newElement);

        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class), is((Object) newElement)));
    }

    @Test
    public void addingMultipleElementAtEndAsUser_shouldAddElements(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[] {"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().addAll(Arrays.asList(newElement));

        final List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT);
        assertThat(changes, hasSize(3));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        for (int i = 0, n = changes.size(); i < n; i++) {
            final PresentationModel change = changes.get(i);
            assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
            assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "primitiveList")));
            assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) (i + 3))));
            assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class),  is((Object) newElement[i])));
        }
    }



    //////////////////////////////////////////////////////////////
    // Removing elements from different positions as user
    //////////////////////////////////////////////////////////////
    @Test
    public void deletingSingleElementInBeginningAsUser_shouldRemoveElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().remove(0);

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), empty());
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "primitiveList")));
        assertThat(change.getAt("from").getValue(),      allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAt("to").getValue(),        allOf(instanceOf(Integer.class), is((Object) 1)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test (enabled = false)
    public void deletingMultipleElementInBeginningAsUser_shouldRemoveElements(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().subList(0, 3).clear();

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), empty());
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        final PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "primitiveList")));
        assertThat(change.getAt("from").getValue(),      allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAt("to").getValue(),        allOf(instanceOf(Integer.class), is((Object) 3)));
    }

    @Test
    public void deletingSingleElementInMiddleAsUser_shouldDeleteElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().remove(1);

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), empty());
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "primitiveList")));
        assertThat(change.getAt("from").getValue(),      allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAt("to").getValue(),        allOf(instanceOf(Integer.class), is((Object) 2)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test (enabled = false)
    public void deletingMultipleElementInMiddleAsUser_shouldDeleteElements(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().subList(1, 4).clear();

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), empty());
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        final PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "primitiveList")));
        assertThat(change.getAt("from").getValue(),      allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAt("to").getValue(),        allOf(instanceOf(Integer.class), is((Object) 4)));
    }

    @Test
    public void deletingSingleElementAtEndAsUser_shouldDeleteElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().remove(2);

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), empty());
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "primitiveList")));
        assertThat(change.getAt("from").getValue(),      allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAt("to").getValue(),        allOf(instanceOf(Integer.class), is((Object) 3)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test (enabled = false)
    public void deletingMultipleElementAtEndAsUser_shouldAddElements(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().subList(3, 6).clear();

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), empty());
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT);
        assertThat(changes, hasSize(1));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT), empty());

        final PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class),  is((Object) "primitiveList")));
        assertThat(change.getAt("from").getValue(),      allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAt("to").getValue(),        allOf(instanceOf(Integer.class), is((Object) 6)));
    }



    //////////////////////////////////////////////////////////////
    // Replacing elements from different positions as user
    //////////////////////////////////////////////////////////////
    @Test
    public void replacingSingleElementAtBeginningAsUser_shouldReplaceElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().set(0, newValue);

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT);
        assertThat(changes, hasSize(1));

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class), is((Object) newValue)));
    }

    @Test
    public void replacingSingleElementInMiddleAsUser_shouldReplaceElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().set(1, newValue);

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT);
        assertThat(changes, hasSize(1));

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class), is((Object) newValue)));
    }

    @Test
    public void replacingSingleElementAtEndAsUser_shouldReplaceElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        dolphin.deleteAllPresentationModelsOfType(DolphinConstants.ADD_FROM_CLIENT);

        model.getPrimitiveList().set(2, newValue);

        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_CLIENT), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_CLIENT), empty());
        List<PresentationModel> changes = dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_CLIENT);
        assertThat(changes, hasSize(1));

        PresentationModel change = changes.get(0);
        assertThat(change.getAt("source").getValue(),    allOf(instanceOf(String.class),  is((Object) sourceModel.getId())));
        assertThat(change.getAt("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAt("pos").getValue(),       allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAt("element").getValue(),   allOf(instanceOf(String.class), is((Object) newValue)));
    }








    ///////////////////////////////////////////////////////////////////
    // Adding, removing, and replacing all element types from dolphin
    ///////////////////////////////////////////////////////////////////
    @Test
    public void addingObjectElementFromDolphin_shouldAddElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.findAllPresentationModelsByType(DolphinConstants.DOLPHIN_BEAN).get(0);
        classDescription.findAttributeByPropertyName("objectList").setValue(ClassRepository.FieldType.DOLPHIN_BEAN.ordinal());
        final SimpleTestModel object = manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        builderFactory.createBuilder()
                .withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("pos", 0)
                .withAttribute("element", objectModel.getId())
                .create();

        assertThat(model.getObjectList(), is(Collections.singletonList(object)));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER), empty());
    }

    @Test
    public void addingPrimitiveElementFromDolphin_shouldAddElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        builderFactory.createBuilder()
                .withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("pos", 0)
                .withAttribute("element", value)
                .create();

        assertThat(model.getPrimitiveList(), is(Collections.singletonList(value)));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER), empty());
    }

    @Test
    public void deletingObjectElementFromDolphin_shouldDeleteElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        builderFactory.createBuilder()
                .withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("pos", 0)
                .withAttribute("element", objectModel.getId())
                .create();
        assertThat(model.getObjectList(), hasSize(1));
        builderFactory.createBuilder()
                .withType(DolphinConstants.DEL_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .create();

        assertThat(model.getObjectList(), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_SERVER), empty());
    }

    @Test
    public void deletingPrimitiveElementFromDolphin_shouldDeleteElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        builderFactory.createBuilder()
                .withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("pos", 0)
                .withAttribute("element", value)
                .create();
        assertThat(model.getPrimitiveList(), hasSize(1));
        builderFactory.createBuilder()
                .withType(DolphinConstants.DEL_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .create();

        assertThat(model.getPrimitiveList(), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_SERVER), empty());
    }

    @Test
    public void replacingObjectElementFromDolphin_shouldReplaceElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = dolphin.findAllPresentationModelsByType(DolphinConstants.DOLPHIN_BEAN).get(0);
        classDescription.findAttributeByPropertyName("objectList").setValue(ClassRepository.FieldType.DOLPHIN_BEAN.ordinal());
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);
        final PresentationModel oldObjectModel = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final List<PresentationModel> models = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        final PresentationModel newObjectModel = oldObjectModel == models.get(1)? models.get(0) : models.get(1);

        builderFactory.createBuilder()
                .withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("pos", 0)
                .withAttribute("element", oldObjectModel.getId())
                .create();
        assertThat(model.getObjectList(), is(Collections.singletonList(oldObject)));
        builderFactory.createBuilder()
                .withType(DolphinConstants.SET_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("pos", 0)
                .withAttribute("element", newObjectModel.getId())
                .create();

        assertThat(model.getObjectList(), is(Collections.singletonList(newObject)));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_SERVER), empty());
    }

    @Test
    public void replacingPrimitiveElementFromDolphin_shouldReplaceElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String oldValue = "Hello";
        final String newValue = "Goodbye";

        builderFactory.createBuilder()
                .withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("pos", 0)
                .withAttribute("element", oldValue)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(oldValue)));
        builderFactory.createBuilder()
                .withType(DolphinConstants.SET_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("pos", 0)
                .withAttribute("element", newValue)
                .create();

        assertThat(model.getPrimitiveList(), is(Collections.singletonList(newValue)));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER), empty());
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_SERVER), empty());
    }



    //////////////////////////////////////////////////////////////
    // Adding elements at different positions from dolphin
    //////////////////////////////////////////////////////////////
    @Test
    public void addingSingleElementInBeginningFromDolphin_shouldAddElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("pos", 0)
                .withAttribute("element", newElement)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "1", "2", "3")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER), empty());
    }

    @Test
    public void addingSingleElementInMiddleFromDolphin_shouldAddElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("pos", 1)
                .withAttribute("element", newElement)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "42", "2", "3")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER), empty());
    }

    @Test
    public void addingSingleElementAtEndFromDolphin_shouldAddElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("pos", 3)
                .withAttribute("element", newElement)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", "42")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.ADD_FROM_SERVER), empty());
    }



    //////////////////////////////////////////////////////////////
    // Removing elements from different positions from dolphin
    //////////////////////////////////////////////////////////////
    @Test
    public void deletingSingleElementInBeginningFromDolphin_shouldRemoveElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.DEL_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("2", "3")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_SERVER), empty());
    }

    @Test
    public void deletingMultipleElementInBeginningFromDolphin_shouldRemoveElements(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.DEL_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 3)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("4", "5", "6")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_SERVER), empty());
    }

    @Test
    public void deletingSingleElementInMiddleFromDolphin_shouldDeleteElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.DEL_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 2)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "3")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_SERVER), empty());
    }

    @Test
    public void deletingMultipleElementInMiddleFromDolphin_shouldRemoveElements(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.DEL_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 4)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "5", "6")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_SERVER), empty());
    }

    @Test
    public void deletingSingleElementAtEndFromDolphin_shouldDeleteElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.DEL_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 3)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_SERVER), empty());
    }

    @Test
    public void deletingMultipleElementAtEndFromDolphin_shouldRemoveElements(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.DEL_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 4)
                .withAttribute("to", 6)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", "4")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.DEL_FROM_SERVER), empty());
    }



    //////////////////////////////////////////////////////////////
    // Replacing elements from different positions from dolphin
    //////////////////////////////////////////////////////////////
    @Test
    public void replacingSingleElementAtBeginningFromDolphin_shouldReplaceElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.SET_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("pos", 0)
                .withAttribute("element", newValue)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "2", "3")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_SERVER), empty());
    }

    @Test
    public void replacingSingleElementInMiddleFromDolphin_shouldReplaceElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.SET_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("pos", 1)
                .withAttribute("element", newValue)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "42", "3")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_SERVER), empty());
    }

    @Test
    public void replacingSingleElementAtEndFromDolphin_shouldReplaceElement(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));

        builderFactory.createBuilder()
                .withType(DolphinConstants.SET_FROM_SERVER)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("pos", 2)
                .withAttribute("element", newValue)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "42")));
        assertThat(dolphin.findAllPresentationModelsByType(DolphinConstants.SET_FROM_SERVER), empty());
    }

}
