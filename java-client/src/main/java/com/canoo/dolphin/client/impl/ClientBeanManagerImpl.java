package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ClientBeanManager;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.PresentationModelBuilder;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType.DOLPHIN_BEAN;

public class ClientBeanManagerImpl extends BeanManagerImpl implements ClientBeanManager {

    private final ClientDolphin dolphin;

    public ClientBeanManagerImpl(BeanRepository beanRepository, BeanBuilder beanBuilder, ClientDolphin dolphin) {
        super(beanRepository, beanBuilder);
        this.dolphin = dolphin;
    }

}
