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

package jme.geometry.model;

/**
 * A Triangle is a polygon in MD3D.  It holds indexes to the three vertices
 * in the model, as well as indexes to its normals.
 *
 * @author naj
 * @version 0.1
 */
public class Triangle {

    /**
     * The flags in MS3D.
     */
    public int flags;

    /**
     * Vertex 1.
     */
    public int vertexIndex1;

    /**
     * Vertex 2.
     */
    public int vertexIndex2;

    /**
     * Vertex 3.
     */
    public int vertexIndex3;

    /**
     * Normal 1.
     */
    public int normalIndex1;

    /**
     * Normal 2.
     */
    public int normalIndex2;

    /**
     * Normal 3.
     */
    public int normalIndex3;

    /**
     * The MS3D smoothing group.
     */
    public int smoothingGroup;

    /**
     * Creates a triangle with the vertices and normals given.
     *
     * @param flags the MS3D flags.
     * @param vertexIndex1 vertex 1.
     * @param vertexIndex2 vertex 2.
     * @param vertexIndex3 vertex 3.
     * @param normalIndex1 normal 1.
     * @param normalIndex2 normal 2.
     * @param normalIndex3 normal 3.
     * @param smoothingGroup the MS3D smoothing group.
     */
    public Triangle(int flags, int vertexIndex1, int vertexIndex2, int vertexIndex3, int normalIndex1, int normalIndex2, int normalIndex3, int smoothingGroup) {
        this.flags = flags;
        this.vertexIndex1 = vertexIndex1;
        this.vertexIndex2 = vertexIndex2;
        this.vertexIndex3 = vertexIndex3;
        this.normalIndex1 = normalIndex1;
        this.normalIndex2 = normalIndex2;
        this.normalIndex3 = normalIndex3;
        this.smoothingGroup = smoothingGroup;
    }

}