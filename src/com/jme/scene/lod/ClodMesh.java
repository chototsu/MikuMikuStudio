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
 * <code>ContinuousLodNode</code>
 * @author Joshua Slack
 * @version $Id: ClodMesh.java,v 1.3 2004-04-07 00:48:13 renanse Exp $
 */
public class ClodMesh extends TriMesh {
  int m_iCurrentRecord, m_iTargetRecord;
  CollapseRecord[] m_akRecord;
ClodCreator creator;
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

    m_iTargetRecord = 0;
    m_iCurrentRecord = 0;

    if (records != null && records.length > 0) {
      m_akRecord = records;
    } else {
      creator = new ClodCreator(vertices, normal, color, texture,
                                            indices);
      m_akRecord = creator.getRecords();
    }


    updateColorBuffer();
    updateNormalBuffer();
    updateVertexBuffer();
    updateTextureBuffer();
    updateIndexBuffer();
    updateModelBound();
  }

  public void selectLevelOfDetail() {
    // Get target record.  The function may be overridden by a derived
    // class to obtain a desired automated change in the target.

    int iTargetRecord = getAutomatedTargetRecord();
    if (iTargetRecord == m_iCurrentRecord) {
      return;
    }

    // collapse mesh (if necessary)
    int i, iC;
    while (m_iCurrentRecord < iTargetRecord) {
      m_iCurrentRecord++;

      // replace indices in connectivity array
      CollapseRecord rkRecord = m_akRecord[m_iCurrentRecord];
      for (i = 0; i < rkRecord.m_iIQuantity; i++) {
        iC = rkRecord.indices[i];
        if (! (indices[iC] == rkRecord.vertToThrow))throw new AssertionError();
        indices[iC] = rkRecord.vertToKeep;
      }

      // reduce vertex count (vertices are properly ordered)
      vertQuantity = rkRecord.m_iVQuantity;

      // reduce triangle count (triangles are properly ordered)
      triangleQuantity = rkRecord.m_iTQuantity;
    }

    // expand mesh (if necessary)
    while (m_iCurrentRecord > iTargetRecord) {
      // restore indices in connectivity array
      CollapseRecord rkRecord = m_akRecord[m_iCurrentRecord];
      for (i = 0; i < rkRecord.m_iIQuantity; i++) {
        iC = rkRecord.indices[i];
        if (! (indices[iC] == rkRecord.vertToKeep))throw new AssertionError();
        indices[iC] = rkRecord.vertToThrow;
      }

      m_iCurrentRecord--;
      CollapseRecord rkPrevRecord = m_akRecord[m_iCurrentRecord];

      // increase vertex count (vertices are properly ordered)
      vertQuantity = rkPrevRecord.m_iVQuantity;

      // increase triangle count (triangles are properly ordered)
      triangleQuantity = rkPrevRecord.m_iTQuantity;
    }
    System.err.println("Current record: "+m_iCurrentRecord+" tris: "+triangleQuantity);
    for (int j = 0; j < triangleQuantity; j++) {
      System.err.println(j+". tri: " + vertex[indices[j*3]] + "," + vertex[indices[j*3+1]] + "," + vertex[indices[j*3+2]]);
    }
    updateColorBuffer();
    updateNormalBuffer();
    updateVertexBuffer();
    updateTextureBuffer();
    updateIndexBuffer();
  }

  public void draw(Renderer r) {
    selectLevelOfDetail();
    super.draw(r);
  }

  public int getRecordQuantity() {
    return m_akRecord.length;
  }

  public int getTargetRecord() {
    return m_iTargetRecord;
  }

  public void setTargetRecord(int target) {
    m_iTargetRecord = target;
    if (m_iTargetRecord < 0)
      m_iTargetRecord = 0;
    else if (m_iTargetRecord > m_akRecord.length - 1)
      m_iTargetRecord = m_akRecord.length - 1;
  }

  public int getAutomatedTargetRecord() {
    return m_iTargetRecord;
  }
}
