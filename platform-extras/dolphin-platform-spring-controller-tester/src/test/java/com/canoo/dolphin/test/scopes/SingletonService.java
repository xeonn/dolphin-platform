package com.canoo.dolphin.test.scopes;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SingletonService {

    private final String id;

    public SingletonService() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
}
