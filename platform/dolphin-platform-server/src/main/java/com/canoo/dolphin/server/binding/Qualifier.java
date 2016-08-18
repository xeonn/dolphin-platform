package com.canoo.dolphin.server.binding;

import com.canoo.dolphin.util.Assert;

import java.util.UUID;

/**
 * A qualifier to define a server site binding of properties (see {@link com.canoo.dolphin.mapping.Property}).
 *
 * @param <T> generic type of the property that can be bound by using the qualifier
 * @see PropertyBinder
 */
public final class Qualifier<T> {

    private final String identifier;

    public Qualifier(final String identifier) {
        this.identifier = Assert.requireNonNull(identifier, "identifier");
    }

    public String getIdentifier() {
        return identifier;
    }

    public static <T> Qualifier<T> create() {
        return new Qualifier<>(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Qualifier<?> qualifier = (Qualifier<?>) o;

        return identifier != null ? identifier.equals(qualifier.identifier) : qualifier.identifier == null;

    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Qualifier: " + identifier;
    }
}
