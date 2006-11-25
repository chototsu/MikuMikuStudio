/*Copyright*/
package com.jme.input.action;

/**
 * An object implementing <code>InputActionInterface</code> can be subscribed at an {@link com.jme.input.InputHandler}
 * to get its {@link #performAction} method called on specific event triggers.
 * @author Irrisor
 * @version $Id: InputActionInterface.java,v 1.1 2006-11-25 11:45:19 irrisor Exp $
 */
public interface InputActionInterface {
    /**
     *
     * <code>performAction</code> executes the action. The InputActionEvent
     * is supplied to define what keys are pressed, what other actions were
     * called and the time of the event.
     *
     * @param evt the event that triggered the perform action method.
     */
    void performAction(InputActionEvent evt);
}

/*
 * $Log: not supported by cvs2svn $
 */
