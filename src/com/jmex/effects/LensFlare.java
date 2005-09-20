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

package com.jmex.effects;

import java.util.ArrayList;

import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

/**
 * <code>LensFlare</code> Lens flare effect for jME. Notice that currently, it
 * doesn't do occlusion culling.
 * 
 * The easiest way to use this class is to use the LensFlareFactory to create
 * your LensFlare and then attach it as a child to a lightnode. Optionally you
 * can make it a child or a sibling of an object you wish to have a 'glint' on.
 * In the case of sibling, use
 * setLocalTranslation(sibling.getLocalTranslation()) or something similar to
 * ensure position.
 * 
 * Only FlareQuad objects are acceptable as children.
 * 
 * @author Joshua Slack
 * @version $Id: LensFlare.java,v 1.7 2005-09-20 21:51:38 renanse Exp $
 */

public class LensFlare extends Node {
    private static final long serialVersionUID = 1L;

    private Vector2f midPoint;

    private Vector3f flarePoint;

    private Vector3f scale = new Vector3f(1, 1, 1);

    /**
     * Creates a new LensFlare node without FlareQuad children. Use attachChild
     * to attach FlareQuads.
     * 
     * @param name
     *            The name of the node.
     */
    public LensFlare(String name) {
        super(name);
        init();
    }

    /**
     * Init basic params of Lensflare...
     */
    private void init() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        midPoint = new Vector2f(display.getWidth() >> 1,
                display.getHeight() >> 1);

        // Set the renderstates for lensflare to all defaults...
        for (int i = 0; i < RenderState.RS_MAX_STATE; i++) {
            setRenderState(defaultStateList[i]);
        }

        // Set a alpha blending state.
        AlphaState as1 = display.getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        as1.setEnabled(true);
        setRenderState(as1);

        setRenderQueueMode(Renderer.QUEUE_ORTHO);
        setLightCombineMode(LightState.OFF);
        setTextureCombineMode(TextureState.REPLACE);
    }

    /**
     * Get the flare's reference midpoint, usually the center of the screen.
     * 
     * @return Vector2f
     */
    public Vector2f getMidPoint() {
        return midPoint;
    }

    /**
     * Set the flare's reference midpoint, the center of the screen by default.
     * It may be useful to change this if the whole screen is not used for a
     * scene (for example, if part of the screen is taken up by a status bar.)
     * 
     * @param midPoint
     *            Vector2f
     */
    public void setMidPoint(Vector2f midPoint) {
        this.midPoint = midPoint;
    }

    /**
     * Query intensity of the flares.
     * 
     * @return current value of field intensity
     * @see #setIntensity(float)
     */
    public float getIntensity() {
        return this.intensity;
    }

    /**
     * store the value for field intensity.
     */
    private float intensity = 1;

    /**
     * Set intensity of the flare. Intensity 0 means flares are not visible, 1
     * means maximum size and opacity.
     * 
     * @param value
     *            new value between 0 and 1
     */
    public void setIntensity(final float value) {
        if (value > 1) {
            this.intensity = 1;
        } else if (value < 0) {
            this.intensity = 0;
        } else {
            this.intensity = value;
        }
    }

    /**
     * <code>onDraw</code> checks the node with the camera to see if it should
     * be culled, if not, the node's draw method is called.
     * 
     * @param r
     *            the renderer used for display.
     */
    public void onDraw(Renderer r) {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        midPoint.set(r.getWidth() >> 1, r.getHeight() >> 1);
        // Locate light src on screen x,y
        flarePoint = display.getScreenCoordinates(worldTranslation, flarePoint)
                .subtractLocal(midPoint.x, midPoint.y, 0);
        if (flarePoint.z >= 1.0f) { // if it's behind us
            setCullMode(Spatial.CULL_ALWAYS);
            return;
        } else
            setCullMode(Spatial.CULL_DYNAMIC);
        // define a line from light src to one opposite across the center point
        // draw main flare at src point

        super.onDraw(r);
    }

    /**
     * <code>draw</code> calls the onDraw method for each child maintained by
     * this node.
     * 
     * @param r
     *            the renderer to draw to.
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     */
    public void draw(Renderer r) {
        DisplaySystem display = DisplaySystem.getDisplaySystem();

        // irrisor: compensate for different size renderer
        float intensity = getIntensity();
        if (display.getWidth() != r.getWidth()
                || display.getHeight() != r.getHeight()) {
            float factorX = (float) display.getWidth() / r.getWidth();
            flarePoint.x *= factorX;
            float factorY = (float) display.getHeight() / r.getHeight();
            flarePoint.y *= factorY;
            midPoint.x *= factorX;
            midPoint.y *= factorY;
            scale.x = intensity;
            scale.y = intensity * factorY / factorX;
            scale.z = intensity;
        } else {
            scale.x = intensity;
            scale.y = intensity;
            scale.z = intensity;
        }

        if (rootNode != null) {
            pickResults.clear();
            Vector3f origin = pickRay.getOrigin();
            screenPos.set(flarePoint.x + midPoint.x, flarePoint.y + midPoint.y);
            display.getWorldCoordinates(screenPos, 0, origin); // todo:
                                                                // neccessary?!
            pickRay.getDirection().set(getWorldTranslation()).subtractLocal(
                    origin);
            pickBoundsGeoms.clear();
            rootNode.findPick(pickRay, pickResults);
            this.setIntensity(1);
            occludingTriMeshes.clear();
            for (int i = pickBoundsGeoms.size() - 1; i >= 0; i--) {
                Geometry mesh = (Geometry) pickBoundsGeoms.get(i);
                if (!mesh.getWorldTranslation().equals(
                        this.getWorldTranslation())
                        && !((mesh.getParent().getType() & Spatial.SKY_BOX) != 0)
                        && mesh.getRenderQueueMode() != Renderer.QUEUE_TRANSPARENT) {
                    if ((mesh.getType() & Spatial.TRIMESH) != 0) {
                        occludingTriMeshes.add(mesh);
                    } else {
                        this.setIntensity(0);
                        break;
                    }
                }
            }
            if (occludingTriMeshes.size() > 0 && getIntensity() > 0) {
                checkRealOcclusion();
            }
        }

        for (int x = getQuantity(); --x >= 0;) {
            FlareQuad fq = (FlareQuad) getChild(x);
            fq.setLocalScale(scale);
            fq.updatePosition(flarePoint, midPoint);
        }

        super.draw(r);
    }

    private float maxNotOccludedOffset;

    private float minNotOccludedOffset;

    private Ray secondRay = new Ray();

    private Vector2f secondScreenPos = new Vector2f();

    private Vector3f flaresWorldAxis = new Vector3f();

    private void checkRealOcclusion() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        secondRay.direction.set(pickRay.direction);

        float flareDistanceFromMidPoint = flarePoint.length();
        secondScreenPos.x = screenPos.x + flarePoint.x
                / flareDistanceFromMidPoint;
        secondScreenPos.y = screenPos.y + flarePoint.y
                / flareDistanceFromMidPoint;
        display.getWorldCoordinates(secondScreenPos, 0, flaresWorldAxis);
        flaresWorldAxis.subtractLocal(pickRay.origin).normalizeLocal()
                .multLocal(0.01f);

        final int radius = 25;
        secondRay.origin.set(flaresWorldAxis).multLocal(-radius).addLocal(
                pickRay.origin);
        maxNotOccludedOffset = -radius;
        minNotOccludedOffset = -radius;
        while (isRayCatched(secondRay) && (maxNotOccludedOffset < radius)) {
            secondRay.origin.addLocal(flaresWorldAxis);
            minNotOccludedOffset += 1;
            maxNotOccludedOffset += 1;
        }
        if (maxNotOccludedOffset < radius) {
            do {
                secondRay.origin.addLocal(flaresWorldAxis);
                maxNotOccludedOffset += 1;
            } while (!isRayCatched(secondRay)
                    && (maxNotOccludedOffset < radius));
        }

        setIntensity(Math.abs(maxNotOccludedOffset - minNotOccludedOffset)
                / (radius >> 1));
        // flarePoint.addLocal( flarePoint.normalize().multLocal(
        // (maxNotOccludedOffset+minNotOccludedOffset) ) );
    }

    private boolean isRayCatched(Ray ray) {
        pickTriangles.clear();
        for (int i = occludingTriMeshes.size() - 1; i >= 0; i--) {
            TriMesh triMesh = (TriMesh) occludingTriMeshes.get(i);
            triMesh.findTrianglePick(ray, pickTriangles);
            if (pickTriangles.size() > 0) {
                return true;
            } else {
                // fine - not occluded by this one
            }
        }
        return false;
    }

    /**
     * Calls Node's attachChild after ensuring child is a FlareQuad.
     * 
     * @see com.jme.scene.Node#attachChild(Spatial)
     * @param spat
     *            Spatial
     * @return int
     */
    public int attachChild(Spatial spat) {
        if (!(spat instanceof FlareQuad))
            throw new JmeException(
                    "Only children of type FlareQuad may be attached to LensFlare.");
        return super.attachChild(spat);
    }

    /**
     * getter for field rootNode
     * 
     * @return current value of field rootNode
     */
    public Node getRootNode() {
        return this.rootNode;
    }

    /**
     * store the value for field rootNode
     */
    private Node rootNode;

    /**
     * setter for field rootNode
     * 
     * @param value
     *            new value
     */
    public void setRootNode(final Node value) {
        final Node oldValue = this.rootNode;
        if (oldValue != value) {
            this.rootNode = value;
        }
    }

    private Ray pickRay = new Ray();

    // optimize memory allocation:
    private PickResults pickResults = new BoundingPickResults() {
        public void addPick(Ray ray, Geometry s) {
            pickBoundsGeoms.add(s);
        }
    };

    private Vector2f screenPos = new Vector2f();

    private ArrayList pickTriangles = new ArrayList();

    private ArrayList pickBoundsGeoms = new ArrayList();

    private ArrayList occludingTriMeshes = new ArrayList();
}
