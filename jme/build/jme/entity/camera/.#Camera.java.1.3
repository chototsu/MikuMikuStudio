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

package jme.entity.camera;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLU;

import jme.entity.Entity;
import jme.locale.external.feature.Sky;
import jme.math.Vector;

/**
 * <code>Camera</code> defines a camera in three dimensional space. The camera
 * has three aspects. A position defines where the camera is located, the view
 * defines what point the camera is looking at and the up defines the orientation
 * of the camera. To set the viewport a single call to <code>look</code> is all
 * that is required.
 * 
 * @author Mark Powell
 * @version 1
 */
public class Camera extends Entity{
	
    private Frustum frustum;
    private Vector view = null;
    private Vector up = null;
    private Vector strafe = null;
    /**
     * <code>sky</code> the sky representation.
     */
    protected Sky sky;

    /**
     * Default constructor sets all three attributes to (0, 0, 0).
     */
    public Camera(int id) {
        super(id);
        view = new Vector();
        up = new Vector();
	}
    
    /**
     * Constructor instantiates a new <code>Camera</code> object with
     * a given entity as a child.
     * @param id the id of the camera.
     * @param child the entity child.
     */
    public Camera(int id, Entity child) {
        super(id, child);
        view = new Vector();
        up = new Vector();
    }

    /**
     * Constructor takes the nine parameters that make up the three attributes
     * and initializes the camera's vectors to them. 
     * 
     * @param positionX the x position of the camera.
     * @param positionY the y position of the camera.
     * @param positionZ the z position of the camera.
     * @param viewX the x position of the point viewed.
     * @param viewY the y position of the point viewed.
     * @param viewZ the z position of the point viewed.
     * @param upX the x component of the up vector.
     * @param upY the y component of the up vector.
     * @param upZ the z component of the up vector.
     */
    public Camera(int id,
        float positionX,
        float positionY,
        float positionZ,
        float viewX,
        float viewY,
        float viewZ,
        float upX,
        float upY,
        float upZ) {
        
        super(id);

        setUp(new Vector(upX, upY, upZ));
        setView(new Vector(viewX, viewY, viewZ));
        setPosition(new Vector(positionX, positionY, positionZ));
        frustum = new Frustum();
    }

    /**
     * Constructor takes three <code>Vector3</code> parameters and sets them
     * to the camera's attributes.
     * 
     * @param position the position of the camera.
     * @param view the point the camera is looking.
     * @param up the orientation of the camera.
     */
    public Camera(int id, Vector position, Vector view, Vector up) {
        super(id);
        setPosition(position);
        setView(view);
        setUp(up);
    }
    
    /**
     * <code>setAttributes</code> sets the camera's attributes to the new nine 
     * parameters that make up the three attributes and initializes the 
     * camera's vectors to them. 
     * 
     * @param positionX the x position of the camera.
     * @param positionY the y position of the camera.
     * @param positionZ the z position of the camera.
     * @param viewX the x position of the point viewed.
     * @param viewY the y position of the point viewed.
     * @param viewZ the z position of the point viewed.
     * @param upX the x component of the up vector.
     * @param upY the y component of the up vector.
     * @param upZ the z component of the up vector.
     */
    public void setAttributes(float positionX,
        float positionY,
        float positionZ,
        float viewX,
        float viewY,
        float viewZ,
        float upX,
        float upY,
        float upZ) {

        setUp(new Vector(upX, upY, upZ));
        setView(new Vector(viewX, viewY, viewZ));
        setPosition(new Vector(positionX, positionY, positionZ));
    }
    
    /**
     * <code>updateFrustum</code> sets the view frustum to the newest
     * values based on the location and orientation of the camera.
     */
    public void updateFrustum() {
    	frustum.update();
    }
    
    /**
     * <code>getFrustum</code> returns the view frustum of the camera.
     * @return the view frustum
     */
    public Frustum getFrustum() {
    	return frustum;
    }

    /**
     * <code>set</codes> sets the camera's attributes to the three 
     * <code>Vector3</code> parameters and sets them to the camera's attributes.
     * 
     * @param position the position of the camera.
     * @param view the point the camera is looking.
     * @param up the orientation of the camera.
     */
    public void set(Vector position, Vector view, Vector up) {
        setPosition(position);
        setView(view);
        setUp(up);
    }
    
    /**
     * <code>setView</code> sets the point at which the camera is looking.
     * 
     * @param x the x component of the new view.
     * @param y the y component of the new view.
     * @param z the z component of the new view.
     */
    public void setView(float x, float y, float z) {
        view = new Vector(x, y, z);
    }

    /**
     * <code>setView</code> sets the point vector at which the camera is 
     * looking.
     * 
     * @param view the new view point.
     */
    public void setView(Vector view) {
        this.view = view;
    }

    /**
     * <code>setUp</code> sets the orientation of the camera.
     * 
     * @param x the new x component of the orientation vector.
     * @param y the new y component of the orientation vector.
     * @param z the new z component of the orientation vector.
     */
    public void setUp(float x, float y, float z) {
        up = new Vector(x, y, z);
    }

    /**
     * <code>setUp</code> sets the orienation of the camera.
     * 
     * @param up the new orientation vector of the camera.
     */
    public void setUp(Vector up) {
        this.up = up;
    }

    /**
     * <code>setStrafe</code> sets the strafe vector of the entity.
     * @param strafe the strafe vector.
     */
    public void setStrafe(Vector strafe) {
        this.strafe = strafe;
    }
        
    /**
    * <code>getStrafe</code> returns the strafe vector of the camera.
    * 
    * @return the strafe vector of the camera.
    */
   public Vector getStrafe() {
       return strafe;
   }
   
   /**
    * <code>getView</code> returns the three dimensional point that the
    * camera is looking at.
    * 
    * @return the view of the camera.
    */
   public Vector getView() {
       return view;
   }

   /**
    * <code>getUp</code> returns the "up" orientation of the camera.
    * 
    * @return the up vector of the camera.
    */
   public Vector getUp() {
       return up;
   }
    
    /**
     * <code>setSky</code> sets the sky representation of the terrain. This
     * takes an <code>Sky</code> subclass. The client is reponsible
     * for calling the sky's render method during the terrain's render call.
     */
    public void setSky(Sky sky) {
        this.sky = sky;
    }
    
    /**
     * <code>update</code> updates the sky parameters if necessary.
     * @param time the time between frames.
     */
    public void update(float time) {
        if(null != sky) {
            sky.update(time);
        }
    }
    
    /**
     * <code>look</code> uses the current position, view and up vector of the
     * camera to determine the view. 
     */
    public void render() {
        GLU.gluLookAt(
            getPosition().x, getPosition().y, getPosition().z,
            getView().x, getView().y, getView().z,
            getUp().x, getUp().y, getUp().z);
        
        if(null != sky) {
            GL.glPushMatrix();
            float x = getPosition().x - sky.getSize() / 2;
            float y = getPosition().y - sky.getSize() * 0.75f;
            float z = getPosition().z - sky.getSize() / 2;
            GL.glTranslatef(x, y, z);
        
            sky.render();
            GL.glPopMatrix();
        }
        
        for(int i = 0; i < children.size(); i++) {
            ((Entity)children.get(i)).render();
        }
        
    }
    
    /**
     * <code>toString</code> returns the string representation of this
     * camera object in the format:<br><br>
     * jme.entity.camera.Camera@861f24<br>
     * Position: {VECTOR}<br>
     * View: {VECTOR}<br>
     * Up: {VECTOR}<br>
     * 
     * @return string representation of this object.
     */
    public String toString() {
        String string = super.toString();
        string += "\nPosition: " + getPosition().toString();
        string += "\nView: " + getView().toString();
        string += "\nUp: " + getUp().toString();
        return string;
    }

}
