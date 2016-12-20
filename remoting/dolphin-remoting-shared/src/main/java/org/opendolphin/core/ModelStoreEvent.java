package org.opendolphin.core;

public class ModelStoreEvent<A extends Attribute, P extends PresentationModel<A>> {
    public ModelStoreEvent(Type eventType, P presentationModel) {
        this.type = eventType;
        this.presentationModel = presentationModel;
    }

    public Type getType() {
        return type;
    }

    public P getPresentationModel() {
        return presentationModel;
    }

    public String toString() {
        return new StringBuilder().append("PresentationModel ").append(type.equals(Type.ADDED) ? "ADDED" : "REMOVED").append(" ").append(presentationModel.getId()).toString();
    }

    private final Type type;
    private final P presentationModel;

    public enum Type {
        ADDED, REMOVED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelStoreEvent<?, ?> that = (ModelStoreEvent<?, ?>) o;

        if (type != that.type) return false;
        return presentationModel != null ? presentationModel.equals(that.presentationModel) : that.presentationModel == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (presentationModel != null ? presentationModel.hashCode() : 0);
        return result;
    }
}
