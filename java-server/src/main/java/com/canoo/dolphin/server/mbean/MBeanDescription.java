package com.canoo.dolphin.server.mbean;

public class MBeanDescription {

    private String domainName;

    private String name;

    private String type;

    public MBeanDescription(String domainName, String name, String type) {
        this.domainName = domainName;
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMBeanName(String uniqueIdentifier) {
        return domainName + ":00=" + type + ",name=" + name + "-" + uniqueIdentifier;
    }

}
