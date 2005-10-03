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

import com.jme.input.InputHandler;
import com.jme.input.thirdperson.ThirdPersonMouseLook;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Spatial;

/**
 * Camera that will smoothly follow a set scene element, allowing for rotation
 * about and zoom on that element.
 * 
 * @author <a href="mailto:josh@renanse.com">Joshua Slack</a>
 * @version $Revision: 1.3 $
 */

public class ChaseCamera extends InputHandler {

    private Vector3f idealSphereCoords;
    private Vector3f idealPosition;
    private Camera cam;
    private Vector3f velocity;
    private Spatial target;
    private float springK, dampingK;
    private Vector3f dirVec;
    private Vector3f worldUpVec;
    private Vector3f upVec;
    private Vector3f leftVec;
    private Vector3f targetOffset;
    private Vector3f targetPos = new Vector3f();

    /** The ThirdPersonMouseLook action, kept as a field to allow easy access to setting speeds and y axis flipping. */
    protected ThirdPersonMouseLook mouseLook;

    public ChaseCamera(Camera cam, Spatial target, Vector3f targetOffset) {
        super();
        this.cam = cam;
        this.target = target;
        this.targetOffset = targetOffset;
        velocity = new Vector3f();
        idealSphereCoords = new Vector3f(150, 0, 0 * FastMath.DEG_TO_RAD);
        dirVec = new Vector3f();
        upVec = new Vector3f();
        leftVec = new Vector3f();

        idealPosition = new Vector3f();
        worldUpVec = new Vector3f(0,1,0);  // default

        dampingK = 12f; // play with this number for camera velocity...
        springK = (dampingK * dampingK) / 4;
        setupMouse();
    }

    private void setupMouse() {
        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        mouse.setMouseInput(InputSystem.getMouseInput());
        setMouse(mouse);

        if (mouseLook != null)
            removeAction(mouseLook);
        
        mouseLook = new ThirdPersonMouseLook(mouse, this, target);
        addAction(mouseLook);
    }

    public void setCamera(Camera cam) {
        this.cam = cam;
    }

    public Camera getCamera() {
        return cam;
    }

    public void update(float time) {
        super.update(time);
        Vector3f camPos = cam.getLocation();
        targetPos.set(target.getWorldTranslation());

        if (!Vector3f.isValidVector(camPos)
                || !Vector3f.isValidVector(targetPos))
            return;

        targetPos.addLocal(targetOffset);

        // update camera based on "springs"
        float offX = (camPos.x - targetPos.x);
        float offZ = (camPos.z - targetPos.z);
        if (offX < 0)
            idealSphereCoords.y = FastMath.PI + FastMath.atan(offZ / offX);
        else if (offX > 0)
            idealSphereCoords.y = FastMath.atan(offZ / offX);
        else if (offZ < 0)
            idealSphereCoords.y = FastMath.PI * 1.5f;
        else if (offZ > 0)
            idealSphereCoords.y = FastMath.PI * .5f;
        else
            idealSphereCoords.y = 0;

        FastMath.sphericalToCartesian(idealSphereCoords, idealPosition).addLocal(
                targetPos);

        Vector3f displace = camPos.subtract(idealPosition);
        displace.multLocal(-springK).subtractLocal(velocity.x * dampingK,
                velocity.y * dampingK, velocity.z * dampingK);

        velocity.addLocal(displace.multLocal(time));
        camPos.addLocal(velocity.x * time, velocity.y * time, velocity.z
                        * time);
        setCameraView(camPos, targetPos);
    }

    Vector3f oldCameraDir = new Vector3f();

    /**
     * setCameraView
     * 
     * @param camPos
     *            Vector3f
     * @param targetPos
     *            Vector3f
     */
    public void setCameraView(Vector3f camPos, Vector3f targetPos) {
        dirVec.set(targetPos).subtractLocal(camPos).normalizeLocal();

        // check to see if we haven't really updated camera -- no need to call
        // sets.
        if (oldCameraDir.equals(dirVec)) {
            return;
        }

        oldCameraDir.set(dirVec);
        upVec.set(worldUpVec);
        leftVec = upVec.cross(dirVec).normalizeLocal();
        upVec = dirVec.cross(leftVec).normalizeLocal();
        cam.setAxes(leftVec, upVec, dirVec);
        cam.onFrameChange();
    }

    public Vector3f getIdealSphereCoords() {
        return idealSphereCoords;
    }

    public Vector3f getIdealPosition() {
        return idealPosition;
    }

    /**
     * @return Returns the dampingK.
     */
    public float getDampingK() {
        return dampingK;
    }

    /**
     * @param dampingK The dampingK to set.
     */
    public void setDampingK(float dampingK) {
        this.dampingK = dampingK;
    }

    /**
     * @return Returns the springK.
     */
    public float getSpringK() {
        return springK;
    }

    /**
     * @param springK The springK to set.
     */
    public void setSpringK(float springK) {
        this.springK = springK;
    }

    /**
     * @return Returns the target.
     */
    public Spatial getTarget() {
        return target;
    }

    /**
     * @param target The target to set
     */
    public void setTarget(Spatial target) {
        this.target = target;
    }

    /**
     * @return Returns the targetOffset.
     */
    public Vector3f getTargetOffset() {
        return targetOffset;
    }

    /**
     * @param targetOffset The targetOffset to set (as copy)
     */
    public void setTargetOffset(Vector3f targetOffset) {
        this.targetOffset.set(targetOffset);
    }

    /**
     * @param worldUpVec The worldUpVec to set (as copy)
     */
    public void setWorldUpVec(Vector3f worldUpVec) {
        this.worldUpVec.set(worldUpVec);
    }

    /**
     * @return Returns the mouseLook.
     */
    public ThirdPersonMouseLook getMouseLook() {
        return mouseLook;
    }
}
