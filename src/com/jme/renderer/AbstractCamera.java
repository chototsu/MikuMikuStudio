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

/*
 * EDIT:  02/09/2004 - Added viewport accessors. GOP
 */
package com.jme.renderer;

import java.util.logging.Level;

import com.jme.math.Quaternion;
import com.jme.math.Matrix3f;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.scene.BoundingVolume;
import com.jme.util.LoggingSystem;

/**
 * <code>AbstractCamera</code> implments the <code>Camera</code> interface
 * implementing all non-API specific camera calculations. Those requiring
 * API (LWJGL, JOGL, etc) specific calls are not implemented making this
 * class abstract. API specific classes are expected to extend this class and
 * handle renderer viewport setting.
 * @author Mark Powell
 * @author Joshua Slack -- Quats
 * @version $Id: AbstractCamera.java,v 1.10 2004-03-02 16:40:08 renanse Exp $
 */
public abstract class AbstractCamera implements Camera {
    //planes of the frustum
    /**
     * LEFT_PLANE represents the left plane of the camera frustum.
     */
    public static final int LEFT_PLANE = 0;
    /**
     * RIGHT_PLANE represents the right plane of the camera frustum.
     */
    public static final int RIGHT_PLANE = 1;
    /**
     * BOTTOM_PLANE represents the bottom plane of the camera frustum.
     */
    public static final int BOTTOM_PLANE = 2;
    /**
     * TOP_PLANE represents the top plane of the camera frustum.
     */
    public static final int TOP_PLANE = 3;
    /**
     * FAR_PLANE represents the far plane of the camera frustum.
     */
    public static final int FAR_PLANE = 4;
    /**
     * NEAR_PLANE represents the near plane of the camera frustum.
     */
    public static final int NEAR_PLANE = 5;
    /**
     * FRUSTUM_PLANES represents the number of planes of the camera frustum.
     */
    public static final int FRUSTUM_PLANES = 6;
    /**
     * MAX_WORLD_PLANES holds the maximum planes allowed by the system.
     */
    public static final int MAX_WORLD_PLANES = 32;

    //the location and orientation of the camera.
    protected Vector3f location;
    protected Vector3f left;
    protected Vector3f up;
    protected Vector3f direction;

    //frustum plane distances
    protected float frustumNear;
    protected float frustumFar;
    protected float frustumLeft;
    protected float frustumRight;
    protected float frustumTop;
    protected float frustumBottom;

    //Temporary values computed in onFrustumChange that are needed if a
    //call is made to onFrameChange.
    protected float coeffLeft[];
    protected float coeffRight[];
    protected float coeffBottom[];
    protected float coeffTop[];

    //frustum planes always processed for culling
    protected int planeQuantity;

    //view port coordinates
    protected float viewPortLeft;
    protected float viewPortRight;
    protected float viewPortTop;
    protected float viewPortBottom;

    protected Plane[] worldPlane;
    protected Vector3f lookAt = new Vector3f();
    private int planeState;

    /**
     * Constructor instantiates a new <code>AbstractCamera</code> object. All
     * values of the camera are set to default.
     *
     */
    public AbstractCamera() {
        location = new Vector3f();
        left = new Vector3f(1, 0, 0);
        up = new Vector3f(0, 1, 0);
        direction = new Vector3f(0, 0, 1);

        frustumNear = 1.0f;
        frustumFar = 2.0f;
        frustumLeft = -0.5f;
        frustumRight = 0.5f;
        frustumTop = 0.5f;
        frustumBottom = -0.5f;

        coeffLeft = new float[2];
        coeffRight = new float[2];
        coeffBottom = new float[2];
        coeffTop = new float[2];

        viewPortLeft = 0.0f;
        viewPortRight = 1.0f;
        viewPortTop = 1.0f;
        viewPortBottom = 0.0f;

        planeQuantity = 6;

        worldPlane = new Plane[MAX_WORLD_PLANES];
        for (int i = 0; i < MAX_WORLD_PLANES; i++) {
            worldPlane[i] = new Plane();
        }

        //call the API specific rendering
        onFrustumChange();
        onViewPortChange();
        onFrameChange();

        LoggingSystem.getLogger().log(Level.INFO, "Camera created.");
    }

    /**
     * <code>getFrustumBottom</code> returns the value of the bottom frustum
     * plane.
     * @return the value of the bottom frustum plane.
     */
    public float getFrustumBottom() {
        return frustumBottom;
    }

    /**
     * <code>setFrustumBottom</code> sets the value of the bottom frustum
     * plane.
     * @param frustumBottom the value of the bottom frustum plane.
     */
    public void setFrustumBottom(float frustumBottom) {
        this.frustumBottom = frustumBottom;
        onFrustumChange();
    }

    /**
     * <code>getFrustumFar</code> gets the value of the far frustum plane.
     * @return the value of the far frustum plane.
     */
    public float getFrustumFar() {
        return frustumFar;
    }

    /**
     * <code>setFrustumFar</code> sets the value of the far frustum plane.
     * @param frustumFar the value of the far frustum plane.
     */
    public void setFrustumFar(float frustumFar) {
        this.frustumFar = frustumFar;
        onFrustumChange();
    }

    /**
     * <code>getFrustumLeft</code> gets the value of the left frustum plane.
     * @return the value of the left frustum plane.
     */
    public float getFrustumLeft() {
        return frustumLeft;
    }

    /**
     * <code>setFrustumLeft</code> sets the value of the left frustum plane.
     * @param frustumLeft the value of the left frustum plane.
     */
    public void setFrustumLeft(float frustumLeft) {
        this.frustumLeft = frustumLeft;
        onFrustumChange();
    }

    /**
     * <code>getFrustumNear</code> gets the value of the near frustum plane.
     * @return the value of the near frustum plane.
     */
    public float getFrustumNear() {
        return frustumNear;
    }

    /**
     * <code>setFrustumNear</code> sets the value of the near frustum plane.
     * @param frustumNear the value of the near frustum plane.
     */
    public void setFrustumNear(float frustumNear) {
        this.frustumNear = frustumNear;
        onFrustumChange();
    }

    /**
     * <code>getFrustumRight</code> gets the value of the right frustum plane.
     * @return frustumRight the value of the right frustum plane.
     */
    public float getFrustumRight() {
        return frustumRight;
    }

    /**
     * <code>setFrustumRight</code> sets the value of the right frustum plane.
     * @param frustumRight the value of the right frustum plane.
     */
    public void setFrustumRight(float frustumRight) {
        this.frustumRight = frustumRight;
        onFrustumChange();
    }

    /**
     * <code>getFrustumTop</code> gets the value of the top frustum plane.
     * @return the value of the top frustum plane.
     */
    public float getFrustumTop() {
        return frustumTop;
    }

    /**
     * <code>setFrustumTop</code> sets the value of the top frustum plane.
     * @param frustumTop the value of the top frustum plane.
     */
    public void setFrustumTop(float frustumTop) {
        this.frustumTop = frustumTop;
        onFrustumChange();
    }

    /**
     *
     * <code>getLocation</code> retrieves the location vector of the camera.
     * @see com.jme.renderer.Camera#getLocation()
     * @return the position of the camera.
     */
    public Vector3f getLocation() {
        return location;
    }

    /**
     * <code>getDirection</code> retrieves the direction vector the camera is
     * facing.
     * @see com.jme.renderer.Camera#getDirection()
     * @return the direction the camera is facing.
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     * <code>getLeft</code> retrieves the left axis of the camera.
     * @see com.jme.renderer.Camera#getLeft()
     * @return the left axis of the camera.
     */
    public Vector3f getLeft() {
        return left;
    }

    /** <code>getUp</code> retrieves the up axis of the camera.
     * @see com.jme.renderer.Camera#getUp()
     * @return the up axis of the camera.
     */
    public Vector3f getUp() {
        return up;
    }

    /**
     *
     * <code>setLocation</code> sets the position of the camera.
     * @see com.jme.renderer.Camera#setLocation(com.jme.math.Vector3f)
     * @param location the position of the camera.
     */
    public void setLocation(Vector3f location) {
        this.location = location;
        onFrameChange();
    }

    /**
     * <code>setDirection</code> sets the direction this camera is facing.
     * @see com.jme.renderer.Camera#setDirection(com.jme.math.Vector3f)
     * @param direction the direction this camera is facing.
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
        onFrameChange();
    }

    /**
     * <code>setLeft</code> sets the left axis of this camera.
     * @see com.jme.renderer.Camera#setLeft(com.jme.math.Vector3f)
     * @param left the left axis of this camera.
     */
    public void setLeft(Vector3f left) {
        this.left = left;
        onFrameChange();
    }

    /**
     * <code>setUp</code> sets the up axis of this camera.
     * @see com.jme.renderer.Camera#setUp(com.jme.math.Vector3f)
     * @param up the up axis of this camera.
     */
    public void setUp(Vector3f up) {
        this.up = up;
        onFrameChange();
    }

    /**
     * <code>setAxes</code> sets the axes (left, up and direction) for this
     * camera.
     * @see com.jme.renderer.Camera#setAxes(com.jme.math.Vector3f,com.jme.math.Vector3f,com.jme.math.Vector3f)
     * @param left the left axis of the camera.
     * @param up the up axis of the camera.
     * @param direction the direction the camera is facing.
     */
    public void setAxes(Vector3f left, Vector3f up, Vector3f direction) {
        this.left = left;
        this.up = up;
        this.direction = direction;
        onFrameChange();
    }

    /**
     * <code>setAxes</code> uses a rotational matrix to set the axes of the
     * camera.
     * @see com.jme.renderer.Camera#setAxes(com.jme.math.Matrix3f)
     * @param axes the matrix that defines the orientation of the camera.
     */
    public void setAxes(Quaternion axes) {
        left = axes.getRotationColumn(0, left);
        up = axes.getRotationColumn(1, up);
        direction = axes.getRotationColumn(2, direction);
        onFrameChange();
    }

    /**
     * <code>setFrustum</code> sets the frustum of this camera object.
     * @see com.jme.renderer.Camera#setFrustum(float, float, float, float, float, float)
     * @param near the near plane.
     * @param far the far plane.
     * @param left the left plane.
     * @param right the right plane.
     * @param top the top plane.
     * @param bottom the bottom plane.
     */
    public void setFrustum(
        float near,
        float far,
        float left,
        float right,
        float top,
        float bottom) {

        frustumNear = near;
        frustumFar = far;
        frustumLeft = left;
        frustumRight = right;
        frustumTop = top;
        frustumBottom = bottom;
        onFrustumChange();
    }

    /**
     * <code>setFrame</code> sets the orientation and location of the camera.
     * @see com.jme.renderer.Camera#setFrame(com.jme.math.Vector3f, com.jme.math.Vector3f, com.jme.math.Vector3f, com.jme.math.Vector3f)
     * @param location the point position of the camera.
     * @param left the left axis of the camera.
     * @param up the up axis of the camera.
     * @param direction the facing of the camera.
     */
    public void setFrame(
        Vector3f location,
        Vector3f left,
        Vector3f up,
        Vector3f direction) {

        this.location = location;
        this.left = left;
        this.up = up;
        this.direction = direction;
        onFrameChange();

    }

    /**
     * <code>setFrame</code> sets the orientation and location of the camera.
     * @see com.jme.renderer.Camera#setFrame(com.jme.math.Vector3f, com.jme.math.Matrix3f)
     * @param location the point position of the camera.
     * @param axes the orientation of the camera.
     */
    public void setFrame(Vector3f location, Quaternion axes) {
        this.location = location;
        left = axes.getRotationColumn(0, left);
        up = axes.getRotationColumn(1, up);
        direction = axes.getRotationColumn(2, direction);
        onFrameChange();
    }

    /**
     * <code>update</code> updates the camera parameters by calling
     * <code>onFrustumChange</code>, <code>onViewPortChange</code> and
     * <code>onFrameChange</code>.
     * @see com.jme.renderer.Camera#update()
     */
    public void update() {
        onFrustumChange();
        onViewPortChange();
        onFrameChange();

    }

    /**
     * <code>getPlaneState</code> returns the state of the frustum planes. So
     * checks can be made as to which frustum plane has been examined for
     * culling thus far.
     *
     * @return the current plane state int.
     */
    public int getPlaneState() {
        return planeState;
    }

    /**
     * <code>setPlaneState</code> sets the state to keep track of tested planes
     * for culling.
     * @param planeState the updated state.
     */
    public void setPlaneState(int planeState) {
        this.planeState = planeState;
    }

    /**
     * <code>getViewPortLeft</code> gets the left boundary of the viewport
     * @return the left boundary of the viewport
     */
    public float getViewPortLeft() {
        return viewPortLeft;
    }

    /**
     * <code>setViewPortLeft</code> sets the left boundary of the viewport
     * @param left the left boundary of the viewport
     */
    public void setViewPortLeft(float left) {
        viewPortLeft = left;
    }

    /**
     * <code>getViewPortRight</code> gets the right boundary of the viewport
     * @return the right boundary of the viewport
     */
    public float getViewPortRight() {
        return viewPortRight;
    }

    /**
     * <code>setViewPortRight</code> sets the right boundary of the viewport
     * @param right the right boundary of the viewport
     */
    public void setViewPortRight(float right) {
        viewPortRight = right;
    }

    /**
     * <code>getViewPortTop</code> gets the top boundary of the viewport
     * @return the top boundary of the viewport
     */
    public float getViewPortTop() {
        return viewPortTop;
    }

    /**
     * <code>setViewPortTop</code> sets the top boundary of the viewport
     * @param top the top boundary of the viewport
     */
    public void setViewPortTop(float top) {
        viewPortTop = top;
    }

    /**
     * <code>getViewPortBottom</code> gets the bottom boundary of the viewport
     * @return the bottom boundary of the viewport
     */
    public float getViewPortBottom() {
        return viewPortBottom;
    }

    /**
     * <code>setViewPortBottom</code> sets the bottom boundary of the viewport
     * @param bottom the bottom boundary of the viewport
     */
    public void setViewPortBottom(float bottom) {
        viewPortBottom = bottom;
    }

    /**
     * <code>setViewPort</code> sets the boundaries of the viewport
     * @param left the left boundary of the viewport
     * @param right the right boundary of the viewport
     * @param bottom the bottom boundary of the viewport
     * @param top the top boundary of the viewport
     */
    public void setViewPort(float left, float right, float bottom, float top) {
        setViewPortLeft(left);
        setViewPortRight(right);
        setViewPortBottom(bottom);
        setViewPortTop(top);
    }

    /**
     * <code>culled</code> tests a bounding volume against the planes of the
     * camera's frustum. The frustums planes are set such that the normals
     * all face in towards the viewable scene. Therefore, if the bounding
     * volume is on the negative side of the plane is can be culled out. If
     * the object should be culled (i.e. not rendered) true is returned,
     * otherwise, false is returned. If bound is null, false is returned and
     * the object will not be culled.
     * @param bound the bound to check for culling
     * @return true if the bound should be culled, false otherwise.
     */
    public boolean culled(BoundingVolume bound) {
        if(bound == null) {
            return false;
        }

        int planeCounter = planeQuantity - 1;
        int mask = 1 << planeCounter;

        for (; planeCounter >= 0; planeCounter--, mask >>= 1) {
            if ((planeState & mask) == 0) {
                int side = bound.whichSide(worldPlane[planeCounter]);

                if (side == Plane.NEGATIVE_SIDE) {
                    //object is outside of frustum
                    return true;
                }

                if (side == Plane.POSITIVE_SIDE) {
                    //object is visible on *this* plane, so mark this plane
                    //so that we don't check it for sub nodes.
                    planeState |= mask;
                }
            }
        }

        return false;
    }

    /**
     * <code>onFrustumChange</code> updates the frustum to reflect any changes
     * made to the planes. The new frustum values are kept in a temporary
     * location for use when calculating the new frame. It should be noted
     * that the abstract implementation of this class only updates the data,
     * and does not make any rendering calls. As such, any impelmenting
     * subclass should insure to override this method call it with super and
     * then call the rendering specific code.
     */
    public void onFrustumChange() {
        float nearSquared = frustumNear * frustumNear;
        float leftSquared = frustumLeft * frustumLeft;
        float rightSquared = frustumRight * frustumRight;
        float bottomSquared = frustumBottom * frustumBottom;
        float topSquared = frustumTop * frustumTop;

        float inverseLength =
            1.0f / (float) Math.sqrt(nearSquared + leftSquared);
        coeffLeft[0] = frustumNear * inverseLength;
        coeffLeft[1] = -frustumLeft * inverseLength;

        inverseLength = 1.0f / (float) Math.sqrt(nearSquared + rightSquared);
        coeffRight[0] = -frustumNear * inverseLength;
        coeffRight[1] = frustumRight * inverseLength;

        inverseLength = 1.0f / (float) Math.sqrt(nearSquared + bottomSquared);
        coeffBottom[0] = frustumNear * inverseLength;
        coeffBottom[1] = -frustumBottom * inverseLength;

        inverseLength = 1.0f / (float) Math.sqrt(nearSquared + topSquared);
        coeffTop[0] = -frustumNear * inverseLength;
        coeffTop[1] = frustumTop * inverseLength;
    }

    /**
     * <code>onFrameChange</code> updates the view frame of the camera. It
     * should be noted that the abstract implementation of this class only
     * updates the data, and does not make any rendering calls. As such, any
     * implementing  subclass should insure to override this method call it with
     * super and then call the rendering specific code.
     */
    public void onFrameChange() {
        float dirDotLocation = direction.dot(location);

        // left plane
        Vector3f leftPlaneNormal = worldPlane[LEFT_PLANE].getNormal();
        leftPlaneNormal.x = left.x; leftPlaneNormal.y = left.y; leftPlaneNormal.z = left.z;
        worldPlane[LEFT_PLANE].setNormal(leftPlaneNormal.multLocal(coeffLeft[0]).addLocal(direction.x*coeffLeft[1], direction.y*coeffLeft[1], direction.z*coeffLeft[1]));
        worldPlane[LEFT_PLANE].setConstant(
            location.dot(leftPlaneNormal));

        // right plane
        Vector3f rightPlaneNormal = worldPlane[RIGHT_PLANE].getNormal();
        rightPlaneNormal.x = left.x; rightPlaneNormal.y = left.y; rightPlaneNormal.z = left.z;
        worldPlane[RIGHT_PLANE].setNormal(rightPlaneNormal.multLocal(coeffRight[0]).addLocal(direction.x*coeffRight[1], direction.y*coeffRight[1], direction.z*coeffRight[1]));
        worldPlane[RIGHT_PLANE].setConstant(
            location.dot(rightPlaneNormal));

        // bottom plane
        Vector3f bottomPlaneNormal = worldPlane[BOTTOM_PLANE].getNormal();
        bottomPlaneNormal.x = up.x; bottomPlaneNormal.y = up.y; bottomPlaneNormal.z = up.z;
        worldPlane[BOTTOM_PLANE].setNormal(bottomPlaneNormal.multLocal(coeffBottom[0]).addLocal(direction.x*coeffBottom[1], direction.y*coeffBottom[1], direction.z*coeffBottom[1]));
        worldPlane[BOTTOM_PLANE].setConstant(
            location.dot(bottomPlaneNormal));

        // top plane
        Vector3f topPlaneNormal = worldPlane[TOP_PLANE].getNormal();
        topPlaneNormal.x = up.x; topPlaneNormal.y = up.y; topPlaneNormal.z = up.z;
        worldPlane[TOP_PLANE].setNormal(topPlaneNormal.multLocal(coeffTop[0]).addLocal(direction.x*coeffTop[1], direction.y*coeffTop[1], direction.z*coeffTop[1]));
        worldPlane[TOP_PLANE].setConstant(
            location.dot(topPlaneNormal));

        // far plane
        worldPlane[FAR_PLANE].setNormal(worldPlane[FAR_PLANE].getNormal().negateLocal());
        worldPlane[FAR_PLANE].setConstant(- (dirDotLocation + frustumFar));

        // near plane
        worldPlane[NEAR_PLANE].setNormal(direction);
        worldPlane[NEAR_PLANE].setConstant(dirDotLocation + frustumNear);
    }

}
