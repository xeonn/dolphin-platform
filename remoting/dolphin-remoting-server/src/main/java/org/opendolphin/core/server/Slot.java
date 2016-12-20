package org.opendolphin.core.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Slot corresponds to an attribute in the presentation model.
 * A slot consists of a propertyName (String), a value, a baseValue, a qualifier, and a tag.
 * A DTO (data transfer object) consists of a list of slots; the DTO is the equivalent of a presentation model.
 *
 * @see DTO
 */
public final class Slot {

    private String propertyName;

    private Object value;

    private Object baseValue;

    private String qualifier;

    public Slot(String propertyName, Object value) {
        this(propertyName, value, null);
    }


    /**
     * Convenience method with positional parameters to create an attribute specification from name/value pairs.
     * Especially useful when creating DTO objects.
     */
    public Slot(String propertyName, Object value, String qualifier) {
        this.propertyName = propertyName;
        this.value = value;
        this.baseValue = value;
        this.qualifier = qualifier;
    }

    /**
     * Converts a data map like <tt>[a:1, b:2]</tt> into a list of attribute-Maps.
     * Especially useful when a service returns data that an action puts into presentation models.
     */
    public static List<Slot> slots(Map<String, Object> data) {
        List<Slot> list = new ArrayList<Slot>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            ((ArrayList<Slot>) list).add(new Slot(entry.getKey(), entry.getValue()));
        }

        return list;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("propertyName", propertyName);
        map.put("value", value);
        map.put("baseValue", baseValue);
        map.put("qualifier", qualifier);
        return map;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getValue() {
        return value;
    }

    public Object getBaseValue() {
        return baseValue;
    }

    public String getQualifier() {
        return qualifier;
    }

}
