package com.canoo.dolphin.test.qualifier;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.binding.Binding;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.binding.PropertyBinder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.canoo.dolphin.test.qualifier.QualifierTestConstants.BIND_ACTION;
import static com.canoo.dolphin.test.qualifier.QualifierTestConstants.BOOLEAN_QUALIFIER;
import static com.canoo.dolphin.test.qualifier.QualifierTestConstants.DUMMY_ACTION;
import static com.canoo.dolphin.test.qualifier.QualifierTestConstants.INTEGER_QUALIFIER;
import static com.canoo.dolphin.test.qualifier.QualifierTestConstants.QUALIFIER_CONTROLLER_NAME;
import static com.canoo.dolphin.test.qualifier.QualifierTestConstants.STRING_QUALIFIER;
import static com.canoo.dolphin.test.qualifier.QualifierTestConstants.UNBIND_ACTION;

@DolphinController(QUALIFIER_CONTROLLER_NAME)
public class QualifierTestController {

    @DolphinModel
    private QualifierTestModel model;

    @Inject
    private BeanManager beanManager;

    @Inject
    private PropertyBinder binder;

    private List<Binding> bindings = new ArrayList<>();

    @PostConstruct
    public void init() {
        QualifierTestSubModelOne model1 = beanManager.create(QualifierTestSubModelOne.class);
        model.subModelOneProperty().set(model1);

        QualifierTestSubModelTwo model2 = beanManager.create(QualifierTestSubModelTwo.class);
        model.subModelTwoProperty().set(model2);

        bind();
    }

    private void bind() {
        bindings.add(binder.bind(model.subModelOneProperty().get().booleanProperty(), BOOLEAN_QUALIFIER));
        bindings.add(binder.bind(model.subModelOneProperty().get().stringProperty(), STRING_QUALIFIER));
        bindings.add(binder.bind(model.subModelOneProperty().get().integerProperty(), INTEGER_QUALIFIER));

        bindings.add(binder.bind(model.subModelTwoProperty().get().booleanProperty(), BOOLEAN_QUALIFIER));
        bindings.add(binder.bind(model.subModelTwoProperty().get().stringProperty(), STRING_QUALIFIER));
        bindings.add(binder.bind(model.subModelTwoProperty().get().integerProperty(), INTEGER_QUALIFIER));
    }

    private void unbind() {
        for(Binding binding : bindings) {
            binding.unbind();
        }
        bindings.clear();
    }

    @DolphinAction(DUMMY_ACTION)
    public void dummyAction() {

    }

    @DolphinAction(BIND_ACTION)
    public void bindAction() {
        bind();
    }

    @DolphinAction(UNBIND_ACTION)
    public void unbindAction() {
        unbind();
    }
}
