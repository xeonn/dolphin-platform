package com.canoo.dolphin.client;

import org.opendolphin.StringUtil;

/**
 * The class defines a param that can be used as a action param when calling a action on the server side
 * controller. Each param is defined by a name and a value. The name must be unique for a specific action.
 * See {@link ControllerProxy#invoke(String, Param...)} for more details.
 */
public class Param {

    private final String name;

    private final Object value;

    /**
     * Default constructor
     * @param name name of the param
     * @param value value of the param
     */
    public Param(String name, Object value) {
        if (StringUtil.isBlank(name)) {
            throw new IllegalArgumentException("name must not be null");
        }
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the param name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the param value
     * @return the value
     */
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
