/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer. 
 * 
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * 
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the 
 * names of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package jme.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import jme.exception.MonkeyGLException;
import jme.exception.MonkeyRuntimeException;
import jme.geometry.Geometry;
import jme.math.Vector;
import jme.physics.PhysicsModule; 
import jme.system.DisplaySystem;
import jme.entity.camera.Frustum;
import jme.utility.LoggingSystem;

import org.lwjgl.opengl.GL;

/**
 * <code>Entity</code> defines a game entity that consists of a piece of
 * geometry, a position and an orientation. An entity can be a collection of
 * children entities. An entity with multiple children create a tree structure,
 * as children can have children. Rendering the entity causes the parent
 * entity to render it's own geometry and then render each child.
 * 
 * If the parent of an entity is null, the parent is considered to be the 
 * <code>Locale</code>. The entities local coordinate system is relative to the
 * parent. That is, if a position of a entity is (0, 1, 0), it is one unit 
 * along the Y-Axis of the parent. 
 * 
 * An <code>Entity</code> is not required to maintain geometry. If there is
 * no geometry, it will simply not be rendered. This will allow for an
 * <code>Entity</code> to represent something abstract.
 * 
 * @author Mark Powell
 * @version 0.1.0
 */
public class Entity implements EntityInterface {
    /**
     * Defines using simple point tests for visibility calculations.
     */
    public static final int VISIBILITY_POINT = 0;
    /**
     * Defines using sphere tests for visibility calculations.
     */
    public static final int VISIBILITY_SPHERE = 1;
    /**
     * Defines using cube tests for visibility calculations.
     */
    public static final int VISIBILITY_CUBE = 2;

    //The id of the entity
    private int id = 0;

    //The list of children this entity is the parent of.
    protected List children = null;

    //The geometry of the entity. Not required to be set, no
    //rendering will be done if it is null.
    protected Geometry geometry = null;

    //Orientation and position of the entity.
    private Vector position = null;
    
    //Set the entities orientation
    private float yaw;
    private float roll;
    private float pitch;

    //visibility    
    private boolean hasMoved;
    private boolean isVisible = true;
    private int visibilityType;

    //physics
    private PhysicsModule physics;
    
    //the gl object for translation and rotation of the entity.
    protected GL gl;

    /**
     * Constructor initializes the entity. All attributes of the 
     * <code>Entity</code> are empty.
     * 
     * @param id the id of the entity
     * 
     * @throws MonkeyRuntimeException if the id is less than 1.
     */
    public Entity(int id) {
        if (id < 1) {
            throw new MonkeyRuntimeException("Entity id must be greater than 0");
        }
        children = new ArrayList();
        position = new Vector();
        
		gl = DisplaySystem.getDisplaySystem().getGL();

		if(null == gl) {
			throw new MonkeyGLException("The OpenGL context must be set before " +				"using Entity.");
		}

        LoggingSystem.getLoggingSystem().getLogger().log(
            Level.INFO,
            "Created a new entity");
    }

    /**
     * Constructor intializes the entity with a second entity as a child.
     * 
     * @param id the id of the entity
     * @param child the entities child node.
     *
     * @throws MonkeyRuntimeException if the id is less than 1.
     */
    public Entity(int id, Entity child) {
        if (id < 1) {
            throw new MonkeyRuntimeException("Entity id must be greater than 0");
        }
        children = new ArrayList();
        position = new Vector();
        
        gl = DisplaySystem.getDisplaySystem().getGL();

        children.add(child);

        LoggingSystem.getLoggingSystem().getLogger().log(
            Level.INFO,
            "Created a new entity");
    }

    /**
     * <code>addChild</code> adds an <code>Entity</code> to the entity with
     * this entity the parent. 
     * 
     * @param child the <code>Entity</code> to add to the children list.
     */
    public void addChild(Entity child) {
        children.add(child);
    }

    /**
     * <code>removeChild</code> removes the requested child from the list
     * of children.
     * 
     * @param child the child to remove from the list.
     */
    public void removeChild(Entity child) {
        children.remove(child);
    }

    /**
     * <code>setPosition</code> sets the position of this entity.
     * 
     * @param position the new position of this entity.
     */
    public void setPosition(Vector position) {
        this.position = position;
    }

    /**
     * <code>setYaw</code> sets the yaw angle of the entity. Where yaw is
     * defined as rotation about the local Y axis.
     * @param angle the angle of yaw.
     */
    public void setYaw(float angle) {
        this.yaw = angle;
    }
    
    /**
     * <code>setRoll</code> sets the roll angle of the entity. Where roll
     * is defined as rotation about the local Z axis.
     * @param angle the angle of roll.
     */
    public void setRoll(float angle) {
        this.roll = angle;
    }
    
    /**
     * <code>setPitch</code> sets the pitch angle of the entity. Where 
     * pitch is defined as rotation about the local x axis.
     * @param angle the angle of pitch.
     */
    public void setPitch(float angle) {
        this.pitch = angle;
    }

    /**
     * <code>setMoved</code> sets the moved flag.
     * @param hasMoved true if the entity has moved, false otherwise.
     */
    public void setMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * <code>setPhysicsModule</code> sets the module the defines how the
     * physics of the entity are handled. This allows the entity to 
     * interact with the world (what ever that may be) in a realistic and
     * appropriate manner.
     * @param physics the physics module for this entity.
     */
    public void setPhysicsModule(PhysicsModule physics) {
        this.physics = physics;
    }

    /**
     * <code>hasMoved</code> returns true if the entity has moved during the
     * last update, false otherwise.
     * @return true if the entity has moved, false if it hasn't.
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * <code>setGeometry</code> sets the geometry of this entity. If the
     * geometry object is null, nothing will be rendered for this entity.
     * 
     * @param geometry the geometry to set for this entity.
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * <code>getPosition</code> returns the position of the entity in 
     * three dimensional space.
     * 
     * @return the position of the entity.
     */
    public Vector getPosition() {
        return position;
    }
    
    public PhysicsModule getPhysics() {
        return physics;
    }

    /**
     * <code>update</code> updates the state of the entity. 
     */
    public void update(float time) {
        if(null != physics) {
            physics.update(time);
            physics.updatePosition(position);
        }
    }

    /**
     * <code>render</code> translates and rotates the entity based on it's 
     * attributes. It then renders the geometry of the entity if there is any.
     * Each child is then rendered in turn.
     */
    public void render() {
        gl.pushMatrix();
		gl.enable(GL.DEPTH_TEST);
        gl.translatef(position.x, position.y, position.z);
        gl.rotatef(roll, 0, 0, 1);
        gl.rotatef(yaw, 0, 1, 0);
        gl.rotatef(pitch, 1, 0, 1);
        
        //no geometry, so don't render.
        if (null != geometry) {
            geometry.render();
        }

        //render each child.
        for (int i = 0; i < children.size(); i++) {
            ((Entity)children.get(i)).render();
        }
		gl.disable(GL.DEPTH_TEST);
        gl.popMatrix();
    }

    /**
     * <code>isVisible</code> returns true if the entity is currently visible
     * and false if it is not.
     * 
     * @return true if the entity is visible, false otherwise.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * <code>checkVisibility</code> updates the visibility of this entity
     * based on the frustum levels.
     * @param frustum the camera's view frustum.
     */
    public void checkVisibility(Frustum frustum) {
        switch (visibilityType) {
            case VISIBILITY_POINT :
                isVisible =
                    frustum.containsPoint(position.x, position.y, position.z);
                break;
            case VISIBILITY_SPHERE :
                if(null == geometry) {
                    break;
                }
                isVisible =
                    frustum.containsSphere(position.x, position.y, position.z,
                    geometry.getBoundingSphere().getRadius());
                break;
            case VISIBILITY_CUBE :
                if(null == geometry) {
                    break;
                }
                isVisible = 
                    frustum.containsCube(position.x, position.y, 
                    position.z, (float)geometry.getBoundingBox().getRadius());
                break;
            default :
                break;
        }
    }

    /**
     * <code>setVisibilityType</code> sets what type of visibility check will
     * be used for this entity. Valid parameters are: VISIBILITY_POINT,
     * VISIBILITY_SPHERE, and VISIBILITY_CUBE.
     * @param type what type of test to make for visibilty.
     * @throws MonkeyRuntimeException if the visibilty flag is not valid.
     */
    public void setVisibilityType(int type) {
        if (type != VISIBILITY_POINT
            && type != VISIBILITY_SPHERE
            && type != VISIBILITY_CUBE) {

            throw new MonkeyRuntimeException("Invalid visibility type.");
        }
        this.visibilityType = type;
    }

    /**
     * <code>toString</code> creates a string representation of the 
     * <code>Entity</code> object. The format is as follows:<br><br>
     * 
     * -----Entity: <br>
     * Position: {VECTOR POSITION}<br>
     *<br>
     * Child 0: <br>
     * -----Entity: <br>
     * Position: {VECTOR POSITION}<br>
     * <br>
     * Geometry: {GEOMETRY STRING}<br>
     * -----<br>
     *<br>
     * Geometry: {GEOMETRY STRING}<br>
     * -----<br>
     * 
     * @return the string representation of this object.
     */
    public String toString() {
        String string = "-----";
        string += "Entity: \nPosition: " + position.toString() + "\n";

        for (int i = 0; i < children.size(); i++) {
            string += ("\nChild " + i + ": \n");
            string += ((Entity)children.get(i)).toString() + "\n";
        }

        if(null != geometry) {
            string += "\nGeometry: " + geometry.toString() + "\n-----";
        }
        
        return string;
    }
	/**
	 * @return
	 */
	public Geometry getGeometry() {
		return geometry;
	}

}
