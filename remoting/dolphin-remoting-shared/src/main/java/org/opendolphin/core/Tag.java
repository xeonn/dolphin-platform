package org.opendolphin.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Tags are used to differentiate various data and metadata associated with an Attribute.
 * Every attribute has a tag; by default, the VALUE tag is used.
 * <p>
 * By creating multiple attributes with the same property name but different tags, any amount of metadata can be kept
 * for the property.  This metadata can consist of any additional information that the view layer might find useful,
 * for example, the localized label to be displayed next to the property, the tooltip for the property, validation
 * criteria for the property, whether the UI control for the property should be visible or enabled, and so on.
 * <p>
 * Note that Dolphin itself does not take any special action based on the tag type, except to assume a tag type of VALUE
 * if none is otherwise specified.  The view code must supply the logic to enforce tag meanings.
 * Therefore, additional tags may be created as needed with no change to Dolphin itself.
 * The UI toolkit can bind against these other tag attributes just as it can against the "value" attribute.
 * <p>
 * Tags are essentially statically typed Strings, and you can make your own by using the "tagFor" factory or
 * by subclassing.
 * <p>
 * Several tags are predeclared; see documentation for suggested usage.
 *
 * @see Attribute
 */
public final class Tag {

    private static Map<String, Tag> cache = new HashMap<>();

    /**
     * The actual value of the attribute. This is the default if no tag is given.
     */
    public static final Tag VALUE = tagFor("VALUE");
    /**
     * the to-be-displayed String, not the key. Internationalization happens on the server.
     */
    public static final Tag LABEL = tagFor("LABEL");
    /**
     * Tooltip text for the attribute.
     */
    public static final Tag TOOLTIP = tagFor("TOOLTIP");
    /**
     * "true" or "false"; maps to Grails constraint display:true
     */
    public static final Tag VISIBLE = tagFor("VISIBLE");
    /**
     * "true" or "false"
     */
    public static final Tag ENABLED = tagFor("ENABLED");

    protected Tag(String name) {
        this.name = name;
    }

    public final String toString() {
        return name;
    }

    public final String getName() {
        return name;
    }

    private final String name;

    /**
     * Factory method with flyweight pattern
     */
    public static final Tag tagFor(final String name) {
        if (!cache.containsKey(name)) {
            cache.put(name, new Tag(name));
        }
        return cache.get(name);
    }
}
