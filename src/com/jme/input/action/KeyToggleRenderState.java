/*
 * Created on Jul 21, 2004
 *
 */
package com.jme.input.action;

import com.jme.scene.state.*;
import com.jme.scene.*;

/**
 * Toggles a renderstate enabled/disabled.
 * 
 * @author Joel Schuster
 *  
 */
public class KeyToggleRenderState extends KeyInputAction {

    //the state to manipulate
    private RenderState state = null;

    //the node that owns this state.
    private Node ownerNode = null;

    /**
     * instantiates a new KeyToggleRenderState object. The state to switch and
     * the owner of the state are supplied during creation.
     * 
     * @param state
     *            the state to switch.
     * @param owner
     *            the owner of the state.
     */
    public KeyToggleRenderState(RenderState state, Node owner) {
        this.state = state;
        this.ownerNode = owner;
        this.setAllowsRepeats(false);
    }

    /**
     * switch the state from on to off or off to on.
     * 
     * @param evt
     *            the event that executed the action.
     */
    public void performAction(InputActionEvent evt) {
        state.setEnabled(!state.isEnabled());
        ownerNode.updateRenderState();
    }
}