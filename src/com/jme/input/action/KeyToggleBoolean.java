/*
 * Created on Jul 21, 2004
 *
 */
package com.jme.input.action;

/**
 * KeyToggleBoolean switches a boolean value from true to false based on a
 * trigger.
 * 
 * @author Joel Schuster
 * @version $Id: KeyToggleBoolean.java,v 1.2 2004-10-14 01:23:00 mojomonkey Exp $
 */
public class KeyToggleBoolean extends KeyInputAction {

    //the value to switch
    private boolean value = false;

    /**
     * Instantiates a new KeyToggleBoolean object. The initial value is
     * supplied.
     * 
     * @param value
     *            the initial value to use for the toggle.
     */
    public KeyToggleBoolean(boolean value) {
        this.value = value;
        this.setAllowsRepeats(false);
    }

    /**
     * switches the value from true to false, or false to true.
     * 
     * @param evt
     *            the event that called this action.
     */
    public void performAction(InputActionEvent evt) {
        value = !value;
    }

    /**
     * returns the value.
     * 
     * @return Returns the value.
     */
    public boolean isValue() {
        return value;
    }

    /**
     * sets the value.
     * 
     * @param value
     *            The value to set.
     */
    public void setValue(boolean value) {
        this.value = value;
    }
}