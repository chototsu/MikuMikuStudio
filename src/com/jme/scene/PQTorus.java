/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
package com.jme.scene;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.math.FastMath;

/**
 * <code>PQTorus</code>
 *
 * @author MASTER
 * @version $Id: PQTorus.java,v 1.2 2004-03-20 07:15:18 renanse Exp $
 */
public class PQTorus extends TriMesh {

  private float p, q;
  private float radius;

  private int steps, radialSamples;

  public PQTorus(String name, float p, float q, float radius, int steps,
                 int radialSamples) {
    super(name);

    this.p = p;
    this.q = q;
    this.radius = radius;
    this.steps = steps;
    this.radialSamples = radialSamples;

    setGeometryData();
    setColorData();
  }

  private void setGeometryData() {
    final float THETA_STEP = (float) (FastMath.PI * 2f / steps);
    final float BETA_STEP = (float) (FastMath.PI * 2f / radialSamples);

    Vector3f[] toruspoints = new Vector3f[steps];
    vertex = new Vector3f[radialSamples * steps];
    normal = new Vector3f[vertex.length];

    Vector3f pointB = new Vector3f(), T = new Vector3f(), N = new Vector3f(),
        B = new Vector3f();

    float r, x, y, z, theta = 0.0f, beta = 0.0f;
    int nvertex = 0;

    //Move along the length of the pq torus
    for (int i = 0; i < steps; i++) {
      theta += THETA_STEP;

      //Find the point on the torus
      r = (float) (0.5f * (2.0f + FastMath.sin(q * theta)) * radius);
      x = (float) (r * FastMath.cos(p * theta) * radius);
      y = (float) (r * FastMath.sin(p * theta) * radius);
      z = (float) (r * FastMath.cos(q * theta) * radius);
      toruspoints[i] = new Vector3f(x, y, z);

      //Now find a point slightly farther along the torus
      r = (float) (0.5f * (2.0f + FastMath.sin(q * (theta + 0.01f))) * radius);
      x = (float) (r * FastMath.cos(p * (theta + 0.01f)) * radius);
      y = (float) (r * FastMath.sin(p * (theta + 0.01f)) * radius);
      z = (float) (r * FastMath.cos(q * (theta + 0.01f)) * radius);
      pointB = new Vector3f(x, y, z);

      //Approximate the Frenet Frame
      T = pointB.subtract(toruspoints[i]);
      N = toruspoints[i].add(pointB);
      B = T.cross(N);
      N = B.cross(T);

      //Normalise the two vectors before use
      N = N.normalize();
      B = B.normalize();

      //Create a circle oriented by these new vectors
      beta = 0.0f;
      for (int j = 0; j < radialSamples; j++) {
        beta += BETA_STEP;
        float cx = (float) FastMath.cos(beta);
        float cy = (float) FastMath.sin(beta);

        vertex[nvertex] = new Vector3f();
        vertex[nvertex].x = (cx * N.x + cy * B.x) + toruspoints[i].x;
        vertex[nvertex].y = (cx * N.y + cy * B.y) + toruspoints[i].y;
        vertex[nvertex].z = (cx * N.z + cy * B.z) + toruspoints[i].z;
        normal[nvertex] = vertex[nvertex].subtract(toruspoints[i]);
        nvertex++;
      }
    }

    setVertices(vertex);
    setNormals(normal);

    int[] indices = new int[6 * vertex.length];
    int j = 0;
    for (int i = 0; i < vertex.length; i++) {
      indices[j++] = i;
      indices[j++] = i + 1;
      indices[j++] = i - radialSamples;

      indices[j++] = i + 1;
      indices[j++] = i - radialSamples;
      indices[j++] = i - radialSamples + 1;
    }
    for (int i = 0; i < indices.length; i++) {
      if (indices[i] < 0) indices[i] += vertex.length;
      if (indices[i] >= vertex.length) indices[i] -= vertex.length;
    }
    setIndices(indices);

    //TODO:
    // - calculate texture coordinates
    // - small optimizations (code for clarity to begin, then optimize! :)
    // - set color data
  }

  private void setColorData() {
    color = new ColorRGBA[vertex.length];
    //initialize colors to white
    for (int x = 0; x < vertex.length; x++) {
      color[x] = new ColorRGBA();
    }
    setColors(color);
  }
}
