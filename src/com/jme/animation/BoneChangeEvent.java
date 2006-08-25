package com.jme.animation;

public class BoneChangeEvent {
    
    protected transient Object source;

    public BoneChangeEvent(Object source) {
        if (source == null)
            throw new IllegalArgumentException("null source");

        this.source = source;
    }
    
    public Object getSource() {
        return source;
    }

    public String toString() {
        return getClass().getName() + "[source=" + source + "]";
    }
}
