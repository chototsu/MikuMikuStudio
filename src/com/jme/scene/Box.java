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
package com.jme.scene;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * <code>Box</code> provides an extension of <code>TriMesh</code> 
 * @author Mark Powell
 * @version $Id: Box.java,v 1.2 2003-10-31 22:02:54 mojomonkey Exp $
 */
public class Box extends TriMesh {
    public Box(Vector3f min, Vector3f max) {
        Vector3f[] verts = new Vector3f[24];
        Vector3f vert0 = min;
        Vector3f vert1 = new Vector3f(max.x,min.y,min.z);
        Vector3f vert2 = new Vector3f(max.x,max.y,min.z);
        Vector3f vert3 = new Vector3f(min.x,max.y,min.z);
        Vector3f vert4 = new Vector3f(min.x,min.y,max.z);
        Vector3f vert5 = new Vector3f(max.x,min.y,max.z);
        Vector3f vert6 = new Vector3f(min.x,max.y,max.z);
        Vector3f vert7 = max;
        
        //Front
        verts[0] = vert0;
        verts[1] = vert1;
        verts[2] = vert2;
        verts[3] = vert3;
        
        //Right
        verts[4] = vert1; 
        verts[5] = vert4;
        verts[6] = vert3;
        verts[7] = vert6;
        
        //Back
        verts[8] = vert4;
        verts[9] = vert5;
        verts[10] = vert6;
        verts[11] = vert7;
        
        //Left
        verts[12] = vert2;
        verts[13] = vert7;
        verts[14] = vert0;
        verts[15] = vert5;
        
        //Top
        verts[16] = vert3;
        verts[17] = vert6;
        verts[18] = vert2;
        verts[19] = vert7;
        
        //Bottom
        verts[20] = vert0;
        verts[21] = vert5;
        verts[22] = vert1;
        verts[23] = vert4;
        
        setVertices(verts);
        
        Vector3f[] normals = new Vector3f[24];
        Vector3f front = new Vector3f(0,0,-1);
        Vector3f right = new Vector3f(1,0,0);
        Vector3f back = new Vector3f(0,0,1);
        Vector3f left = new Vector3f(-1,0,0);
        Vector3f top = new Vector3f(0,1,0);
        Vector3f bottom = new Vector3f(0,-1,0);
        
        //front
        normals[0] = front;
        normals[1] = front;
        normals[2] = front;
        normals[3] = front;
        
        //right
        normals[4] = right;
        normals[5] = right;
        normals[6] = right;
        normals[7] = right;
        
        //back
        normals[8] = back;
        normals[9] = back;
        normals[10] = back;
        normals[11] = back;
        
        //left
        normals[12] = left;
        normals[13] = left;
        normals[14] = left;
        normals[15] = left;
        
        //top
        normals[16] = top;
        normals[17] = top;
        normals[18] = top;
        normals[19] = top;
        
        //bottom
        normals[20] = bottom;
        normals[21] = bottom;
        normals[22] = bottom;
        normals[23] = bottom;
        
        setNormals(normals);
        
        int[] indices = {
            0,1,3,
            0,3,2,
            4,5,7,
            4,7,6,
            8,9,11,
            8,11,10,
            12,13,15,
            12,15,14,
            16,17,19,
            16,19,18,
            20,21,23,
            20,23,22
        };
        
        setIndices(indices);
        
        ColorRGBA[] color = new ColorRGBA[24];
        for(int i = 0; i < color.length; i++) {
            color[i] = new ColorRGBA(1,1,1,1);
        }
        
        this.setColors(color);
        
    }
}
