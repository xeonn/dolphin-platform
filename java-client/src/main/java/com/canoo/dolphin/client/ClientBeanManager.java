package com.canoo.dolphin.client;

import com.canoo.dolphin.client.impl.ClientEventDispatcher;
import com.canoo.dolphin.client.impl.ClientPresentationModelBuilder;
import com.canoo.dolphin.client.impl.ClientPresentationModelBuilderFactory;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.ClassRepository.FieldType;
import com.canoo.dolphin.impl.collections.ListMapper;
import javafx.application.Platform;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.comm.JsonCodec;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.canoo.dolphin.impl.ClassRepository.FieldType.DOLPHIN_BEAN;

public class ClientBeanManager extends BeanManagerImpl {

    private static final String POLL_ACTION = "ServerPushController:longPoll";

    private static final String RELEASE_ACTION = "ServerPushController:release";

    private final ClientDolphin dolphin;

    public static class Param {
        private final String name;
        private final Object value;

        public Param(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

    public static ClientBeanManager create(ClientConfiguration clientConfiguration) {
        final ClientDolphin dolphin = new ClientDolphin();
        dolphin.setClientModelStore(new ClientModelStore(dolphin));
        final HttpClientConnector clientConnector = new HttpClientConnector(dolphin, clientConfiguration.getServerEndpoint());
        clientConnector.setCodec(new JsonCodec());
        clientConnector.setUiThreadHandler(Platform::runLater);
        dolphin.setClientConnector(clientConnector);
        final EventDispatcher dispatcher = new ClientEventDispatcher(dolphin);
        final BeanRepository beanRepository = new BeanRepository(dolphin, dispatcher);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new BeanBuilder(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        if(clientConfiguration.isUsePush()) {
            dolphin.startPushListening(POLL_ACTION, RELEASE_ACTION);
        }
        return new ClientBeanManager(beanRepository, beanBuilder, dolphin);
    }

    public static ClientBeanManager create(String url) {
        return create(new ClientConfiguration(url));
    }


    private ClientBeanManager(BeanRepository beanRepository, BeanBuilder beanBuilder, ClientDolphin dolphin) {
        super(beanRepository, beanBuilder);
        this.dolphin = dolphin;
    }

    public CompletableFuture<Void> send(String command, Param... params) {
        if (params != null && params.length > 0) {
            final PresentationModelBuilder builder = new ClientPresentationModelBuilder(dolphin)
                    .withType(DolphinConstants.DOLPHIN_PARAMETER);
            for (final Param param : params) {
                final FieldType type = DolphinUtils.getFieldType(param.value);
                final Object value = type == DOLPHIN_BEAN? beanRepository.getDolphinId(param.value) : param.value;
                builder.withAttribute(param.name, value, Tag.VALUE)
                        .withAttribute(param.name, DolphinUtils.mapFieldTypeToDolphin(type), Tag.VALUE_TYPE);
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

            }
        });
        return result;
    }
}
