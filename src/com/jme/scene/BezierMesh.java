/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.system.JmeException;

/**
 * <code>BezierMesh</code> is defined by a collection of
 * <code>BezierPatch</code> objects that define a 4x4 patch of control
 * anchors. These anchors define the curve the surface of the mesh will take.
 * The patch also contains information about it's detail level, which defines
 * how smooth the mesh will be.
 * 
 * @author Mark Powell
 * @version $Id: BezierMesh.java,v 1.11 2004-04-26 14:39:57 mojomonkey Exp $
 */
public class BezierMesh extends TriMesh {

    private BezierPatch patch;

    /**
     * Constructor creates a default <code>BezierMesh</code> object.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     */
    public BezierMesh(String name) {
        super(name);
    }

    /**
     * Constructor creates a new <code>BezierMesh</code> object with the given
     * <code>BezierPatch</code>. The mesh is then automatically tessellated.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     * @param patch
     *            the <code>BezierPatch</code> used to define this mesh.
     */
    public BezierMesh(String name, BezierPatch patch) {
        super(name);
        this.patch = patch;
        tessellate();
    }

    /**
     * 
     * <code>setPatch</code> sets the <code>BezierPatch</code> of the mesh.
     * It is then tessellated.
     * 
     * @param patch
     *            the patch to use for this mesh.
     */
    public void setPatch(BezierPatch patch) {
        this.patch = patch;
        tessellate();
    }

    /**
     * 
     * <code>tessellate</code> generates the <code>BezierMesh</code>
     * vertices from the supplied patch and detail level. This method is called
     * when patch is set, and therefore, should normally have to be called.
     * However, if patch is changed externally, and you wish to update the mesh,
     * a call to <code>tessellate</code> is appropriate.
     *  
     */
    public void tessellate() {
        if (patch == null) { return; }
        int u = 0, v;
        float py, px, pyold;
        int detailLevel = patch.getDetailLevel();

        Vector3f[] temp = new Vector3f[4];
        Vector3f[] last = new Vector3f[detailLevel + 1];

        temp[0] = patch.getAnchor(0, 3);
        temp[1] = patch.getAnchor(1, 3);
        temp[2] = patch.getAnchor(2, 3);
        temp[3] = patch.getAnchor(3, 3);

        for (v = 0; v <= detailLevel; v++) {
            px = ((float) v) / ((float) detailLevel);
            last[v] = calcBerstein(px, temp);
        }

        u = 1;
        Vector3f[] vertex = new Vector3f[((detailLevel * 2) + 2) * detailLevel];
        Vector2f[] texture = new Vector2f[((detailLevel * 2) + 2) * detailLevel];
        Vector3f[] normal = new Vector3f[vertex.length];
        int[] indices = new int[detailLevel * detailLevel * 6];

        int count = 0;
        for (u = 1; u <= detailLevel; u++) {

            py = ((float) u) / ((float) detailLevel);
            pyold = (u - 1.0f) / (detailLevel);
            temp[0] = calcBerstein(py, patch.getAnchors()[0]);
            temp[1] = calcBerstein(py, patch.getAnchors()[1]);
            temp[2] = calcBerstein(py, patch.getAnchors()[2]);
            temp[3] = calcBerstein(py, patch.getAnchors()[3]);

            for (v = 0; v <= detailLevel; v++) {
                px = ((float) v) / ((float) detailLevel);
                texture[count] = new Vector2f(pyold, px);
                vertex[count] = new Vector3f(last[v].x, last[v].y, last[v].z);
                count++;
                last[v] = calcBerstein(px, temp);
                texture[count] = new Vector2f(py, px);
                vertex[count] = new Vector3f(last[v].x, last[v].y, last[v].z);
                count++;
            }

        }

        int index = -1;
        for (int i = 0; i < detailLevel * detailLevel * 6; i = i + 6) {

            index++;
            if (i > 0 && i % (detailLevel * 6) == 0) {
                index += 1;
            }

            indices[i] = 2 * index;
            indices[(i + 1)] = (2 * index) + 1;
            indices[(i + 2)] = (2 * index) + 2;

            indices[(i + 3)] = (2 * index) + 3;
            indices[(i + 4)] = (2 * index) + 2;
            indices[(i + 5)] = (2 * index) + 1;
        }

        int normalIndex = 0;
        for (int i = 0; i < detailLevel; i++) {
            for (int j = 0; j < (detailLevel * 2) + 2; j++) {
                if (j % 2 == 0) {
                    if (i == 0) {
                        if (j < (detailLevel * 2)) {
                            //right cross up
                            normal[normalIndex] = vertex[normalIndex + 1]
                                    .subtract(vertex[normalIndex])
                                    .cross(
                                            vertex[normalIndex + 2]
                                                    .subtract(vertex[normalIndex]))
                                    .normalizeLocal();
                        } else {
                            //down cross right
                            normal[normalIndex] = vertex[normalIndex - 2]
                                    .subtract(vertex[normalIndex])
                                    .cross(
                                            vertex[normalIndex + 1]
                                                    .subtract(vertex[normalIndex]))
                                    .normalizeLocal();
                        }
                    } else {
                        normal[normalIndex] = (Vector3f) normal[normalIndex
                                - (detailLevel * 2 + 1)].clone();
                    }
                } else {
                    if (j < (detailLevel * 2) + 1) {
                        //up cross left
                        normal[normalIndex] = vertex[normalIndex + 2].subtract(
                                vertex[normalIndex]).cross(
                                vertex[normalIndex - 1]
                                        .subtract(vertex[normalIndex]))
                                .normalizeLocal();
                    } else {
                        //left cross down
                        normal[normalIndex] = vertex[normalIndex - 1].subtract(
                                vertex[normalIndex]).cross(
                                vertex[normalIndex - 2]
                                        .subtract(vertex[normalIndex]))
                                .normalizeLocal();
                    }
                }
                normalIndex++;
            }
        }

        setVertices(vertex);
        setTextures(texture);
        setIndices(indices);
        setNormals(normal);
    }

    /**
     * 
     * <code>calcBerstein</code> calculates the Berstein number for the given
     * u and control points.
     * 
     * @param u
     *            the u value.
     * @param p
     *            the control points.
     * @return the Berstein number.
     */
    private Vector3f calcBerstein(float u, Vector3f[] p) {
        if (p.length != 4) { throw new JmeException(
                "Point parameter must be length 4."); }
        Vector3f a = p[0].mult((float) Math.pow(u, 3));
        Vector3f b = p[1].mult(3 * (float) Math.pow(u, 2) * (1 - u));
        Vector3f c = p[2].mult(3 * u * (float) Math.pow((1 - u), 2));
        Vector3f d = p[3].mult((float) Math.pow((1 - u), 3));

        return (a.addLocal(b)).addLocal((c.addLocal(d)));
    }
}