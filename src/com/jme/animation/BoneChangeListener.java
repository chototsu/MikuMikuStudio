package com.jme.animation;

public interface BoneChangeListener {
    /**
     * Invoked when the target of the listener has changed bone status.
     *
     * @param e  a ChangeEvent object
     */
    void boneChanged(BoneChangeEvent e);

}
