package com.jme.input;

/**
 * Superclass for all parts of the input system.
 * Subclasses should provide a method called addListener with a specific listener type as parameter to subscribe
 * event listeners.
 */
public abstract class Input {
    /**
     * Poll data for this input system part (update the values) and send events to all listeners
     * (events will not be generated if no listeners were added via addListener).
     */
    public abstract void update();
}
