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

package jme.controller;

import java.awt.Point;
import java.util.logging.Level;

import org.lwjgl.Display;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.vector.Vector3f;

import jme.exception.MonkeyRuntimeException;
import jme.system.KeyBindingManager;
import jme.entity.camera.Camera;
import jme.utility.LoggingSystem;

/**
 * <code>BaseFPSController</code> creates a base class for developing a 
 * first person shooter style control system. That is, the view is controlled
 * via moving forward, backward, strafing to the left and right and changing
 * orientation based on mouse movements. The basic assumption is that the
 * keyboard is used to alter the position of the camera while the mouse is
 * used to alter it's orientation. The key bindings have a default of:<br>
 * <br>
 * w - forward
 * s - backward
 * a - strafe left 
 * d - strafe right.
 * <br>
 * <br>
 * 
 * Addition key bindings are provided by overriding the checkAdditionalKeys
 * method.
 * 
 * @author Mark Powell
 * @version 0.1.0
 */
public class BaseFPSController extends AbstractGameController {

    //accuracy denotes the level of granularity for mouse movements.
    protected float accuracy = 0.0f;

    //the speed to move.
    private float motionSpeed = 10.0f;
    private float rotationSpeed = 1.0f;

    //current mouse position.
    private Point mousePosition;

    private Camera entity;

    //keybindings
    protected KeyBindingManager key;

    /**
     * Constructor builds a controller with all default values.
     */
    public BaseFPSController(int id) {
        entity = new Camera(id);

        this.accuracy = 10.0f;
        mousePosition = new Point();

        try {
            Mouse.create();
            Keyboard.create();
        } catch (Exception e) {
            e.printStackTrace();
            LoggingSystem.getLoggingSystem().getLogger().log(
                Level.WARNING,
                "Error creating Mouse and/or Keyboard");
        }

        setDefaultKeyBindings();
        LoggingSystem.getLoggingSystem().getLogger().log(
            Level.INFO,
            "Created game controller");
    }

    /**
     * Constructor sets the entity to the given entity and initializes the
     * controller for use.
     * 
     * @param entity the entity to control.
     * @throws MonkeyRuntimeException if the entity is null.
     */
    public BaseFPSController(Camera entity) {
        if (null == entity) {
            throw new MonkeyRuntimeException("Camera cannot be null");
        }

        this.entity = entity;

        this.accuracy = 10.0f;
        mousePosition = new Point();

        try {
            Mouse.create();
            Keyboard.create();
        } catch (Exception e) {
            e.printStackTrace();
            LoggingSystem.getLoggingSystem().getLogger().log(
                Level.WARNING,
                "Error creating Mouse and/or Keyboard");

        }

        setMousePosition(Display.getWidth() >> 1, Display.getHeight() >> 1);

        setDefaultKeyBindings();
        LoggingSystem.getLoggingSystem().getLogger().log(
            Level.INFO,
            "Created game controller");

    }

    /**
     * <code>tiltView</code> rotates the entity about it's local X-Axis. Allowing
     * the entity to look up and down. The amount of tilting is denoted by the
     * angle parameter, where positive angle is up and negative angle is down.
     * 
     * @param angle the amount to tilt.
     */
    public void tiltView(int angle) {
        if (0 == angle) {
            return;
        }

        float rotate = 0.0f;

        rotate = (float)angle / accuracy;

        //Determine the axis at which we are rotating.
        Vector3f temp =
            Vector3f.sub(entity.getView(), entity.getPosition(), null);

        Vector3f axis = Vector3f.cross(temp, entity.getUp(), null);
        axis = axis.normalise(null);

        //rotate the entity.
        rotate(rotate, axis);
    }

    /**
     * <code>paneView</code> rotates the entity about it's local Y-Axis. 
     * Allowing the entity to look left and right. The amount of panning is 
     * denoted by the angle parameter, where positive angle is right and
     * negative angle is left.
     * 
     * @param angle to amount to tilt.
     */
    public void panView(float angle) {
        if (0 == angle) {
            return;
        }

        float rotate = 0.0f;

        rotate = angle / accuracy;

        rotate(-rotate, new Vector3f(0, 1, 0));
    }

    /**
     * <code>raise</code> alters the position and view of the entity such
     * that it raises or lowers along the local Y-Axis. The amount of 
     * altitude change is denoted by the speed parameter, where positive
     * speed will raise and negative speed will lower.
     * 
     * @param speed the amount to raise or lower.
     */
    public void raise(float speed) {
        entity.getPosition().y += speed;
        entity.getView().y += speed;
    }

    /**
     * <code>update</code> overrides <code>AbstractGameController</code>'s 
     * <code>update</code> method. This is intended to be called each frame
     * or round. During an update, the mouse is polled and the change in
     * position reflects in a call to <code>tiltView</code> and 
     * <code>panView</code>. A poll is also made to the keyboard, where the
     * key bindings are check against the keyboard state. If necessary, the
     * entity is then moved. The method returns a boolean. This boolean
     * notifies if the game should be stopped or not. Default false is 
     * generated by hitting the escape key.
     * @param frameRate denotes the current frames per second of the renderer
     * 		to allow for time based movement.
     * @return boolean true continue for another frame, false stop.
     */
    public boolean update(float time) {
        entity.setMoved(false);
        Vector3f temp =
            Vector3f.sub(entity.getView(), entity.getPosition(), null);
        Vector3f vCross = Vector3f.cross(temp, entity.getUp(), null);

        entity.setStrafe(vCross.normalise(null));

        pollMouse();
        int middleX = Display.getWidth() >> 1;
        int middleY = Display.getHeight() >> 1;

        int angleX = 0;
        int angleY = 0;

        mousePosition.x = this.getMousePositionX();
        mousePosition.y = this.getMousePositionY();

        setMousePosition(middleX, middleY);

        angleX = middleX - mousePosition.x;
        angleY = middleY - mousePosition.y;

        tiltView(angleY);
        panView(-angleX);

        if (entity instanceof Camera) {
            ((Camera)entity).updateFrustum();
        }

        entity.update(time);

        Keyboard.poll();

        if (isKeyDown("forward")) {
            move(motionSpeed / time);
        }

        if (isKeyDown("backward")) {
            move(-motionSpeed / time);
        }

        if (isKeyDown("strafeLeft")) {
            strafe(-motionSpeed / time);
        }

        if (isKeyDown("strafeRight")) {
            strafe(motionSpeed / time);
        }

        if (isKeyDown("turnRight")) {
            panView(rotationSpeed / time);
        }

        if (isKeyDown("turnLeft")) {
            panView(-rotationSpeed / time);
        }

        if (isKeyDown("rise")) {
            raise(motionSpeed / time);
        }

        if (isKeyDown("lower")) {
            raise(-motionSpeed / time);
        }

        return checkAdditionalKeys();
    }

    public void render() {
        entity.render();
    }

    /**
     * <code>setAccuracy</code> sets the accuracy value for mouse control.
     * 
     * @param value the new accuracy value.
     */
    public void setAccuracy(float value) {
        accuracy = value;
    }

    /**
     * <code>setMovementSpeed</code> adjusts the speed at which forward/backward
     * movement occurs. Default value is 10. 
     * @param value the new speed of movement.
     */
    public void setMovementSpeed(float value) {
        motionSpeed = value;
    }

    /**
     * <code>getEntityView</code> returns the point that the camera is 
     * viewing.
     * @return the point that the camera is looking at.
     */
    public Vector3f getEntityView() {
        return entity.getView();
    }

    /**
     * <code>getEntityUp</code> return the orientation of the camera.
     * @return the orientation of the camera.
     */
    public Vector3f getEntityUp() {
        return entity.getUp();
    }

    /**
     * <code>setEntityView</code> sets the point at which the camera is
     * pointed at.
     * 
     * @param view the point the camera is looking at.
     */
    public void setEntityView(Vector3f view) {
        entity.setView(view);
    }

    /**
     * <code>setEntityUp</code> sets the vector that represents the
     * up or orientation of the entity.
     * @param the vector that represents the up.
     */
    public void setEntityUp(Vector3f up) {
        entity.setUp(up);
    }

    /**
     * <code>rotate</code> rotates the camera's view about a given axis.
     * @param angle the angle to rotate.
     * @param axis the axis to rotate about.
     */
    public void rotate(float angle, Vector3f axis) {
        float x, y, z;
        x = axis.x;
        y = axis.y;
        z = axis.z;
        Vector3f newView = new Vector3f();

        Vector3f view =
            Vector3f.sub(entity.getView(), entity.getPosition(), null);

        float cosTheta = org.lwjgl.Math.cos(angle);
        float sinTheta = org.lwjgl.Math.sin(angle);

        newView.x = (cosTheta + (1 - cosTheta) * x * x) * view.x;
        newView.x += ((1 - cosTheta) * x * y - z * sinTheta) * view.y;
        newView.x += ((1 - cosTheta) * x * z + y * sinTheta) * view.z;

        newView.y = ((1 - cosTheta) * x * y + z * sinTheta) * view.x;
        newView.y += (cosTheta + (1 - cosTheta) * y * y) * view.y;
        newView.y += ((1 - cosTheta) * y * z - x * sinTheta) * view.z;

        newView.z = ((1 - cosTheta) * x * z - y * sinTheta) * view.x;
        newView.z += ((1 - cosTheta) * y * z + x * sinTheta) * view.y;
        newView.z += (cosTheta + (1 - cosTheta) * z * z) * view.z;

        entity.setView(Vector3f.add(entity.getPosition(), newView, null));
    }

    /**
     * <code>move</code> changes the position of the camera depending on
     * it's orientation.
     * 
     * @param speed how much to move the camera.
     */
    public void move(float speed) {
        Vector3f vec =
            (
                Vector3f.sub(
                    entity.getView(),
                    entity.getPosition(),
                    null)).normalise(
                null);

        Vector3f newView = entity.getView();

        entity.getPosition().x += vec.x * speed;
        entity.getView().x += vec.x * speed;

        entity.getPosition().y += vec.y * speed;
        entity.getView().y += vec.y * speed;

        entity.getPosition().z += vec.z * speed;
        entity.getView().z += vec.z * speed;

        entity.setMoved(true);

    }

    /**
     *<code>strafe</code> moves the camera along the perpendicular axis of
     *it's view. 
     *
     *@param speed how much to strafe the camera.
     */
    public void strafe(float speed) {
        entity.getPosition().x += entity.getStrafe().x * speed;
        entity.getPosition().z += entity.getStrafe().z * speed;

        entity.getView().x += entity.getStrafe().x * speed;
        entity.getView().z += entity.getStrafe().z * speed;
    }

    /**
     * <code>checkAdditionalKeys</code> tests the keyboard for any additional
     * key bindings. It is recommended that this be overridden by any 
     * subclass to add additional key bindings. The method returns a boolean. 
     * This boolean notifies if the game should be stopped or not. Default false
     * is generated by hitting the escape key.
     * 
     * @param boolean true continue for another frame, false stop.
     */
    protected boolean checkAdditionalKeys() {
        if (isKeyDown("exit")) {
            return false;
        }

        return true;
    }

    /**
     * <code>setDefaultKeyBindings</code> sets the default bindings for
     * forward, backward, strafe left/right, turn left/right, rise and lower.
     */
    private void setDefaultKeyBindings() {
        key = KeyBindingManager.getKeyBindingManager();
        key.set("forward", Keyboard.KEY_W);
        key.set("backward", Keyboard.KEY_S);
        key.set("strafeLeft", Keyboard.KEY_A);
        key.set("strafeRight", Keyboard.KEY_D);
        key.set("turnLeft", Keyboard.KEY_LEFT);
        key.set("turnRight", Keyboard.KEY_RIGHT);
        key.set("rise", Keyboard.KEY_Q);
        key.set("lower", Keyboard.KEY_Z);
        key.set("exit", Keyboard.KEY_ESCAPE);
    }
}
