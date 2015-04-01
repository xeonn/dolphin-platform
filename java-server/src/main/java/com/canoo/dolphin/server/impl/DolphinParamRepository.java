package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.mapping.util.ParamConstants;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

/**
 * Created by hendrikebbers on 31.03.15.
 */
public class DolphinParamRepository {

    private ServerDolphin dolphin;

    public DolphinParamRepository(ServerDolphin dolphin) {
        this.dolphin = dolphin;
    }

    public Object getParam(String name) {
        for(ServerPresentationModel model : dolphin.findAllPresentationModelsByType(ParamConstants.PM_TYPE)) {
            if(name.equals(model.getAt(ParamConstants.NAME_ATTRIBUTE).getValue())) {
                return model.getAt(ParamConstants.VALUE_ATTRIBUTE).getValue();
            }
        }
        throw new RuntimeException("param not found");
    }
}
