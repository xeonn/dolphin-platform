package com.canoo.dolphin.test.scopes;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Scope("request")
public class RequestService {

    private final String id;

    public RequestService() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
}
