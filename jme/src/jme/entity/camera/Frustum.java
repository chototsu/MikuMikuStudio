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

package jme.entity.camera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.logging.Level;

import jme.utility.LoggingSystem;

import org.lwjgl.opengl.GL;

/**
 * <code>Frustum</code> defines the view frustum of the camera.
 * This frustum defines the area that can be seen in the view port.
 * It contains multiple containing methods to allow for testing 
 * for whether an object is in the view frustum or not. 
 * 
 * For reference see <a href="http://www.markmorley.com/opengl/frustumculling.html">
 * @author Mark Powell
 * @version 1
 */
public class Frustum {
    private GL gl;
    
    private float buffer = 1.0f;
    private float[][] frustum = new float[6][4];

    private float[] proj;
    private float[] modl;
    private float[] clip;
    private FloatBuffer projBuf;
    private FloatBuffer modlBuf;

    /**
     * Constructor instantiates a new <code>Frustum</code> object. This 
     * sets up the required matrices for the frustum information. 
     *
     */
    public Frustum() {
        //gl = DisplaySystem.getDisplaySystem().getGL();
        proj = new float[16];
        modl = new float[16];
        clip = new float[16];

        projBuf =
            ByteBuffer
                .allocateDirect(64)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        modlBuf =
            ByteBuffer
                .allocateDirect(64)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        LoggingSystem.getLoggingSystem().getLogger().log(
            Level.INFO,
            "Created a frustum.");
    }
    
    /**
     * <code>update</code> refreshes the frustum planes based on any
     * change of the view port.
     *
     */
    public void update() {
        float t;
        projBuf.clear();
        modlBuf.clear();
        GL.glGetFloat(GL.GL_PROJECTION_MATRIX, projBuf);
        projBuf.get(proj);

        GL.glGetFloat(GL.GL_MODELVIEW_MATRIX, modlBuf);
        modlBuf.get(modl);
       
        clip[0] =
            modl[0] * proj[0]
                + modl[1] * proj[4]
                + modl[2] * proj[8]
                + modl[3] * proj[12];
        clip[1] =
            modl[0] * proj[1]
                + modl[1] * proj[5]
                + modl[2] * proj[9]
                + modl[3] * proj[13];
        clip[2] =
            modl[0] * proj[2]
                + modl[1] * proj[6]
                + modl[2] * proj[10]
                + modl[3] * proj[14];
        clip[3] =
            modl[0] * proj[3]
                + modl[1] * proj[7]
                + modl[2] * proj[11]
                + modl[3] * proj[15];

        clip[4] =
            modl[4] * proj[0]
                + modl[5] * proj[4]
                + modl[6] * proj[8]
                + modl[7] * proj[12];
        clip[5] =
            modl[4] * proj[1]
                + modl[5] * proj[5]
                + modl[6] * proj[9]
                + modl[7] * proj[13];
        clip[6] =
            modl[4] * proj[2]
                + modl[5] * proj[6]
                + modl[6] * proj[10]
                + modl[7] * proj[14];
        clip[7] =
            modl[4] * proj[3]
                + modl[5] * proj[7]
                + modl[6] * proj[11]
                + modl[7] * proj[15];

        clip[8] =
            modl[8] * proj[0]
                + modl[9] * proj[4]
                + modl[10] * proj[8]
                + modl[11] * proj[12];
        clip[9] =
            modl[8] * proj[1]
                + modl[9] * proj[5]
                + modl[10] * proj[9]
                + modl[11] * proj[13];
        clip[10] =
            modl[8] * proj[2]
                + modl[9] * proj[6]
                + modl[10] * proj[10]
                + modl[11] * proj[14];
        clip[11] =
            modl[8] * proj[3]
                + modl[9] * proj[7]
                + modl[10] * proj[11]
                + modl[11] * proj[15];

        clip[12] =
            modl[12] * proj[0]
                + modl[13] * proj[4]
                + modl[14] * proj[8]
                + modl[15] * proj[12];
        clip[13] =
            modl[12] * proj[1]
                + modl[13] * proj[5]
                + modl[14] * proj[9]
                + modl[15] * proj[13];
        clip[14] =
            modl[12] * proj[2]
                + modl[13] * proj[6]
                + modl[14] * proj[10]
                + modl[15] * proj[14];
        clip[15] =
            modl[12] * proj[3]
                + modl[13] * proj[7]
                + modl[14] * proj[11]
                + modl[15] * proj[15];

        //Right plane...
        frustum[0][0] = clip[3] - clip[0];
        frustum[0][1] = clip[7] - clip[4];
        frustum[0][2] = clip[11] - clip[8];
        frustum[0][3] = clip[15] - clip[12];

        /* Normalize the result */
        t =
            (float)Math.sqrt(
                frustum[0][0] * frustum[0][0]
                    + frustum[0][1] * frustum[0][1]
                    + frustum[0][2] * frustum[0][2]);
        frustum[0][0] /= t;
        frustum[0][1] /= t;
        frustum[0][2] /= t;
        frustum[0][3] /= t;

        /* Extract the numbers for the LEFT plane */
        frustum[1][0] = clip[3] + clip[0];
        frustum[1][1] = clip[7] + clip[4];
        frustum[1][2] = clip[11] + clip[8];
        frustum[1][3] = clip[15] + clip[12];

        /* Normalize the result */
        t =
            (float)Math.sqrt(
                frustum[1][0] * frustum[1][0]
                    + frustum[1][1] * frustum[1][1]
                    + frustum[1][2] * frustum[1][2]);
        frustum[1][0] /= t;
        frustum[1][1] /= t;
        frustum[1][2] /= t;
        frustum[1][3] /= t;

        /* Extract the BOTTOM plane */
        frustum[2][0] = clip[3] + clip[1];
        frustum[2][1] = clip[7] + clip[5];
        frustum[2][2] = clip[11] + clip[9];
        frustum[2][3] = clip[15] + clip[13];

        /* Normalize the result */
        t =
            (float)Math.sqrt(
                frustum[2][0] * frustum[2][0]
                    + frustum[2][1] * frustum[2][1]
                    + frustum[2][2] * frustum[2][2]);
        frustum[2][0] /= t;
        frustum[2][1] /= t;
        frustum[2][2] /= t;
        frustum[2][3] /= t;

        /* Extract the TOP plane */
        frustum[3][0] = clip[3] - clip[1];
        frustum[3][1] = clip[7] - clip[5];
        frustum[3][2] = clip[11] - clip[9];
        frustum[3][3] = clip[15] - clip[13];

        /* Normalize the result */
        t =
            (float)Math.sqrt(
                frustum[3][0] * frustum[3][0]
                    + frustum[3][1] * frustum[3][1]
                    + frustum[3][2] * frustum[3][2]);
        frustum[3][0] /= t;
        frustum[3][1] /= t;
        frustum[3][2] /= t;
        frustum[3][3] /= t;

        /* Extract the FAR plane */
        frustum[4][0] = clip[3] - clip[2];
        frustum[4][1] = clip[7] - clip[6];
        frustum[4][2] = clip[11] - clip[10];
        frustum[4][3] = clip[15] - clip[14];

        /* Normalize the result */
        t =
            (float)Math.sqrt(
                frustum[4][0] * frustum[4][0]
                    + frustum[4][1] * frustum[4][1]
                    + frustum[4][2] * frustum[4][2]);
        frustum[4][0] /= t;
        frustum[4][1] /= t;
        frustum[4][2] /= t;
        frustum[4][3] /= t;

        /* Extract the NEAR plane */
        frustum[5][0] = clip[3] + clip[2];
        frustum[5][1] = clip[7] + clip[6];
        frustum[5][2] = clip[11] + clip[10];
        frustum[5][3] = clip[15] + clip[14];

        /* Normalize the result */
        t =
            (float)Math.sqrt(
                frustum[5][0] * frustum[5][0]
                    + frustum[5][1] * frustum[5][1]
                    + frustum[5][2] * frustum[5][2]);
        frustum[5][0] /= t;
        frustum[5][1] /= t;
        frustum[5][2] /= t;
        frustum[5][3] /= t;
    
    }

    /**
     * <code>containsCube</code> returns a boolean based on if a cube is
     * contained within the frustum or not. The cube only need be partly
     * within the frustum for true to be returned. If the cube is 
     * entirely outside of the frustum false is returned.
     * @param x the x coordinate of the center of the cube.
     * @param y the y coordinate of the center of the cube.
     * @param z the z coordinate of the center of the cube.
     * @param size the size of a single side of the cube.
     * @return true if the cube is within the frustum false if it is completely
     * 		outside of the frustum.
     */
    public boolean containsCube(float x, float y, float z, float size) {
        //check each plane of the frustum
        for (int p = 0; p < 6; p++) {
            if (frustum[p][0] * (x - size)
                + frustum[p][1] * (y - size)
                + frustum[p][2] * (z - size)
                + frustum[p][3]
                >= buffer - 1)
                continue;
            if (frustum[p][0] * (x + size)
                + frustum[p][1] * (y - size)
                + frustum[p][2] * (z - size)
                + frustum[p][3]
                >= buffer - 1)
                continue;
            if (frustum[p][0] * (x - size)
                + frustum[p][1] * (y + size)
                + frustum[p][2] * (z - size)
                + frustum[p][3]
                >= buffer - 1)
                continue;
            if (frustum[p][0] * (x + size)
                + frustum[p][1] * (y + size)
                + frustum[p][2] * (z - size)
                + frustum[p][3]
                >= buffer - 1)
                continue;
            if (frustum[p][0] * (x - size)
                + frustum[p][1] * (y - size)
                + frustum[p][2] * (z + size)
                + frustum[p][3]
                >= buffer - 1)
                continue;
            if (frustum[p][0] * (x + size)
                + frustum[p][1] * (y - size)
                + frustum[p][2] * (z + size)
                + frustum[p][3]
                >= buffer - 10)
                continue;
            if (frustum[p][0] * (x - size)
                + frustum[p][1] * (y + size)
                + frustum[p][2] * (z + size)
                + frustum[p][3]
                >= buffer - 1)
                continue;
            if (frustum[p][0] * (x + size)
                + frustum[p][1] * (y + size)
                + frustum[p][2] * (z + size)
                + frustum[p][3]
                >= buffer - 1)
                continue;
            return false;

        }

        return true;
    }

    /**
     * <code>containsPoint</code> returns true if the point supplied is 
     * within the boundries of the view frustum. If the point is not 
     * in the boundries, false is returned.
     * @param x the x coordinate of the point.
     * @param y the y coordinate of the point.
     * @param z the z coordinate of the point.
     * @return true if the point is in the view frustum, false otherwise.
     */
    public boolean containsPoint(float x, float y, float z) {
        for (int i = 0; i < 6; i++) {
            if (frustum[i][0] * x
                + frustum[i][1] * y
                + frustum[i][2] * z
                + frustum[i][3]
                <= buffer - 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * <code>containsSphere</code> returns true if any part of a sphere is
     * within the boundries of the view frustum and false otherwise.
     * @param x the x coordinate of the center of the sphere.
     * @param y the y coordinate of the center of the sphere.
     * @param z the z coordinate of the center of the sphere.
     * @param radius the radius of the sphere.
     * @return true if the any part of the sphere is in the view frustum, false
     *      otherwise.
     */
    public boolean containsSphere(float x, float y, float z, float radius) {
        for (int i = 0; i < 6; i++) {
            if (frustum[i][0] * x
                + frustum[i][1] * y
                + frustum[i][2] * z
                + frustum[i][3]
                <= -radius * buffer) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * <code>setBuffer</code> sets a buffer for detection if a shape is within
     * the frustum or not. This is a scalar, where a higher number allows more
     * checks to pass. The default is 1.0.
     * @param value the new buffer value.
     */
    public void setBuffer(float value) {
        buffer = value;
    }
}
