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
package com.jme.scene.shape;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.model.XMLparser.XMLloadable;
import com.jme.bounding.BoundingBox;

/**
 * <code>Box</code> provides an extension of <code>TriMesh</code>. A
 * <code>Box</code> is defined by a minimal point and a maximum point. The
 * eight vertices that make the box are then computed. They are computed in
 * such a way as to generate an axis-aligned box.
 * @author Mark Powell
 * @version $Id: Box.java,v 1.6 2004-06-24 20:37:10 renanse Exp $
 */
public class Box extends TriMesh implements XMLloadable{
    public float xExtent, yExtent, zExtent;
    public Vector3f center;

    public final static Vector3f AXIS_X = new Vector3f(1,0,0);
    public final static Vector3f AXIS_Y = new Vector3f(0,1,0);
    public final static Vector3f AXIS_Z = new Vector3f(0,0,1);

    /**
     * instantiates a new <code>Box</code> object.  All information must be applies later.
     * For internal usage only
     */
    public Box(){
        super("temp");
    }

    /**
     * Constructor instantiates a new <code>Box</code> object.  Center and vertice
     * information must be supplied later.
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     */
    public Box(String name) {
        super(name);
    }

    /**
     * Constructor instantiates a new <code>Box</code> object. The minimum and
     * maximum point are provided. These two points define the shape and size
     * of the box, but not it's orientation or position. You should use the
     * <code>setLocalTranslation</code> and <code>setLocalRotation</code> for
     * those attributes.
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     * @param min the minimum point that defines the box.
     * @param max the maximum point that defines the box.
     */
    public Box(String name, Vector3f min, Vector3f max) {
        super(name);
        setData(min, max, true);
    }

    public Box(String name, Vector3f center, float xExtent, float yExtent, float zExtent) {
        super(name);
        setData(center, xExtent, yExtent, zExtent, true);
    }

    public void setData(Vector3f minPoint, Vector3f maxPoint, boolean updateBuffers) {
        center.set(maxPoint).addLocal(minPoint).multLocal(0.5f);

        float x = maxPoint.x - center.x;
        float y = maxPoint.y - center.y;
        float z = maxPoint.z - center.z;
        setData(center, x, y, z, updateBuffers);
    }

    public void setData(Vector3f center, float xExtent, float yExtent, float zExtent, boolean updateBuffers) {
        if (center != null)
            this.center = center;
        else this.center = new Vector3f(0,0,0);
        this.xExtent = xExtent;
        this.yExtent = yExtent;
        this.zExtent = zExtent;

        if (updateBuffers) {
          setVertexData();
          setNormalData();
          setColorData();
          setTextureData();
          setIndexData();
        }
    }

    /**
     *
     * <code>setVertexData</code> sets the vertex positions that define the
     * box. These eight points are determined from the minimum and maximum
     * point.
     *
     */
    private void setVertexData() {
        Vector3f[] verts = new Vector3f[24];
        Vector3f[] vert = computeVertices(); // returns 8

        //Front
        verts[0] = vert[0];
        verts[1] = vert[1];
        verts[2] = vert[2];
        verts[3] = vert[3];

        //Right
        verts[4] = vert[1];
        verts[5] = vert[4];
        verts[6] = vert[6];
        verts[7] = vert[2];

        //Back
        verts[8] = vert[4];
        verts[9] = vert[5];
        verts[10] = vert[7];
        verts[11] = vert[6];

        //Left
        verts[12] = vert[5];
        verts[13] = vert[0];
        verts[14] = vert[3];
        verts[15] = vert[7];

        //Top
        verts[16] = vert[2];
        verts[17] = vert[6];
        verts[18] = vert[7];
        verts[19] = vert[3];

        //Bottom
        verts[20] = vert[0];
        verts[21] = vert[5];
        verts[22] = vert[4];
        verts[23] = vert[1];
        setVertices(verts);

    }

    /**
     *
     * <code>setNormalData</code> sets the normals of each of the box's planes.
     *
     *
     */
    private void setNormalData() {
        Vector3f[] normals = new Vector3f[24];
        Vector3f front = new Vector3f(0, 0, 1);
        Vector3f right = new Vector3f(1, 0, 0);
        Vector3f back = new Vector3f(0, 0, -1);
        Vector3f left = new Vector3f(-1, 0, 0);
        Vector3f top = new Vector3f(0, 1, 0);
        Vector3f bottom = new Vector3f(0, -1, 0);

        //back
        normals[0] = back;
        normals[1] = back;
        normals[2] = back;
        normals[3] = back;

        //right
        normals[4] = right;
        normals[5] = right;
        normals[6] = right;
        normals[7] = right;

        //front
        normals[8] = front;
        normals[9] = front;
        normals[10] = front;
        normals[11] = front;

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

    }

    /**
     *
     * <code>setTextureData</code> sets the points that define the texture of
     * the box. It's a one-to-one ratio, where each plane of the box has it's
     * own copy of the texture. That is, the texture is repeated one time for
     * each six faces.
     *
     */
    private void setTextureData() {
        Vector2f[] textures = new Vector2f[24];
        Vector2f br = new Vector2f(0, 0);
        Vector2f bl = new Vector2f(1, 0);
        Vector2f tl = new Vector2f(1, 1);
        Vector2f tr = new Vector2f(0, 1);

        textures[0] = bl;
        textures[1] = br;
        textures[2] = tr;
        textures[3] = tl;

        textures[4] = bl;
        textures[5] = br;
        textures[6] = tr;
        textures[7] = tl;

        textures[8] = bl;
        textures[9] = br;
        textures[10] = tr;
        textures[11] = tl;

        textures[12] = bl;
        textures[13] = br;
        textures[14] = tr;
        textures[15] = tl;

        textures[16] = bl;
        textures[17] = br;
        textures[18] = tr;
        textures[19] = tl;

        textures[20] = bl;
        textures[21] = br;
        textures[22] = tr;
        textures[23] = tl;

        setTextures(textures);

    }

    /**
     *
     * <code>setColorData</code> sets the color values for each vertex of the
     * box. Currently, these are set to white.
     *
     */
    private void setColorData() {
        ColorRGBA[] color = new ColorRGBA[24];
        for (int i = 0; i < color.length; i++) {
            color[i] = new ColorRGBA(1, 1, 1, 1);
        }
        setColors(color);
    }

    /**
     *
     * <code>setIndexData</code> sets the indices into the list of vertices,
     * defining all triangles that constitute the box.
     *
     */
    private void setIndexData() {
        int[] indices =
            {
                2,
                1,
                0,
                3,
                2,
                0,
                6,
                5,
                4,
                7,
                6,
                4,
                10,
                9,
                8,
                11,
                10,
                8,
                14,
                13,
                12,
                15,
                14,
                12,
                18,
                17,
                16,
                19,
                18,
                16,
                22,
                21,
                20,
                23,
                22,
                20 };
        setIndices(indices);

    }

    /**
     * <code>clone</code> creates a new Box object containing the same
     * data as this one.
     * @return the new Box
     */
    public Object clone() {
        Box rVal = new Box(name+"_clone", (Vector3f)center.clone(), xExtent, yExtent, zExtent);
        return rVal;
    }

    /**
     *
     * @return a size 8 array of Vectors representing the 8 points of the box.
     */
    public Vector3f[] computeVertices() {

        Vector3f akEAxis[] = {
            AXIS_X.mult(xExtent),
            AXIS_Y.mult(yExtent),
            AXIS_Z.mult(zExtent)
        };

        Vector3f rVal[] = new Vector3f[8];
        rVal[0] = center.subtract(akEAxis[0]).subtractLocal(akEAxis[1]).subtractLocal(akEAxis[2]);
        rVal[1] = center.add(akEAxis[0]).subtractLocal(akEAxis[1]).subtractLocal(akEAxis[2]);
        rVal[2] = center.add(akEAxis[0]).addLocal(akEAxis[1]).subtractLocal(akEAxis[2]);
        rVal[3] = center.subtract(akEAxis[0]).addLocal(akEAxis[1]).subtractLocal(akEAxis[2]);
        rVal[4] = center.add(akEAxis[0]).subtractLocal(akEAxis[1]).addLocal(akEAxis[2]);
        rVal[5] = center.subtract(akEAxis[0]).subtractLocal(akEAxis[1]).addLocal(akEAxis[2]);
        rVal[6] = center.add(akEAxis[0]).addLocal(akEAxis[1]).addLocal(akEAxis[2]);
        rVal[7] = center.subtract(akEAxis[0]).addLocal(akEAxis[1]).addLocal(akEAxis[2]);
        return rVal;
    }


	public Vector3f getCenter(){
		return center;
	}
	public void setCenter(Vector3f aCenter){
		center = aCenter;
	}

    public String writeToXML() {
        StringBuffer returnedValue=new StringBuffer();
        returnedValue.append(center.x).append(' ').append(center.y).append(' ').append(center.z).append(' ');
        returnedValue.append(xExtent).append(' ').append(yExtent).append(' ').append(zExtent).append(' ');
        return returnedValue.toString();
    }

    public Object loadFromXML(String args) {
        String[] parts=args.split(" ");
        setData(new Vector3f(Float.parseFloat(parts[0]),Float.parseFloat(parts[1]),Float.parseFloat(parts[2]))
                , Float.parseFloat(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]), true);
        this.setModelBound(new BoundingBox());
        this.updateModelBound();
        return this;
    }
}
