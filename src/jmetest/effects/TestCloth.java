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
package jmetest.effects;

import java.util.ArrayList;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.scene.Line;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.MaterialState;
import com.jme.scene.shape.Sphere;

/**
 * <code>TestCloth</code>
 * @author Joshua Slack
 */
public class TestCloth extends SimpleGame {

  TriMesh cloth;

  float FLOOR = -63.6f;
  int RADIUS = 10;

  float SpringLength = .8f;
  int ClothWid = 50;
  int ClothHgt = 50;
  float ClothXStep = 1f / (float)ClothWid;
  float ClothYStep = 1f / (float)ClothHgt;
  float ClothRWid = ClothWid * SpringLength;
  float ClothRHgt = ClothHgt * SpringLength;

  float WIND_STRENGTH = 0.4f;

  int ClothTot = (ClothWid + 1) * (ClothHgt + 1);
  Vector3f CircPos = new Vector3f(0, -40, 40);
  Vector3f InitPos = new Vector3f( -23, 10, -13);
  Vector3f windDirection = new Vector3f(0.0f, 0.2f, 0.01f);

  SpringNode[] nodes = new SpringNode[ClothTot];
  ArrayList links = new ArrayList();
  Vector3f[] normals = new Vector3f[nodes.length];

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestCloth app = new TestCloth();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }


  public void simpleUpdate() {
    CalcSystem(links, nodes);
//    CalcCollision();

    int i, inde;
    Vector3f T = new Vector3f();
    int V[] = new int[3];
    for (int y = 0; y <= ClothHgt-1; y++) {
       i = 0;
       //Have to calculate Normals every frame to update Lighting, and eviroment effects
        for (int x = 0; x <= ClothWid; x++) {
         inde = getIndex(x, y);
         V[i] = inde;
         i++;
         if (i >= 2) {
             getNormal(nodes[V[0]].position, nodes[V[1]].position, nodes[V[2]].position, T);
             normals[V[0]].set(T);
             normals[V[1]].set(T);
             normals[V[2]].set(T);
             i = 0;
         }
         inde = getIndex(x, y+1);
         V[i] = inde;
         i++;
         if (i >= 3) {
         getNormal(nodes[V[0]].position, nodes[V[1]].position, nodes[V[2]].position, T);
         normals[V[0]].set(T);
         normals[V[1]].set(T);
         normals[V[2]].set(T);
         i = 0;
         }
        }
    }

    applyWind();
    cloth.updateVertexBuffer();
    cloth.updateNormalBuffer();
  }


  Vector3f V1 = new Vector3f();
  Vector3f V2 = new Vector3f();
  Vector3f V3 = new Vector3f();
  // Used to get the lighting and enviroment mapping on the cloth.
  public Vector3f getNormal(Vector3f vert1, Vector3f vert2, Vector3f vert3, Vector3f store) {
    V1.set(vert1);
    V2.set(vert2);
    V3.set(vert3);

    //  Mirror(v2, TRUE, TRUE, TRUE);
    V2.negateLocal();

    //  Translate(v1, v2);
    V1.addLocal(V2);

    //  Translate(v3, v2);
    V3.addLocal(V2);

    //  Result = CrossProduct(v1, v3);
    V1.cross(V3, store);

    //  Normalize(Result);
    store.normalizeLocal();
    return store;
  }


  public void applyWind() {
    float wind = 1;
    for (int i = 0; i < ClothTot; i++) {
        wind = normals[i].dot(windDirection) * WIND_STRENGTH;
        nodes[i].velocity.addLocal(windDirection.x * wind,
                            windDirection.y * wind,
                            windDirection.z * wind);
    }
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    cam.setLocation(new Vector3f(0.0f, -30.0f, 150.0f));
    cam.update();
    input.setKeySpeed(20);
//
//    Sphere s = new Sphere("sample", 20, 20, RADIUS);
//    s.getLocalTranslation().set(CircPos);
//    s.setVBOColorEnabled(true);
//    s.setVBONormalEnabled(true);
//    s.setVBOTextureEnabled(true);
//    s.setVBOVertexEnabled(true);
//    s.setRenderState(display.getRenderer().createWireframeState());
//    MaterialState ms = display.getRenderer().createMaterialState();
//    ms.setDiffuse(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f));
//    s.setRenderState(ms);
//    rootNode.attachChild(s);
//
//    ArrayList al = new ArrayList();
//    for (int x = -10; x <= 10; x++) {
//      for (int y = 0; y <= 20; y++) {
//        al.add(new Vector3f(x * 5, FLOOR, 0));
//        al.add(new Vector3f(x * 5, FLOOR, 100));
//        al.add(new Vector3f( -50, FLOOR, y * 5));
//        al.add(new Vector3f(50, FLOOR, y * 5));
//      }
//    }
//    Vector3f[] floorVec = new Vector3f[al.size()];
//    al.toArray(floorVec);
//    Line floor = new Line("floor lines", floorVec, null, null, null);
//    rootNode.attachChild(floor);

    initCloth();
    TextureState ts = display.getRenderer().createTextureState();
    ts.setTexture(
        TextureManager.loadTexture(
        TestCloth.class.getClassLoader().getResource(
        "jmetest/data/images/Monkey.jpg"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR,
        true));
    cloth.setRenderState(ts);
    cloth.setLocalTranslation(new Vector3f(0, -14, 0));
    cloth.setLocalScale(5);
    rootNode.attachChild(cloth);
  }


  int maxPos = 0;
  public int getIndex(int x, int y) {
    int rVal = y * (ClothWid + 1) + x;
    if (rVal > maxPos) maxPos = rVal;
    return rVal;
  }

  public void initCloth() {

    cloth = new TriMesh("cloth");

    for (int i = 0; i < nodes.length; i++)
      nodes[i] = new SpringNode();

    for (int y = 0; y <= ClothHgt; y++) {
      for (int x = 0; x <= ClothWid; x++) {
        int ind = getIndex(x, y);
        nodes[ind] = createNode(
            InitPos.x + (x * SpringLength),
            InitPos.y + (y * SpringLength),
            InitPos.z,
            (y == ClothHgt)); // fix top of cloth in place
      }
    }

    for (int y = 1; y <= ClothHgt; y++) {
      for (int x = 1; x <= ClothWid; x++) {
        int ind = getIndex(x, y);
        if (ind >= nodes.length) continue;
        //Each point must be linked to its neighbouring points.
        //the number of neighbours linked to, determines the stiffness of the cloth.

        if (x+1 <= ClothWid)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x + 1, y)]));
        if (y+1 <= ClothHgt)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x, y + 1)]));
        if (x+1 <= ClothWid && y+1 <= ClothHgt)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x + 1, y + 1)]));

        //Secondary links, positive direction
        if (x+2 <= ClothWid)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x + 2, y)]));
        if (y+2 <= ClothHgt)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x, y + 2)]));
        if (x+2 <= ClothWid && y+2 <= ClothHgt)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x + 2, y + 2)]));

        if (x > 0)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x - 1, y)]));

        if (y > 0)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x, y - 1)]));

        if (x > 0 && y > 0)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x - 1, y - 1)]));

          //Secondary Links, negative direction
        if (x > 1)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x - 2, y)]));

        if (y > 1)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x, y - 2)]));

        if (x > 1 && y > 1)
          links.add(new SpringLink(nodes[getIndex(x, y)], nodes[getIndex(x - 2, y - 2)]));

      }
    }

    for (int x = 0; x <= ClothWid; x++) //Scrunch up the cloth being held so it creases..
      nodes[getIndex(x, ClothHgt)].position.x *= 0.8f;

    Vector3f[] vertices = new Vector3f[nodes.length];
    for (int i = 0; i < nodes.length; i++) {
      vertices[i] = nodes[i].position;
      normals[i] = new Vector3f();
    }
    cloth.setVertices(vertices);
    cloth.setNormals(normals);

    int[] indices = new int[6*(ClothHgt)*(ClothWid)];
    Vector2f[] texts = new Vector2f[vertices.length];
    int i = 0;
    for (int Y = 0; Y < ClothHgt; Y++) {
      for (int X = 0; X < ClothWid; X++) {
        indices[i] = getIndex(X, Y);
        i++;
        indices[i] = getIndex(X, Y+1);
        i++;
        indices[i] = getIndex(X+1, Y+1);
        i++;

        indices[i] = indices[i-3];
        i++;
        indices[i] = indices[i-2];
        i++;
        indices[i] = getIndex(X+1, Y);
        i++;
      }
    }


    i = 0;
    for (int Y = 0; Y <= ClothHgt; Y++) {
      for (int X = 0; X <= ClothWid; X++) {
        texts[i] = new Vector2f(X * ClothXStep, Y * ClothYStep);
        i++;
      }
    }

    cloth.setIndices(indices);
    cloth.setTextures(texts);

    System.err.println("max: "+maxPos+" sm: "+ClothTot);
  }

  public static final float SPRING_TOLERANCE = 0.0005f;
  public static final int SPRING_MAX = 1;
  public static final int DISTANCEFALLOFF = 3;

  private float Kcoefficent = 0.1f;
  private float SpringDamp = 0.99f;
  private Vector3f SpringGrav = new Vector3f(0, -0.01f, 0);

  public class SpringNode {
    boolean fixed;
    Vector3f position = new Vector3f(),
             velocity = new Vector3f(),
             force = new Vector3f();
  }

  public class SpringLink {
    float length;
    SpringNode node1, node2;

    public SpringLink(SpringNode node1, SpringNode node2) {
      this.node1 = node1;
      this.node2 = node2;
      length = node1.position.distance(node2.position);
    }
  }

  public SpringNode createNode(float x, float y, float z, boolean fixed) {
    SpringNode node = new SpringNode();
    node.position.set(x, y, z);
    node.fixed = fixed;
    return node;
  }

  Vector3f ForceDir = new Vector3f();
  public void ComputeSingleSpring(SpringLink L) {
    float Intensity, Dist, Delta;
    Dist = L.node1.position.distance(L.node2.position);

    if (Dist < SPRING_TOLERANCE) {
      L.node1.force.zero();
      L.node2.force.zero();
      return;
    }

    ForceDir.set(L.node2.position).subtractLocal(L.node1.position);
    ForceDir.divideLocal(Dist);

    Delta = Dist - L.length;
    Intensity = Kcoefficent * Delta;

    ForceDir.multLocal(Intensity);

    L.node1.force.addLocal(ForceDir);
    L.node2.force.subtractLocal(ForceDir);
  }

  public void CalcSystem(ArrayList links, SpringNode[] N) {

    for (int I = 0; I < N.length; I++)
      N[I].force.zero();

    for (int I = 0; I < links.size(); I++)
      ComputeSingleSpring( (SpringLink) links.get(I));

    for (int I = 0; I < N.length; I++) {

      if (N[I].fixed) {
        N[I].velocity.set(0, 0, 0);
        N[I].force.set(0, 0, 0);
      } else {
        N[I].velocity.addLocal(N[I].force).addLocal(SpringGrav)
            .multLocal(SpringDamp);
//        if (FastMath.abs(N[I].velocity.x) > SPRING_MAX)
//          N[I].velocity.x *= 0.1f;
//        if (FastMath.abs(N[I].velocity.y) > SPRING_MAX)
//          N[I].velocity.y *= 0.1f;
//        if (FastMath.abs(N[I].velocity.z) > SPRING_MAX)
//          N[I].velocity.z *= 0.1f;
        N[I].position.addLocal(N[I].velocity);
      }
    }
  }

}
