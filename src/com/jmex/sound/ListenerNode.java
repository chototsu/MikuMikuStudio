/*Copyright*/
package com.jmex.sound;

import com.jme.renderer.Camera;
import com.jme.scene.Node;

/**
 * A listener node adjusts the listener location and orientation according to a camera or this nodes translation and
 * rotation. If a camera was set with {@link #setCamera} the camera location is used otherwise the node data.
 * To be operational this node must be attached to the scenegraph or {@link #updateGeometricState} must be called
 * manually.
 * @author Alberto Plebani
 * @author Irrisor
 */
public abstract class ListenerNode extends Node {

    /**
     * This method is used to set ear position. Because usually we want to
     * set ears in the exact place of the camera and with the same
     * orientation, the easiest way to obtain this is to use the setCamera
     * method.
     *
     * @param camera: the camera we are actually using.
     */
    public abstract void setCamera( Camera camera );

    protected ListenerNode( String name ) {
        super( name );
    }

    /**
     * @param name name of the node
     * @return a new ListenerNode
     */
    public static ListenerNode create( String name ) {
        return new OpenALListenerNode( name );
    }
}

/*
 * $log$
 */

