/*
 * Created on Jul 21, 2004
 *
 */
package com.jme.input.action;

import com.jme.scene.state.*;
import com.jme.scene.*;

/**
 * @author schustej
 *
 */
public class KeyToggleRenderState extends AbstractInputAction {

    RenderState state = null;
    Node ownerNode = null;
    
    public KeyToggleRenderState( RenderState state, Node owner) {
        this.state = state;
        this.ownerNode = owner;
        this.setAllowsRepeats( false);
    }
    
    public void performAction(float time) {
        state.setEnabled( !state.isEnabled());
        ownerNode.updateRenderState();
    }
}
