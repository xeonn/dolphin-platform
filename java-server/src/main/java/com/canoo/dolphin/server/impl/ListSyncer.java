package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.server.PresentationModelBuilder;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

import java.util.List;

public class ListSyncer {

    public enum ListChangeType {
        ADD_FROM_SERVER, DEL_FROM_SERVER, SET_FROM_SERVER, ADD_FROM_CLIENT, DEL_FROM_CLIENT, SET_FROM_CLIENT;

        public String getKey() {
            return ListSyncer.class.getName() + "." + name();
        }
    }
    public static final String ADD_FROM_SERVER = ListSyncer.class.getName() + "_ADD_FROM_SERVER";
    public static final String DEL_FROM_SERVER = ListSyncer.class.getName() + "_DEL_FROM_SERVER";
    public static final String SET_FROM_SERVER = ListSyncer.class.getName() + "_SET_FROM_SERVER";
    public static final String ADD_FROM_CLIENT = ListSyncer.class.getName() + "_ADD_FROM_CLIENT";
    public static final String DEL_FROM_CLIENT = ListSyncer.class.getName() + "_DEL_FROM_CLIENT";
    public static final String SET_FROM_CLIENT = ListSyncer.class.getName() + "_SET_FROM_CLIENT";

    private final ServerDolphin dolphin;
    private final BeanManagerAccess beanManagerAccess;

    public ListSyncer(ServerDolphin dolphin, final BeanManagerAccess beanManagerAccess) {
        this.dolphin = dolphin;
        this.beanManagerAccess = beanManagerAccess;
    }

    public void processEvent(Class<?> beanClass, String sourceId, String attributeName, ListChangeEvent<?> evt) {
        for (final ListChangeEvent.Change<?> change : evt.getChanges()) {

            final int to = change.getTo();
            int from = change.getFrom();
            int removedCount = change.getRemovedElements().size();

            if (change.isReplaced()) {
                final int n = Math.min(to - from, removedCount);
                final List<?> newElements = evt.getSource().subList(from, from + n);
                int pos = from;
                for (final Object element : newElements) {
                    sendReplace(beanClass, sourceId, attributeName, pos++, element);
                }
                from += n;
                removedCount -= n;
            }
            if (to > from) {
                final List<?> newElements = evt.getSource().subList(from, to);
                int pos = from;
                for (final Object element : newElements) {
                    sendAdd(beanClass, sourceId, attributeName, pos++, element);
                }
            } else if (removedCount > 0) {
                sendRemove(sourceId, attributeName, from, from + removedCount);
            }
        }
    }

    private void sendAdd(Class<?> beanClass, String sourceId, String attributeName, int pos, Object element) {
        final PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);
        final ServerPresentationModel presentationModel = builder.withType(ADD_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("pos", pos)
                .withAttribute("element")
                .create();
        final Attribute relationAttribute = presentationModel.findAttributeByPropertyName("element");
        beanManagerAccess.setValue(beanClass, attributeName, relationAttribute, element);
    }

    private void sendRemove(String sourceId, String attributeName, int from, int to) {
        final PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);
        builder.withType(DEL_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("from", from)
                .withAttribute("to", to)
                .create();
    }

    private void sendReplace(Class<?> beanClass, String sourceId, String attributeName, int pos, Object element) {
        final PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);
        final ServerPresentationModel presentationModel = builder.withType(SET_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("pos", pos)
                .withAttribute("element")
                .create();
        final Attribute relationAttribute = presentationModel.findAttributeByPropertyName("element");
        beanManagerAccess.setValue(beanClass, attributeName, relationAttribute, element);
    }

    public interface BeanManagerAccess {
        void setValue(Class<?> beanClass, String attributeName, Attribute relationAttribute, Object value);
    }
}
