/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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

package com.jme.intersection;

import java.util.ArrayList;

import com.jme.math.Ray;
import com.jme.scene.Geometry;

/**
 * 
 * PickData contains information about a picking operation (or Ray/Volume
 * intersection). This data contains the mesh the ray hit, the triangles it hit,
 * and the ray itself.
 * 
 * @author Mark Powell
 */
public class PickData {

    private Ray ray;

    private Geometry targetMesh;

    private ArrayList targetTris;

    public PickData(Ray ray, Geometry targetMesh) {
        this(ray, targetMesh, null);
    }

    /**
     * instantiates a new PickData object.
     * 
     * @param mesh
     *            the mesh the relevant TriMesh collided with.
     * @param source
     *            the triangles of the relevant TriMesh that made contact.
     * @param target
     *            the triangles of the second mesh that made contact.
     */
    public PickData(Ray ray, Geometry targetMesh, ArrayList targetTris) {
        this.ray = ray;
        this.targetMesh = targetMesh;
        this.targetTris = targetTris;
    }

    /**
     * 
     * <code>getTargetMesh</code> returns the geometry that was hit by the
     * ray.
     * 
     * @return the geometry hit by the ray.
     */
    public Geometry getTargetMesh() {
        return targetMesh;
    }

    /**
     * 
     * <code>setTargetMesh</code> sets the geometry hit by the ray.
     * 
     * @param mesh
     *            the geometry hit by the ray.
     */
    public void setTargetMesh(Geometry mesh) {
        this.targetMesh = mesh;
    }

    /**
     * @return Returns the target.
     */
    public ArrayList getTargetTris() {
        return targetTris;
    }

    /**
     * @param target
     *            The target to set.
     */
    public void setTargetTris(ArrayList target) {
        this.targetTris = target;
    }

    /**
     * @return Returns the ray.
     */
    public Ray getRay() {
        return ray;
    }

    /**
     * @param ray
     *            The ray to set.
     */
    public void setRay(Ray ray) {
        this.ray = ray;
    }
}