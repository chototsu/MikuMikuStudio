package com.jme.util;

/**
 * Interface for items wanting to do an action guarenteed to be done inside the
 * render thread.
 * 
 * @author Joshua Slack
 */
public interface RenderThreadExecutable {

    /**
     * Perform the actual work.
     */
    public void doAction();

}
