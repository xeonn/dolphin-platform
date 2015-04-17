package com.canoo.dolphin.server.event;

import java.util.UUID;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public class HandlerIdentifier {

    private String id;

    protected HandlerIdentifier() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HandlerIdentifier)) return false;

        HandlerIdentifier that = (HandlerIdentifier) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
