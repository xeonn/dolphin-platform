package com.canoo.dolphin.client;

import org.opendolphin.StringUtil;

public class Param {

    private final String name;

    private final Object value;

    public Param(String name, Object value) {
        if (StringUtil.isBlank(name)) {
            throw new IllegalArgumentException("name must not be null");
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Param)) return false;

        Param param = (Param) o;

        if (!name.equals(param.name)) return false;
        if (value != null ? !value.equals(param.value) : param.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
