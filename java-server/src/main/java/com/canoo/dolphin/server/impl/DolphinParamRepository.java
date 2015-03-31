package com.canoo.dolphin.server.impl;

import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

/**
 * Created by hendrikebbers on 31.03.15.
 */
public class DolphinParamRepository {

    private ServerDolphin dolphin;

    private static final String PM_TYPE = DolphinParamRepository.class.getName();

    private static final String NAME_ATTRIBUTE = "name";

    private static final String VALUE_ATTRIBUTE = "value";

    public DolphinParamRepository(ServerDolphin dolphin) {
        this.dolphin = dolphin;
    }

    public Object getParam(String name) {
        for(ServerPresentationModel model : dolphin.findAllPresentationModelsByType(PM_TYPE)) {
            if(name.equals(model.getAt(NAME_ATTRIBUTE).getValue())) {
                return model.getAt(VALUE_ATTRIBUTE).getValue();
            }
        }
        throw new RuntimeException("param not found");
    }
}
