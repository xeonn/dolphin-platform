package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerActionException;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.PresentationModelBuilder;
import org.opendolphin.StringUtil;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType.DOLPHIN_BEAN;

public class ControllerProxyImpl<T> implements ControllerProxy<T> {

    private final String controllerId;

    private final ClientContext context;

    private final BeanRepository beanRepository;

    private final ClientDolphin dolphin;

    private T model;

    private boolean destroyed = false;

    public ControllerProxyImpl(String controllerId, T model, ClientContext context, ClientDolphin dolphin, BeanRepository beanRepository) {
        if (StringUtil.isBlank(controllerId)) {
            throw new NullPointerException("controllerId must not be null");
        }
        if (dolphin == null) {
            throw new NullPointerException("dolphin must not be null");
        }
        if (context == null) {
            throw new NullPointerException("context must not be null");
        }
        if (beanRepository == null) {
            throw new NullPointerException("beanRepository must not be null");
        }
        this.beanRepository = beanRepository;
        this.dolphin = dolphin;
        this.controllerId = controllerId;
        this.model = model;
        this.context = context;
    }

    @Override
    public T getModel() {
        return model;
    }

    @Override
    public CompletableFuture<Void> invoke(String actionName, Param... params) {
        if (destroyed) {
            throw new IllegalStateException("The controller was already destroyed");
        }

        final String actionId = UUID.randomUUID().toString();

        if (params != null && params.length > 0) {
            for (final Param param : params) {
                ControllerActionCallParamBean paramBean = context.getBeanManager().create(ControllerActionCallParamBean.class);
                final ClassRepositoryImpl.FieldType type = DolphinUtils.getFieldType(param.getValue());
                final Object value = type == DOLPHIN_BEAN ? beanRepository.getDolphinId(param.getValue()) : param.getValue();
                paramBean.setValue(value);
                paramBean.setValueType(DolphinUtils.mapFieldTypeToDolphin(type));
                paramBean.setActionId(actionId);
            }
        }

        ControllerActionCallBean bean = context.getBeanManager().findAll(ControllerActionCallBean.class).get(0);
        bean.setControllerid(controllerId);
        bean.setActionName(actionName);
        bean.setId(actionId);


        final CompletableFuture<Void> result = new CompletableFuture<>();
        dolphin.send(PlatformConstants.CALL_CONTROLLER_ACTION_COMMAND_NAME, new OnFinishedHandler() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                List<ControllerActionCallErrorBean> errors = context.getBeanManager().findAll(ControllerActionCallErrorBean.class);
                if(errors.isEmpty()) {
                    result.complete(null);
                } else {
                    result.obtrudeException(new ControllerActionException());
                }
                context.getBeanManager().removeAll(ControllerActionCallErrorBean.class);
            }

            @Override
            public void onFinishedData(List<Map> data) {
                //Unused....
            }
        });
        return result;
    }

    @Override
    public CompletableFuture<Void> destroy() {
        if (destroyed) {
            throw new IllegalStateException("The controller was already destroyed");
        }
        destroyed = true;
        ControllerDestroyBean bean = context.getBeanManager().findAll(ControllerDestroyBean.class).get(0);
        bean.setControllerid(controllerId);

        final CompletableFuture<Void> ret = new CompletableFuture<>();

        dolphin.send(PlatformConstants.DESTROY_CONTROLLER_COMMAND_NAME, new OnFinishedHandler() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                model = null;
                ret.complete(null);
            }

            @Override
            public void onFinishedData(List<Map> data) {
                //Unused....
            }
        });
        return ret;
    }
}
