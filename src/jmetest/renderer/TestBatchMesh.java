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

package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BatchMesh;
import com.jme.scene.Line;
import com.jme.scene.batch.LineBatch;
import com.jme.scene.batch.PointBatch;
import com.jme.scene.batch.QuadBatch;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.state.CullState;
import com.jme.util.geom.BufferUtils;

/**
 * <code>TestBatchMesh</code> shows how to use the different batches and their modes.
 * @author Tijl Houtbeckers
 * @version $Id: TestBatchMesh.java,v 1.2 2006-09-11 23:43:00 llama Exp $
 */
public class TestBatchMesh extends SimpleGame {

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestBatchMesh app = new TestBatchMesh();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  /**
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Test BatchMesh. Press T for wireframe view.");
    
    lightState.setEnabled(false);
    
    TriangleBatch tbstrip = new TriangleBatch();
    tbstrip.setMode(TriangleBatch.TRIANGLE_STRIP);
    tbstrip.setVertexBuffer(BufferUtils.createFloatBuffer(getVerts(3, 0)));
    // create a triangle strip, makes 4 triangles by specifying 6 points
    tbstrip.setIndexBuffer(BufferUtils.createIntBuffer(new int[] { 0, 1, 2, 3, 4, 5 }));
    
    TriangleBatch tbfan = new TriangleBatch();
    tbfan.setMode(TriangleBatch.TRIANGLE_FAN);
    tbfan.setVertexBuffer(BufferUtils.createFloatBuffer(getVerts(-1, 0)));
    // create a triangle fan, makes a 4 triangle "fan" around 0,0
    tbfan.setIndexBuffer(BufferUtils.createIntBuffer(new int[] {2, 0, 1, 3, 5, 4 }));
    
    TriangleBatch tbTriangles = new TriangleBatch();
    tbTriangles.setMode(TriangleBatch.TRIANGLES);
    tbTriangles.setVertexBuffer(BufferUtils.createFloatBuffer(getVerts(-5, 0)));
    // create seperate triangles using counter clockwise ordering.
    // specifying 6 points only gets us 2 triangles
    tbTriangles.setIndexBuffer(BufferUtils.createIntBuffer(new int[] {0, 1, 2, 3, 5, 4 }));
    
    PointBatch pb = new PointBatch();
    pb.setPointSize(10f);
    pb.setVertexBuffer(BufferUtils.createFloatBuffer(getVerts(-5, -5)));
    // make 6 points. with ugly colors.
    pb.setColorBuffer( BufferUtils.createFloatBuffer(
    		new ColorRGBA[] {ColorRGBA.blue, ColorRGBA.orange, ColorRGBA.cyan, ColorRGBA.gray, ColorRGBA.green, ColorRGBA.magenta})
    		);    
    pb.setIndexBuffer(BufferUtils.createIntBuffer(new int[] {0, 1, 2, 3, 4, 5 }));
    
    LineBatch lbConnected = new LineBatch();
    lbConnected.setMode(Line.CONNECTED);
    lbConnected.setVertexBuffer(BufferUtils.createFloatBuffer(getVerts(-5, 5)));
    // 5 connected lines
    lbConnected.setIndexBuffer(BufferUtils.createIntBuffer(new int[] {0, 1, 2, 3, 4, 5 }));

    LineBatch lbSegments = new LineBatch();
    lbSegments.setMode(Line.SEGMENTS);
    lbSegments.setVertexBuffer(BufferUtils.createFloatBuffer(getVerts(-1, 5)));
    // 3 seperate lines
    lbSegments.setIndexBuffer(BufferUtils.createIntBuffer(new int[] {0, 1, 2, 3, 4, 5 }));

    LineBatch lbLoop = new LineBatch();
    lbLoop.setMode(Line.LOOP);
    lbLoop.setVertexBuffer(BufferUtils.createFloatBuffer(getVerts(3, 5)));
    // 6 lines forming a loop
    lbLoop.setIndexBuffer(BufferUtils.createIntBuffer(new int[] {0, 1, 2, 3, 4, 5 }));
    
    QuadBatch qbQuads = new QuadBatch();
    qbQuads.setMode(QuadBatch.QUADS);
    qbQuads.setVertexBuffer(BufferUtils.createFloatBuffer(getVerts(-1, -5)));
    // 1 quad,  specified in counter clockwise order. 
    qbQuads.setIndexBuffer(BufferUtils.createIntBuffer(new int[] {0, 1, 3, 2}));

    QuadBatch qbStrip = new QuadBatch();
    qbStrip.setMode(QuadBatch.QUAD_STRIP);
    qbStrip.setVertexBuffer(BufferUtils.createFloatBuffer(getVerts(3, -5)));
    // A strip of 2 quads. Beware that QUAD_STRIP ordering is different from QUADS,
    // The third indice actually points to the start of *next* quad.
    qbStrip.setIndexBuffer(BufferUtils.createIntBuffer(new int[] {0, 1, 2, 3, 4, 5}));
    
        CullState cull = display.getRenderer().createCullState();
    cull.setCullMode(CullState.CS_BACK);
    BatchMesh mesh = new BatchMesh("batches", tbstrip, tbfan, tbTriangles, pb, lbConnected, lbSegments, lbLoop, qbQuads, qbStrip);
    // we set a cull state to hide the back of our batches, "proving" they are camera facing.
    mesh.setRenderState(cull);
    mesh.updateRenderState();
    rootNode.attachChild(mesh);
  }
  
  Vector3f[] getVerts(int x, int y) {
	  Vector3f[] verts = new Vector3f[] { 
	            new Vector3f(0+x,1+y,0), // 0
	            new Vector3f(0+x,0+y,0), // 1
	            new Vector3f(1+x,1+y,0), // 2
	            new Vector3f(1+x,0+y,0), // 3
	            new Vector3f(2+x,1+y,0), // 4
	            new Vector3f(2+x,0+y,0)  // 5
	    };
	  return verts;
  }
  
}
