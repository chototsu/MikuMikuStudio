/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.input;

import com.jme.app.AbstractGame;
import com.jme.input.action.KeyExitAction;
import com.jme.input.thirdperson.MovementPermitter;
import com.jme.input.thirdperson.ThirdPersonBackwardAction;
import com.jme.input.thirdperson.ThirdPersonForwardAction;
import com.jme.input.thirdperson.ThirdPersonLeftAction;
import com.jme.input.thirdperson.ThirdPersonMouseLook;
import com.jme.input.thirdperson.ThirdPersonRightAction;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;

/**
 * <code>ThirdPersonHandler</code> defines an InputHandler that sets
 * input to be controlled similar to games such as Zelda Windwaker and
 * Mario 64, etc.
 * 
 * @author <a href="mailto:josh@renanse.com">Joshua Slack</a>
 * @version $Revision: 1.4 $
 */

public class ThirdPersonHandler extends InputHandler {

  private Node node;
  protected Vector3f prevLoc;
  protected Vector3f loc;
  protected float faceAngle;
  protected float turnSpeed = 1.5f*FastMath.PI;
  private MovementPermitter perm;
  private Vector3f upAngle = new Vector3f(0,1,0);
  private Vector3f _calc = new Vector3f();
  private ChaseCamera chaseCam;
  private ThirdPersonMouseLook mouseLook;
  private boolean doGradualRotation = true;

  /**
   * Constructor instantiates a new <code>ThirdPersonHandler</code> object.
   * @param node the node to control.
   * @param cam the cam to use.
   * @param api the api to use for input.
   */
  public ThirdPersonHandler(AbstractGame app, Node node, ChaseCamera cam,
            MovementPermitter perm, String api) {
        this.node = node;
        this.perm = perm;
        prevLoc = new Vector3f();
        loc = new Vector3f();
        setKeyBindings(api);
        setActions(app, cam.getCamera());
        setChaseCamera(cam);
    }

    private void setChaseCamera(ChaseCamera cam) {
        chaseCam = cam;
        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        mouse.setMouseInput(InputSystem.getMouseInput());
        setMouse(mouse);

        mouseLook = new ThirdPersonMouseLook(mouse, cam, node);
        addAction(mouseLook);
    }

    /**
     * 
     * <code>setKeyBindings</code> binds the keys to use for the actions.
     * 
     * @param api
     *            the api to use for the input.
     */
    protected void setKeyBindings(String api) {
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();
        InputSystem.createInputSystem(api);

        keyboard.setKeyInput(InputSystem.getKeyInput());
        keyboard.set("forward", KeyInput.KEY_W);
        keyboard.set("backward", KeyInput.KEY_S);
        keyboard.set("left", KeyInput.KEY_A);
        keyboard.set("right", KeyInput.KEY_D);
        keyboard.set("exit", KeyInput.KEY_ESCAPE);

        setKeyBindingManager(keyboard);
    }

    /**
     * 
     * <code>setActions</code> sets the keyboard actions with the
     * corresponding key command.
     * 
     * @param cam
     */
    protected void setActions(AbstractGame app, Camera cam) {
        ThirdPersonForwardAction forward = new ThirdPersonForwardAction(node,
                cam, perm, 0.5f);
        forward.setKey("forward");
        addAction(forward);
        ThirdPersonBackwardAction backward = new ThirdPersonBackwardAction(
                node, cam, perm, 0.5f);
        backward.setKey("backward");
        addAction(backward);
        ThirdPersonRightAction right = new ThirdPersonRightAction(node, cam,
                perm, 1f);
        right.setKey("right");
        addAction(right);
        ThirdPersonLeftAction left = new ThirdPersonLeftAction(node, cam, perm,
                1f);
        left.setKey("left");
        addAction(left);
        KeyExitAction exit = new KeyExitAction(app);
        exit.setKey("exit");
        addAction(exit);
    }

    public void update(float time) {
        prevLoc.set(node.getLocalTranslation());
        loc.set(prevLoc);
        super.update(time);
        loc.subtractLocal(node.getLocalTranslation());
        if (!loc.equals(Vector3f.ZERO)) {
            float distance = loc.length();
            loc.normalizeLocal();
            float actAngle;
            if (loc.x < 0)
                actAngle = FastMath.atan(loc.z / loc.x);
            else if (loc.x > 0)
                actAngle = FastMath.PI + FastMath.atan(loc.z / loc.x);
            else if (loc.z > 0)
                actAngle = FastMath.PI;
            else
                actAngle = 0;
            if (doGradualRotation) {
                float oldAct = actAngle;
                actAngle -= faceAngle;

                // redo: THE FOLLOWING IS SUPPOSED TO CORRECT THE RAPID CHANGE
                // FROM ONE DIR TO ANOTHER... IT DOESN'T.
                if (actAngle == FastMath.PI) {
                    actAngle -= FastMath.PI / 8f;
                    oldAct -= FastMath.PI / 8f;
                }
                actAngle = FastMath.normalizeAngle(actAngle);

                // update rotation
                if (actAngle > 0) {
                    faceAngle += time * turnSpeed;
                    if (faceAngle > oldAct)
                        faceAngle = oldAct;
                } else if (actAngle < 0) {
                    faceAngle -= time * turnSpeed;
                    if (faceAngle < oldAct)
                        faceAngle = oldAct;
                }
            }

            node.getLocalRotation().fromAngleNormalAxis(-faceAngle, upAngle);
            node.getLocalTranslation().set(prevLoc);
            node.getLocalRotation().getRotationColumn(0, _calc).multLocal(
                    distance);
            node.getLocalTranslation().addLocal(_calc);
        }
        chaseCam.update(time);
    }

    /**
     * @return Returns the turnSpeed.
     */
    public float getTurnSpeed() {
        return turnSpeed;
    }

    /**
     * @param turnSpeed
     *            The turnSpeed to set.
     */
    public void setTurnSpeed(float turnSpeed) {
        this.turnSpeed = turnSpeed;
    }

    /**
     * @return Returns the upAngle.
     */
    public Vector3f getUpAngle() {
        return upAngle;
    }

    /**
     * @param upAngle
     *            The upAngle to set (as copy)
     */
    public void setUpAngle(Vector3f upAngle) {
        this.upAngle.set(upAngle);
    }

    /**
     * @return Returns the faceAngle (in radians)
     */
    public float getFaceAngle() {
        return faceAngle;
    }

    /**
     * @return Returns the chaseCam.
     */
    public ChaseCamera getChaseCam() {
        return chaseCam;
    }

    /**
     * @return Returns the mouseLook.
     */
    public ThirdPersonMouseLook getMouseLook() {
        return mouseLook;
    }

    /**
     * @return Returns the doGradualRotation.
     */
    public boolean isDoGradualRotation() {
        return doGradualRotation;
    }

    /**
     * @param doGradualRotation The doGradualRotation to set.
     */
    public void setDoGradualRotation(boolean doGradualRotation) {
        this.doGradualRotation = doGradualRotation;
    }
}
