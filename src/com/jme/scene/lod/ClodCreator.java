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

package com.jme.scene.lod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * <code>ClodCreator</code>
 * originally ported from David Eberly's c++, modifications and
 * enhancements made from there.<br><br>
 * This class is used by ClodMesh to create automatically generated records.
 * The reason for lack of documentation is that it should have little use to someone
 * outside the API, unless they already know how to use it.
 * @author Joshua Slack
 * @version $Id: ClodCreator.java,v 1.15 2004-08-31 01:50:16 renanse Exp $
 */

public class ClodCreator extends VETMesh {
  private Vector3f[] vertices;
  private Vector3f[] normals;
  private ColorRGBA[] colors;
  private Vector2f[] textures;
  private int[] indices;

  private int currentVertex, currentTriangle, numbTriangles;
  private int[] orderedVertices;
  private int[] permuteVertices;
  private int[] newIndices;

  int heapSize;

  HeapRecord[] heapArray;
  boolean collapsing;

  // for reordering vertices and triangles
  TreeSet deletedVertices; // <int>
  ArrayList deletedEdges; // <CollapseRecord>
  CollapseRecord[] records;

  private static final Vector3f tempVa=new Vector3f();
  private static final Vector3f tempVb=new Vector3f();
  private static final Vector3f tempVc=new Vector3f();

  protected class HeapRecord {
    public HeapRecord() {
      m_kEdge = new Edge( -1, -1);
      m_iHIndex = -1;
      m_fMetric = -1.0f;
    }

    public Edge m_kEdge;
    public int m_iHIndex;
    public float m_fMetric;

    public boolean equals(Object obj) {
      HeapRecord rkH = (HeapRecord) obj;
      return m_kEdge.equals(rkH.m_kEdge);
    }
  };

  public ClodCreator(
      Vector3f[] vertexArray,
      Vector3f[] normalArray,
      ColorRGBA[] colorArray,
      Vector2f[] textureArray,
      int[] indiceArray) {
    // Hang onto these to avoid having to pass them through member function
    // calls.
    this.vertices = vertexArray;
    this.normals = normalArray;
    this.colors = colorArray;
    this.textures = textureArray;
    this.indices = indiceArray;
    numbTriangles = indiceArray.length / 3;

    // for reordering vertices and triangles
    currentVertex = vertices.length - 1;
    currentTriangle = numbTriangles - 1;
    orderedVertices = new int[vertices.length];
    permuteVertices = new int[vertices.length];
    newIndices = new int[indices.length];

    deletedEdges = new ArrayList();
    deletedVertices = new TreeSet();

    // Insert the triangles into the mesh.  The triangle indices are attached
    // as extra data.
    collapsing = false;
    for (int i = 0; i < numbTriangles; i++) {
//      int iV0 = m_aiConnect[3 * i];
//      int iV1 = m_aiConnect[3 * i + 1];
//      int iV2 = m_aiConnect[3 * i + 2];
//      if (!(iV0 != iV1 && iV0 != iV2 && iV1 != iV2)) throw new AssertionError();
      Triangle tri = new Triangle(indices[3 * i], indices[3 * i + 1],
                                 indices[3 * i + 2]);
      insertTriangle(tri);
      setData(tri, new Integer(i));
    }

    if (triangleMap.size() != numbTriangles) {
      // We must have duplicates...  lets weed them out and make a new Clod.
      int redoneIndices[] = new int[triangleMap.size() * 3];
      Iterator it = triangleMap.keySet().iterator();
      int i = 0;
      while (it.hasNext()) {
        Triangle t = (Triangle) it.next();
        redoneIndices[i * 3 + 0] = t.vert[0];
        redoneIndices[i * 3 + 1] = t.vert[1];
        redoneIndices[i * 3 + 2] = t.vert[2];
        i++;
      }
      ClodCreator creator = new ClodCreator(vertexArray, normalArray, colorArray, textureArray,
                                            redoneIndices);
      records = creator.getRecords();
      creator = null;
      // Copy the reduced indices back to the original indice array.  There will
      // be some bogus ones on the end, but thats ok because they will never be shown thanks to
      // the number of triangles field.
      for (i = 0; i < redoneIndices.length; i++)
        indices[i] = redoneIndices[i];

        // clear the triangle map so a call to remove triangles doesn't bomb.
      triangleMap.clear();
      return;
    }

//    if (m_kVMap.size() != m_akVertex.length)throw new AssertionError();
//    if (m_kTMap.size() != m_iTQuantity)throw new AssertionError(
//        "triangle map size: " + m_kTMap.size() + " != m_iTQuantity: " +
//        m_iTQuantity);

    initializeHeap();

    collapsing = true;
    while (heapSize > 0) {
      if (heapArray[0].m_fMetric == Float.MAX_VALUE) {
        // all remaining heap elements have infinite weight
        flushVertices();
        flushTriangles();
        break;
      }

      doCollapse();

//      if (! ( (m_kVMap.size()) == m_iVCurrent + 1))throw new AssertionError();
//      if (! ( (m_kTMap.size()) == m_iTCurrent + 1))throw new AssertionError(
//          "triangle map size: " + m_kTMap.size() + " != m_iTCurrent+1: " +
//          (m_iTCurrent + 1));
    }

    collapsing = false;

    // Permute the vertices and triangle connectivity so that the last
    // vertex/triangle in the array is the first vertex/triangle to be
    // removed.
    reorder();

    // The collapse records store the incremental changes that are used for
    // dynamic LOD changes in the caller of this constructor.
    records = computeRecords();
  }

  public CollapseRecord[] getRecords() {
    return records;
  }

  public void doCollapse() {
    // Define a 2-edge to be an edge that has exactly two triangles sharing
    // it.  An edge is collapsible if it is a 2-edge and has at least one end
    // point whose sharing edges are all 2-edges.  In this case, such an end
    // point will be the 'throw' vertex.  This keeps the boundary and junction
    // edges from changing geometry and helps preserve the shape of the mesh.
    // The topology is always guaranteed not to change.

    // When this function is called, the metric has already been calculated
    // and is finite (so exactly two triangles must be sharing this edge).
//    if (!(m_apkHeap[0].m_fMetric < Float.MAX_VALUE)) throw new AssertionError();
    Edge kEdge = heapArray[0].m_kEdge;

    // test end points to see if either has only 2-edges sharing it
    int i;
    for (i = 0; i < 2; i++) {
      ExVector pkESet = (ExVector) getEdges(kEdge.vert[i]).clone();
      int j;
      for (j = 0; j < pkESet.size(); j++) {
        EdgeAttribute pkEM = (EdgeAttribute) edgeMap.get(pkESet.toArray()[j]);
//        if (!(pkEM != null)) throw new AssertionError();
        if (pkEM.triangleSet.size() != 2)
          break;
      }

      if (j == pkESet.size()) {
        // all edges sharing this end point are 2-edges
        break;
      }
    }

    if (i < 2) {
      int iVThrow = kEdge.vert[i];
      int iVKeep = kEdge.vert[1 - i];
      if (!collapseCausesFolding(iVKeep, iVThrow)) {
        remove();
        collapseEdge(iVKeep, iVThrow);
        return;
      }
    }

    // edge not collapsible, assign it infinite weight and update the heap
    update(0, Float.MAX_VALUE);
  }

  public boolean collapseCausesFolding(int iVKeep, int iVThrow) {
    VertexAttribute pkVT = (VertexAttribute) vertexMap.get(new Integer(iVThrow));
//    if (!(pkVT != null)) throw new AssertionError();

    Edge kCollapse = new Edge(iVKeep, iVThrow);
    for (int j = 0; j < pkVT.triangleSet.size(); j++) {
      Triangle kT = (Triangle) pkVT.triangleSet.toArray()[j];
      if (kCollapse.equals(new Edge(kT.vert[0], kT.vert[1]))
          || kCollapse.equals(new Edge(kT.vert[1], kT.vert[2]))
          || kCollapse.equals(new Edge(kT.vert[2], kT.vert[0]))) {
        // This triangle would be removed in a collapse, so it does not
        // contribute to any folding.
        continue;
      }

      for (int i = 0; i < 3; i++) {
        if (kT.vert[i] == iVThrow) {
          // Test if potential replacement triangle (either ordering)
          // is in the mesh.
          int iV0 = iVKeep;
          int iV1 = kT.vert[ (i + 1) % 3];
          int iV2 = kT.vert[ (i + 2) % 3];

          if (triangleMap.get(new Triangle(iV0, iV1, iV2)) != null
              || triangleMap.get(new Triangle(iV0, iV2, iV1)) != null) {
            return true;
          }
        }
      }
    }

    return false;
  }

  public float getMetric(Edge pkE, EdgeAttribute pkEA) {
    float fLengthWeight = 10.0f;
    float fAngleWeight = 1.0f;

    // Compute the metric for the edge.  Only manifold edges (exactly two
    // triangles sharing the edge) are allowed to collapse.
    if (pkEA.triangleSet.size() == 2) {
      // length contribution
      Vector3f rkEnd0 = vertices[pkE.vert[0]];
      Vector3f rkEnd1 = vertices[pkE.vert[1]];
      Vector3f kDiff = rkEnd1.subtract(rkEnd0,tempVa);
      float fMetric = fLengthWeight * kDiff.length();

      // angle/area contribution
      Triangle kT = (Triangle) pkEA.triangleSet.toArray()[0];
      Vector3f kV0 = vertices[kT.vert[0]];
      Vector3f kV1 = vertices[kT.vert[1]];
      Vector3f kV2 = vertices[kT.vert[2]];
      Vector3f kE0 = kV1.subtract(kV0,tempVa);
      Vector3f kE1 = kV2.subtract(kV0,tempVb);
      Vector3f kN0 = kE0.cross(kE1,tempVc);

      kT = (Triangle) pkEA.triangleSet.toArray()[1];
      kV0 = vertices[kT.vert[0]];
      kV1 = vertices[kT.vert[1]];
      kV2 = vertices[kT.vert[2]];
      kE0 = kV1.subtract(kV0,tempVa);
      kE1 = kV2.subtract(kV0,tempVb);
      Vector3f kN1 = kE0.crossLocal(kE1);

      Vector3f kCross = kN0.cross(kN1,tempVa);
      fMetric += fAngleWeight * kCross.length();

      return fMetric;
    }

    // Boundary edges (one triangle containing edge) and junction edges
    // (3 or more triangles sharing edge) are not allowed to collapse.
    return Float.MAX_VALUE;
  }

  public void removeTriangle(Triangle rkT) {
    // If the triangle is an original one, reorder the connectivity array so
    // that the triangle occurs at the end.
    int iTIndex = ( (Integer) getData(rkT)).intValue();
    if (iTIndex >= 0) {
//      if (!(m_iTCurrent >= 0)) throw new AssertionError();
      newIndices[3 * currentTriangle] = indices[3 * iTIndex];
      newIndices[3 * currentTriangle + 1] = indices[3 * iTIndex + 1];
      newIndices[3 * currentTriangle + 2] = indices[3 * iTIndex + 2];
      currentTriangle--;
    }

    super.removeTriangle(rkT);
  }

  public void modifyTriangle(Triangle rkT, int iVKeep, int iVThrow) {
    // Get the index of the pre-modified triangle, then remove the triangle
    // from the mesh.
    int iTIndex = ( (Integer) getData(rkT)).intValue();
    super.removeTriangle(rkT);

    // replace 'throw' by 'keep'
    for (int i = 0; i < 3; i++) {
      if (rkT.vert[i] == iVThrow) {
        rkT.vert[i] = iVKeep;
        break;
      }
    }

    // Indices on modified triangles are the same as the indices on the
    // pre-modified triangles.
    insertTriangle(rkT);
    setData(rkT, new Integer(iTIndex));
  }

  public void collapseEdge(int iVKeep, int iVThrow) {
    // find the edge to collapse
    Edge kCollapse = new Edge(iVKeep, iVThrow);
    EdgeAttribute pkEM = (EdgeAttribute) edgeMap.get(kCollapse);
//    if (pkEM == null) throw new AssertionError("Edge unexpectedly missing from EdgeMap!");

    // keep track of vertices that are deleted in the collapse
    deletedVertices.clear();

    // Remove the collapse-edge-shared triangles.  Using a copy of the
    // triangle set from the collapse edge is required since removal of the
    // last triangle sharing the collapse edge will remove that edge from
    // the edge map, thereby invalidating any iterator that points to data
    // in the collapse edge.
    ExVector kTSet = (ExVector) pkEM.triangleSet.clone(); // <Triangle>
    int iTDeletions = kTSet.size();
//    if (!(iTDeletions > 0)) throw new AssertionError();
    for (int j = 0; j < kTSet.size(); j++)
      removeTriangle( (Triangle) kTSet.toArray()[j]);

      // Replace 'throw' vertices by 'keep' vertices in the remaining triangles
      // at the 'throw' vertex.  The old triangles are removed and the modified
      // triangles are inserted.
    Triangle kT;
    VertexAttribute pkVM = (VertexAttribute) vertexMap.get(new Integer(iVThrow));
    if (pkVM != null) {
      kTSet = (ExVector) pkVM.triangleSet.clone();
      for (int j = 0; j < kTSet.size(); j++) {
        kT = (Triangle) kTSet.toArray()[j];
        modifyTriangle(kT, iVKeep, iVThrow);
      }
    }

    // The set of potentially modified edges consists of all those edges that
    // are shared by the triangles containing the 'keep' vertex.  Modify these
    // metrics and update the heap.
    TreeSet kModified = new TreeSet(); // <Edge>
    ExVector pkTSet = (ExVector) getTriangles(iVKeep).clone(); // <Triangle>
    if (pkTSet != null) {
      kTSet = (ExVector) pkTSet.clone();
      for (int j = 0; j < kTSet.size(); j++) {
        kT = (Triangle) kTSet.toArray()[j];
        kModified.add(new Edge(kT.vert[0], kT.vert[1]));
        kModified.add(new Edge(kT.vert[1], kT.vert[2]));
        kModified.add(new Edge(kT.vert[2], kT.vert[0]));
      }

      Iterator it = kModified.iterator();
      while (it.hasNext()) {
        Edge pkES = (Edge) it.next();
        pkEM = (EdgeAttribute) edgeMap.get(pkES);
        HeapRecord pkRecord = (HeapRecord) pkEM.data;
        float fMetric = getMetric(pkES, pkEM);
        if (pkRecord.m_iHIndex >= 0)
          update(pkRecord.m_iHIndex, fMetric);
      }
    }

    // save vertex reordering information
    Iterator it = deletedVertices.iterator();
    int iV;
    while (it.hasNext()) {
//      if(!( 0 <= m_iVCurrent && m_iVCurrent < m_akVertex.length )) throw new AssertionError();
      iV = ( (Integer) it.next()).intValue();
//      if(!( 0 <= iV && iV < m_akVertex.length )) throw new AssertionError();
      orderedVertices[currentVertex] = iV;
      permuteVertices[iV] = currentVertex;
      currentVertex--;
    }

    // Save the collapse information for use in constructing the final
    // collapse records for the caller of the constructor of this class.
    CollapseRecord kCR = new CollapseRecord(iVKeep, iVThrow, deletedVertices.size(),
                                            iTDeletions);
    deletedEdges.add(kCR);
  }

  public void flushVertices() {
    Iterator it = vertexMap.keySet().iterator();
    while (it.hasNext()) {
      Integer val = (Integer) it.next();
      orderedVertices[currentVertex] = val.intValue();
      permuteVertices[val.intValue()] = currentVertex;
      currentVertex--;
    }

//    if (!(m_iVCurrent == -1)) throw new AssertionError();
  }

  public void flushTriangles() {
    Iterator it = triangleMap.entrySet().iterator();
    while (it.hasNext()) {
      Entry entry = (Entry) it.next();
      TriangleAttribute pkTA = (TriangleAttribute) entry.getValue();
      int iTIndex = ( (Integer) pkTA.data).intValue();
      if (iTIndex >= 0) {
//        if (!(m_iTCurrent >= 0)) throw new AssertionError();
        newIndices[3 * currentTriangle] = indices[3 * iTIndex];
        newIndices[3 * currentTriangle + 1] = indices[3 * iTIndex + 1];
        newIndices[3 * currentTriangle + 2] = indices[3 * iTIndex + 2];
        currentTriangle--;
      }
    }

//    if (!(m_iTCurrent == -1)) throw new AssertionError();
  }

  public void reorder() {
    // permute the vertices and copy to the original array
    Vector3f[] akNewVertex = new Vector3f[vertices.length];
    int i;
    for (i = 0; i < vertices.length; i++)
      akNewVertex[i] = vertices[orderedVertices[i]];
    for (i = 0; i < vertices.length; i++)
      vertices[i] = akNewVertex[i];
    akNewVertex = null;

    // permute the normal vectors (if any)
    if (normals != null) {
      Vector3f[] akNewNormal = new Vector3f[vertices.length];
      for (i = 0; i < vertices.length; i++)
        akNewNormal[i] = normals[orderedVertices[i]];
      for (i = 0; i < vertices.length; i++)
        normals[i] = akNewNormal[i];
      akNewNormal = null;
    }

    // permute the colors (if any)
    if (colors != null) {
      ColorRGBA[] akNewColor = new ColorRGBA[vertices.length];
      for (i = 0; i < vertices.length; i++)
        akNewColor[i] = colors[orderedVertices[i]];
      for (i = 0; i < vertices.length; i++)
        colors[i] = akNewColor[i];
      akNewColor = null;
    }

    // permute the texture coordinates (if any)
    if (textures != null) {
      Vector2f[] akNewTexture = new Vector2f[vertices.length];
      for (i = 0; i < vertices.length; i++)
        akNewTexture[i] = textures[orderedVertices[i]];
      for (i = 0; i < vertices.length; i++)
        textures[i] = akNewTexture[i];
      akNewTexture = null;
    }

    // permute the connectivity array and copy to the original array
    for (i = 0; i < 3 * numbTriangles; i++)
      indices[i] = permuteVertices[newIndices[i]];

      // permute the keep/throw pairs
    for (i = 0; i < (int) deletedEdges.size(); i++) {
      CollapseRecord rkCR = (CollapseRecord) deletedEdges.get(i);
      rkCR.vertToKeep = permuteVertices[rkCR.vertToKeep];
      rkCR.vertToThrow = permuteVertices[rkCR.vertToThrow];
    }
  }

  public CollapseRecord[] computeRecords() {
    // build the collapse records for the caller
    int riCQuantity = (int) deletedEdges.size() + 1;
    CollapseRecord[] rakCRecord = new CollapseRecord[riCQuantity];
    for (int i = 0; i < riCQuantity; i++)
      rakCRecord[i] = new CollapseRecord();

      // initial record only stores the initial vertex and triangle quantities
    rakCRecord[0].numbVerts = vertices.length;
    rakCRecord[0].numbTriangles = numbTriangles;

    // construct the replacement arrays
    int iVQuantity = vertices.length, iTQuantity = numbTriangles;
    int iR, i;
    for (iR = 0; iR < (int) deletedEdges.size(); iR++) {
      CollapseRecord rkERecord = (CollapseRecord) deletedEdges.get(iR);
      CollapseRecord rkRecord = rakCRecord[iR + 1];

      iVQuantity -= rkERecord.numbVerts;
      iTQuantity -= rkERecord.numbTriangles;

      rkRecord.vertToKeep = rkERecord.vertToKeep;
      rkRecord.vertToThrow = rkERecord.vertToThrow;
      rkRecord.numbVerts = iVQuantity;
      rkRecord.numbTriangles = iTQuantity;
      rkRecord.numbIndices = 0;

      if (iTQuantity > 0) {
        int iIMax = 3 * iTQuantity;
        int[] aiIndex = new int[iIMax];
        for (i = 0; i < iIMax; i++) {
          if (indices[i] == rkRecord.vertToThrow) {
            indices[i] = rkRecord.vertToKeep;
            aiIndex[rkRecord.numbIndices++] = i;
          }
        }

        if (rkRecord.numbIndices > 0) {
          rkRecord.indices = new int[rkRecord.numbIndices];
          for (i = 0; i < rkRecord.numbIndices; i++)
            rkRecord.indices[i] = aiIndex[i];
        }

        aiIndex = null;
      } else {
        rkRecord.indices = null;
      }
    }

    // expand mesh back to original
    for (iR = riCQuantity - 1; iR > 0; iR--) {
      // restore indices in connectivity array
      CollapseRecord rkRecord = rakCRecord[iR];
      for (i = 0; i < rkRecord.numbIndices; i++) {
        int iC = rkRecord.indices[i];
//        if (!(m_aiConnect[iC] == rkRecord.vertToKeep)) throw new AssertionError();
        indices[iC] = rkRecord.vertToThrow;
      }
    }
    return rakCRecord;
  }

// ---------------------- heap operations ----------------------

  public void initializeHeap() {
    // It is possible that during an edge collapse, the number of *temporary*
    // edges is larger than the original number of edges in the mesh.  To
    // make sure there is enough heap space, allocate two times the number of
    // original edges.
    heapSize = (int) edgeMap.size();
    heapArray = new HeapRecord[2 * heapSize];

    int iHIndex = 0;
    Iterator it = edgeMap.entrySet().iterator();
    while (it.hasNext()) {
      Entry entry = (Entry) it.next();
      Edge pkE = (Edge) entry.getKey();
      EdgeAttribute pkEA = (EdgeAttribute) entry.getValue();
      heapArray[iHIndex] = (HeapRecord) pkEA.data;
      heapArray[iHIndex].m_kEdge = pkE;
      heapArray[iHIndex].m_iHIndex = iHIndex;
      heapArray[iHIndex].m_fMetric = getMetric(pkE, pkEA);
      iHIndex++;
    }

    sort();
  }

  public void sort() {
    int iLast = heapSize - 1;
    for (int iLeft = iLast / 2; iLeft >= 0; iLeft--) {
      HeapRecord pkRecord = heapArray[iLeft];
      int iPa = iLeft, iCh = 2 * iLeft + 1;
      while (iCh <= iLast) {
        if (iCh < iLast) {
          if (heapArray[iCh].m_fMetric > heapArray[iCh + 1].m_fMetric)
            iCh++;
        }

        if (heapArray[iCh].m_fMetric >= pkRecord.m_fMetric)
          break;

        heapArray[iCh].m_iHIndex = iPa;
        heapArray[iPa] = heapArray[iCh];
        iPa = iCh;
        iCh = 2 * iCh + 1;
      }

      pkRecord.m_iHIndex = iPa;
      heapArray[iPa] = pkRecord;
    }
  }

  public void add(float fMetric) {
    // Under normal heap operations, you would have to make sure that the
    // heap storage grows if necessary.  Increased storage demand will not
    // happen in this application.  The creation of the heap record itself is
    // done in OnEdgeCreate.
    heapSize++;

    int iCh = heapSize - 1;
    HeapRecord pkRecord = heapArray[iCh];
    pkRecord.m_fMetric = fMetric;
    while (iCh > 0) {
      int iPa = (iCh - 1) / 2;
      if (heapArray[iPa].m_fMetric <= fMetric)
        break;

      heapArray[iPa].m_iHIndex = iCh;
      heapArray[iCh] = heapArray[iPa];
      pkRecord.m_iHIndex = iPa;
      pkRecord.m_fMetric = fMetric;
      heapArray[iPa] = pkRecord;
      iCh = iPa;
    }

    heapArray[iCh].m_fMetric = fMetric;
  }

  public void remove() {
    HeapRecord pkRoot = heapArray[0];

    int iLast = heapSize - 1;
    HeapRecord pkRecord = heapArray[iLast];
    int iPa = 0, iCh = 1;
    while (iCh <= iLast) {
      if (iCh < iLast) {
        int iChP = iCh + 1;
        if (heapArray[iCh].m_fMetric > heapArray[iChP].m_fMetric)
          iCh = iChP;
      }

      if (heapArray[iCh].m_fMetric >= pkRecord.m_fMetric)
        break;

      heapArray[iCh].m_iHIndex = iPa;
      heapArray[iPa] = heapArray[iCh];
      iPa = iCh;
      iCh = 2 * iCh + 1;
    }

    pkRecord.m_iHIndex = iPa;
    heapArray[iPa] = pkRecord;
    heapSize--;

    // To notify OnEdgeDestroy that this edge was already removed from the
    // heap, but the object must be deleted by that callback.
    pkRoot.m_iHIndex = -1;
  }

  public void update(int iHIndex, float fMetric) {
    HeapRecord pkRecord = heapArray[iHIndex];
    int iPa, iCh, iChP, iMaxCh;

    if (fMetric > pkRecord.m_fMetric) {
      pkRecord.m_fMetric = fMetric;

      // new weight larger than old, propagate it towards the leaves
      iPa = iHIndex;
      iCh = 2 * iPa + 1;
      while (iCh < heapSize) {
        // at least one child exists
        if (iCh < heapSize - 1) {
          // two children exist
          iChP = iCh + 1;
          if (heapArray[iCh].m_fMetric <= heapArray[iChP].m_fMetric)
            iMaxCh = iCh;
          else
            iMaxCh = iChP;
        } else {
          // one child exists
          iMaxCh = iCh;
        }

        if (heapArray[iMaxCh].m_fMetric >= fMetric)
          break;

        heapArray[iMaxCh].m_iHIndex = iPa;
        heapArray[iPa] = heapArray[iMaxCh];
        pkRecord.m_iHIndex = iMaxCh;
        heapArray[iMaxCh] = pkRecord;
        iPa = iMaxCh;
        iCh = 2 * iPa + 1;
      }
    } else if (fMetric < pkRecord.m_fMetric) {
      pkRecord.m_fMetric = fMetric;

      // new weight smaller than old, propagate it towards the root
      iCh = iHIndex;
      while (iCh > 0) {
        // a parent exists
        iPa = (iCh - 1) / 2;

        if (heapArray[iPa].m_fMetric <= fMetric)
          break;

        heapArray[iPa].m_iHIndex = iCh;
        heapArray[iCh] = heapArray[iPa];
        pkRecord.m_iHIndex = iPa;
        pkRecord.m_fMetric = fMetric;
        heapArray[iPa] = pkRecord;
        iCh = iPa;
      }
    }
  }

  public boolean isValidHeap(int iStart, int iFinal) {
    for (int iC = iStart; iC <= iFinal; iC++) {
      int iP = (iC - 1) / 2;
      if (iP > iStart) {
        if (heapArray[iP].m_fMetric > heapArray[iC].m_fMetric)
          return false;

        if (heapArray[iP].m_iHIndex != iP)
          return false;
      }
    }

    return true;
  }

  public boolean isValidHeap() {
    return isValidHeap(0, heapSize - 1);
  }

// mesh insert/remove callbacks

  public void onVertexInsert(Integer vert, boolean bCreate, VertexAttribute att) {
    // It is possible that a 'keep' vertex was removed because the triangles
    // sharing the collapse edge were removed first, but then the insertion
    // of a modified triangle reinserts the 'keep' vertex.
    if (bCreate && collapsing)
      deletedVertices.remove(vert);
  }

  public void onVertexRemove(Integer vert, boolean bDestroy, VertexAttribute att) {
    // Keep track of vertices removed during the edge collapse.
    if (bDestroy && collapsing)
      deletedVertices.add(vert);
  }

  public void onEdgeInsert(Edge rkE, boolean bCreate, EdgeAttribute att) {
    if (bCreate) {
      att.data = new HeapRecord();
      if (collapsing) {
        heapArray[heapSize] = (HeapRecord) att.data;
        heapArray[heapSize].m_kEdge = rkE;
        heapArray[heapSize].m_iHIndex = heapSize;
        add(getMetric(rkE, (EdgeAttribute) edgeMap.get(rkE)));
      }
    } else {
      if (collapsing) {
        HeapRecord pkRecord = (HeapRecord) att.data;
//        if (!(pkRecord.m_kEdge.equals(rkE))) throw new AssertionError();
        if (pkRecord.m_iHIndex >= 0) {
          update(pkRecord.m_iHIndex,
                 getMetric(rkE, (EdgeAttribute) edgeMap.get(rkE)));
        } else {
//          if (!(pkRecord.m_iHIndex == -1)) throw new AssertionError();
          pkRecord.m_iHIndex = heapSize;
          add(getMetric(rkE, (EdgeAttribute) edgeMap.get(rkE)));
        }
      }
    }
  }

  public void onEdgeRemove(Edge rkE, boolean bDestroy, EdgeAttribute att) {
    // Remove the edge from the heap.  The metric of the edge is set to
    // -INFINITY so that it has the minimum value of all edges.  The update
    // call bubbles the edge to the root of the heap.  The edge is then
    // removed from the root.

    if (bDestroy) {
      HeapRecord pkRecord = (HeapRecord) att.data;
      if (pkRecord.m_iHIndex >= 0) {
        update(pkRecord.m_iHIndex, -Float.MAX_VALUE);
        remove();
      }
      pkRecord = null;
    }
  }

  public void onTriangleInsert(Triangle tri, boolean bCreate,
                               TriangleAttribute att) {
    if (bCreate)
      att.data = new Integer( -1);
  }

  public void onTriangleRemove(Triangle tri, boolean bDestroy,
                               TriangleAttribute att) {
    if (bDestroy)
      att.data = null;
  }

}
