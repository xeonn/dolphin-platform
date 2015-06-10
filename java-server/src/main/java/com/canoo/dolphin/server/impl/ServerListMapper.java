package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.BeanRepository;
import com.canoo.dolphin.impl.ClassRepository;
import com.canoo.dolphin.impl.DolphinConstants;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.collections.ListMapper;
import org.opendolphin.core.Dolphin;

public class ServerListMapper extends ListMapper {

    public ServerListMapper(Dolphin dolphin, ClassRepository classRepository, BeanRepository beanRepository, PresentationModelBuilderFactory builderFactory) {
        super(dolphin, classRepository, beanRepository, builderFactory);
    }

    @Override
    protected String getAddEntryKey() {
        return DolphinConstants.ADD_FROM_CLIENT;
    }

    @Override
    protected String getDelEntryKey() {
        return DolphinConstants.DEL_FROM_CLIENT;
    }

    @Override
    protected String getSetEntryKey() {
        return DolphinConstants.SET_FROM_CLIENT;
    }

    @Override
    protected void sendAdd(String sourceId, String attributeName, int pos, Object element) {
        builderFactory.createBuilder()
                .withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("pos", pos)
                .withAttribute("element", element)
                .create();
    }

    @Override
    protected void sendRemove(String sourceId, String attributeName, int from, int to) {
        builderFactory.createBuilder()
                .withType(DolphinConstants.DEL_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("from", from)
                .withAttribute("to", to)
                .create();
    }

    @Override
    protected void sendReplace(String sourceId, String attributeName, int pos, Object element) {
        builderFactory.createBuilder()
                .withType(DolphinConstants.SET_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("pos", pos)
                .withAttribute("element", element)
                .create();
    }
}
