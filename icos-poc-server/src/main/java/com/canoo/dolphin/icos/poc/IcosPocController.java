package com.canoo.dolphin.icos.poc;

import com.canoo.dolphin.icos.poc.model.Option;
import com.canoo.dolphin.icos.poc.model.Question;
import com.canoo.dolphin.icos.poc.model.Questionnaire;
import com.canoo.dolphin.icos.poc.model.Section;
import com.canoo.dolphin.icos.poc.platform.AbstractDolphinCommand;
import com.canoo.dolphin.icos.poc.platform.DolphinCommand;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.impl.BeanManagerImpl;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.impl.collections.ListMapper;
import org.opendolphin.core.server.ServerDolphin;

@DolphinCommand("COMMAND_INIT")
public class IcosPocController extends AbstractDolphinCommand {

    public void action() {
        final ServerDolphin serverDolphin = getDolphin();
        final ClassRepository classRepository = new ClassRepository(serverDolphin);
        final BeanRepository beanRepository = new BeanRepository(serverDolphin, classRepository);
        beanRepository.setListMapper(new ListMapper(serverDolphin, classRepository, beanRepository));
        final BeanManager manager = new BeanManagerImpl(beanRepository);

        final Questionnaire questionnaire = manager.create(Questionnaire.class);

        final Section section = manager.create(Section.class);
        section.setLabel("Base Data");
        questionnaire.getSections().add(section);

        final Question legalEntity = manager.create(Question.class);
        legalEntity.setLabel("Legal Entity");
        legalEntity.setType(Question.Type.RADIO_GROUP);
        legalEntity.setVisible(true);
        section.getQuestions().add(legalEntity);

        final Option legalEntity1 = manager.create(Option.class);
        legalEntity1.setKey("yes");
        legalEntity1.setLabel("Yes");
        legalEntity.getOptions().add(legalEntity1);

        final Option legalEntity2 = manager.create(Option.class);
        legalEntity2.setKey("no");
        legalEntity2.setLabel("No");
        legalEntity.getOptions().add(legalEntity2);

        final Question typeOfRelation = manager.create(Question.class);
        typeOfRelation.setLabel("Type of Relation");
        typeOfRelation.setType(Question.Type.RADIO_GROUP);
        typeOfRelation.setVisible(true);
        section.getQuestions().add(typeOfRelation);

        final Option typeOfRelation1 = manager.create(Option.class);
        typeOfRelation1.setKey("employee");
        typeOfRelation1.setLabel("Employee");
        typeOfRelation.getOptions().add(typeOfRelation1);

        final Option typeOfRelation2 = manager.create(Option.class);
        typeOfRelation2.setKey("jointAccount");
        typeOfRelation2.setLabel("Joint Account");
        typeOfRelation.getOptions().add(typeOfRelation2);

        final Option typeOfRelation3 = manager.create(Option.class);
        typeOfRelation3.setKey("singleAccount");
        typeOfRelation3.setLabel("Single Account");
        typeOfRelation.getOptions().add(typeOfRelation3);



        // A simple business rule :)
        legalEntity.getValueProperty().subscribeToValueChanges(new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> event) {
                typeOfRelation.setVisible(!"yes".equals(event.getNewValue()));
            }
        });
    }
}
