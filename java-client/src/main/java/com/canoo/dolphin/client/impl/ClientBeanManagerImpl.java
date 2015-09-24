package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ClientBeanManager;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.ClassRepository.FieldType;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.canoo.dolphin.impl.ClassRepository.FieldType.DOLPHIN_BEAN;

public class ClientBeanManagerImpl extends BeanManagerImpl implements ClientBeanManager {

    private final ClientDolphin dolphin;

    public ClientBeanManagerImpl(BeanRepository beanRepository, BeanBuilder beanBuilder, ClientDolphin dolphin) {
        super(beanRepository, beanBuilder);
        this.dolphin = dolphin;
    }

    @Override
    public CompletableFuture<Void> invoke(String command, Param... params) {
        if (params != null && params.length > 0) {
            final PresentationModelBuilder builder = new ClientPresentationModelBuilder(dolphin)
                    .withType(DolphinConstants.DOLPHIN_PARAMETER);
            for (final Param param : params) {
                final FieldType type = DolphinUtils.getFieldType(param.getValue());
                final Object value = type == DOLPHIN_BEAN ? beanRepository.getDolphinId(param.getValue()) : param.getValue();
                builder.withAttribute(param.getName(), value, Tag.VALUE)
                        .withAttribute(param.getName(), DolphinUtils.mapFieldTypeToDolphin(type), Tag.VALUE_TYPE);
            }
            builder.create();
        }
        final CompletableFuture<Void> result = new CompletableFuture<>();
        dolphin.send(command, new OnFinishedHandler() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                result.complete(null);
            }

            @Override
            public void onFinishedData(List<Map> data) {
                //Unused....
            }
        });
        return result;
    }
}
