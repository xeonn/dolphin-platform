package com.canoo.dolphin.test.qualifier;

import com.canoo.dolphin.server.binding.Qualifier;

/**
 * Created by hendrikebbers on 07.07.16.
 */
public interface QualifierTestConstants {

    Qualifier<String> STRING_QUALIFIER = Qualifier.create();

    Qualifier<Boolean> BOOLEAN_QUALIFIER = Qualifier.create();

    Qualifier<Integer> INTEGER_QUALIFIER = Qualifier.create();

    String QUALIFIER_CONTROLLER_NAME = "QualifierController";

    String DUMMY_ACTION = "dummyAction";

    String BIND_ACTION = "bindAction";

    String UNBIND_ACTION = "unbindAction";
}
