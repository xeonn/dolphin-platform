package com.canoo.dolphin.mapping;

import java.util.UUID;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public class ReferenceIdentifier {

    private String id;

    protected ReferenceIdentifier() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReferenceIdentifier)) return false;

        ReferenceIdentifier that = (ReferenceIdentifier) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
