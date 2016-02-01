package com.canoo.dolphin.server.event;

import java.util.UUID;

/**
 * This class defines a topic for the {@link DolphinEventBus}. By using the event bus data messages can be send to
 * a specific topic. A topic is defined by it's name that means that each topic needs a unique name.
 * @param <T> the type of data that can be send to this topic
 */
public class Topic<T> {

    private final String name;

    /**
     * Default constructur that uses a {@link UUID} based string for the name of this topic.
     */
    public Topic() {
        this(UUID.randomUUID().toString());
    }

    /**
     * Constructor that creates a topic based on its name. This name must be unique.
     * @param name the name
     */
    public Topic(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the topic
     * @return name of the topic
     */
    public String getName() {
        return name;
    }

    /**
     * Convenience methods that creates a new topic based on the given unique name
     * @param uniqueName the name
     * @param <T> the type of data that can be send to the created topic
     * @return the topic
     */
    public static <T> Topic<T> create(String uniqueName) {
        return new Topic<>(uniqueName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;

        Topic<?> topic = (Topic<?>) o;

        return name != null ? name.equals(topic.name) : topic.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
