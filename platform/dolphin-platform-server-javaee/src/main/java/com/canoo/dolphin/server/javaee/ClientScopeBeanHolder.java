package com.canoo.dolphin.server.javaee;

import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.BeanManager;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hendrikebbers on 06.01.17.
 */
public class ClientScopeBeanHolder implements Serializable {

    private Map<String, ContextualStorage> storageMap = new ConcurrentHashMap<>();

    private final boolean useConcurrentStorage;
    private final boolean usePassivationCapableStorage;

    protected ClientScopeBeanHolder()
    {
        this(true, true);
    }

    protected ClientScopeBeanHolder(boolean useConcurrentStorage, boolean usePassivationCapableStorage)
    {
        this.useConcurrentStorage = useConcurrentStorage;
        this.usePassivationCapableStorage = usePassivationCapableStorage;
    }

    public ContextualStorage getContextualStorage(BeanManager beanManager, String key, boolean createIfNotExist)
    {
        ContextualStorage contextualStorage = storageMap.get(key);

        if (contextualStorage == null && createIfNotExist)
        {
            contextualStorage = createContextualStorage(beanManager, key);
        }

        return contextualStorage;
    }

    protected synchronized ContextualStorage createContextualStorage(BeanManager beanManager, String key)
    {
        ContextualStorage contextualStorage = storageMap.get(key);
        if (contextualStorage == null)
        {
            contextualStorage = new ContextualStorage(beanManager, useConcurrentStorage, usePassivationCapableStorage);
            storageMap.put(key, contextualStorage);
        }
        return contextualStorage;
    }

    public Map<String, ContextualStorage> getStorageMap()
    {
        return storageMap;
    }

    public Map<String, ContextualStorage> forceNewStorage()
    {
        Map<String, ContextualStorage> oldStorageMap = storageMap;
        storageMap = new ConcurrentHashMap<String, ContextualStorage>();
        return oldStorageMap;
    }

    @PreDestroy
    public void destroyBeans()
    {
        Map<String, ContextualStorage> oldWindowContextStorages = forceNewStorage();

        for (ContextualStorage contextualStorage : oldWindowContextStorages.values())
        {
            AbstractContext.destroyAllActive(contextualStorage);
        }
    }
}