/*
 * Copyright (c) 2004, jMonkeyEngine - Mojo Monkey Coding
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

package com.jme.scene.lod;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.TriMesh;

/**
 * <code>ClodMesh</code>
 * originally ported from David Eberly's c++, modifications and
 * enhancements made from there.
 * @author Joshua Slack
 * @version $Id: ClodMesh.java,v 1.10 2004-04-14 02:30:22 mojomonkey Exp $
 */
public class ClodMesh extends TriMesh {
  int currentRecord, targetRecord;
  CollapseRecord[] records;

  public ClodMesh(String name) {
      super(name);
  }
  
  public ClodMesh(
      String name,
      TriMesh data,
      CollapseRecord[] records) {

    this(name, data.getVertices(), data.getNormals(), data.getColors(),
         data.getTextures(), data.getIndices(), records);

  }

  public ClodMesh(
      String name,
      Vector3f[] vertices,
      Vector3f[] normal,
      ColorRGBA[] color,
      Vector2f[] texture,
      int[] indices, CollapseRecord[] records) {

    super(name, vertices, normal, color, texture, indices);

    targetRecord = 0;
    currentRecord = 0;

    if (records != null && records.length > 0) {
      this.records = records;
    } else {
      ClodCreator creator = new ClodCreator(vertices, normal, color, texture,
                                indices);
      this.records = creator.getRecords();
      creator.removeAllTriangles();
      creator = null;
    }
    triangleQuantity = this.records[0].numbTriangles;
    vertQuantity = this.records[0].numbVerts;

    updateColorBuffer();
    updateNormalBuffer();
    updateVertexBuffer();
    updateTextureBuffer();
    updateIndexBuffer();
    updateModelBound();
  }
  
  public void create(CollapseRecord[] records) {
  
      targetRecord = 0;
      currentRecord = 0;

      if (records != null && records.length > 0) {
        this.records = records;
      } else {
        ClodCreator creator = new ClodCreator(this.getVertices(), this.getNormals(), this.getColors(), this.getTextures(),
                                  this.getIndices());
        this.records = creator.getRecords();
        creator.removeAllTriangles();
        creator = null;
      }
      triangleQuantity = this.records[0].numbTriangles;
      vertQuantity = this.records[0].numbVerts;

      updateColorBuffer();
      updateNormalBuffer();
      updateVertexBuffer();
      updateTextureBuffer();
      updateIndexBuffer();
      updateModelBound();
      
  }

  public void selectLevelOfDetail(Renderer r) {
    // Get target record.  The function may be overridden by a derived
    // class to obtain a desired automated change in the target.

    int iTargetRecord = chooseTargetRecord(r);
    if (iTargetRecord == currentRecord) {
      return;
    }

    // collapse mesh (if necessary)
    int i, iC;
    while (currentRecord < iTargetRecord) {
      currentRecord++;

      // replace indices in connectivity array
      CollapseRecord rkRecord = records[currentRecord];
      for (i = 0; i < rkRecord.numbIndices; i++) {
        iC = rkRecord.indices[i];
//        if (! (indices[iC] == rkRecord.vertToThrow))throw new AssertionError();
        indices[iC] = rkRecord.vertToKeep;
      }

      // reduce vertex count (vertices are properly ordered)
      vertQuantity = rkRecord.numbVerts;

      // reduce triangle count (triangles are properly ordered)
      triangleQuantity = rkRecord.numbTriangles;
    }

    // expand mesh (if necessary)
    while (currentRecord > iTargetRecord) {
      // restore indices in connectivity array
      CollapseRecord rkRecord = records[currentRecord];
      for (i = 0; i < rkRecord.numbIndices; i++) {
        iC = rkRecord.indices[i];
//        if (! (indices[iC] == rkRecord.vertToKeep))throw new AssertionError();
        indices[iC] = rkRecord.vertToThrow;
      }

      currentRecord--;
      CollapseRecord rkPrevRecord = records[currentRecord];

      // increase vertex count (vertices are properly ordered)
      vertQuantity = rkPrevRecord.numbVerts;

      // increase triangle count (triangles are properly ordered)
      triangleQuantity = rkPrevRecord.numbTriangles;
    }

    updateVertexBuffer();
    updateIndexBuffer();
  }

  public void draw(Renderer r) {
    selectLevelOfDetail(r);
    super.draw(r);
  }

  public int getRecordQuantity() {
    return records.length;
  }

  public int chooseTargetRecord(Renderer r) {
    return targetRecord;
  }

  public int getTargetRecord() {
    return targetRecord;
  }

  public void setTargetRecord(int target) {
    targetRecord = target;
    if (targetRecord < 0)
      targetRecord = 0;
    else if (targetRecord > records.length - 1)
      targetRecord = records.length - 1;
  }
}
