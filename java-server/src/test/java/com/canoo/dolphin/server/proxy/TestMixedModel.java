package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.server.util.SimpleTestModel;

import java.util.List;

public interface TestMixedModel {

    SimpleTestModel getSimpleModel();
    void setSimpleModel(SimpleTestModel model);

    List<SimpleTestModel> getTestModels();
}
