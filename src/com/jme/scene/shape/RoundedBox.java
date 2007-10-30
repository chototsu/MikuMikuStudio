/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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
package com.jme.scene.shape;

import java.io.IOException;
import java.nio.FloatBuffer;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
*
* @author Pirx
*/
public class RoundedBox extends TriMesh  implements Savable {
   
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector3f extent = new Vector3f(0.5f, 0.5f, 0.5f);
    private Vector3f border = new Vector3f(0.05f, 0.05f, 0.05f);
    private Vector3f slope = new Vector3f(0.02f, 0.02f, 0.02f);
   
    /** Creates a new instance of RoundedBox */
    public RoundedBox(String name) {
        super(name);
        setData();
    }
   
    public RoundedBox(String name, Vector3f extent) {
        super(name);
        this.extent = extent.subtract(slope);
        setData();
    }
   
    public RoundedBox(String name, Vector3f extent, Vector3f border, Vector3f slope) {
        super(name);
        this.extent = extent.subtract(slope);
        this.border = border;
        this.slope = slope;
        setData();
    }

    /**
     * Default ctor for restoring (Savable/Serializable).
     */
    public RoundedBox() {
    }

    private void setData() {
        setVertexAndNormalData();
        setTextureData();
        setIndexData();
    }
   
   
    private void put(FloatBuffer fb, FloatBuffer nb, Vector3f vec) {
        fb.put(vec.x).put(vec.y).put(vec.z);
        Vector3f v = vec.normalize();
        nb.put(v.x).put(v.y).put(v.z);
    }
   
    private void setVertexAndNormalData() {
        TriangleBatch batch = getBatch(0);
        batch.setVertexBuffer(BufferUtils.createVector3Buffer(batch.getVertexBuffer(), 48));
        batch.setNormalBuffer(BufferUtils.createVector3Buffer(48));
        batch.setVertexCount(48);
        Vector3f[] vert = computeVertices(); // returns 32
        FloatBuffer vb = batch.getVertexBuffer();
        FloatBuffer nb = batch.getNormalBuffer();
       
        //bottom
        put(vb, nb, vert[0]); put(vb, nb, vert[1]); put(vb, nb, vert[2]); put(vb, nb, vert[3]);
        put(vb, nb, vert[8]); put(vb, nb, vert[9]); put(vb, nb, vert[10]); put(vb, nb, vert[11]);
       
        //front
        put(vb, nb, vert[1]); put(vb, nb, vert[0]); put(vb, nb, vert[5]); put(vb, nb, vert[4]);
        put(vb, nb, vert[13]); put(vb, nb, vert[12]);  put(vb, nb, vert[15]); put(vb, nb, vert[14]);
       
        //right
         put(vb, nb, vert[3]); put(vb, nb, vert[1]); put(vb, nb, vert[7]);put(vb, nb, vert[5]);
         put(vb, nb, vert[17]); put(vb, nb, vert[16]); put(vb, nb, vert[19]);put(vb, nb, vert[18]);
       
        //back
        put(vb, nb, vert[2]); put(vb, nb, vert[3]); put(vb, nb, vert[6]); put(vb, nb, vert[7]);
        put(vb, nb, vert[20]); put(vb, nb, vert[21]); put(vb, nb, vert[22]); put(vb, nb, vert[23]);
       
        //left
        put(vb, nb, vert[0]); put(vb, nb, vert[2]); put(vb, nb, vert[4]); put(vb, nb, vert[6]);
        put(vb, nb, vert[24]); put(vb, nb, vert[25]); put(vb, nb, vert[26]); put(vb, nb, vert[27]);
       
        //top
         put(vb, nb, vert[5]); put(vb, nb, vert[4]);  put(vb, nb, vert[7]); put(vb, nb, vert[6]);
         put(vb, nb, vert[29]); put(vb, nb, vert[28]);  put(vb, nb, vert[31]); put(vb, nb, vert[30]);
    }
   
    private void setTextureData() {
        TriangleBatch batch = getBatch(0);
        if (batch.getTextureBuffers().get(0) == null) {
            batch.getTextureBuffers().set(0,BufferUtils.createVector2Buffer(48));
            FloatBuffer tex = batch.getTextureBuffers().get(0);
           
            float[][] ratio = new float[][] {
                {0.5f * border.x / (extent.x + slope.x), 0.5f * border.z / (extent.z + slope.z)},
                {0.5f * border.x / (extent.x + slope.x), 0.5f * border.y / (extent.y + slope.y)},
                {0.5f * border.z / (extent.z + slope.z), 0.5f * border.y / (extent.y + slope.y)},
                {0.5f * border.x / (extent.x + slope.x), 0.5f * border.y / (extent.y + slope.y)},
                {0.5f * border.z / (extent.z + slope.z), 0.5f * border.y / (extent.y + slope.y)},
                {0.5f * border.x / (extent.x + slope.x), 0.5f * border.z / (extent.z + slope.z)},
            };
           
            for (int i = 0; i < 6; i++) {
                tex.put(1).put(0);
                tex.put(0).put(0);
                tex.put(1).put(1);
                tex.put(0).put(1);
                tex.put(1 - ratio[i][0]).put(0 + ratio[i][1]);
                tex.put(0 + ratio[i][0]).put(0 + ratio[i][1]);
                tex.put(1 - ratio[i][0]).put(1 - ratio[i][1]);
                tex.put(0 + ratio[i][0]).put(1 - ratio[i][1]);
            }
        }
    }
   
    private void setIndexData() {
        TriangleBatch batch = getBatch(0);
        if (batch.getIndexBuffer() == null) {
            int[] indices = new int[180];
            int[] data = new int[]{0,4,1, 1,4,5, 1,5,3, 3,5,7, 3,7,2, 2,7,6,
               2,6,0, 0,6,4, 4,6,5, 5,6,7};
            for (int i = 0; i < 6; i++) {
                for (int n = 0; n < 30; n++) {
                    indices[30*i + n] = 8*i + data[n];
                }
            }
            batch.setIndexBuffer(BufferUtils.createIntBuffer(indices));
        }
    }
   
    public Vector3f[] computeVertices() {
        return  new Vector3f[] {
            //Cube
            new Vector3f(-extent.x, -extent.y, extent.z), //0
            new Vector3f(extent.x, -extent.y, extent.z), //1
            new Vector3f(-extent.x, -extent.y, -extent.z), //2
            new Vector3f(extent.x, -extent.y, -extent.z), //3
            new Vector3f(-extent.x, extent.y, extent.z), //4
            new Vector3f(extent.x, extent.y, extent.z), //5
            new Vector3f(-extent.x, extent.y, -extent.z), //6
            new Vector3f(extent.x, extent.y, -extent.z), //7
            //bottom
            new Vector3f(-extent.x+border.x, -extent.y-slope.y, extent.z-border.z), //8 (0)
            new Vector3f(extent.x-border.x, -extent.y-slope.y, extent.z-border.z), //9 (1)
            new Vector3f(-extent.x+border.x, -extent.y-slope.y, -extent.z+border.z), //10 (2)
            new Vector3f(extent.x-border.x, -extent.y-slope.y, -extent.z+border.z), //11 (3)
            //front
            new Vector3f(-extent.x+border.x, -extent.y+border.y, extent.z+slope.z), //12 (0)
            new Vector3f(extent.x-border.x, -extent.y+border.y, extent.z+slope.z), //13 (1)
            new Vector3f(-extent.x+border.x, extent.y-border.y, extent.z+slope.z), //14 (4)
            new Vector3f(extent.x-border.x, extent.y-border.y, extent.z+slope.z), //15 (5)
            //right
            new Vector3f(extent.x+slope.x, -extent.y+border.y, extent.z-border.z), //16 (1)
            new Vector3f(extent.x+slope.x, -extent.y+border.y, -extent.z+border.z), //17 (3)
            new Vector3f(extent.x+slope.x, extent.y-border.y, extent.z-border.z), //18 (5)
            new Vector3f(extent.x+slope.x, extent.y-border.y, -extent.z+border.z), //19 (7)
            //back
            new Vector3f(-extent.x+border.x, -extent.y+border.y, -extent.z-slope.z), //20 (2)
            new Vector3f(extent.x-border.x, -extent.y+border.y, -extent.z-slope.z), //21 (3)
            new Vector3f(-extent.x+border.x, extent.y-border.y, -extent.z-slope.z), //22 (6)
            new Vector3f(extent.x-border.x, extent.y-border.y, -extent.z-slope.z), //23 (7)
            //left
            new Vector3f(-extent.x-slope.x, -extent.y+border.y, extent.z-border.z), //24 (0)
            new Vector3f(-extent.x-slope.x, -extent.y+border.y, -extent.z+border.z), //25 (2)
            new Vector3f(-extent.x-slope.x, extent.y-border.y, extent.z-border.z), //26 (4)
            new Vector3f(-extent.x-slope.x, extent.y-border.y, -extent.z+border.z), //27 (6)
            //top
            new Vector3f(-extent.x+border.x, extent.y+slope.y, extent.z-border.z), //28 (4)
            new Vector3f(extent.x-border.x, extent.y+slope.y, extent.z-border.z), //29 (5)
            new Vector3f(-extent.x+border.x, extent.y+slope.y, -extent.z+border.z), //30 (6)
            new Vector3f(extent.x-border.x, extent.y+slope.y, -extent.z+border.z), //31 (7)
        };
    }
   
    /**
     * <code>clone</code> creates a new RoundedBox object containing the same data as
     * this one.
     *
     * @return the new Box
     */
    public Object clone() {
        return new RoundedBox(getName() + "_clone",
                (Vector3f) extent.clone(),
                (Vector3f) border.clone(),
                (Vector3f) slope.clone());
    }
   
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(extent, "extent", Vector3f.ZERO);
        capsule.write(border, "border", Vector3f.ZERO);
        capsule.write(slope, "slope", Vector3f.ZERO);
    }
   
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        extent.set((Vector3f) capsule.readSavable("extent", Vector3f.ZERO.clone()));
        border.set((Vector3f) capsule.readSavable("border", Vector3f.ZERO.clone()));
        slope.set((Vector3f) capsule.readSavable("slope", Vector3f.ZERO.clone()));
    }
}