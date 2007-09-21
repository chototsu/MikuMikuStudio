/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

import com.jme.math.Vector2f;
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
 * Started Date: Aug 22, 2004 <br>
 * <br>
 * This primitive represents a box that has options to orient it acording to its
 * X/Y/Z axis. It is used to create an OrientedBoundingBox mostly.
 * 
 * @author Jack Lindamood
 */
public class OrientedBox extends TriMesh {
	private static final long serialVersionUID = 1L;

	/** Center of the Oriented Box. */
	protected Vector3f center;

	/** X axis of the Oriented Box. */
	protected Vector3f xAxis = new Vector3f(1, 0, 0);

	/** Y axis of the Oriented Box. */
	protected Vector3f yAxis = new Vector3f(0, 1, 0);

	/** Z axis of the Oriented Box. */
	protected Vector3f zAxis = new Vector3f(0, 0, 1);

	/** Extents of the box along the x,y,z axis. */
	protected Vector3f extent = new Vector3f(0, 0, 0);

	/** Texture coordintae values for the corners of the box. */
	protected Vector2f texTopRight, texTopLeft, texBotRight, texBotLeft;

	/** Vector array used to store the array of 8 corners the box has. */
	public Vector3f[] vectorStore;

	/**
	 * If true, the box's vectorStore array correctly represnts the box's
	 * corners.
	 */
	public boolean correctCorners;

	private static final Vector3f tempVa = new Vector3f();

	private static final Vector3f tempVb = new Vector3f();

	private static final Vector3f tempVc = new Vector3f();

	/**
	 * Creates a new OrientedBox with the given name.
	 * 
	 * @param name
	 *            The name of the new box.
	 */
	public OrientedBox(String name) {
		super(name);
		vectorStore = new Vector3f[8];
		for (int i = 0; i < vectorStore.length; i++) {
			vectorStore[i] = new Vector3f();
		}
		texTopRight = new Vector2f(1, 1);
		texTopLeft = new Vector2f(1, 0);
		texBotRight = new Vector2f(0, 1);
		texBotLeft = new Vector2f(0, 0);
		center = new Vector3f(0, 0, 0);
		correctCorners = false;
        computeInformation();
	}

	/**
	 * Takes the plane and center information and creates the correct
	 * vertex,normal,color,texture,index information to represent the
	 * OrientedBox.
	 */
	public void computeInformation() {
		setVertexData();
		setNormalData();
		setTextureData();
		setIndexData();
	}

	/**
	 * Sets the correct indices array for the box.
	 */
	private void setIndexData() {
        TriangleBatch batch = getBatch(0);
		batch.setIndexBuffer(BufferUtils.createIntBuffer(batch.getIndexBuffer(), 36));
	    batch.setTriangleQuantity(12);

		for (int i = 0; i < 6; i++) {
			batch.getIndexBuffer().put(i * 4 + 0);
			batch.getIndexBuffer().put(i * 4 + 1);
			batch.getIndexBuffer().put(i * 4 + 3);
			batch.getIndexBuffer().put(i * 4 + 1);
			batch.getIndexBuffer().put(i * 4 + 2);
			batch.getIndexBuffer().put(i * 4 + 3);
		}
	}

	/**
	 * Sets the correct texture array for the box.
	 */
	private void setTextureData() {
        TriangleBatch batch = getBatch(0);
	    if (batch.getTextureBuffers().get(0) == null) {
	    	batch.getTextureBuffers().set(0, BufferUtils.createVector2Buffer(24));
	
		    for (int x = 0; x < 6; x++) {
                batch.getTextureBuffers().get(0).put(texTopRight.x).put(texTopRight.y);
                batch.getTextureBuffers().get(0).put(texTopLeft.x).put(texTopLeft.y);
                batch.getTextureBuffers().get(0).put(texBotLeft.x).put(texBotLeft.y);
                batch.getTextureBuffers().get(0).put(texBotRight.x).put(texBotRight.y);
		    }
	    }
	}

	/**
	 * Sets the correct normal array for the box.
	 */
	private void setNormalData() {
        TriangleBatch batch = getBatch(0);
	    batch.setNormalBuffer(BufferUtils.createVector3Buffer(batch.getNormalBuffer(), 24));

		// top
		batch.getNormalBuffer().put(yAxis.x).put(yAxis.y).put(yAxis.z);
		batch.getNormalBuffer().put(yAxis.x).put(yAxis.y).put(yAxis.z);
		batch.getNormalBuffer().put(yAxis.x).put(yAxis.y).put(yAxis.z);
		batch.getNormalBuffer().put(yAxis.x).put(yAxis.y).put(yAxis.z);

		// right
		batch.getNormalBuffer().put(xAxis.x).put(xAxis.y).put(xAxis.z);
		batch.getNormalBuffer().put(xAxis.x).put(xAxis.y).put(xAxis.z);
		batch.getNormalBuffer().put(xAxis.x).put(xAxis.y).put(xAxis.z);
		batch.getNormalBuffer().put(xAxis.x).put(xAxis.y).put(xAxis.z);

		// left
		batch.getNormalBuffer().put(-xAxis.x).put(-xAxis.y).put(-xAxis.z);
		batch.getNormalBuffer().put(-xAxis.x).put(-xAxis.y).put(-xAxis.z);
		batch.getNormalBuffer().put(-xAxis.x).put(-xAxis.y).put(-xAxis.z);
		batch.getNormalBuffer().put(-xAxis.x).put(-xAxis.y).put(-xAxis.z);

		// bottom
		batch.getNormalBuffer().put(-yAxis.x).put(-yAxis.y).put(-yAxis.z);
		batch.getNormalBuffer().put(-yAxis.x).put(-yAxis.y).put(-yAxis.z);
		batch.getNormalBuffer().put(-yAxis.x).put(-yAxis.y).put(-yAxis.z);
		batch.getNormalBuffer().put(-yAxis.x).put(-yAxis.y).put(-yAxis.z);

		// back
		batch.getNormalBuffer().put(-zAxis.x).put(-zAxis.y).put(-zAxis.z);
		batch.getNormalBuffer().put(-zAxis.x).put(-zAxis.y).put(-zAxis.z);
		batch.getNormalBuffer().put(-zAxis.x).put(-zAxis.y).put(-zAxis.z);
		batch.getNormalBuffer().put(-zAxis.x).put(-zAxis.y).put(-zAxis.z);

		// front
		batch.getNormalBuffer().put(zAxis.x).put(zAxis.y).put(zAxis.z);
		batch.getNormalBuffer().put(zAxis.x).put(zAxis.y).put(zAxis.z);
		batch.getNormalBuffer().put(zAxis.x).put(zAxis.y).put(zAxis.z);
		batch.getNormalBuffer().put(zAxis.x).put(zAxis.y).put(zAxis.z);
	}

	/**
	 * Sets the correct vertex information for the box.
	 */
	private void setVertexData() {
		computeCorners();
        TriangleBatch batch = getBatch(0);
	    batch.setVertexBuffer(BufferUtils.createVector3Buffer(batch.getVertexBuffer(), 24));
	    batch.setVertexCount(24);

		//Top
		batch.getVertexBuffer().put(vectorStore[0].x).put(vectorStore[0].y).put(vectorStore[0].z);
		batch.getVertexBuffer().put(vectorStore[1].x).put(vectorStore[1].y).put(vectorStore[1].z);
		batch.getVertexBuffer().put(vectorStore[5].x).put(vectorStore[5].y).put(vectorStore[5].z);
		batch.getVertexBuffer().put(vectorStore[3].x).put(vectorStore[3].y).put(vectorStore[3].z);

		//Right
		batch.getVertexBuffer().put(vectorStore[0].x).put(vectorStore[0].y).put(vectorStore[0].z);
		batch.getVertexBuffer().put(vectorStore[3].x).put(vectorStore[3].y).put(vectorStore[3].z);
		batch.getVertexBuffer().put(vectorStore[6].x).put(vectorStore[6].y).put(vectorStore[6].z);
		batch.getVertexBuffer().put(vectorStore[2].x).put(vectorStore[2].y).put(vectorStore[2].z);

		//Left
		batch.getVertexBuffer().put(vectorStore[5].x).put(vectorStore[5].y).put(vectorStore[5].z);
		batch.getVertexBuffer().put(vectorStore[1].x).put(vectorStore[1].y).put(vectorStore[1].z);
		batch.getVertexBuffer().put(vectorStore[4].x).put(vectorStore[4].y).put(vectorStore[4].z);
		batch.getVertexBuffer().put(vectorStore[7].x).put(vectorStore[7].y).put(vectorStore[7].z);

		//Bottom
		batch.getVertexBuffer().put(vectorStore[6].x).put(vectorStore[6].y).put(vectorStore[6].z);
		batch.getVertexBuffer().put(vectorStore[7].x).put(vectorStore[7].y).put(vectorStore[7].z);
		batch.getVertexBuffer().put(vectorStore[4].x).put(vectorStore[4].y).put(vectorStore[4].z);
		batch.getVertexBuffer().put(vectorStore[2].x).put(vectorStore[2].y).put(vectorStore[2].z);

		//Back
		batch.getVertexBuffer().put(vectorStore[3].x).put(vectorStore[3].y).put(vectorStore[3].z);
		batch.getVertexBuffer().put(vectorStore[5].x).put(vectorStore[5].y).put(vectorStore[5].z);
		batch.getVertexBuffer().put(vectorStore[7].x).put(vectorStore[7].y).put(vectorStore[7].z);
		batch.getVertexBuffer().put(vectorStore[6].x).put(vectorStore[6].y).put(vectorStore[6].z);

		//Front
		batch.getVertexBuffer().put(vectorStore[1].x).put(vectorStore[1].y).put(vectorStore[1].z);
		batch.getVertexBuffer().put(vectorStore[4].x).put(vectorStore[4].y).put(vectorStore[4].z);
		batch.getVertexBuffer().put(vectorStore[2].x).put(vectorStore[2].y).put(vectorStore[2].z);
		batch.getVertexBuffer().put(vectorStore[0].x).put(vectorStore[0].y).put(vectorStore[0].z);
	}

	/**
	 * Sets the vectorStore information to the 8 corners of the box.
	 */
	public void computeCorners() {
		correctCorners = true;

		tempVa.set(xAxis).multLocal(extent.x);
		tempVb.set(yAxis).multLocal(extent.y);
		tempVc.set(zAxis).multLocal(extent.z);

		vectorStore[0].set(center).addLocal(tempVa).addLocal(tempVb).addLocal(
				tempVc);
		vectorStore[1].set(center).addLocal(tempVa).subtractLocal(tempVb)
				.addLocal(tempVc);
		vectorStore[2].set(center).addLocal(tempVa).addLocal(tempVb)
				.subtractLocal(tempVc);
		vectorStore[3].set(center).subtractLocal(tempVa).addLocal(tempVb)
				.addLocal(tempVc);
		vectorStore[4].set(center).addLocal(tempVa).subtractLocal(tempVb)
				.subtractLocal(tempVc);
		vectorStore[5].set(center).subtractLocal(tempVa).subtractLocal(tempVb)
				.addLocal(tempVc);
		vectorStore[6].set(center).subtractLocal(tempVa).addLocal(tempVb)
				.subtractLocal(tempVc);
		vectorStore[7].set(center).subtractLocal(tempVa).subtractLocal(tempVb)
				.subtractLocal(tempVc);

		//        float xDotYcrossZ=xAxis.dot(yAxis.cross(zAxis,tempVa));
		//        Vector3f yCrossZmulX=yAxis.cross(zAxis,tempVa).multLocal(extent.x);
		//        Vector3f zCrossXmulY=zAxis.cross(xAxis,tempVb).multLocal(extent.y);
		//        Vector3f xCrossYmulZ=xAxis.cross(yAxis,tempVc).multLocal(extent.z);
		//
		//        vectorStore[0].set(
		//                ((yCrossZmulX.x + zCrossXmulY.x +
		// xCrossYmulZ.x)/xDotYcrossZ)+center.x,
		//                ((yCrossZmulX.y + zCrossXmulY.y +
		// xCrossYmulZ.y)/xDotYcrossZ)+center.y,
		//                ((yCrossZmulX.z + zCrossXmulY.z +
		// xCrossYmulZ.z)/xDotYcrossZ)+center.z
		//        );
		//        vectorStore[1].set(
		//                (-yCrossZmulX.x + zCrossXmulY.x +
		// xCrossYmulZ.x)/xDotYcrossZ+center.x,
		//                (-yCrossZmulX.y + zCrossXmulY.y +
		// xCrossYmulZ.y)/xDotYcrossZ+center.y,
		//                (-yCrossZmulX.z + zCrossXmulY.z + xCrossYmulZ.z)/xDotYcrossZ+center.z
		//        );
		//
		//        vectorStore[2].set(
		//                (yCrossZmulX.x + -zCrossXmulY.x +
		// xCrossYmulZ.x)/xDotYcrossZ+center.x,
		//                (yCrossZmulX.y + -zCrossXmulY.y +
		// xCrossYmulZ.y)/xDotYcrossZ+center.y,
		//                (yCrossZmulX.z + -zCrossXmulY.z + xCrossYmulZ.z)/xDotYcrossZ+center.z
		//        );
		//
		//        vectorStore[3].set(
		//                (yCrossZmulX.x + zCrossXmulY.x +
		// -xCrossYmulZ.x)/xDotYcrossZ+center.x,
		//                (yCrossZmulX.y + zCrossXmulY.y +
		// -xCrossYmulZ.y)/xDotYcrossZ+center.y,
		//                (yCrossZmulX.z + zCrossXmulY.z + -xCrossYmulZ.z)/xDotYcrossZ+center.z
		//        );
		//
		//        vectorStore[4].set(
		//                (-yCrossZmulX.x + -zCrossXmulY.x +
		// xCrossYmulZ.x)/xDotYcrossZ+center.x,
		//                (-yCrossZmulX.y + -zCrossXmulY.y +
		// xCrossYmulZ.y)/xDotYcrossZ+center.y,
		//                (-yCrossZmulX.z + -zCrossXmulY.z +
		// xCrossYmulZ.z)/xDotYcrossZ+center.z
		//        );
		//
		//        vectorStore[5].set(
		//                (-yCrossZmulX.x + zCrossXmulY.x +
		// -xCrossYmulZ.x)/xDotYcrossZ+center.x,
		//                (-yCrossZmulX.y + zCrossXmulY.y +
		// -xCrossYmulZ.y)/xDotYcrossZ+center.y,
		//                (-yCrossZmulX.z + zCrossXmulY.z +
		// -xCrossYmulZ.z)/xDotYcrossZ+center.z
		//        );
		//        vectorStore[6].set(
		//                (yCrossZmulX.x + -zCrossXmulY.x +
		// -xCrossYmulZ.x)/xDotYcrossZ+center.x,
		//                (yCrossZmulX.y + -zCrossXmulY.y +
		// -xCrossYmulZ.y)/xDotYcrossZ+center.y,
		//                (yCrossZmulX.z + -zCrossXmulY.z +
		// -xCrossYmulZ.z)/xDotYcrossZ+center.z
		//        );
		//
		//        vectorStore[7].set(
		//                -(yCrossZmulX.x + zCrossXmulY.x +
		// xCrossYmulZ.x)/xDotYcrossZ+center.x,
		//                -(yCrossZmulX.y + zCrossXmulY.y +
		// xCrossYmulZ.y)/xDotYcrossZ+center.y,
		//                -(yCrossZmulX.z + zCrossXmulY.z + xCrossYmulZ.z)/xDotYcrossZ+center.z
		//        );

	}

	/**
	 * Returns the center of the box.
	 * 
	 * @return The box's center.
	 */
	public Vector3f getCenter() {
		return center;
	}

	/**
	 * Sets the box's center to the given value. Shallow copy only.
	 * 
	 * @param center
	 *            The box's new center.
	 */
	public void setCenter(Vector3f center) {
		this.center = center;
	}

	/**
	 * Returns the box's extent vector along the x,y,z.
	 * 
	 * @return The box's extent vector.
	 */
	public Vector3f getExtent() {
		return extent;
	}

	/**
	 * Sets the box's extent vector to the given value. Shallow copy only.
	 * 
	 * @param extent
	 *            The box's new extent.
	 */
	public void setExtent(Vector3f extent) {
		this.extent = extent;
	}

	/**
	 * Returns the x axis of this box.
	 * 
	 * @return This OB's x axis.
	 */
	public Vector3f getxAxis() {
		return xAxis;
	}

	/**
	 * Sets the x axis of this OB. Shallow copy.
	 * 
	 * @param xAxis
	 *            The new x axis.
	 */
	public void setxAxis(Vector3f xAxis) {
		this.xAxis = xAxis;
	}

	/**
	 * Gets the Y axis of this OB.
	 * 
	 * @return This OB's Y axis.
	 */
	public Vector3f getyAxis() {
		return yAxis;
	}

	/**
	 * Sets the Y axis of this OB. Shallow copy.
	 * 
	 * @param yAxis
	 *            The new Y axis.
	 */
	public void setyAxis(Vector3f yAxis) {
		this.yAxis = yAxis;
	}

	/**
	 * Returns the Z axis of this OB.
	 * 
	 * @return The Z axis.
	 */
	public Vector3f getzAxis() {
		return zAxis;
	}

	/**
	 * Sets the Z axis of this OB. Shallow copy.
	 * 
	 * @param zAxis
	 *            The new Z axis.
	 */
	public void setzAxis(Vector3f zAxis) {
		this.zAxis = zAxis;
	}

	/**
	 * Returns if the corners are set corectly.
	 * 
	 * @return True if the vectorStore is correct.
	 */
	public boolean isCorrectCorners() {
		return correctCorners;
	}
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(center, "center", Vector3f.ZERO);
        capsule.write(xAxis, "xAxis", Vector3f.UNIT_X);
        capsule.write(yAxis, "yAxis", Vector3f.UNIT_Y);
        capsule.write(zAxis, "zAxis", Vector3f.UNIT_Z);
        capsule.write(extent, "extent", Vector3f.ZERO);
        capsule.write(texTopRight, "texTopRight", new Vector2f(1, 1));
        capsule.write(texTopLeft, "texTopLeft", new Vector2f(1, 0));
        capsule.write(texBotRight, "texBotRight", new Vector2f(0, 1));
        capsule.write(texBotLeft, "texBotLeft", new Vector2f(0, 0));
        capsule.write(vectorStore, "vectorStore", new Vector3f[8]);
        capsule.write(correctCorners, "correctCorners", false);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        
        center = (Vector3f)capsule.readSavable("center", Vector3f.ZERO.clone());
        xAxis = (Vector3f)capsule.readSavable("xAxis", Vector3f.UNIT_X.clone());
        yAxis = (Vector3f)capsule.readSavable("yAxis", Vector3f.UNIT_Y.clone());
        zAxis = (Vector3f)capsule.readSavable("zAxis", Vector3f.UNIT_Z.clone());
        extent = (Vector3f)capsule.readSavable("extent", Vector3f.ZERO.clone());
        texTopRight = (Vector2f)capsule.readSavable("texTopRight", new Vector2f(1, 1));
        texTopLeft = (Vector2f)capsule.readSavable("texTopLeft", new Vector2f(1, 0));
        texBotRight = (Vector2f)capsule.readSavable("texBotRight", new Vector2f(0, 1));
        texBotLeft = (Vector2f)capsule.readSavable("texBotLeft", new Vector2f(0, 0));
        
        Savable[] savs = capsule.readSavableArray("vectorStore", new Vector3f[8]);
        if (savs == null)
            vectorStore = null;
        else {
            vectorStore = new Vector3f[savs.length];
            for (int x = 0; x < savs.length; x++) {
                vectorStore[x] = (Vector3f)savs[x];
            }
        }
        
        correctCorners = capsule.readBoolean("correctCorners", false);
    }
}