/*Copyright*/
package com.jme.input.util;

/**
 * @author
 */
abstract class SyntheticTriggerContainer {
    /**
     * @param trigger what to add to list of triggers
     */
    abstract void add( SyntheticTrigger trigger );

    /**
     * @param trigger what to remove from list of triggers
     */
    abstract void remove( SyntheticTrigger trigger );

    /**
     * @return the name of this button
     */
    public abstract String getName();

    /**
     * @return index of this button (used when registering with InputHandler)
     * @see #getDeviceName()
     */
    public abstract int getIndex();
}

/*
 * $log$
 */
