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

/**
 * <code>Box</code>
 * @author Mark Powell
 * @version 
 */
public class Box extends TriMesh {
    public Box(Vector3f min, Vector3f max) {
        Vector3f[] verts = new Vector3f[8];
        
        verts[0] = min;
        verts[1] = new Vector3f(max.x,min.y,min.z);
        verts[2] = new Vector3f(max.x,max.y,min.z);
        verts[3] = new Vector3f(min.x,max.y,min.z);
        verts[4] = new Vector3f(min.x,min.y,max.z);
        verts[5] = new Vector3f(max.x,min.y,max.z);
        verts[6] = new Vector3f(min.x,max.y,max.z);
        verts[7] = max;
        
        setVertices(verts);
        
        int[] indices = {0,1,2,
                        0,2,3,
                        4,0,3,
                        4,3,6,
                        5,6,4,
                        5,7,6,
                        1,5,7,
                        1,7,2,
                        4,5,1,
                        4,1,0,
                        3,2,7,
                        3,7,6};
        setIndices(indices);
        
    }
}
