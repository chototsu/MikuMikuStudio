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

package com.jme.util.geom;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.OrientedBox;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;

/**
 * <code>Debugger</code> provides tools for viewing scene data such as
 * boundings and normals.
 * 
 * @author Joshua Slack
 * @author Emond Papegaaij (normals ideas and previous normal tool)
 * @version $Id: Debugger.java,v 1.14 2005-09-26 17:12:23 renanse Exp $
 */
public final class Debugger {

    // -- **** METHODS FOR DRAWING BOUNDING VOLUMES **** -- //
    
    private static final Sphere boundingSphere = new Sphere("bsphere", 10, 10, 1);;
    static { 
        boundingSphere.setRenderQueueMode(Renderer.QUEUE_SKIP);
    }
    private static final Box boundingBox = new Box("bbox", new Vector3f(), 1, 1, 1);
    static { 
        boundingBox.setRenderQueueMode(Renderer.QUEUE_SKIP);
    }
    private static final OrientedBox boundingOB = new OrientedBox("bobox");
    static { 
        boundingOB.setRenderQueueMode(Renderer.QUEUE_SKIP);
    }
    
    private static WireframeState boundsWireState;
    private static ZBufferState boundsZState;

    /**
     * <code>drawBounds</code> draws the bounding volume for a given Spatial
     * and its children.
     * 
     * @param spat
     *            the Spatial to draw boundings for.
     * @param r
     *            the Renderer to use to draw the bounding.
     */
    public static void drawBounds(Spatial spat, Renderer r) {
        drawBounds(spat, r, true);
    }
    
    /**
     * <code>drawBounds</code> draws the bounding volume for a given Spatial
     * and optionally its children.
     * 
     * @param spat
     *            the Spatial to draw boundings for.
     * @param r
     *            the Renderer to use to draw the bounding.
     * @param doChildren
     *            if true, boundings for any children will also be drawn
     */
    public static void drawBounds(Spatial spat, Renderer r, boolean doChildren) {
        if (spat == null) return;

        if (boundsWireState == null) {
            boundsWireState = r.createWireframeState();
            boundsZState = r.createZBufferState();
        }
        
        if (spat.getWorldBound() != null && spat.getCullMode() != Spatial.CULL_ALWAYS) {
            int state = r.getCamera().getPlaneState();
            if (r.getCamera().contains(spat.getWorldBound()) != Camera.OUTSIDE_FRUSTUM)
                drawBounds(spat.getWorldBound(), r);
            else
                doChildren = false;
            r.getCamera().setPlaneState(state);
        }
        if (doChildren && (spat.getType() & Spatial.NODE) != 0) {
            Node n = (Node)spat;
            if (n.getChildren() != null) {
                for (int i = n.getChildren().size(); --i >= 0; )
                    drawBounds(n.getChild(i), r, true);
            }
        }
    }
    
    private static void drawBounds(BoundingVolume bv, Renderer r) {
        setBoundsStates();
        switch (bv.getType()) {
            case BoundingVolume.BOUNDING_BOX:
                drawBoundingBox((BoundingBox) bv, r);
                break;
            case BoundingVolume.BOUNDING_SPHERE:
                drawBoundingSphere((BoundingSphere) bv, r);
                break;
            case BoundingVolume.BOUNDING_OBB:
                drawOBB((OrientedBoundingBox) bv, r);
                break;
            default:
                break;
        }
    }

    private static void drawBoundingSphere(BoundingSphere sphere, Renderer r) {
        boundingSphere.getCenter().set(sphere.getCenter());
        boundingSphere.setData(boundingSphere.getCenter(), 10, 10, sphere.getRadius()); // pass back bs center to prevent accidently data access.
        boundingSphere.draw(r);
    }

    private static void drawBoundingBox(BoundingBox box, Renderer r) {
        boundingBox.getCenter().set(box.getCenter());
        boundingBox.setData(boundingBox.getCenter(), box.xExtent, box.yExtent, box.zExtent); 
        boundingBox.draw(r);
    }

    private static void drawOBB(OrientedBoundingBox box, Renderer r) {
        boundingOB.getCenter().set(box.getCenter());
        boundingOB.getxAxis().set(box.getXAxis());
        boundingOB.getyAxis().set(box.getYAxis());
        boundingOB.getzAxis().set(box.getZAxis());
        boundingOB.getExtent().set(box.getExtent());
        boundingOB.computeInformation();
        boundingOB.draw(r);
    }
    
    private static void setBoundsStates() {
        for (int x = 0; x < Spatial.defaultStateList.length; x++) {
            if (x != RenderState.RS_ZBUFFER && x != RenderState.RS_WIREFRAME)
                Spatial.defaultStateList[x].apply();
        }
        
        boundsWireState.apply();
        boundsZState.apply();
        
        Geometry.clearCurrentStates();
    }

    // -- **** METHODS FOR DRAWING NORMALS **** -- //
    

    private static final Line normalLines = new Line("normLine");
    static {
        normalLines.setLineWidth(3.0f);
        normalLines.setMode(Line.SEGMENTS);
        normalLines.setVertexBuffer(BufferUtils.createVector3Buffer(1000));
    }
    private static final Vector3f _normalVect = new Vector3f();
    private static ZBufferState normZState;
    public static ColorRGBA NORMAL_COLOR = ColorRGBA.red;
    
    
    /**
     * <code>drawNormals</code> draws lines representing normals for a given Spatial
     * and its children.
     * 
     * @param spat
     *            the Spatial to draw normals for.
     * @param r
     *            the Renderer to use to draw the normals.
     */
    public static void drawNormals(Spatial spat, Renderer r) {
        drawNormals(spat, r, 1.0f, true);
    }

    /**
     * <code>drawNormals</code> draws the normals for a given Spatial
     * and optionally its children.
     * 
     * @param spat
     *            the Spatial to draw normals for.
     * @param r
     *            the Renderer to use to draw the normals.
     * @param size
     *            the length of the drawn normal (default is 1.0f).
     * @param doChildren
     *            if true, normals for any children will also be drawn
     */
    public static void drawNormals(Spatial spat, Renderer r, float size, boolean doChildren) {
        if (spat == null) return;

        if (normZState == null) {
            normZState = r.createZBufferState();
        }

        int state = r.getCamera().getPlaneState();
        if (spat.getWorldBound() != null && r.getCamera().contains(spat.getWorldBound()) == Camera.OUTSIDE_FRUSTUM) {
            r.getCamera().setPlaneState(state);
            return;
        }
        r.getCamera().setPlaneState(state);
        if ((spat.getType() & Spatial.GEOMETRY) != 0 && spat.getCullMode() != Spatial.CULL_ALWAYS) {
            Geometry g = (Geometry)spat;
            FloatBuffer norms = g.getNormalBuffer();
            FloatBuffer verts = g.getVertexBuffer();
            if (norms != null && verts != null  && norms.capacity() == verts.capacity()) {
                FloatBuffer lineVerts = normalLines.getVertexBuffer();
                if (lineVerts.capacity() < (3 * (2 * g.getVertQuantity()))) {
                    normalLines.setVertexBuffer(null);
                    System.gc();
                    lineVerts = BufferUtils.createVector3Buffer(g.getVertQuantity() * 2);
                    normalLines.setVertexBuffer(lineVerts);
                } else {
                    normalLines.setVertQuantity(2 * g.getVertQuantity());
                    lineVerts.clear();
                }
                IntBuffer lineInds = normalLines.getIndexBuffer();
                if (lineInds == null || lineInds.capacity() < (normalLines.getVertQuantity())) {
                    normalLines.setIndexBuffer(null);
                    System.gc();
                    lineInds = BufferUtils.createIntBuffer(g.getVertQuantity() * 2);
                    normalLines.setIndexBuffer(lineInds);
                } else {
                    lineInds.clear();
                    lineInds.limit(normalLines.getVertQuantity());
                }
                
                verts.rewind();
                norms.rewind();
                lineVerts.rewind();
                lineInds.rewind();
                
                for (int x = 0; x < g.getVertQuantity(); x++ ) {
                    _normalVect.set(verts.get(), verts.get(), verts.get());
                    lineVerts.put(_normalVect.x);
                    lineVerts.put(_normalVect.y);
                    lineVerts.put(_normalVect.z);
                    lineInds.put(x*2);
                    
                    _normalVect.addLocal(norms.get()*size, norms.get()*size, norms.get()*size);
                    lineVerts.put(_normalVect.x);
                    lineVerts.put(_normalVect.y);
                    lineVerts.put(_normalVect.z);
                    lineInds.put((x*2)+1);
                }
                
                normalLines.setDefaultColor(NORMAL_COLOR);
                setNormStates();
                normalLines.setLocalTranslation(g.getWorldTranslation());
                normalLines.setLocalScale(g.getWorldScale());
                normalLines.setLocalRotation(g.getWorldRotation());
                normalLines.draw(r);
            }
        }
        
        if (doChildren && (spat.getType() & Spatial.NODE) != 0) {
            Node n = (Node)spat;
            if (n.getChildren() != null) {
                for (int i = n.getChildren().size(); --i >= 0; )
                    drawNormals(n.getChild(i), r, size, true);
            }
        }
    }

    
    private static void setNormStates() {
        for (int x = 0; x < Spatial.defaultStateList.length; x++) {
            if (x != RenderState.RS_ZBUFFER)
                Spatial.defaultStateList[x].apply();
        }
        
        normZState.apply();
        
        Geometry.clearCurrentStates();
    }
}
