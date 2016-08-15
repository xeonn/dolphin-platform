package com.canoo.dolphin.test.scopes;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Scope("session")
public class SessionService {

    private final String id;

    public SessionService() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
}
