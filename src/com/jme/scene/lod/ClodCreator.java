package com.jme.scene.lod;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.math.Vector2f;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ClodCreator extends VETMesh {
  private Vector3f[] m_akVertex;
  private Vector3f[] m_akNormal;
  private ColorRGBA[] m_akColor;
  private Vector2f[] m_akTexture;
  private int[] m_aiConnect;

  private int m_iVCurrent, m_iTCurrent, m_iTQuantity;
  private int[] m_aiVOrdered;
  private int[] m_aiVPermute;
  private int[] m_aiNewConnect;

  int m_iHQuantity;

  HeapRecord[] m_apkHeap;
  boolean m_bCollapsing;

  // for reordering vertices and triangles
  TreeSet m_kVDelete; // <int>
  Vector m_kEDelete; // <CollapseRecord>
  CollapseRecord[] rakCRecord;

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
//      if (obj == null) return false;
      HeapRecord rkH = (HeapRecord)obj;
      return m_kEdge.equals(rkH.m_kEdge);
    }
  };

  public ClodCreator(
      Vector3f[] vertices,
      Vector3f[] normal,
      ColorRGBA[] color,
      Vector2f[] texture,
      int[] indices) {
    // Hang onto these to avoid having to pass them through member function
    // calls.
    m_akVertex = vertices;
    m_akNormal = normal;
    m_akColor = color;
    m_akTexture = texture;
    m_aiConnect = indices;
    m_iTQuantity = indices.length/3;

    // for reordering vertices and triangles
    m_iVCurrent = vertices.length - 1;
    m_iTCurrent = m_iTQuantity - 1;
    m_aiVOrdered = new int[vertices.length];
    m_aiVPermute = new int[vertices.length];
    m_aiNewConnect = new int[indices.length];

    m_kEDelete = new Vector();
    m_kVDelete = new TreeSet();

    // Insert the triangles into the mesh.  The triangle indices are attached
    // as extra data.
    m_bCollapsing = false;
    for (int i = 0; i < m_iTQuantity; i++) {
      int iV0 = indices[3 * i];
      int iV1 = indices[3 * i + 1];
      int iV2 = indices[3 * i + 2];
      if (!(iV0 != iV1 && iV0 != iV2 && iV1 != iV2)) throw new AssertionError();
      Triangle kT = new Triangle(indices[3 * i], indices[3 * i + 1],
                                 indices[3 * i + 2]);
      insertTriangle(kT);
      setData(kT, new Integer(i));
    }

    if (m_kVMap.size() != m_akVertex.length)throw new AssertionError();
    if (m_kTMap.size() != m_iTQuantity)throw new AssertionError(
        "triangle map size: " + m_kTMap.size() + " != m_iTQuantity: " +
        m_iTQuantity);

    initializeHeap();

    m_bCollapsing = true;
//    System.err.println("m_iHQuantity: "+m_iHQuantity);
    while (m_iHQuantity > 0) {
      if (m_apkHeap[0].m_fMetric == Float.MAX_VALUE) {
        // all remaining heap elements have infinite weight
        flushVertices();
        flushTriangles();
        break;
      }

      doCollapse();
//      System.err.println("triangle map size: " + m_kTMap.size() + " m_iTCurrent+1: " +
//          (m_iTCurrent + 1));

      if (! ( (m_kVMap.size()) == m_iVCurrent + 1))throw new AssertionError();
      if (! ( (m_kTMap.size()) == m_iTCurrent + 1))throw new AssertionError(
          "triangle map size: " + m_kTMap.size() + " != m_iTCurrent+1: " +
          (m_iTCurrent + 1));
    }
//    System.err.println("done with while");
//    for (int j = 0; j < m_kTMap.size(); j++) {
//      Triangle t = (Triangle)m_kTMap.keySet().toArray()[j];
//      System.err.println("tri: "+j+"  v: "+t.m_aiV[0]+", "+t.m_aiV[1]+", "+t.m_aiV[2]);
//    }
    m_bCollapsing = false;

    // Permute the vertices and triangle connectivity so that the last
    // vertex/triangle in the array is the first vertex/triangle to be
    // removed.
    reorder();
//    System.err.println("done with reorder");
//    for (int j = 0; j < m_kTMap.size(); j++) {
//      Triangle t = (Triangle)m_kTMap.keySet().toArray()[j];
//      System.err.println("tri: "+j+"  v: "+t.m_aiV[0]+", "+t.m_aiV[1]+", "+t.m_aiV[2]);
//    }

    // The collapse records store the incremental changes that are used for
    // dynamic LOD changes in the caller of this constructor.
    rakCRecord = computeRecords();
//    System.err.println("done with computeRecords");
//    for (int j = 0; j < m_kTMap.size(); j++) {
//      Triangle t = (Triangle)m_kTMap.keySet().toArray()[j];
//      System.err.println("tri: "+j+"  v: "+t.m_aiV[0]+", "+t.m_aiV[1]+", "+t.m_aiV[2]);
//    }

//    for (int x = 0; x < rakCRecord.length; x++) {
//      System.err.println("***** record: "+x);
//      System.err.println("keep: "+rakCRecord[x].vertToKeep);
//      System.err.println("throw: "+rakCRecord[x].vertToThrow);
//      System.err.println("indices: "+rakCRecord[x].indices);
//      System.err.println("tris: "+rakCRecord[x].m_iTQuantity);
//      System.err.println("inds: "+rakCRecord[x].m_iIQuantity);
//      System.err.println("verts: "+rakCRecord[x].m_iVQuantity);
//    }
  }

  public CollapseRecord[] getRecords() {
    return rakCRecord;
  }

  public void doCollapse() {
//    System.err.println("do collapse!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    // Define a 2-edge to be an edge that has exactly two triangles sharing
    // it.  An edge is collapsible if it is a 2-edge and has at least one end
    // point whose sharing edges are all 2-edges.  In this case, such an end
    // point will be the 'throw' vertex.  This keeps the boundary and junction
    // edges from changing geometry and helps preserve the shape of the mesh.
    // The topology is always guaranteed not to change.

    // When this function is called, the metric has already been calculated
    // and is finite (so exactly two triangles must be sharing this edge).
    if (!(m_apkHeap[0].m_fMetric < Float.MAX_VALUE)) throw new AssertionError();
    Edge kEdge = m_apkHeap[0].m_kEdge;

    // test end points to see if either has only 2-edges sharing it
    int i;
    for (i = 0; i < 2; i++) {
      ExVector pkESet = (ExVector)getEdges(kEdge.m_aiV[i]).clone();
      int j;
      for (j = 0; j < pkESet.size(); j++) {
        EdgeAttribute pkEM = (EdgeAttribute)m_kEMap.get(pkESet.toArray()[j]);
        if (!(pkEM != null)) throw new AssertionError();
        if (pkEM.m_kTSet.size() != 2)
          break;
      }

      if (j == pkESet.size()) {
        // all edges sharing this end point are 2-edges
        break;
      }
    }

    if (i < 2) {
      int iVThrow = kEdge.m_aiV[i];
      int iVKeep = kEdge.m_aiV[1 - i];
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
    VertexAttribute pkVT = (VertexAttribute) m_kVMap.get(new Integer(iVThrow));
    if (!(pkVT != null)) throw new AssertionError();

    Edge kCollapse = new Edge(iVKeep, iVThrow);
    for (int j = 0; j < pkVT.m_kTSet.size(); j++) {
      Triangle kT = (Triangle)pkVT.m_kTSet.toArray()[j];
      if (kCollapse.equals(new Edge(kT.m_aiV[0], kT.m_aiV[1]))
          || kCollapse.equals(new Edge(kT.m_aiV[1], kT.m_aiV[2]))
          || kCollapse.equals(new Edge(kT.m_aiV[2], kT.m_aiV[0]))) {
        // This triangle would be removed in a collapse, so it does not
        // contribute to any folding.
        continue;
      }

      for (int i = 0; i < 3; i++) {
        if (kT.m_aiV[i] == iVThrow) {
          // Test if potential replacement triangle (either ordering)
          // is in the mesh.
          int iV0 = iVKeep;
          int iV1 = kT.m_aiV[ (i + 1) % 3];
          int iV2 = kT.m_aiV[ (i + 2) % 3];

          if (m_kTMap.get(new Triangle(iV0, iV1, iV2)) != null
              || m_kTMap.get(new Triangle(iV0, iV2, iV1)) != null) {
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
//    System.err.println("pkEA.m_kTSet.size(): "+pkEA.m_kTSet.size());
    if (pkEA.m_kTSet.size() == 2) {
      // length contribution
      Vector3f rkEnd0 = m_akVertex[pkE.m_aiV[0]];
      Vector3f rkEnd1 = m_akVertex[pkE.m_aiV[1]];
      Vector3f kDiff = rkEnd1.subtract(rkEnd0);
      float fMetric = fLengthWeight * kDiff.length();

      // angle/area contribution
      Triangle kT = (Triangle) pkEA.m_kTSet.toArray()[0];
      Vector3f kV0 = m_akVertex[kT.m_aiV[0]];
      Vector3f kV1 = m_akVertex[kT.m_aiV[1]];
      Vector3f kV2 = m_akVertex[kT.m_aiV[2]];
      Vector3f kE0 = kV1.subtract(kV0);
      Vector3f kE1 = kV2.subtract(kV0);
      Vector3f kN0 = kE0.cross(kE1);

      kT = (Triangle) pkEA.m_kTSet.toArray()[1];
      kV0 = m_akVertex[kT.m_aiV[0]];
      kV1 = m_akVertex[kT.m_aiV[1]];
      kV2 = m_akVertex[kT.m_aiV[2]];
      kE0 = kV1.subtract(kV0);
      kE1 = kV2.subtract(kV0);
      Vector3f kN1 = kE0.cross(kE1);

      Vector3f kCross = kN0.cross(kN1);
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
//    System.err.println("flushing tri... index: "+iTIndex);
    if (iTIndex >= 0) {
      if (!(m_iTCurrent >= 0)) throw new AssertionError();
      m_aiNewConnect[3 * m_iTCurrent] = m_aiConnect[3 * iTIndex];
      m_aiNewConnect[3 * m_iTCurrent + 1] = m_aiConnect[3 * iTIndex + 1];
      m_aiNewConnect[3 * m_iTCurrent + 2] = m_aiConnect[3 * iTIndex + 2];
      m_iTCurrent--;
    }

    super.removeTriangle(rkT);
  }

  public void modifyTriangle(Triangle rkT, int iVKeep, int iVThrow) {
//  #ifdef _DEBUG
//      int iTStart = (int)m_kTMap.size();
//  #endif

    // Get the index of the pre-modified triangle, then remove the triangle
    // from the mesh.
//    System.err.println("mod tri: "+rkT.hashCodeStr());
    int iTIndex = ( (Integer) getData(rkT)).intValue();
//    System.err.println("iTIndex: "+iTIndex);
//    System.err.println("MOD TRI.  MAP SIZE 1:"+m_kTMap.size());
    super.removeTriangle(rkT);
//    System.err.println("MOD TRI.  MAP SIZE 2:"+m_kTMap.size());

    // replace 'throw' by 'keep'
    for (int i = 0; i < 3; i++) {
      if (rkT.m_aiV[i] == iVThrow) {
        rkT.m_aiV[i] = iVKeep;
        break;
      }
    }

    // Indices on modified triangles are the same as the indices on the
    // pre-modified triangles.
    insertTriangle(rkT);
//    System.err.println("MOD TRI.  MAP SIZE 3:"+m_kTMap.size());
    setData(rkT, new Integer(iTIndex));
//      *(int*)getData(rkT) = iTIndex;

//  #ifdef _DEBUG
//      int iTFinal = (int)m_kTMap.size();
//      if (!( iTFinal == iTStart );
//  #endif
  }

  public void collapseEdge(int iVKeep, int iVThrow) {
//    System.err.println("collapse Edge");
//  #ifdef _DEBUG
//      int iVStart = (int)m_kVMap.size();
//      int iTStart = (int)m_kTMap.size();
//      if (!( iVStart > 0 && iTStart > 0 );
//  #endif

    // find the edge to collapse
    Edge kCollapse = new Edge(iVKeep, iVThrow);
    EdgeAttribute pkEM = (EdgeAttribute) m_kEMap.get(kCollapse);
    if (pkEM == null) throw new AssertionError("Edge unexpectedly missing from EdgeMap!");

    // keep track of vertices that are deleted in the collapse
    m_kVDelete.clear();

    // Remove the collapse-edge-shared triangles.  Using a copy of the
    // triangle set from the collapse edge is required since removal of the
    // last triangle sharing the collapse edge will remove that edge from
    // the edge map, thereby invalidating any iterator that points to data
    // in the collapse edge.
    ExVector kTSet = (ExVector)pkEM.m_kTSet.clone(); // <Triangle>
    int iTDeletions = kTSet.size();
    if (!(iTDeletions > 0)) throw new AssertionError();
    for (int j = 0; j < kTSet.size(); j++)
      removeTriangle( (Triangle) kTSet.toArray()[j]);

      // Replace 'throw' vertices by 'keep' vertices in the remaining triangles
      // at the 'throw' vertex.  The old triangles are removed and the modified
      // triangles are inserted.
    Triangle kT;
    VertexAttribute pkVM = (VertexAttribute) m_kVMap.get(new Integer(iVThrow));
    if (pkVM != null) {
      kTSet = (ExVector)pkVM.m_kTSet.clone();
      for (int j = 0; j < kTSet.size(); j++) {
        kT = (Triangle) kTSet.toArray()[j];
        modifyTriangle(kT, iVKeep, iVThrow);
      }
    }

    // The set of potentially modified edges consists of all those edges that
    // are shared by the triangles containing the 'keep' vertex.  Modify these
    // metrics and update the heap.
    TreeSet kModified = new TreeSet(); // <Edge>
    ExVector pkTSet = (ExVector)getTriangles(iVKeep).clone(); // <Triangle>
    if (pkTSet != null) {
      kTSet = (ExVector)pkTSet.clone();
      for (int j = 0; j < kTSet.size(); j++) {
        kT = (Triangle) kTSet.toArray()[j];
        kModified.add(new Edge(kT.m_aiV[0], kT.m_aiV[1]));
        kModified.add(new Edge(kT.m_aiV[1], kT.m_aiV[2]));
        kModified.add(new Edge(kT.m_aiV[2], kT.m_aiV[0]));
      }

      Iterator it = kModified.iterator();
      while (it.hasNext()) {
        Edge pkES = (Edge)it.next();
        pkEM = (EdgeAttribute) m_kEMap.get(pkES);
        HeapRecord pkRecord = (HeapRecord) pkEM.m_pvData;
        float fMetric = getMetric(pkES, pkEM);
//        System.err.println("C: m_iHIndex: "+pkRecord.m_iHIndex+" edge:"+pkRecord.m_kEdge.hashCode());
        if (pkRecord.m_iHIndex >= 0)
          update(pkRecord.m_iHIndex, fMetric);
      }
    }

//  #ifdef _DEBUG
//      int iVFinal = (int)m_kVMap.size();
//      int iVDiff = iVStart - iVFinal;
//      int iTFinal = (int)m_kTMap.size();
//      int iTDiff = iTStart - iTFinal;
//      if (!( iVDiff == (int)(m_kVDelete.size()) && iTDiff == iTDeletions );
//  #endif

    // save vertex reordering information
    Iterator it = m_kVDelete.iterator();
    int iV;
    while (it.hasNext()) {
      if(!( 0 <= m_iVCurrent && m_iVCurrent < m_akVertex.length )) throw new AssertionError();
      iV = ((Integer)it.next()).intValue();
      if(!( 0 <= iV && iV < m_akVertex.length )) throw new AssertionError();
      m_aiVOrdered[m_iVCurrent] = iV;
      m_aiVPermute[iV] = m_iVCurrent;
      m_iVCurrent--;
    }

    // Save the collapse information for use in constructing the final
    // collapse records for the caller of the constructor of this class.
    CollapseRecord kCR = new CollapseRecord(iVKeep, iVThrow, m_kVDelete.size(), iTDeletions);
//    System.err.println("we got a new record! "+kCR);
    m_kEDelete.add(kCR);
  }

  public void flushVertices() {
    Iterator it = m_kVMap.keySet().iterator();
    while (it.hasNext()) {
      Integer val = (Integer)it.next();
      m_aiVOrdered[m_iVCurrent] = val.intValue();
      m_aiVPermute[val.intValue()] = m_iVCurrent;
      m_iVCurrent--;
    }

    if (!(m_iVCurrent == -1)) throw new AssertionError();
  }

  public void flushTriangles() {
//    System.err.println("TRI MAP SIZE (b4 flush): "+m_kTMap.size());
    Iterator it = m_kTMap.entrySet().iterator();
    while (it.hasNext()) {
      Entry entry = (Entry)it.next();
      TriangleAttribute pkTA = (TriangleAttribute)entry.getValue();
      int iTIndex = ( (Integer) pkTA.m_pvData).intValue();
//      System.err.println("flushing tri... index: "+iTIndex);
      if (iTIndex >= 0) {
        if (!(m_iTCurrent >= 0)) throw new AssertionError();
        m_aiNewConnect[3 * m_iTCurrent] = m_aiConnect[3 * iTIndex];
        m_aiNewConnect[3 * m_iTCurrent + 1] = m_aiConnect[3 * iTIndex + 1];
        m_aiNewConnect[3 * m_iTCurrent + 2] = m_aiConnect[3 * iTIndex + 2];
        m_iTCurrent--;
      }
    }

    if (!(m_iTCurrent == -1)) throw new AssertionError();
  }

  public void reorder() {
    // permute the vertices and copy to the original array
    Vector3f[] akNewVertex = new Vector3f[m_akVertex.length];
    int i;
    for (i = 0; i < m_akVertex.length; i++)
      akNewVertex[i] = m_akVertex[m_aiVOrdered[i]];
    for (i = 0; i < m_akVertex.length; i++)
      m_akVertex[i] = akNewVertex[i];
    akNewVertex = null;

    // permute the normal vectors (if any)
    if (m_akNormal != null) {
      Vector3f[] akNewNormal = new Vector3f[m_akVertex.length];
      for (i = 0; i < m_akVertex.length; i++)
        akNewNormal[i] = m_akNormal[m_aiVOrdered[i]];
      for (i = 0; i < m_akVertex.length; i++)
        m_akNormal[i] = akNewNormal[i];
      akNewNormal = null;
    }

    // permute the colors (if any)
    if (m_akColor != null) {
      ColorRGBA[] akNewColor = new ColorRGBA[m_akVertex.length];
      for (i = 0; i < m_akVertex.length; i++)
        akNewColor[i] = m_akColor[m_aiVOrdered[i]];
      for (i = 0; i < m_akVertex.length; i++)
        m_akColor[i] = akNewColor[i];
      akNewColor = null;
    }

    // permute the texture coordinates (if any)
    if (m_akTexture != null) {
      Vector2f[] akNewTexture = new Vector2f[m_akVertex.length];
      for (i = 0; i < m_akVertex.length; i++)
        akNewTexture[i] = m_akTexture[m_aiVOrdered[i]];
      for (i = 0; i < m_akVertex.length; i++)
        m_akTexture[i] = akNewTexture[i];
      akNewTexture = null;
    }

    // permute the connectivity array and copy to the original array
    for (i = 0; i < 3*m_iTQuantity; i++)
      m_aiConnect[i] = m_aiVPermute[m_aiNewConnect[i]];

      // permute the keep/throw pairs
    for (i = 0; i < (int) m_kEDelete.size(); i++) {
      CollapseRecord rkCR = (CollapseRecord) m_kEDelete.get(i);
      rkCR.vertToKeep = m_aiVPermute[rkCR.vertToKeep];
      rkCR.vertToThrow = m_aiVPermute[rkCR.vertToThrow];
    }
  }

  public CollapseRecord[] computeRecords() {
    // build the collapse records for the caller
    int riCQuantity = (int) m_kEDelete.size() + 1;
//    System.err.println("riCQuantity: "+riCQuantity);
    CollapseRecord[] rakCRecord = new CollapseRecord[riCQuantity];
    for (int i = 0; i < riCQuantity; i++)
      rakCRecord[i] = new CollapseRecord();

    // initial record only stores the initial vertex and triangle quantities
    rakCRecord[0].m_iVQuantity = m_akVertex.length;
    rakCRecord[0].m_iTQuantity = m_iTQuantity;

    // construct the replacement arrays
    int iVQuantity = m_akVertex.length, iTQuantity = m_iTQuantity;
    int iR, i;
    for (iR = 0; iR < (int) m_kEDelete.size(); iR++) {
      CollapseRecord rkERecord = (CollapseRecord) m_kEDelete.get(iR);
      CollapseRecord rkRecord = rakCRecord[iR + 1];

      iVQuantity -= rkERecord.m_iVQuantity;
      iTQuantity -= rkERecord.m_iTQuantity;

      rkRecord.vertToKeep = rkERecord.vertToKeep;
      rkRecord.vertToThrow = rkERecord.vertToThrow;
      rkRecord.m_iVQuantity = iVQuantity;
      rkRecord.m_iTQuantity = iTQuantity;
      rkRecord.m_iIQuantity = 0;

      if (iTQuantity > 0) {
        int iIMax = 3 * iTQuantity;
        int[] aiIndex = new int[iIMax];
        for (i = 0; i < iIMax; i++) {
          if (m_aiConnect[i] == rkRecord.vertToThrow) {
            m_aiConnect[i] = rkRecord.vertToKeep;
            aiIndex[rkRecord.m_iIQuantity++] = i;
          }
        }

        if (rkRecord.m_iIQuantity > 0) {
          rkRecord.indices = new int[rkRecord.m_iIQuantity];
          for (i = 0; i < rkRecord.m_iIQuantity; i++)
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
      for (i = 0; i < rkRecord.m_iIQuantity; i++) {
        int iC = rkRecord.indices[i];
        if (!(m_aiConnect[iC] == rkRecord.vertToKeep)) throw new AssertionError();
        m_aiConnect[iC] = rkRecord.vertToThrow;
      }
    }
    return rakCRecord;
  }

// heap operations

  public void initializeHeap() {
    // It is possible that during an edge collapse, the number of *temporary*
    // edges is larger than the original number of edges in the mesh.  To
    // make sure there is enough heap space, allocate two times the number of
    // original edges.
    m_iHQuantity = (int) m_kEMap.size();
    m_apkHeap = new HeapRecord[2 * m_iHQuantity];

    int iHIndex = 0;
    Iterator it = m_kEMap.entrySet().iterator();
    while (it.hasNext()) {
      Entry entry = (Entry)it.next();
      Edge pkE = (Edge)entry.getKey();
      EdgeAttribute pkEA = (EdgeAttribute)entry.getValue();
      m_apkHeap[iHIndex] = (HeapRecord) pkEA.m_pvData;
      m_apkHeap[iHIndex].m_kEdge = pkE;
      m_apkHeap[iHIndex].m_iHIndex = iHIndex;
      m_apkHeap[iHIndex].m_fMetric = getMetric(pkE, pkEA);
      iHIndex++;
    }
//    System.err.println("initial iHIndex: "+iHIndex);

    sort();
  }

  public void sort() {
    int iLast = m_iHQuantity - 1;
    for (int iLeft = iLast / 2; iLeft >= 0; iLeft--) {
      HeapRecord pkRecord = m_apkHeap[iLeft];
      int iPa = iLeft, iCh = 2 * iLeft + 1;
      while (iCh <= iLast) {
        if (iCh < iLast) {
          if (m_apkHeap[iCh].m_fMetric > m_apkHeap[iCh + 1].m_fMetric)
            iCh++;
        }

        if (m_apkHeap[iCh].m_fMetric >= pkRecord.m_fMetric)
          break;

        m_apkHeap[iCh].m_iHIndex = iPa;
        m_apkHeap[iPa] = m_apkHeap[iCh];
        iPa = iCh;
        iCh = 2 * iCh + 1;
      }

      pkRecord.m_iHIndex = iPa;
      m_apkHeap[iPa] = pkRecord;
    }
  }

  public void add(float fMetric) {
    // Under normal heap operations, you would have to make sure that the
    // heap storage grows if necessary.  Increased storage demand will not
    // happen in this application.  The creation of the heap record itself is
    // done in OnEdgeCreate.
    m_iHQuantity++;

    int iCh = m_iHQuantity - 1;
    HeapRecord pkRecord = m_apkHeap[iCh];
    pkRecord.m_fMetric = fMetric;
    while (iCh > 0) {
      int iPa = (iCh - 1) / 2;
      if (m_apkHeap[iPa].m_fMetric <= fMetric)
        break;

      m_apkHeap[iPa].m_iHIndex = iCh;
      m_apkHeap[iCh] = m_apkHeap[iPa];
      pkRecord.m_iHIndex = iPa;
      pkRecord.m_fMetric = fMetric;
      m_apkHeap[iPa] = pkRecord;
      iCh = iPa;
    }

    m_apkHeap[iCh].m_fMetric = fMetric;
  }

  public void remove() {
    HeapRecord pkRoot = m_apkHeap[0];

    int iLast = m_iHQuantity - 1;
    HeapRecord pkRecord = m_apkHeap[iLast];
    int iPa = 0, iCh = 1;
    while (iCh <= iLast) {
      if (iCh < iLast) {
        int iChP = iCh + 1;
        if (m_apkHeap[iCh].m_fMetric > m_apkHeap[iChP].m_fMetric)
          iCh = iChP;
      }

      if (m_apkHeap[iCh].m_fMetric >= pkRecord.m_fMetric)
        break;

      m_apkHeap[iCh].m_iHIndex = iPa;
      m_apkHeap[iPa] = m_apkHeap[iCh];
      iPa = iCh;
      iCh = 2 * iCh + 1;
    }

    pkRecord.m_iHIndex = iPa;
//    System.err.println("D1: m_iHIndex: "+pkRecord.m_iHIndex+" edge:"+pkRecord.m_kEdge.hashCode());
    m_apkHeap[iPa] = pkRecord;
    m_iHQuantity--;

    // To notify OnEdgeDestroy that this edge was already removed from the
    // heap, but the object must be deleted by that callback.
    pkRoot.m_iHIndex = -1;
//    System.err.println("D2: m_iHIndex: "+pkRoot.m_iHIndex+" edge:"+pkRoot.m_kEdge.hashCode());
  }

  public void update(int iHIndex, float fMetric) {
    HeapRecord pkRecord = m_apkHeap[iHIndex];
    int iPa, iCh, iChP, iMaxCh;

    if (fMetric > pkRecord.m_fMetric) {
      pkRecord.m_fMetric = fMetric;

      // new weight larger than old, propagate it towards the leaves
      iPa = iHIndex;
      iCh = 2 * iPa + 1;
      while (iCh < m_iHQuantity) {
        // at least one child exists
        if (iCh < m_iHQuantity - 1) {
          // two children exist
          iChP = iCh + 1;
          if (m_apkHeap[iCh].m_fMetric <= m_apkHeap[iChP].m_fMetric)
            iMaxCh = iCh;
          else
            iMaxCh = iChP;
        } else {
          // one child exists
          iMaxCh = iCh;
        }

        if (m_apkHeap[iMaxCh].m_fMetric >= fMetric)
          break;

        m_apkHeap[iMaxCh].m_iHIndex = iPa;
        m_apkHeap[iPa] = m_apkHeap[iMaxCh];
        pkRecord.m_iHIndex = iMaxCh;
        m_apkHeap[iMaxCh] = pkRecord;
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

        if (m_apkHeap[iPa].m_fMetric <= fMetric)
          break;

        m_apkHeap[iPa].m_iHIndex = iCh;
        m_apkHeap[iCh] = m_apkHeap[iPa];
        pkRecord.m_iHIndex = iPa;
        pkRecord.m_fMetric = fMetric;
        m_apkHeap[iPa] = pkRecord;
        iCh = iPa;
      }
    }
//    System.err.println("F: m_iHIndex: "+pkRecord.m_iHIndex+" edge:"+pkRecord.m_kEdge.hashCode());
  }

  public boolean isValidHeap(int iStart, int iFinal) {
    for (int iC = iStart; iC <= iFinal; iC++) {
      int iP = (iC - 1) / 2;
      if (iP > iStart) {
        if (m_apkHeap[iP].m_fMetric > m_apkHeap[iC].m_fMetric)
          return false;

        if (m_apkHeap[iP].m_iHIndex != iP)
          return false;
      }
    }

    return true;
  }

  public boolean isValidHeap() {
    return isValidHeap(0, m_iHQuantity - 1);
  }

  public void printHeap(String acFilename) {
//      ofstream kOStr(acFilename);
//      for (int i = 0; i < m_iHQuantity; i++)
//      {
//          HeapRecord* pkRecord = m_apkHeap[i];
//          kOStr << pkRecord.m_iHIndex << "= <" << pkRecord.m_kEdge.m_aiV[0]
//                << ',' << pkRecord.m_kEdge.m_aiV[1] << "> "
//                << pkRecord.m_fMetric << endl;
//
//          int iValue = i+2;
//          int iBits = 0;
//          while ( iValue != 0 )
//          {
//              if ( iValue & 1 )
//                  iBits++;
//              iValue >>= 1;
//          }
//          if ( iBits == 1 )
//              kOStr << endl;
//      }
  }

// mesh insert/remove callbacks

  public void onVertexInsert(int iV, boolean bCreate, VertexAttribute att) {
    // It is possible that a 'keep' vertex was removed because the triangles
    // sharing the collapse edge were removed first, but then the insertion
    // of a modified triangle reinserts the 'keep' vertex.
    if (bCreate && m_bCollapsing)
      m_kVDelete.remove(new Integer(iV));
  }

  public void onVertexRemove(int iV, boolean bDestroy, VertexAttribute att) {
    // Keep track of vertices removed during the edge collapse.
    if (bDestroy && m_bCollapsing)
      m_kVDelete.add(new Integer(iV));
  }

  public void onEdgeInsert(Edge rkE, boolean bCreate, EdgeAttribute att) {
    if (bCreate) {
      att.m_pvData = new HeapRecord();
      if (m_bCollapsing) {
//        System.err.println("A: m_iHIndex: "+m_iHQuantity+" edge:"+rkE.hashCode());
        m_apkHeap[m_iHQuantity] = (HeapRecord) att.m_pvData;
        m_apkHeap[m_iHQuantity].m_kEdge = rkE;
        m_apkHeap[m_iHQuantity].m_iHIndex = m_iHQuantity;
        add(getMetric(rkE, (EdgeAttribute) m_kEMap.get(rkE)));
      }
    } else {
      if (m_bCollapsing) {
        HeapRecord pkRecord = (HeapRecord) att.m_pvData;
//        System.err.println("B: m_iHIndex: "+pkRecord.m_iHIndex+" edge:"+rkE.hashCode());
        if (!(pkRecord.m_kEdge.equals(rkE))) throw new AssertionError();
        if (pkRecord.m_iHIndex >= 0) {
          update(pkRecord.m_iHIndex,
                 getMetric(rkE, (EdgeAttribute) m_kEMap.get(rkE)));
        } else {
          if (!(pkRecord.m_iHIndex == -1)) throw new AssertionError();
          pkRecord.m_iHIndex = m_iHQuantity;
          add(getMetric(rkE, (EdgeAttribute) m_kEMap.get(rkE)));
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
      HeapRecord pkRecord = (HeapRecord) att.m_pvData;
      if (pkRecord.m_iHIndex >= 0) {
        update(pkRecord.m_iHIndex, -Float.MAX_VALUE);
//        System.err.println("inside onEdgeRemove - before remove");
        remove();
//        System.err.println("inside onEdgeRemove - after remove");
      }
      pkRecord = null;
    }
  }

  public void onTriangleInsert(Triangle tri, boolean bCreate,
                               TriangleAttribute att) {
    if (bCreate)
      att.m_pvData = new Integer( -1);
  }

  public void onTriangleRemove(Triangle tri, boolean bDestroy,
                               TriangleAttribute att) {
    if (bDestroy)
      att.m_pvData = null;
  }

}
