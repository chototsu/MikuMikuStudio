/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
package com.jme.effects.water;

import com.jme.math.Bessel;
import com.jme.math.FastMath;
import com.jme.scene.TriMesh;
import com.jme.math.Vector3f;
import com.jme.input.KeyBindingManager;
import com.jme.renderer.Renderer;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;
import com.jme.math.Vector2f;

/**
 * <code>WaterSurface</code>...
 *
 * Portions from Game Programming Gems 4 article by Jerry Tessendorf
 *
 * @author Joshua Slack
 * @version $Id: WaterSurface.java,v 1.4 2004-04-25 20:32:24 renanse Exp $
 */

public class WaterSurface extends TriMesh {
  private int iwidth, iheight, size;
  private float[] vertical_derivative, height, previous_height;
  float kernel[][] = new float[13][13];
  float gravity, tension;
  float display_map[];
  float obstruction[];
  float source[];
  float scaling_factor;
  float stepScale;
  float timeFactor;

  public WaterSurface(String name, int gridWidth, int gridHeight, float stepScale) {
    super(name);
    iwidth = gridWidth;
    iheight = gridHeight;
    this.stepScale = stepScale;

    size = iwidth * iheight;

    tension = .4f;
    timeFactor = 2.0f;
    scaling_factor = 1.0f;

    height = new float[size];
    previous_height = new float[size];
    vertical_derivative = new float[size];
    obstruction = new float[size];
    source = new float[size];
    display_map = new float[size];

    clearWaves();
    clearObstruction();

    // build the convolution kernel
    initializeKernel();
    buildVertices();
    buildTextureCoordinates();

    convertToDisplay();
    initialize(source, size, 0);
  }

  /**
   * buildVertices
   */
  private void buildVertices() {
    vertex = new Vector3f[size];
    for (int y = 0; y < iheight; y++) {
      for (int x = 0; x < iwidth; x++) {
            vertex[x + (y * iwidth)] = new Vector3f(x * stepScale,
                    0, y * stepScale);
        }
    }

    //set up the indices
    int value = ((iheight - 1) * (iheight - 1)) * 6;
    indices = new int[value];

    int count = 0;

    //go through entire array up to the second to last column.
    for (int i = 0; i < (iheight * (iheight - 1)); i++) {
        //we want to skip the top row.
        if (i % ((iheight * (i / iheight + 1)) - 1) == 0 && i != 0) {
            continue;
        }
        //set the top left corner.
        indices[count++] = i;
        //set the bottom right corner.
        indices[count++] = ((1 + iheight) + i);
        //set the top right corner.
        indices[count++] = (1 + i);
        //set the top left corner
        indices[count++] = i;
        //set the bottom left corner
        indices[count++] = iheight + i;
        //set the bottom right corner
        indices[count++] = ((1 + iheight) + i);

    }

    setVertices(vertex);
    setIndices(indices);
  }

  /**
   * <code>buildTextureCoordinates</code> calculates the texture coordinates
   * of the terrain.
   *
   */
  private void buildTextureCoordinates() {
      texture[0] = new Vector2f[vertex.length];
      for (int i = 0; i < texture[0].length; i++) {
          texture[0][i] = new Vector2f(
                  (vertex[i].x ) / (stepScale * (iheight - 1)),
                  (vertex[i].z ) / (stepScale * (iwidth - 1)));
      }

      setTextures(texture[0]);
  }

      /**
   *
   * <code>buildNormals</code> calculates the normals of each vertex that
   * makes up the block of terrain.
   *
   *
   */
  private void buildNormals() {
    Vector3f[] normal = new Vector3f[vertex.length];

    int normalIndex = 0;
    for (int row = 0; row < iheight; row++) {
      for (int col = 0; col < iwidth; col++) {
        if (row == iheight - 1) {
          if (col == iwidth - 1) { // last row, last col
            // up cross left
            normal[normalIndex] = vertex[normalIndex -
                iwidth].subtract(vertex[normalIndex]).crossLocal(vertex[normalIndex -
                1].subtract(vertex[normalIndex])).normalizeLocal();
          } else { // last row, except for last col
            // right cross up
            normal[normalIndex] = vertex[normalIndex +
                1].subtract(vertex[normalIndex]).crossLocal(vertex[normalIndex -
                iwidth].subtract(vertex[normalIndex])).normalizeLocal();
          }
        } else {
          if (col == iwidth - 1) { // last column except for last row
            // left cross down
            normal[normalIndex] = vertex[normalIndex -
                1].subtract(vertex[normalIndex]).crossLocal(vertex[normalIndex +
                iwidth].subtract(vertex[normalIndex])).normalizeLocal();
          } else { // most cases
            // down cross right
            normal[normalIndex] = vertex[normalIndex +
                iwidth].subtract(vertex[normalIndex]).crossLocal(vertex[normalIndex +
                1].subtract(vertex[normalIndex])).normalizeLocal();
          }
        }
        normalIndex++;
      }
    }

    setNormals(normal);
  }

//--------------------------------------------------------
//  Initialization routines
//
// Initialize all of the fields to zero
  private void initialize(float data[], int size, float value) {
    for (int i = 0; i < size; i++) {
      data[i] = value;
    }
  }

// Compute the elements of the convolution kernel
  private void initializeKernel() {
    double dk = 0.01f;
    double sigma = 1.0f;
    double norm = 0f;

    for (double k = 0; k < 10; k += dk) {
      norm += k * k * Math.exp( -sigma * k * k);
    }

    for (int i = -6; i <= 6; i++) {
      for (int j = -6; j <= 6; j++) {
        double r = FastMath.sqrt( (float) (i * i + j * j));
        double kern = 0;
        for (double k = 0; k < 10; k += dk) {
          kern += k * k * Math.exp( -sigma * k * k) * Bessel.j0(r * k);
        }
        kernel[i + 6][j + 6] = (float) (kern / norm);
      }
    }
  }

  void clearObstruction() {
    for (int i = 0; i < size; i++) {obstruction[i] = 1.0f;
    }
  }

  void clearWaves() {
    for (int i = 0; i < size; i++) {
      height[i] = 0.0f;
      previous_height[i] = 0.0f;
      vertical_derivative[i] = 0.0f;
    }
  }

//----------------------------------------------------

  public void updateGeometricState(float dt, boolean initiator) {
    super.updateGeometricState(dt, initiator);
    if (KeyBindingManager
        .getKeyBindingManager()
        .isValidCommand("drip", false)) {
      int spot = (int)(FastMath.nextRandomFloat()*(size-iwidth*2))+iwidth;
      System.err.println("hit");
      source[spot]+=.25f;
//      source[spot-1]+=.125f;
//      source[spot+1]+=.125f;
//      source[spot+iwidth]+=.125f;
//      source[spot-iwidth]+=.125f;
    }
    propagate(dt);
    convertToDisplay();
  }

  void convertToDisplay() {
    for (int y = 0; y < iheight; y++) {
      for (int x = 0; x < iwidth; x++) {
        vertex[y*iwidth + x].y = 0.5f * (height[y*iwidth + x] / scaling_factor + 1.0f) * obstruction[y*iwidth + x];
      }
    }
    updateVertexBuffer();
    buildNormals();
  }

  void computeVerticalDerivative() {
    // first step:  the interior
    int index, iindex, ix, iix, iy, iiy;
    float vd;
    for (ix = 6; ix < iwidth - 6; ix++) {
      for (iy = 6; iy < iheight - 6; iy++) {
        index = ix + iwidth * iy;
        vd = 0;
        for (iix = -6; iix <= 6; iix++) {
          for (iiy = -6; iiy <= 6; iiy++) {
            iindex = ix + iix + iwidth * (iy + iiy);
            vd += kernel[iix + 6][iiy + 6] * height[iindex];
          }
        }
        vertical_derivative[index] = vd;
      }
    }
  }

  void propagate(float dt) {
    dt *= timeFactor;
    // apply obstruction
    gravity = 9.8f * dt * dt;
    for (int i = 0; i < size; i++)
      height[i] *= obstruction[i];

      // compute vertical derivative
    computeVerticalDerivative();

    // advance surface
    float adt = tension * dt;
    float adt2 = 1.0f / (1.0f + adt);
    for (int i = 0; i < size; i++) {
      float temp = height[i];
      height[i] = height[i] * (2.0f - adt) - previous_height[i] -
          gravity * vertical_derivative[i];
      height[i] *= adt2;
      height[i] += source[i];
      height[i] *= obstruction[i];
      previous_height[i] = temp;
      // reset source each step
      source[i] = 0;
    }
  }
}
