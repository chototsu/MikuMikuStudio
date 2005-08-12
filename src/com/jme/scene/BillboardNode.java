/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
package com.jme.scene;

import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;

/**
 * <code>BillboardNode</code> defines a node that always orients towards the
 * camera. However, it does not tilt up/down as the camera rises. This keep
 * geometry from appearing to fall over if the camera rises or lowers.
 * <code>BillboardNode</code> is useful to contain a single quad that has a
 * image applied to it for lowest detail models. This quad, with the texture,
 * will appear to be a full model at great distances, and save on rendering and
 * memory. It is important to note that for AXIAL mode, the billboards
 * orientation will always be up (0,1,0). This means that a "standard" jME
 * camera with up (0,1,0) is the only camera setting compatible with AXIAL mode.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: BillboardNode.java,v 1.19 2005-08-12 21:49:20 renanse Exp $
 */
public class BillboardNode extends Node {
    private static final long serialVersionUID = 1L;

    private float lastTime;

    private Matrix3f orient;

    private Vector3f look;

    private Vector3f left;

    private Vector3f up;

    private int type;

    /** Alligns this Billboard Node to the screen. */
    public static final int SCREEN_ALIGNED = 0;

    /** Alligns this Billboard Node to the screen, but keeps the Y axis fixed. */
    public static final int AXIAL = 1;

    /** Alligns this Billboard Node to the camera position. */
    public static final int CAMERA_ALIGNED = 2;

    /**
     * Constructor instantiates a new <code>BillboardNode</code>. The name of
     * the node is supplied during construction.
     * 
     * @param name
     *            the name of the node.
     */
    public BillboardNode(String name) {
        super(name);
        orient = new Matrix3f();
        up = new Vector3f();
        look = new Vector3f();
        left = new Vector3f();
        type = SCREEN_ALIGNED;
    }

    /**
     * <code>updateWorldData</code> defers the updating of the billboards
     * orientation until rendering. This keeps the billboard from being
     * needlessly oriented if the player can not actually see it.
     * 
     * @param time
     *            the time between frames.
     * @see com.jme.scene.Spatial#updateWorldData(float)
     */
    public void updateWorldData(float time) {
        lastTime = time;
        updateWorldBound();
    }

    /**
     * <code>draw</code> updates the billboards orientation then renders the
     * billboard's children.
     * 
     * @param r
     *            the renderer used to draw.
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     */
    public void draw(Renderer r) {
        Camera cam = r.getCamera();
        rotateBillboard(cam);

        super.draw(r);
    }

    /**
     * rotate the billboard based on the type set
     * 
     * @param cam
     *            Camera
     */
    public void rotateBillboard(Camera cam) {
        // get the scale, translation and rotation of the node in world space
        updateWorldVectors();

        switch (type) {
        case AXIAL:
            rotateAxial(cam);
            break;
        case SCREEN_ALIGNED:
            rotateScreenAligned(cam);
            break;
        case CAMERA_ALIGNED:
            rotateCameraAligned(cam);
            break;
        }

        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            Spatial child = (Spatial) children.get(i);
            if (child != null) {
                child.updateGeometricState(lastTime, false);
            }
        }
    }

    /**
     * rotateCameraAligned
     * 
     * @param camera
     *            Camera
     */
    private void rotateCameraAligned(Camera camera) {
        look.set(camera.getLocation()).subtractLocal(worldTranslation);
        look.normalizeLocal();

        float el = FastMath.asin(look.y);
        float az = FastMath.atan2(look.x, look.z);
        float elCos = FastMath.cos(el);
        float azCos = FastMath.cos(az);
        float elSin = FastMath.sin(el);
        float azSin = FastMath.sin(az);

        // compute the local orientation matrix for the billboard
        orient.m00 = azCos;
        orient.m01 = azSin * -elSin;
        orient.m02 = azSin * elCos;
        orient.m10 = 0;
        orient.m11 = elCos;
        orient.m12 = elSin;
        orient.m20 = -azSin;
        orient.m21 = azCos * -elSin;
        orient.m22 = azCos * elCos;

        // The billboard must be oriented to face the camera before it is
        // transformed into the world.
        worldRotation.apply(orient);
    }

    /**
     * Rotate the billboard so it points directly opposite the direction the
     * camera's facing
     * 
     * @param camera
     *            Camera
     */
    private void rotateScreenAligned(Camera camera) {
        // coopt diff for our in direction:
        look.set(camera.getDirection()).negateLocal();
        // coopt loc for our left direction:
        left.set(camera.getLeft()).negateLocal();
        orient.fromAxes(left, camera.getUp(), look);
        worldRotation.fromRotationMatrix(orient);
    }

    /**
     * Rotate the billboard towards the camera, but keeping the y axis fixed.
     * 
     * @param camera
     *            Camera
     */
    private void rotateAxial(Camera camera) {
        // Compute the additional rotation required for the billboard to face
        // the camera. To do this, the camera must be inverse-transformed into
        // the model space of the billboard.
        look.set(camera.getLocation()).subtractLocal(worldTranslation);
        worldRotation.mult(look, left); // coopt left for our own purposes.
        left.x *= 1.0f / worldScale.x;
        left.y *= 1.0f / worldScale.y;
        left.z *= 1.0f / worldScale.z;

        // squared length of the camera projection in the xz-plane
        float lengthSquared = left.x * left.x + left.z * left.z;
        if (lengthSquared < FastMath.FLT_EPSILON) {
            // camera on the billboard axis, rotation not defined
            return;
        }

        // unitize the projection
        float invLength = FastMath.invSqrt(lengthSquared);
        left.x *= invLength;
        left.y = 0.0f;
        left.z *= invLength;

        // compute the local orientation matrix for the billboard
        orient.m00 = left.z;
        orient.m01 = 0;
        orient.m02 = left.x;
        orient.m10 = 0;
        orient.m11 = 1;
        orient.m12 = 0;
        orient.m20 = -left.x;
        orient.m21 = 0;
        orient.m22 = left.z;

        // The billboard must be oriented to face the camera before it is
        // transformed into the world.
        worldRotation.apply(orient);
    }

    /**
     * Returns the type of rotation this BillboardNode is set too.
     * 
     * @return The type of rotation, AXIAL or SCREEN.
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type of rotation this BillboardNode will have. The type can be
     * either SCREEN_ALIGNED or AXIAL. Invalid types will assume no billboard
     * rotation.
     */
    public void setType(int type) {
        this.type = type;
    }
}