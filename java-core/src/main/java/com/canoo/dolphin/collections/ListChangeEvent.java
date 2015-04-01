package com.canoo.dolphin.collections;

import java.util.Collections;
import java.util.List;

public class ListChangeEvent<E> {

    private final ObservableList<E> source;
    private final List<Change<E>> changes;

    ListChangeEvent(ObservableList<E> source, int from, int to, List<E> removedElements) {
        this(source, Collections.singletonList(new Change<E>(from, to, removedElements)));
    }

    ListChangeEvent(ObservableList<E> source, List<Change<E>> changes) {
        this.source = source;
        this.changes = changes;
    }

    public ObservableList<E> getSource() {
        return source;
    }

    public List<Change<E>> getChanges() {
        return changes;
    }

    public static class Change<S> {

        private final int from;
        private final int to;
        private final List<S> removedElements;

        Change(int from, int to, List<S> removedElements) {
            this.from = from;
            this.to = to;
            this.removedElements = removedElements;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

        public List<S> getRemovedElements() {
            return removedElements;
        }

        public boolean isAdded() {
            return getTo() > getFrom() && getRemovedElements().isEmpty();
        }

        public boolean isRemoved() {
            return getTo() <= getFrom() && !getRemovedElements().isEmpty();
        }

        public boolean isReplaced() {
            return getTo() > getFrom() && !getRemovedElements().isEmpty();
        }
    }
}
