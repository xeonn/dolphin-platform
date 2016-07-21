package com.canoo.dolphin.test.scopes;

import com.canoo.dolphin.server.spring.ClientScoped;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ClientScoped
public class ClientService {

    private final String id;

    public ClientService() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
}
