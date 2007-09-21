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

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Hexagon</code> provides an extension of <code>TriMesh</code>. A
 * <code>Hexagon</code> provides a regular hexagon with each triangle having
 * side length that is given in the constructor.
 * 
 * @author Joel Schuster
 * @version $Id: Hexagon.java,v 1.13 2007-09-21 15:45:27 nca Exp $
 */
public class Hexagon extends TriMesh {
	private static final long serialVersionUID = 1L;

	private static final int NUM_POINTS = 7;

	private static final int NUM_TRIS = 6;

	private float sideLength;

	/**
	 * Hexagon Constructor instantiates a new Hexagon. This element is center on
	 * 0,0,0 with all normals pointing up. The user must move and rotate for
	 * positioning.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param sideLength
	 *            The length of all the sides of the tiangles
	 */
	public Hexagon(String name, float sideLength) {
		super(name);
		this.sideLength = sideLength;
        TriangleBatch batch = getBatch(0);

		// allocate vertices
		batch.setVertexCount(NUM_POINTS);
		batch.setVertexBuffer(BufferUtils.createVector3Buffer(batch.getVertexCount()));
		batch.setNormalBuffer(BufferUtils.createVector3Buffer(batch.getVertexCount()));
		batch.getTextureBuffers().set(0, BufferUtils.createVector2Buffer(batch.getVertexCount()));
		
		batch.setTriangleQuantity(NUM_TRIS);
		batch.setIndexBuffer(BufferUtils.createIntBuffer(3 * batch.getTriangleCount()));

		setVertexData();
		setIndexData();
		setTextureData();
		setNormalData();

	}

	/**
	 * Vertexes are set up like this: 0__1 / \ / \ 5/__\6/__\2 \ / \ / \ /___\ /
	 * 4 3
	 * 
	 * All lines on this diagram are sideLength long. Therefore, the width of
	 * the hexagon is sideLength * 2, and the height is 2 * the height of one
	 * equalateral triangle with all side = sideLength which is .866
	 *  
	 */
	private void setVertexData() {
        TriangleBatch batch = getBatch(0);
	    batch.getVertexBuffer().put(-(sideLength / 2)).put(sideLength * 0.866f).put(0.0f);
	    batch.getVertexBuffer().put(sideLength / 2).put(sideLength * 0.866f).put(0.0f);
	    batch.getVertexBuffer().put(sideLength).put(0.0f).put(0.0f);
	    batch.getVertexBuffer().put(sideLength / 2).put(-sideLength * 0.866f).put(0.0f);
	    batch.getVertexBuffer().put(-(sideLength / 2)).put(-sideLength * 0.866f).put(0.0f);
	    batch.getVertexBuffer().put(-sideLength).put(0.0f).put(0.0f);
	    batch.getVertexBuffer().put(0.0f).put(0.0f).put(0.0f);
	}

	/**
     * Sets up the indexes of the mesh. These go in a clockwise fashion and thus
     * only the 'up' side of the hex is lit properly. If you wish to have to
     * either set two sided lighting or create two hexes back-to-back
     */

	private void setIndexData() {
        TriangleBatch batch = getBatch(0);
		batch.getIndexBuffer().rewind();
		// tri 1
		batch.getIndexBuffer().put(0);
		batch.getIndexBuffer().put(6);
		batch.getIndexBuffer().put(1);
		// tri 2
		batch.getIndexBuffer().put(1);
		batch.getIndexBuffer().put(6);
		batch.getIndexBuffer().put(2);
		// tri 3
		batch.getIndexBuffer().put(2);
		batch.getIndexBuffer().put(6);
		batch.getIndexBuffer().put(3);
		// tri 4
		batch.getIndexBuffer().put(3);
		batch.getIndexBuffer().put(6);
		batch.getIndexBuffer().put(4);
		// tri 5
		batch.getIndexBuffer().put(4);
		batch.getIndexBuffer().put(6);
		batch.getIndexBuffer().put(5);
		// tri 6
		batch.getIndexBuffer().put(5);
		batch.getIndexBuffer().put(6);
		batch.getIndexBuffer().put(0);
	}

	private void setTextureData() {
        TriangleBatch batch = getBatch(0);
        batch.getTextureBuffers().get(0).put(0.25f).put(0);
        batch.getTextureBuffers().get(0).put(0.75f).put(0);
        batch.getTextureBuffers().get(0).put(1.0f).put(0.5f);
        batch.getTextureBuffers().get(0).put(0.75f).put(1.0f);
        batch.getTextureBuffers().get(0).put(0.25f).put(1.0f);
        batch.getTextureBuffers().get(0).put(0.0f).put(0.5f);
        batch.getTextureBuffers().get(0).put(0.5f).put(0.5f);
	}

	/**
	 * Sets all the default vertex normals to 'up', +1 in the Z direction.
	 *  
	 */
	private void setNormalData() {
        TriangleBatch batch = getBatch(0);
	    Vector3f zAxis = new Vector3f(0, 0, 1); 
		for (int i = 0; i < NUM_POINTS; i++)
		    BufferUtils.setInBuffer(zAxis, batch.getNormalBuffer(), i);
	}
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(sideLength, "sideLength", 0);
        
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        sideLength = capsule.readInt("sideLength", 0);
        
    }

}

/*
 * $Log: not supported by cvs2svn $
 * Revision 1.12  2007/03/05 15:11:30  nca
 * Issue:  improper winding corrected on Hexagon.
 *
 * Revision 1.11  2006/06/21 20:32:50  nca
 * ISSUE MINOR:
 *
 * Lots of removal of warnings: casting and unnecessary if/else blocks.
 *
 * Revision 1.10  2006/05/11 19:39:24  nca
 * ISSUE(S): 197, 191, MINOR
 *
 * Major additions, possible issues will be uncovered:
 *
 * 1. Added the Savable system, allowing extensible savings of jME Scenes.
 * 2. Changed GeomBatch to support RenderQueue (by making it a Spatial).
 * 3. Shapes added, cleaned up.
 * 4. Node.getChildren will now return null if no children have been added, this should be checked for.
 * 5. LightController enhancements.
 *
 * Revision 1.9  2006/03/17 20:04:17  nca
 * Contribution from NCsoft.
 *
 * Initial framework of batching system. Batches are stored in geometry object and are responsible for displaying their own buffers. They can override states to allow for state changes within single meshes.
 *
 * Batches can also describe how they are to be rendered. Currently only TRIANGLES is supported.
 *
 * Revision 1.8  2006/01/13 19:39:36  renanse
 * MINOR: Copyright information updated to 2006.
 *
 * Revision 1.7  2005/12/10 05:28:46  renanse
 * Code from Mark to handle texture coords nicer.
 *
 * Revision 1.6  2005/09/21 17:52:55  renanse
 * Added defaultColor - ability to set a single color per geometry object (used if colorBuffer == null)
 *
 * Revision 1.5  2005/09/15 17:13:42  renanse
 * Removed Geometry and Spatial object arrays, fixed resulting errors, cleaned up license comments and imports and fixed all tests.  Also removed widget and ui packages.
 *
 * Revision 1.4  2004/09/14 21:52:21  mojomonkey
 * Clean Up:
 * 1. Added serialVersionUID to those classes that needed it.
 * 2. Formatted a significant number of classes.
 * Revision 1.3 2004/05/27 02:28:26 guurk Corrected shape
 * for height.
 * 
 * Revision 1.2 2004/05/27 02:06:31 guurk Added some CVS keyword replacements.
 *  
 */
