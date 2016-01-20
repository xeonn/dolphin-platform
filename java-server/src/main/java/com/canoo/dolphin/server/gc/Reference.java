package com.canoo.dolphin.server.gc;

public abstract class Reference {

    private Instance parent;

    private Instance child;

    public Reference(Instance parent, Instance child) {
        this.parent = parent;
        this.child = child;
    }

    public Instance getParent() {
        return parent;
    }

    public boolean hasCircularDependency() {
        return recursiveCircularDependencyCheck(parent);
    }

    private boolean recursiveCircularDependencyCheck(Instance currentInstance) {
        if(currentInstance == this.child) {
            return true;
        }
        for(Reference reference : currentInstance.getReferences()) {
            if(recursiveCircularDependencyCheck(reference.getParent())) {
                return true;
            }
        }
        return false;
    }
}
