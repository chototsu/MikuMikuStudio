/*
 * Created on Jul 21, 2004
 *
 */
package com.jme.input.action;

/**
 * @author schustej
 *
 */
public class KeyToggleBoolean extends AbstractInputAction {
    boolean value = false;
    public KeyToggleBoolean( boolean value) {
        this.value = value;
        this.setAllowsRepeats( false);
    }
    public void performAction( float time) {
        value = !value;
    }
}
