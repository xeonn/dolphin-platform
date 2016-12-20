package org.opendolphin.core;

public class TestBean {

    private String name;

    public TestBean() {
    }

    public TestBean(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
