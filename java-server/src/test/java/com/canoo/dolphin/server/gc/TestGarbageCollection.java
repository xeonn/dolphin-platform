package com.canoo.dolphin.server.gc;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

/**
 * Created by hendrikebbers on 20.01.16.
 */
public class TestGarbageCollection {

    private Random random = new Random();

    @Test
    public void testForRootBean() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithProperties testBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(testBean, true);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
    }

    @Test
    public void testForBean() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithProperties testBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(testBean, false);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(1));
        assertTrue(removedObjects.get(0) == testBean);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testErrorForMultipleBeanCreation() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithProperties testBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(testBean, false);
        garbageCollection.onBeanCreated(testBean, false);
    }

    @Test
    public void testIgnoreBasicProperties() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithProperties testBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(testBean, true);

        testBean.booleanProperty().set(false);
        testBean.booleanProperty().set(null);
        testBean.doubleProperty().set(0.0);
        testBean.doubleProperty().set(null);
        testBean.stringProperty().set("hello");
        testBean.stringProperty().set(null);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        testBean.booleanProperty().set(false);
        testBean.booleanProperty().set(true);
        testBean.doubleProperty().set(0.0);
        testBean.doubleProperty().set(24.9);
        testBean.stringProperty().set("hello");
        testBean.stringProperty().set("world");

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();
    }

    @Test
    public void testIgnoreBasicLists() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithLists testBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(testBean, true);

        testBean.getBooleanList().add(false);
        testBean.getDoubleList().add(0.4);
        testBean.getStringList().add("Hello");

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        testBean.getBooleanList().add(true);
        testBean.getDoubleList().add(3.4);
        testBean.getStringList().add("World");

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        testBean.getBooleanList().clear();
        testBean.getDoubleList().clear();
        testBean.getStringList().clear();

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();
    }

    @Test
    public void testForBeanProperty() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithProperties parentBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        TestBeanWithProperties childBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();

        childBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        parentBean.beanProperty().set(childBean);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        parentBean.beanProperty().set(null);
        parentBean.beanProperty().set(childBean);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        parentBean.beanProperty().set(null);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();
    }

    @Test
    public void testForBeanList() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithLists parentBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        TestBeanWithProperties childBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();

        childBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        parentBean.getBeansList2().add(childBean);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        parentBean.getBeansList2().clear();
        parentBean.getBeansList2().add(childBean);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        parentBean.getBeansList2().clear();

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testCircleBySameProperty() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithProperties bean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(bean, true);

        bean.beanProperty().set(bean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testCircleBySameList() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithLists bean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(bean, true);

        bean.getBeansList().add(bean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testSimpleCircleByProperty() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithProperties parentBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        TestBeanWithProperties childBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        parentBean.beanProperty().set(childBean);
        childBean.beanProperty().set(parentBean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testSimpleCircleByList() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithLists parentBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        TestBeanWithLists childBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        parentBean.getBeansList().add(childBean);
        childBean.getBeansList().add(parentBean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testMixedCircle() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithLists parentBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        TestBeanWithProperties childBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        parentBean.getBeansList2().add(childBean);
        childBean.listBeanProperty().set(parentBean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testDeepCircleByProperty() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithProperties parentBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        TestBeanWithProperties wrapperBean1 = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(wrapperBean1, false);

        TestBeanWithProperties wrapperBean2 = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(wrapperBean2, false);

        TestBeanWithProperties childBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        parentBean.beanProperty().set(wrapperBean1);
        wrapperBean1.beanProperty().set(wrapperBean2);
        wrapperBean2.beanProperty().set(childBean);
        childBean.beanProperty().set(wrapperBean1);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testDeepCircleByList() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithLists parentBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        TestBeanWithLists wrapperBean1 = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(wrapperBean1, false);

        TestBeanWithLists wrapperBean2 = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(wrapperBean2, false);

        TestBeanWithLists childBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        parentBean.getBeansList().add(wrapperBean1);
        wrapperBean1.getBeansList().add(wrapperBean2);
        wrapperBean2.getBeansList().add(childBean);
        childBean.getBeansList().add(wrapperBean1);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testVeryDeepCircleByProperty() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithProperties parentBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        TestBeanWithProperties lastWrapperBean = new TestBeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(lastWrapperBean, false);
        parentBean.beanProperty().set(lastWrapperBean);

        for (int i = 0; i < 1000; i++) {
            TestBeanWithProperties wrapperBean = new TestBeanWithProperties(garbageCollection);
            garbageCollection.onBeanCreated(wrapperBean, false);
            lastWrapperBean.beanProperty().set(wrapperBean);
            lastWrapperBean = wrapperBean;
        }
        lastWrapperBean.beanProperty().set(parentBean);
    }

    @Test(expectedExceptions = CircularDependencyException.class)
    public void testVeryDeepCircleByList() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithLists parentBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        TestBeanWithLists lastWrapperBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(lastWrapperBean, false);
        parentBean.getBeansList().add(lastWrapperBean);

        for (int i = 0; i < 1000; i++) {
            TestBeanWithLists wrapperBean = new TestBeanWithLists(garbageCollection);
            garbageCollection.onBeanCreated(wrapperBean, false);
            lastWrapperBean.getBeansList().add(wrapperBean);
            lastWrapperBean = wrapperBean;
        }
        lastWrapperBean.getBeansList().add(parentBean);
    }

    @Test
    public void testManyObjects() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithLists parentBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        TestBeanWithLists lastWrapperBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(lastWrapperBean, false);
        parentBean.getBeansList().add(lastWrapperBean);

        int beanCount = addSomeContent(lastWrapperBean, 18, 0, garbageCollection);

        System.out.println("Added " + beanCount + " beans");

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();
    }

    @Test
    public void testAddAndRemoveOfManyObjects() {
        final List<Object> removedObjects = new ArrayList<>();
        GarbageCollectionCallback gcConsumer = new GarbageCollectionCallback() {
            @Override
            public void onRemove(Set<Instance> instances) {
                for (Instance instance : instances) {
                    removedObjects.add(instance.getBean());
                }
            }
        };
        GarbageCollection garbageCollection = new GarbageCollection(gcConsumer);

        TestBeanWithLists parentBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        TestBeanWithLists lastWrapperBean = new TestBeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(lastWrapperBean, false);
        parentBean.getBeansList().add(lastWrapperBean);

        int beanCount = addSomeContent(lastWrapperBean, 18, 0, garbageCollection);

        System.out.println("Added " + beanCount + " beans");

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        parentBean.getBeansList().clear();

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(beanCount + 1));
        removedObjects.clear();
    }

    private int addSomeContent(TestBeanWithLists parent, int maxDeep, int currentDeep, GarbageCollection garbageCollection) {
        int addedCount = 0;
        if (currentDeep >= maxDeep) {
            return addedCount;
        }

        for (int i = 0; i < random.nextInt(maxDeep - currentDeep); i++) {
            if (random.nextBoolean()) {
                TestBeanWithLists child = new TestBeanWithLists(garbageCollection);
                garbageCollection.onBeanCreated(child, false);
                parent.getBeansList().add(child);
                addedCount++;
                addedCount = addedCount + addSomeContent(child, maxDeep, currentDeep + 1, garbageCollection);
            }
            if (random.nextBoolean()) {
                TestBeanWithProperties child = new TestBeanWithProperties(garbageCollection);
                garbageCollection.onBeanCreated(child, false);
                parent.getBeansList2().add(child);
                addedCount++;
                addedCount = addedCount + addSomeContent(child, maxDeep, currentDeep + 1, garbageCollection);
            }
        }
        return addedCount;
    }

    private int addSomeContent(TestBeanWithProperties parent, int maxDeep, int currentDeep, GarbageCollection garbageCollection) {
        int addedCount = 0;
        if (currentDeep >= maxDeep) {
            return addedCount;
        }
        Random random = new Random();
        if (random.nextBoolean()) {
            TestBeanWithLists child = new TestBeanWithLists(garbageCollection);
            garbageCollection.onBeanCreated(child, false);
            parent.listBeanProperty().set(child);
            addedCount++;
            addedCount = addedCount + addSomeContent(child, maxDeep, currentDeep + 1, garbageCollection);
        }
        if (random.nextBoolean()) {
            TestBeanWithProperties child = new TestBeanWithProperties(garbageCollection);
            garbageCollection.onBeanCreated(child, false);
            parent.beanProperty().set(child);
            addedCount++;
            addedCount = addedCount + addSomeContent(child, maxDeep, currentDeep + 1, garbageCollection);
        }
        return addedCount;
    }

}
