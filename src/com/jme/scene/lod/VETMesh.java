package com.jme.scene.lod;

import java.util.TreeSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class VETMesh {

  protected TreeMap m_kVMap; // std::map<Integer,VertexAttribute>
  protected TreeMap m_kEMap; // std::map<Edge,EdgeAttribute>
  protected TreeMap m_kTMap; // std::map<Triangle,TriangleAttribute>

  // vertex is <v>
  // edge is <v0,v1> where v0 = min(v0,v1)
  // triangle is <v0,v1,v2> where v0 = min(v0,v1,v2)

  public class Edge implements Comparable {
    int m_aiV[] = new int[2];

    public Edge() {}

    public Edge(int iV0, int iV1) {
      if (iV0 < iV1) {
        // v0 is minimum
        m_aiV[0] = iV0;
        m_aiV[1] = iV1;
      } else {
        // v1 is minimum
        m_aiV[0] = iV1;
        m_aiV[1] = iV0;
      }
    }

    public boolean lessThan(Edge rkE) {
      if (m_aiV[1] < rkE.m_aiV[1])
        return true;

      if (m_aiV[1] == rkE.m_aiV[1])
        return m_aiV[0] < rkE.m_aiV[0];

      return false;
    }

    public boolean equals(Object obj) {
      Edge rkE = (Edge)obj;
      return (m_aiV[0] == rkE.m_aiV[0]) && (m_aiV[1] == rkE.m_aiV[1]);
    }

    public int compareTo(Object o) {
      Edge e = (Edge)o;
      if (lessThan(e))
        return -1;
      else if (equals(e))
        return 0;
      else
        return 1;
    }
//
//    public int hashCode() {
//      return hashCodeStr().hashCode();
//    }
//
//    public String hashCodeStr() {
//      StringBuffer code = new StringBuffer("v");
//      code.append(getStr(m_aiV[0], 8));
//      code.append("v");
//      code.append(getStr(m_aiV[1], 8));
//
//      return code.toString();
//    }

  };

  public class Triangle implements Comparable {
    public int m_aiV[] = new int[3];

    public Triangle() {}

    public Triangle(int iV0, int iV1, int iV2) {
      if (iV0 < iV1) {
        if (iV0 < iV2) {
          // v0 is minimum
          m_aiV[0] = iV0;
          m_aiV[1] = iV1;
          m_aiV[2] = iV2;
        } else {
          // v2 is minimum
          m_aiV[0] = iV2;
          m_aiV[1] = iV0;
          m_aiV[2] = iV1;
        }
      } else {
        if (iV1 < iV2) {
          // v1 is minimum
          m_aiV[0] = iV1;
          m_aiV[1] = iV2;
          m_aiV[2] = iV0;
        } else {
          // v2 is minimum
          m_aiV[0] = iV2;
          m_aiV[1] = iV0;
          m_aiV[2] = iV1;
        }
      }

    }

    public boolean lessThan(Triangle rkT) {
      if (m_aiV[2] < rkT.m_aiV[2])
        return true;

      if (m_aiV[2] == rkT.m_aiV[2]) {
        if (m_aiV[1] < rkT.m_aiV[1])
          return true;

        if (m_aiV[1] == rkT.m_aiV[1])
          return m_aiV[0] < rkT.m_aiV[0];
      }

      return false;
    }

    public boolean equals(Object obj) {
      Triangle rkT = (Triangle)obj;
      return (m_aiV[0] == rkT.m_aiV[0]) &&
          ( (m_aiV[1] == rkT.m_aiV[1] && m_aiV[2] == rkT.m_aiV[2]) ||
           (m_aiV[1] == rkT.m_aiV[2] && m_aiV[2] == rkT.m_aiV[1]) );
    }

    public int compareTo(Object o) {
      Triangle t = (Triangle)o;
      if (lessThan(t))
        return -1;
      else if (equals(t))
        return 0;
      else
        return 1;
    }
//
//    public int hashCode() {
//      return hashCodeStr().hashCode();
//    }
//    public String hashCodeStr() {
//      StringBuffer code = new StringBuffer("v");
//      code.append(getStr(m_aiV[0],8));
//      code.append("v");
//      if (m_aiV[1] <= m_aiV[2]) {
//        code.append(getStr(m_aiV[1], 8));
//        code.append("v");
//        code.append(getStr(m_aiV[2], 8));
//      } else {
//        code.append(getStr(m_aiV[2], 8));
//        code.append("v");
//        code.append(getStr(m_aiV[1], 8));
//      }
//
//      return code.toString();
//    }
  };

  private String getStr(int code, int length) {
    StringBuffer rVal = new StringBuffer(""+code);
    while (rVal.length() < length) {
      rVal.insert(0, "0");
    }
    return rVal.toString();
  }

  public class VertexAttribute {
    public VertexAttribute() {
      m_kESet = new ExVector(8,8);
      m_kTSet = new ExVector(8,8);
      m_pvData = null;
    }

    public Object m_pvData;
    public ExVector m_kESet; //<Edge>
    public ExVector m_kTSet; //<Triangle>
  };

  public class EdgeAttribute {

    public EdgeAttribute() {
      m_kTSet = new ExVector(2,2);
      m_pvData = null;
    }

    public Object m_pvData;
    public ExVector m_kTSet; //<Triangle>
  };

  public class TriangleAttribute {
    public TriangleAttribute() {
      m_pvData = null;
    }

    public Object m_pvData;
  };

//      // for readability of the code
//      typedef std::map<int,VertexAttribute> TreeMap;
//      typedef TreeMap::iterator Iterator;
//      typedef TreeMap::const_iterator Iterator;
//      typedef std::map<Edge,EdgeAttribute> TreeMap;
//      typedef TreeMap::iterator Iterator;
//      typedef TreeMap::const_iterator Iterator;
//      typedef std::map<Triangle,TriangleAttribute> TreeMap;
//      typedef TreeMap::iterator Iterator;
//      typedef TreeMap::const_iterator Iterator;


  // construction
  public VETMesh() {
    m_kEMap = new TreeMap();
    m_kVMap = new TreeMap();
    m_kTMap = new TreeMap();
  }

  // accessors for sizes
  public int getVertexQuantity() {
    return (int) m_kVMap.size();
  }

  public int getEdgeQuantity() {
    return (int) m_kEMap.size();
  }

  public int getTriangleQuantity() {
    return (int) m_kTMap.size();
  }

  // Used for operations that create new meshes from the current one.  This
  // allows derived class construction within the base class operations.
  public VETMesh create() {
    return new VETMesh();
  };

  // Callbacks that are executed when vertices, edges, or triangles are
  // inserted or removed during triangle insertion, triangle removal, or
  // edge collapsing.  The default behavior for the creation is to return
  // null pointers.  A derived class may override the creation and return
  // data to be attached to the specific mesh component.  The default
  // behavior for the destruction is to do nothing.  A derived class may
  // override the destruction and handle the data that was detached from
  // the specific mesh component before its destruction.
  public void onVertexInsert(int iV, boolean insert, VertexAttribute va) {}

  public void onVertexRemove(int iV, boolean remove, VertexAttribute va) {}

  public void onEdgeInsert(Edge rkE, boolean insert, EdgeAttribute va) {}

  public void onEdgeRemove(Edge rkE, boolean remove, EdgeAttribute va) {}

  public void onTriangleInsert(Triangle rkT, boolean insert,
                               TriangleAttribute ta) {}

  public void onTriangleRemove(Triangle rkT, boolean remove,
                               TriangleAttribute ta) {}

  public void insertTriangle(int iV0, int iV1, int iV2) {
    boolean hadTri = false, hadV0 = false, hadV1 = false, hadV2 = false,
        hadE0 = false, hadE1 = false, hadE2 = false;
    Triangle kT = new Triangle(iV0, iV1, iV2);
    Edge kE0 = new Edge(iV0, iV1), kE1 = new Edge(iV1, iV2),
        kE2 = new Edge(iV2, iV0);

    // insert triangle
    TriangleAttribute kRT_TA = new TriangleAttribute();
//    Pair kRT =
    hadTri = (m_kTMap.get(kT) != null);
    m_kTMap.put(kT, kRT_TA); //pair<Iterator,boolean>

    // insert vertices
    VertexAttribute iV0_VA = (VertexAttribute)m_kVMap.get(new Integer(iV0));
    if (iV0_VA == null) iV0_VA = new VertexAttribute();
    else hadV0 = true;
    iV0_VA.m_kESet.add(kE0);
    iV0_VA.m_kESet.add(kE2);
    iV0_VA.m_kTSet.add(kT);
    //    Pair kRV0 =
    m_kVMap.put(new Integer(iV0), iV0_VA); //pair<Iterator,boolean>

    VertexAttribute iV1_VA = (VertexAttribute)m_kVMap.get(new Integer(iV1));
    if (iV1_VA == null) iV1_VA = new VertexAttribute();
    else hadV1 = true;
    iV1_VA.m_kESet.add(kE0);
    iV1_VA.m_kESet.add(kE1);
    iV1_VA.m_kTSet.add(kT);
    //    Pair kRV1 =
    m_kVMap.put(new Integer(iV1), iV1_VA); //pair<Iterator,boolean>

    VertexAttribute iV2_VA = (VertexAttribute)m_kVMap.get(new Integer(iV2));
    if (iV2_VA == null) iV2_VA = new VertexAttribute();
    else hadV2 = true;
    iV2_VA.m_kESet.add(kE1);
    iV2_VA.m_kESet.add(kE2);
    iV2_VA.m_kTSet.add(kT);
    //    Pair kRV2 =
    m_kVMap.put(new Integer(iV2), iV2_VA); //pair<Iterator,boolean>

    // insert edges
    EdgeAttribute kE0_EA = (EdgeAttribute)m_kEMap.get(kE0);
    if (kE0_EA == null) kE0_EA = new EdgeAttribute();
    else hadE0 = true;
    kE0_EA.m_kTSet.add(kT);
//    Pair kRE0 =
    m_kEMap.put(kE0, kE0_EA); //pair<Iterator,boolean>

    EdgeAttribute kE1_EA = (EdgeAttribute)m_kEMap.get(kE1);
    if (kE1_EA == null) kE1_EA = new EdgeAttribute();
    else hadE1 = true;
    kE1_EA.m_kTSet.add(kT);
//    Pair kRE1 =
    m_kEMap.put(kE1, kE1_EA); //pair<Iterator,boolean>

    EdgeAttribute kE2_EA = (EdgeAttribute)m_kEMap.get(kE2);
    if (kE2_EA == null) kE2_EA = new EdgeAttribute();
    else hadE2 = true;
    kE2_EA.m_kTSet.add(kT);
//    Pair kRE2 =
    m_kEMap.put(kE2, kE2_EA); //pair<Iterator,boolean>

    // Notify derived classes that mesh components have been inserted.  The
    // notification occurs here to make sure the derived classes have access
    // to the current state of the mesh after the triangle insertion.
    onVertexInsert(iV0, !hadV0, iV0_VA);
    onVertexInsert(iV1, !hadV1, iV1_VA);
    onVertexInsert(iV2, !hadV2, iV2_VA);
    onEdgeInsert(kE0, !hadE0, kE0_EA);
    onEdgeInsert(kE1, !hadE1, kE1_EA);
    onEdgeInsert(kE2, !hadE2, kE2_EA);
    onTriangleInsert(kT, !hadTri, kRT_TA);
  }

  public void insertTriangle(Triangle rkT) {
    insertTriangle(rkT.m_aiV[0], rkT.m_aiV[1], rkT.m_aiV[2]);
  }

  public void removeTriangle(int iV0, int iV1, int iV2) {
    // remove triangle
    Triangle kT = new Triangle(iV0, iV1, iV2);
    TriangleAttribute pkTA = (TriangleAttribute) m_kTMap.get(kT);
    if (pkTA == null) {
      // triangle does not exist, nothing to do
      return;
    }

    // update edges
    Edge kE0 = new Edge(iV0, iV1), kE1 = new Edge(iV1, iV2),
        kE2 = new Edge(iV2, iV0);

    EdgeAttribute pkE0 = (EdgeAttribute) m_kEMap.get(kE0);
//    assert(pkE0 != null);
    pkE0.m_kTSet.remove(kT);

    EdgeAttribute pkE1 = (EdgeAttribute) m_kEMap.get(kE1);
//    assert(pkE1 != null);
    pkE1.m_kTSet.remove(kT);

    EdgeAttribute pkE2 = (EdgeAttribute) m_kEMap.get(kE2);
//    assert(pkE2 != null);
    pkE2.m_kTSet.remove(kT);

    // update vertices
    VertexAttribute pkV0 = (VertexAttribute) m_kVMap.get(new Integer(iV0));
//    assert(pkV0 != null);
    pkV0.m_kTSet.remove(kT);

    VertexAttribute pkV1 = (VertexAttribute) m_kVMap.get(new Integer(iV1));
//    assert(pkV1 != null);
    pkV1.m_kTSet.remove(kT);

    VertexAttribute pkV2 = (VertexAttribute) m_kVMap.get(new Integer(iV2));
//    assert(pkV2 != null);
    pkV2.m_kTSet.remove(kT);

    if (pkE0.m_kTSet.size() == 0) {
      pkV0.m_kESet.remove(kE0);
      pkV1.m_kESet.remove(kE0);
    }

    if (pkE1.m_kTSet.size() == 0) {
      pkV1.m_kESet.remove(kE1);
      pkV2.m_kESet.remove(kE1);
    }

    if (pkE2.m_kTSet.size() == 0) {
      pkV0.m_kESet.remove(kE2);
      pkV2.m_kESet.remove(kE2);
    }

    // Notify derived classes that mesh components are about to be destroyed.
    // The notification occurs here to make sure the derived classes have
    // access to the current state of the mesh before the triangle removal.

    boolean bDestroy = pkV0.m_kESet.size() == 0 &&
        pkV0.m_kTSet.size() == 0;
    onVertexRemove(iV0, bDestroy, pkV0);
    if (bDestroy)
      m_kVMap.remove(new Integer(iV0));

    bDestroy = pkV1.m_kESet.size() == 0 &&
        pkV1.m_kTSet.size() == 0;
    onVertexRemove(iV1, bDestroy, pkV1);
    if (bDestroy)
      m_kVMap.remove(new Integer(iV1));

    bDestroy = pkV2.m_kESet.size() == 0 &&
        pkV2.m_kTSet.size() == 0;
    onVertexRemove(iV2, bDestroy, pkV2);
    if (bDestroy)
      m_kVMap.remove(new Integer(iV2));

    bDestroy = pkE0.m_kTSet.size() == 0;
    onEdgeRemove(kE0, bDestroy, pkE0);
    if (bDestroy)
      m_kEMap.remove(kE0);

    bDestroy = pkE1.m_kTSet.size() == 0;
    onEdgeRemove(kE1, bDestroy, pkE1);
    if (bDestroy)
      m_kEMap.remove(kE1);

    bDestroy = pkE2.m_kTSet.size() == 0;
    onEdgeRemove(kE2, bDestroy, pkE2);
    if (bDestroy)
      m_kEMap.remove(kE2);

    onTriangleRemove(kT, true, pkTA);
    m_kTMap.remove(kT);
  }

  public void removeTriangle(Triangle rkT) {
    removeTriangle(rkT.m_aiV[0], rkT.m_aiV[1], rkT.m_aiV[2]);
  }

  // This should be called before Mesh destruction if a derived class has
  // allocated vertex, edge, or triangle data and attached it to the mesh
  // components.  Since the creation and destruction callbacks are virtual,
  // any insert/remove operations in the base Mesh destructor will only
  // call the base virtual callbacks, not any derived-class ones.  An
  // alternative to calling this is that the derived class maintain enough
  // information to know which data objects to destroy during its own
  // destructor call.

  public void removeAllTriangles() {
    Object[] tris = m_kTMap.keySet().toArray();
    for (int x = 0; x < tris.length; x++) {
      Triangle tri = (Triangle) tris[x];
      int iV0 = tri.m_aiV[0];
      int iV1 = tri.m_aiV[1];
      int iV2 = tri.m_aiV[2];
      removeTriangle(iV0, iV1, iV2);
    }
  }

  // write the mesh to an ASCII file
  public void printToFile(String acFilename) {
//      ofstream kOStr(acFilename);
//      int i;
//
//      // print vertices
//      kOStr << "vertex quantity = " << (int)m_kVMap.size() << endl;
//      for (Iterator pkVM = m_kVMap.begin(); pkVM != m_kVMap.end(); pkVM++)
//      {
//          kOStr << "v<" << pkVM.first << "> : e ";
//
//         TreeMap<Edge>& rkESet = pkVM.second.m_kESet;
//          for (i = 0; i < rkESet.GetSize(); i++)
//          {
//              kOStr << '<' << rkESet[i].m_aiV[0]
//                    << ',' << rkESet[i].m_aiV[1]
//                    << "> ";
//          }
//
//          kOStr << ": t ";
//         TreeMap<Triangle>& rkTSet = pkVM.second.m_kTSet;
//          for (i = 0; i < rkTSet.GetSize(); i++)
//          {
//              kOStr << '<' << rkTSet[i].m_aiV[0]
//                    << ',' << rkTSet[i].m_aiV[1]
//                    << ',' << rkTSet[i].m_aiV[2]
//                    << "> ";
//          }
//          kOStr << endl;
//      }
//      kOStr << endl;
//
//      // print edges
//      kOStr << "edge quantity = " << (int)m_kEMap.size() << endl;
//      for (Iterator pkEM = m_kEMap.begin(); pkEM != m_kEMap.end(); pkEM++)
//      {
//          kOStr << "e<" << pkEM.first.m_aiV[0] << ',' << pkEM.first.m_aiV[1];
//          kOStr << "> : t ";
//         TreeMap<Triangle>& rkTSet = pkEM.second.m_kTSet;
//          for (i = 0; i < rkTSet.GetSize(); i++)
//          {
//              kOStr << '<' << rkTSet[i].m_aiV[0]
//                    << ',' << rkTSet[i].m_aiV[1]
//                    << ',' << rkTSet[i].m_aiV[2]
//                    << "> ";
//          }
//          kOStr << endl;
//      }
//      kOStr << endl;
//
//      // print triangles
//      kOStr << "triangle quantity = " << (int)m_kTMap.size() << endl;
//      for (Iterator pkTM = m_kTMap.begin(); pkTM != m_kTMap.end(); pkTM++)
//      {
//          kOStr << "t<" << pkTM.first.m_aiV[0] << ',' << pkTM.first.m_aiV[1];
//          kOStr << ',' << pkTM.first.m_aiV[2]  << ">" << endl;
//      }
//      kOStr << endl;
  }

  // vertex attributes
  public TreeMap getVertexMap() {
    return m_kVMap;
  }

  // edge attributes
  public TreeMap getEdgeMap() {
    return m_kEMap;
  }

  public ExVector getTriangles(int iV0, int iV1) { //<Triangle>
    EdgeAttribute pkE = (EdgeAttribute) m_kEMap.get(new Edge(iV0, iV1));
    return (pkE != null ? pkE.m_kTSet : null);
  }

  // triangle attributes
  public TreeMap getTriangleMap() {
    return m_kTMap;
  }

  // The mesh is manifold if each edge has at most two adjacent triangles.
  // It is possible that the mesh has multiple connected components.
  public boolean isManifold() {
    Iterator it = m_kEMap.values().iterator();
    while (it.hasNext()) {
      EdgeAttribute ea = (EdgeAttribute) it.next();
      if (ea.m_kTSet.size() > 2)
        return false;
    }
    return true;
  }

  // The mesh is closed if each edge has exactly two adjacent triangles.
  // It is possible that the mesh has multiple connected components.
  public boolean isClosed() {
    Iterator it = m_kEMap.values().iterator();
    while (it.hasNext()) {
      EdgeAttribute ea = (EdgeAttribute) it.next();
      if (ea.m_kTSet.size() != 2)
        return false;
    }
    return true;
  }

  // The mesh is connected if each triangle can be reached from any other
  // triangle by a traversal.
  public boolean isConnected() {
    // Do a depth-first search of the mesh.  It is connected if and only if
    // all of the triangles are visited on a single search.

    int iTSize = (int) m_kTMap.size();
    if (iTSize == 0)
      return true;

    // for marking visited triangles during the traversal
    TreeMap kVisitedMap = new TreeMap(); // Triangle, Boolean
    Iterator it = m_kTMap.keySet().iterator();
    while (it.hasNext()) {
      kVisitedMap.put(it.next(), Boolean.FALSE);
    }

      // start the traversal at any triangle in the mesh
    Stack kStack = new Stack(); // <Triangle>
    kStack.push(m_kTMap.keySet().toArray()[0]);
    kVisitedMap.put(kStack.get(0), Boolean.TRUE);
    iTSize--;

    Iterator triIt;
    while (!kStack.empty()) {
      // start at the current triangle
      Triangle kT = (Triangle)kStack.pop();

      for (int i = 0; i < 3; i++) {
        // get an edge of the current triangle
        EdgeAttribute pkE = (EdgeAttribute)m_kEMap.get(new Edge(kT.m_aiV[i], kT.m_aiV[ (i + 1) % 3]));

        // visit each adjacent triangle
        ExVector rkTSet = (ExVector)pkE.m_kTSet.clone(); // <Triangle>
        triIt = rkTSet.iterator();
        while (triIt.hasNext()) {
          Triangle rkTAdj = (Triangle)triIt.next();
          if (Boolean.FALSE.equals(kVisitedMap.get(rkTAdj))) {
            // this adjacent triangle not yet visited
            kStack.push(rkTAdj);
            kVisitedMap.put(rkTAdj, Boolean.TRUE);
            iTSize--;
          }
        }
      }
    }

    return iTSize == 0;
  }

  // Extract the connected components from the mesh.  For large data sets,
  // the array of VETMesh can use a lot of memory.  Instead use the
  // second form that just stores a sorted connectivity array.  Let N be
  // the number of components.  The value Index[i] indicates the starting
  // index for component i with 0 <= i < N, so it is always the case that
  // Index[0] = 0.  The value Index[N] is the total number of indices in
  // the raiConnect array.  The quantity of indices for component i is
  // Q(i) = Index[i+1]-Index[i] for 0 <= i < N.  The application is
  // responsible for deleting raiConnect.
  public void getComponents(Vector rkComponents) { // <VETMesh*>
    // Do a depth-first search of the mesh to find connected components.
    int iTSize = (int) m_kTMap.size();
    if (iTSize == 0)
      return;

    // for marking visited triangles during the traversal
    TreeMap kVisitedMap = new TreeMap(); // Triangle, Boolean
    Iterator it = m_kTMap.keySet().iterator();
    while (it.hasNext()) {
      kVisitedMap.put(it.next(), Boolean.FALSE);
    }

    while (iTSize > 0) {
      // find an unvisited triangle in the mesh
      Stack kStack = new Stack(); // <Triangle>
      Iterator visIt = kVisitedMap.keySet().iterator();
      while (visIt.hasNext()) {
        Triangle tri = (Triangle)visIt.next();
        if (Boolean.FALSE.equals(kVisitedMap.get(tri))) {
          // this triangle not yet visited
          kStack.push(tri);
          kVisitedMap.put(tri, Boolean.TRUE);
          iTSize--;
          break;
        }
      }

      // traverse the connected component of the starting triangle
      VETMesh pkComponent = create();
      Iterator triIt;
      while (!kStack.empty()) {
        // start at the current triangle
        Triangle kT = (Triangle)kStack.pop();
        pkComponent.insertTriangle(kT);

        for (int i = 0; i < 3; i++) {
          // get an edge of the current triangle
          Edge kE = new Edge(kT.m_aiV[i], kT.m_aiV[ (i + 1) % 3]);
          EdgeAttribute pkE = (EdgeAttribute)m_kEMap.get(kE);

          // visit each adjacent triangle
          ExVector rkTSet = (ExVector)pkE.m_kTSet.clone(); // <Triangle>
          triIt = rkTSet.iterator();
          while (triIt.hasNext()) {
            Triangle rkTAdj = (Triangle)triIt.next();
            if (Boolean.FALSE.equals(kVisitedMap.get(rkTAdj))) {
              // this adjacent triangle not yet visited
              kStack.push(rkTAdj);
              kVisitedMap.put(rkTAdj, Boolean.TRUE);
              iTSize--;
            }
          }
        }
      }
      rkComponents.add(pkComponent);
    }
  }

  public void getComponents(Vector rkIndex, int[] raiConnect) { // <int>
    rkIndex.clear();

    // Do a depth-first search of the mesh to find connected components.
    int iTSize = (int) m_kTMap.size();
    if (iTSize == 0) {
      raiConnect = null;
      return;
    }

    int iIQuantity = 3 * iTSize;
    int iIndex = 0;
    raiConnect = new int[iIQuantity];

    // for marking visited triangles during the traversal
    TreeMap kVisitedMap = new TreeMap(); // Triangle, Boolean
    Iterator it = m_kTMap.keySet().iterator();
    while (it.hasNext()) {
      kVisitedMap.put(it.next(), Boolean.FALSE);
    }

    while (iTSize > 0) {
      // find an unvisited triangle in the mesh
      Stack kStack = new Stack(); // <Triangle>
      Iterator visIt = kVisitedMap.keySet().iterator();
      while (visIt.hasNext()) {
        Triangle tri = (Triangle)visIt.next();
        if (Boolean.FALSE.equals(kVisitedMap.get(tri))) {
          // this triangle not yet visited
          kStack.push(tri);
          kVisitedMap.put(tri, Boolean.TRUE);
          iTSize--;
          break;
        }
      }

      // traverse the connected component of the starting triangle
      VETMesh pkComponent = create();
      Iterator triIt;
      while (!kStack.empty()) {
        // start at the current triangle
        Triangle kT = (Triangle)kStack.pop();
        pkComponent.insertTriangle(kT);

        for (int i = 0; i < 3; i++) {
          // get an edge of the current triangle
          Edge kE = new Edge(kT.m_aiV[i], kT.m_aiV[ (i + 1) % 3]);
          EdgeAttribute pkE = (EdgeAttribute)m_kEMap.get(kE);

          // visit each adjacent triangle
          ExVector rkTSet = (ExVector)pkE.m_kTSet.clone(); // <Triangle>
          triIt = rkTSet.iterator();
          while (triIt.hasNext()) {
            Triangle rkTAdj = (Triangle)triIt.next();
            if (Boolean.FALSE.equals(kVisitedMap.get(rkTAdj))) {
              // this adjacent triangle not yet visited
              kStack.push(rkTAdj);
              kVisitedMap.put(rkTAdj, Boolean.TRUE);
              iTSize--;
            }
          }
        }
      }

      // store the connectivity information for this component
      TreeSet kTSet = new TreeSet(); // <Triangle>
      pkComponent.getTriangles(kTSet);
      pkComponent = null;

      rkIndex.add(new Integer(iIndex));
      TreeSet pkTIter = new TreeSet(); // <Triangle>::iterator
      Iterator tsetIter = kTSet.iterator();
      while (tsetIter.hasNext()) {
        Triangle rkT = (Triangle)tsetIter.next();
        raiConnect[iIndex++] = rkT.m_aiV[0];
        raiConnect[iIndex++] = rkT.m_aiV[1];
        raiConnect[iIndex++] = rkT.m_aiV[2];
      }
    }

    rkIndex.add(new Integer(iIQuantity));
  }

  // Extract a connected component from the mesh and remove all the
  // triangles of the component from the mesh.  This is useful for computing
  // the components in a very large mesh that uses a lot of memory.  The
  // intention is that the function is called until all components are
  // found.  The typical code is
  //
  //     VETMesh kMesh = <some mesh>;
  //     int iITotalQuantity = 3*kMesh.GetTriangleQuantity();
  //     int* aiConnect = new int[iITotalQuantity];
  //     for (int iIQuantity = 0; iIQuantity < iITotalQuantity; /**/ )
  //     {
  //         int iCurrentIQuantity;
  //         int* aiCurrentConnect = aiConnect + iIQuantity;
  //         kMesh.RemoveComponent(iCurrentIQuantity,aiCurrentConnect);
  //         iIQuantity += iCurrentIQuantity;
  //     }

  public int removeComponent(int[] aiConnect) {
    // Do a depth-first search of the mesh to find connected components.  The
    // input array is assumed to be large enough to hold the component (see
    // the comments in WmlTriangleMesh.h for RemoveComponent).
    int riIQuantity = 0;

    int iTSize = (int) m_kTMap.size();
    if (iTSize == 0)
      return riIQuantity;

    // Find the connected component containing the first triangle in the mesh.
    // A set is used instead of a stack to avoid having a large-memory
    // 'visited' map.
    TreeSet kVisited = new TreeSet(); // <Triangle>
    kVisited.add(m_kTMap.keySet().toArray()[0]);

    // traverse the connected component
    Iterator triIt;
    while (!kVisited.isEmpty()) {
      // start at the current triangle
      Triangle kT = (Triangle)kVisited.toArray()[0];

      // add adjacent triangles to the set for recursive processing
      for (int i = 0; i < 3; i++) {
        // get an edge of the current triangle
        Edge kE = new Edge(kT.m_aiV[i], kT.m_aiV[ (i + 1) % 3]);
        EdgeAttribute pkE = (EdgeAttribute)m_kEMap.get(kE);
//        assert(pkE != null);

        // visit each adjacent triangle
        ExVector rkTSet = (ExVector)pkE.m_kTSet.clone(); // <Triangle>
        triIt = rkTSet.iterator();
        while (triIt.hasNext()) {
          Triangle kTAdj = (Triangle)triIt.next();
          if (!kTAdj.equals(kT))
            kVisited.add(kTAdj);
        }
      }

      // add triangle to connectivity array
      aiConnect[riIQuantity++] = kT.m_aiV[0];
      aiConnect[riIQuantity++] = kT.m_aiV[1];
      aiConnect[riIQuantity++] = kT.m_aiV[2];

      // remove the current triangle (visited, no longer needed)
      kVisited.remove(kT);
      removeTriangle(kT);
    }
    return riIQuantity;
  }

  // Extract the connected components from the mesh, but each component has
  // a consistent ordering across all triangles of that component.  The
  // mesh must be manifold.  The return value is 'true' if and only if the
  // mesh is manifold.  If the mesh has multiple components, each component
  // will have a consistent ordering.  However, the mesh knows nothing about
  // the mesh geometry, so it is possible that ordering across components is
  // not consistent.  For example, if the mesh has two disjoint closed
  // manifold components, one of them could have an ordering that implies
  // outward pointing normals and the other inward pointing normals.
  //
  // NOTE.  It is possible to create a nonorientable mesh such as a Moebius
  // strip.  In this case, GetConsistentComponents will return connected
  // components, but in fact the triangles will not (and can not) be
  // consistently ordered.
  public boolean getConsistentComponents(Vector rkComponents) { // <VETMesh*>
    if (!isManifold())
      return false;

    // Do a depth-first search of the mesh to find connected components.
    int iTSize = (int) m_kTMap.size();
    if (iTSize == 0)
      return true;

    // for marking visited triangles during the traversal
    TreeMap kVisitedMap = new TreeMap(); // Triangle, Boolean
    Iterator it = m_kTMap.keySet().iterator();
    while (it.hasNext()) {
      kVisitedMap.put(it.next(), Boolean.FALSE);
    }

    while (iTSize > 0) {
      // Find an unvisited triangle in the mesh.  Any triangle pushed onto
      // the stack is considered to have a consistent ordering.
      Stack kStack = new Stack(); // <Triangle>
      Iterator visIt = kVisitedMap.keySet().iterator();
      while (visIt.hasNext()) {
        Triangle tri = (Triangle)visIt.next();
        if (Boolean.FALSE.equals(kVisitedMap.get(tri))) {
          // this triangle not yet visited
          kStack.push(tri);
          kVisitedMap.put(tri, Boolean.TRUE);
          iTSize--;
          break;
        }
      }

      // traverse the connected component of the starting triangle
      VETMesh pkComponent = create();
      while (!kStack.empty()) {
        // start at the current triangle
        Triangle kT = (Triangle)kStack.pop();
        pkComponent.insertTriangle(kT);

        for (int i = 0; i < 3; i++) {
          // get an edge of the current triangle
          int iV0 = kT.m_aiV[i], iV1 = kT.m_aiV[ (i + 1) % 3], iV2;
          Edge kE = new Edge(iV0, iV1);
          EdgeAttribute pkE = (EdgeAttribute)m_kEMap.get(kE);

          int iSize = pkE.m_kTSet.size();
//          assert(iSize == 1 || iSize == 2); // mesh is manifold
          Triangle pkTAdj = (Triangle)pkE.m_kTSet.toArray()[0];
          if (iSize == 2) {
            // get the adjacent triangle to the current one
            if (pkTAdj.equals(kT))
              pkTAdj = (Triangle)pkE.m_kTSet.toArray()[1];

            if (Boolean.FALSE.equals(kVisitedMap.get(pkTAdj))) {
              // adjacent triangle not yet visited
              if ( (pkTAdj.m_aiV[0] == iV0 && pkTAdj.m_aiV[1] == iV1)
                  || (pkTAdj.m_aiV[1] == iV0 && pkTAdj.m_aiV[2] == iV1)
                  || (pkTAdj.m_aiV[2] == iV0 && pkTAdj.m_aiV[0] == iV1)) {
                // adjacent triangle must be reordered
                iV0 = pkTAdj.m_aiV[0];
                iV1 = pkTAdj.m_aiV[1];
                iV2 = pkTAdj.m_aiV[2];
                kVisitedMap.remove(pkTAdj);
                removeTriangle(iV0, iV1, iV2);
                insertTriangle(iV1, iV0, iV2);
                kVisitedMap.put(new Triangle(iV1, iV0,
                                            iV2), Boolean.FALSE);

                // refresh the iterators since maps changed
                pkE = (EdgeAttribute)m_kEMap.get(kE);
                pkTAdj = (Triangle)pkE.m_kTSet.toArray()[0];
                if (pkTAdj == kT)
                  pkTAdj = (Triangle)pkE.m_kTSet.toArray()[1];
              }

              kStack.push(pkTAdj);
              kVisitedMap.put(pkTAdj, Boolean.TRUE);
              iTSize--;
            }
          }
        }
      }
      rkComponents.add(pkComponent);
    }

    return true;
  }

  // Reverse the ordering of all triangles in the mesh.
  public VETMesh getReversedOrderMesh() {
    VETMesh pkReversed = create();

    Iterator it = m_kTMap.keySet().iterator();
    while (it.hasNext()) {
      Triangle t = (Triangle) it.next();
      pkReversed.insertTriangle(t.m_aiV[0], t.m_aiV[2], t.m_aiV[1]);
    }

    return pkReversed;
  }

  // statistics

//  public void getVertices(Set rkVSet) { // <int>&
//    rkVSet.clear();
//    Iterator it = m_kVMap.iterator();
//    while (it.hasNext())
//      rkVSet.add(it.next());
//    for (Iterator pkV = m_kVMap.begin(); pkV != m_kVMap.end(); pkV++)
//      rkVSet.insert(pkV.first);
//  }

  public Object getData(int iV) {
    VertexAttribute pkV = (VertexAttribute)m_kVMap.get(new Integer(iV));
    return (pkV != null ? pkV.m_pvData : null);
  }

  public ExVector getEdges(int iV) { // <Edge>
    VertexAttribute pkV = (VertexAttribute)m_kVMap.get(new Integer(iV));
    return (pkV != null ? pkV.m_kESet : null);
  }

  public ExVector getTriangles(int iV) // <Triangle>
  {
    VertexAttribute pkV = (VertexAttribute)m_kVMap.get(new Integer(iV));
    return (pkV != null ? pkV.m_kTSet : null);
  }

  public void getEdges(TreeSet rkESet) { //<Edge>
    rkESet.clear();
    Iterator it = m_kEMap.keySet().iterator();
    while (it.hasNext()) {
      rkESet.add(it.next());
    }
  }

  public Object getData(int iV0, int iV1) {
    EdgeAttribute pkE = (EdgeAttribute)m_kEMap.get(new Edge(iV0, iV1));
    return (pkE != null ? pkE.m_pvData : null);
  }

  public Object getData(Edge rkE) {
    return getData(rkE.m_aiV[0], rkE.m_aiV[1]);
  }

  public void getTriangles(TreeSet rkTSet) { //<Triangle>
    rkTSet.clear();
    Iterator it = m_kTMap.keySet().iterator();
    while (it.hasNext()) {
      rkTSet.add(it.next());
    }
  }

  public Object getData(int iV0, int iV1, int iV2) {
    TriangleAttribute pkT =
        (TriangleAttribute)m_kTMap.get(new Triangle(iV0, iV1, iV2));
    return (pkT != null ? pkT.m_pvData : null);
  }

  public void setData(int iV0, int iV1, int iV2, Object data) {
    TriangleAttribute pkT =
        (TriangleAttribute)m_kTMap.get(new Triangle(iV0, iV1, iV2));
    if (pkT != null) pkT.m_pvData = data;
//    else System.err.println("PKT WAS NULL!  Could not set data!");
  }

  public Object getData(Triangle rkT) {
    return getData(rkT.m_aiV[0], rkT.m_aiV[1], rkT.m_aiV[2]);
  }

  public void setData(Triangle rkT, Object data) {
    setData(rkT.m_aiV[0], rkT.m_aiV[1], rkT.m_aiV[2], data);
  }

//  // statistics
//  public void getStatistics(int riVQuantity, int riEQuantity,
//                            int riTQuantity, float rfAverageEdgesPerVertex,
//                            float rfAverageTrianglesPerVertex,
//                            float rfAverageTrianglesPerEdge,
//                            int riMaximumEdgesPerVertex,
//                            int riMaximumTrianglesPerVertex,
//                            int riMaximumTrianglesPerEdge) {
//    riVQuantity = (int) m_kVMap.size();
//    riEQuantity = (int) m_kEMap.size();
//    riTQuantity = (int) m_kTMap.size();
//
//    int iESumForV = 0;
//    int iTSumForV = 0;
//    riMaximumEdgesPerVertex = 0;
//    riMaximumTrianglesPerVertex = 0;
//
//    int iESize, iTSize;
//
//    for (Iterator pkV = m_kVMap.begin(); pkV != m_kVMap.end(); pkV++) {
//      iESize = pkV.second.m_kESet.GetSize();
//      iTSize = pkV.second.m_kTSet.GetSize();
//      iESumForV += iESize;
//      iTSumForV += iTSize;
//      if (iESize > riMaximumEdgesPerVertex)
//        riMaximumEdgesPerVertex = iESize;
//      if (iTSize > riMaximumTrianglesPerVertex)
//        riMaximumTrianglesPerVertex = iTSize;
//    }
//
//    int iTSumForE = 0;
//    riMaximumTrianglesPerEdge = 0;
//    for (Iterator pkE = m_kEMap.begin(); pkE != m_kEMap.end(); pkE++) {
//      iTSize = pkE.second.m_kTSet.GetSize();
//      iTSumForE += iTSize;
//      if (iTSize > riMaximumTrianglesPerEdge)
//        riMaximumTrianglesPerEdge = iTSize;
//    }
//
//    rfAverageEdgesPerVertex = ( (float) iESumForV) / riVQuantity;
//    rfAverageTrianglesPerVertex = ( (float) iTSumForV) / riVQuantity;
//    rfAverageTrianglesPerEdge = ( (float) iTSumForE) / riEQuantity;
//  }

}
