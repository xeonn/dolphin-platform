/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.impl.gc;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

/**
 * Created by hendrikebbers on 20.01.16.
 */
public class TestGarbageCollection {

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

        BeanWithProperties testBean = new BeanWithProperties(garbageCollection);
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

        BeanWithProperties testBean = new BeanWithProperties(garbageCollection);
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

        BeanWithProperties testBean = new BeanWithProperties(garbageCollection);
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

        BeanWithProperties testBean = new BeanWithProperties(garbageCollection);
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

        BeanWithLists testBean = new BeanWithLists(garbageCollection);
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

        BeanWithProperties parentBean = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();

        childBean = new BeanWithProperties(garbageCollection);
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

        BeanWithLists parentBean = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();

        childBean = new BeanWithProperties(garbageCollection);
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

        BeanWithProperties bean = new BeanWithProperties(garbageCollection);
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

        BeanWithLists bean = new BeanWithLists(garbageCollection);
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

        BeanWithProperties parentBean = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollection);
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

        BeanWithLists parentBean = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        BeanWithLists childBean = new BeanWithLists(garbageCollection);
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

        BeanWithLists parentBean = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollection);
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

        BeanWithProperties parentBean = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        BeanWithProperties wrapperBean1 = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(wrapperBean1, false);

        BeanWithProperties wrapperBean2 = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(wrapperBean2, false);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollection);
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

        BeanWithLists parentBean = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        BeanWithLists wrapperBean1 = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(wrapperBean1, false);

        BeanWithLists wrapperBean2 = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(wrapperBean2, false);

        BeanWithLists childBean = new BeanWithLists(garbageCollection);
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

        BeanWithProperties parentBean = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        BeanWithProperties lastWrapperBean = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(lastWrapperBean, false);
        parentBean.beanProperty().set(lastWrapperBean);

        for (int i = 0; i < 1000; i++) {
            BeanWithProperties wrapperBean = new BeanWithProperties(garbageCollection);
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

        BeanWithLists parentBean = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        BeanWithLists lastWrapperBean = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(lastWrapperBean, false);
        parentBean.getBeansList().add(lastWrapperBean);

        for (int i = 0; i < 1000; i++) {
            BeanWithLists wrapperBean = new BeanWithLists(garbageCollection);
            garbageCollection.onBeanCreated(wrapperBean, false);
            lastWrapperBean.getBeansList().add(wrapperBean);
            lastWrapperBean = wrapperBean;
        }
        lastWrapperBean.getBeansList().add(parentBean);
    }

    @Test
    public void testLargeList() {
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

        BeanWithLists parentBean = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        for (int i = 0; i < 1000; i++) {
            BeanWithLists wrapperBean = new BeanWithLists(garbageCollection);
            garbageCollection.onBeanCreated(wrapperBean, false);
            parentBean.getBeansList().add(wrapperBean);
        }

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        parentBean.getBeansList().clear();

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(1000));
        removedObjects.clear();
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

        BeanWithLists parentBean = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        BeanWithLists lastWrapperBean = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(lastWrapperBean, false);
        parentBean.getBeansList().add(lastWrapperBean);

        int beanCount = addSomeContent(lastWrapperBean, 7, 0, garbageCollection);

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

        BeanWithLists parentBean = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBean, true);

        BeanWithLists lastWrapperBean = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(lastWrapperBean, false);
        parentBean.getBeansList().add(lastWrapperBean);

        int beanCount = addSomeContent(lastWrapperBean, 7, 0, garbageCollection);

        System.out.println("Added " + beanCount + " beans");

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        parentBean.getBeansList().clear();

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(beanCount + 1));
        removedObjects.clear();
    }

    @Test
    public void testMovingBeans() {
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

        BeanWithLists parentBeanA = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBeanA, true);

        BeanWithLists parentBeanB = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBeanB, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        parentBeanA.getBeansList2().add(childBean);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        parentBeanB.getBeansList2().add(childBean);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        parentBeanA.getBeansList2().remove(childBean);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        parentBeanB.getBeansList2().remove(childBean);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();
    }

    @Test
    public void testSubBeans() {
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

        BeanWithLists parentBeanA = new BeanWithLists(garbageCollection);
        garbageCollection.onBeanCreated(parentBeanA, true);

        BeanWithProperties childBean = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBean, false);

        BeanWithProperties childBeanB = new BeanWithProperties(garbageCollection);
        garbageCollection.onBeanCreated(childBeanB, false);

        childBean.beanProperty().set(childBeanB);
        parentBeanA.getBeansList2().add(childBeanB);

        parentBeanA.getBeansList2().add(childBean);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(0));
        removedObjects.clear();

        parentBeanA.getBeansList2().remove(childBean);

        garbageCollection.gc();
        assertThat(removedObjects, Matchers.hasSize(1));
        assertTrue(removedObjects.get(0) == childBean);
        removedObjects.clear();
    }

    private int addSomeContent(BeanWithLists parent, int maxDeep, int currentDeep, GarbageCollection garbageCollection) {
        int addedCount = 0;
        if (currentDeep >= maxDeep) {
            return addedCount;
        }

        for (int i = 0; i < maxDeep - currentDeep; i++) {
            BeanWithLists listChild = new BeanWithLists(garbageCollection);
            garbageCollection.onBeanCreated(listChild, false);
            parent.getBeansList().add(listChild);
            addedCount++;
            addedCount = addedCount + addSomeContent(listChild, maxDeep, currentDeep + 1, garbageCollection);

            BeanWithProperties propertyChild = new BeanWithProperties(garbageCollection);
            garbageCollection.onBeanCreated(propertyChild, false);
            parent.getBeansList2().add(propertyChild);
            addedCount++;
            addedCount = addedCount + addSomeContent(propertyChild, maxDeep, currentDeep + 1, true, garbageCollection);

            BeanWithProperties propertyChild2 = new BeanWithProperties(garbageCollection);
            garbageCollection.onBeanCreated(propertyChild2, false);
            parent.getBeansList2().add(propertyChild2);
            addedCount++;
            addedCount = addedCount + addSomeContent(propertyChild2, maxDeep, currentDeep + 1, false, garbageCollection);

        }
        return addedCount;
    }

    private int addSomeContent(BeanWithProperties parent, int maxDeep, int currentDeep, boolean addListBean, GarbageCollection garbageCollection) {
        int addedCount = 0;
        if (currentDeep >= maxDeep) {
            return addedCount;
        }
        if (addListBean) {
            BeanWithLists child = new BeanWithLists(garbageCollection);
            garbageCollection.onBeanCreated(child, false);
            parent.listBeanProperty().set(child);
            addedCount++;
            addedCount = addedCount + addSomeContent(child, maxDeep, currentDeep + 1, garbageCollection);
        } else {
            BeanWithProperties child = new BeanWithProperties(garbageCollection);
            garbageCollection.onBeanCreated(child, false);
            parent.beanProperty().set(child);
            addedCount++;
            addedCount = addedCount + addSomeContent(child, maxDeep, currentDeep + 1, !addListBean, garbageCollection);
        }
        return addedCount;
    }

}
