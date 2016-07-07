package com.canoo.dolphin.server.binding;

import com.canoo.dolphin.util.Assert;

import java.util.UUID;

public class Qualifier<T> {

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
}
