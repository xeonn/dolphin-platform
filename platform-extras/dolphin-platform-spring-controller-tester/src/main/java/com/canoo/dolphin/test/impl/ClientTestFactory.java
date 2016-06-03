package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.client.ClientConfiguration;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.client.impl.ClientBeanBuilderImpl;
import com.canoo.dolphin.client.impl.ClientBeanManagerImpl;
import com.canoo.dolphin.client.impl.ClientContextImpl;
import com.canoo.dolphin.client.impl.ClientEventDispatcher;
import com.canoo.dolphin.client.impl.ClientPlatformBeanRepository;
import com.canoo.dolphin.client.impl.ClientPresentationModelBuilderFactory;
import com.canoo.dolphin.client.impl.ControllerProxyFactory;
import com.canoo.dolphin.client.impl.ControllerProxyFactoryImpl;
import com.canoo.dolphin.client.impl.DolphinCommandHandler;
import com.canoo.dolphin.client.impl.ForwardableCallback;
import com.canoo.dolphin.impl.BeanRepositoryImpl;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.collections.ListMapperImpl;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.dolphin.test.ControllerTestException;
import com.canoo.dolphin.test.ControllerUnderTest;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.comm.UiThreadHandler;

import java.util.concurrent.ExecutionException;

public class ClientTestFactory {

    public static ClientContext createClientContext(DolphinTestContext dolphinContext) throws ExecutionException, InterruptedException {
        Assert.requireNonNull(dolphinContext, "dolphinContext");
        final ClientDolphin clientDolphin = dolphinContext.getClientDolphin();
        final ClientConfiguration clientConfiguration = new ClientConfiguration("PIPE", new UiThreadHandler() {
            @Override
            public void executeInsideUiThread(Runnable runnable) {
                runnable.run();
            }
        });
        final DolphinCommandHandler dolphinCommandHandler = new DolphinCommandHandler(clientDolphin);
        final EventDispatcher dispatcher = new ClientEventDispatcher(clientDolphin);
        final BeanRepository beanRepository = new BeanRepositoryImpl(clientDolphin, dispatcher);
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(clientDolphin);
        final ClassRepository classRepository = new ClassRepositoryImpl(clientDolphin, converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(clientDolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new ClientBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        final ClientPlatformBeanRepository platformBeanRepository = new ClientPlatformBeanRepository(clientDolphin, beanRepository, dispatcher, converters);
        final ClientBeanManagerImpl clientBeanManager = new ClientBeanManagerImpl(beanRepository, beanBuilder, clientDolphin);
        final ControllerProxyFactory controllerProxyFactory = new ControllerProxyFactoryImpl(platformBeanRepository, dolphinCommandHandler, clientDolphin);
        final ClientContext clientContext = new ClientContextImpl(clientConfiguration, clientDolphin, controllerProxyFactory, dolphinCommandHandler, platformBeanRepository, clientBeanManager, new ForwardableCallback(), new HttpClientMock());

        //Hack because sometimes the push listening starts too early....
        Thread.sleep(2000);

        clientDolphin.startPushListening(PlatformConstants.POLL_EVENT_BUS_COMMAND_NAME, PlatformConstants.RELEASE_EVENT_BUS_COMMAND_NAME);
        return clientContext;
    }

    public static <T> ControllerUnderTest<T> createController(ClientContext clientContext, String controllerName) {
        Assert.requireNonNull(clientContext, "clientContext");
        Assert.requireNonBlank(controllerName, "controllerName");
        try {
            final ControllerProxy<T> proxy = (ControllerProxy<T>) clientContext.createController(controllerName).get();
            return new ControllerUnderTest<T>() {
                @Override
                public T getModel() {
                    return proxy.getModel();
                }

                @Override
                public void invoke(String actionName, Param... params) {
                    try {
                        proxy.invoke(actionName, params).get();
                    } catch (Exception e) {
                        throw new ControllerTestException("Error in action invocation", e);
                    }
                }

                @Override
                public void destroy() {
                    try {
                        proxy.destroy().get();
                    } catch (Exception e) {
                        throw new ControllerTestException("Error in destroy", e);
                    }
                }
            };
        } catch (Exception e) {
            throw new ControllerTestException("Can't create controller proxy", e);
        }
    }
}
