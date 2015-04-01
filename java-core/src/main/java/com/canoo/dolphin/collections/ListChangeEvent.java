package com.canoo.dolphin.collections;

import java.util.List;

public abstract class ListChangeEvent<E> {

    private final ObservableList<E> source;

    ListChangeEvent(ObservableList<E> source) {
        this.source = source;
    }

    public ObservableList<E> getSource() {
        return source;
    }

    public abstract List<Change> getChanges();

    public class Change {

        private final int from;
        private final int to;
        private final List<E> removedElements;

        Change(int from, int to, List<E> removedElements) {
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

        public List<E> getRemovedElements() {
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

        public List<E> getAddedElements() {
            return getSource().subList(getFrom(), getTo());
        }
    }
}
