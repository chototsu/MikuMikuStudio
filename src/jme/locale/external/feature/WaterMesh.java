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
package jme.locale.external.feature;

import java.util.logging.Level;

import jme.exception.MonkeyGLException;
import jme.exception.MonkeyRuntimeException;
import jme.system.DisplaySystem;
import jme.texture.TextureManager;
import jme.utility.LoggingSystem;

import org.lwjgl.opengl.GL;

/**
 * <code>WaterMesh</code> creates a mesh that represents water height values.
 * This mesh contains a wave origin that causes ripples to flow outward. 
 * @author Mark Powell
 */
public class WaterMesh implements Water {
    private int texId;
    private GL gl;
    private float height;
    private int size;
    private int spacing;

    private float[][] water;
    private float[][] wt;

    private float amplitude;
    private int dx=0,dy=0;
    private int v; /* Wave speed */
       
    /**
     * Constructor instantiates a <code>WaterMesh</code> object. The attributes
     * of the water mesh are defined as the passed parameters. 
     * 
     * @param size the size of the mesh, where the total area is defined as
     *      size x size.
     * @param spacing defines the distance between each vertex.
     * @param amplitude defines the amount a vertex can travel along the
     *      y axis.
     * @throws MonkeyGLException is thrown if water mesh is created before
     *      the OpenGL context.
     * @throws MonkeyRuntimeException if size is negative.
     */
    public WaterMesh(int size, int spacing, float amplitude) {
        gl = DisplaySystem.getDisplaySystem().getGL();

        if (null == gl) {
            throw new MonkeyGLException(
                "OpenGL Context must be created before WaterMesh.");
        }

        water = new float[size][size];
        wt = new float[size][size];
        v = -4;
        if(size < 0) {
            throw new MonkeyRuntimeException("Size cannot be negative.");
        }

        this.size = size;
        this.spacing = spacing;
        this.amplitude = amplitude;
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                water[i][j] = (float) (2 * Math.sin((float)i * 180 / Math.PI));
                wt[i][j] = 0;
            }
        }
        
        LoggingSystem.getLoggingSystem().getLogger().log(Level.INFO,
                "WaterMesh created.");
    }

    /**
     * <code>setTexture</code> sets the texture used for the watermesh.
     * @param filename the image file.
     */
    public void setTexture(String filename) {
        texId =
            TextureManager.getTextureManager().loadTexture(
                filename,
                GL.LINEAR_MIPMAP_LINEAR,
                GL.LINEAR,
                true);
    }

    /**
     * <code>setBaseHeight</code> sets the height of the water. 
     * @param height the height of the water.
     */
    public void setBaseHeight(float height) {
        this.height = height;
    }
    
    /**
     * <code>setOrigin</code> sets the originating point of the water mesh.
     * @param x the x coordinate of the origin.
     * @param z the y coordinate of the origin.
     */
    public void setOrigin(int x, int z) {
        dx = x;
        dy = z;
    }
    
    /**
     * <code>setWindSpeed</code> sets the speed of the winds that generate
     * the waves.
     * @param speed the speed of the winds, which generated the waves.
     */
    public void setWindSpeed(int speed) {
    	v = speed * 10;
    }
    
    /**
     * <code>update</code> sets the coordinates of the mesh to properly 
     * simulate the rise and fall of waves.
     * 
     * @param time the time between frames.
     */
    public void update(float time) {
        /* h = A*sin(2*pi*W*t)*/
        float s, t;
        float W = 0.1f;
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                s = (float)Math.sqrt((j - dx) * (j - dx) + (i - dy) * (i - dy));
                wt[i][j] += 0.1f;
                t = (s / v);
                water[i][j] = (float)(amplitude * Math.sin(2 * Math.PI * W * 
                        (wt[i][j] + t)));
            }
        }
    }

    /**
     * <code>render</code> displays the water mesh to the view port.
     */
    public void render() {
        gl.enable(GL.BLEND);
        gl.enable(GL.TEXTURE_2D);
        gl.enable(GL.DEPTH_TEST);
        gl.disable(GL.CULL_FACE);
        TextureManager.getTextureManager().bind(texId);
        float tx, ty;

        /* Draw water */
        gl.color4f(1f, 1f, 1f, 0.6f);

        gl.begin(GL.TRIANGLES);
        float td = (float)1 / size;

        for (int i = 0; i < size - 1; i++) {
            tx = (float)i / size;
                
            for (int j = 0; j < size - 1; j++) {
                ty = (float)j / size;
                
                gl.texCoord2f(tx, ty);
                gl.vertex3f(spacing * i, water[i][j] + height, spacing * j);
                gl.texCoord2f(tx + td, ty);
                gl.vertex3f(
                    spacing * (i + 1),
                    water[i + 1][j] + height,
                    spacing * j);
                gl.texCoord2f(tx + td, ty + td);
                gl.vertex3f(
                    spacing * (i + 1),
                    water[i + 1][j + 1] + height,
                    spacing * (j + 1));

                gl.texCoord2f(tx, ty + td);
                gl.vertex3f(
                    spacing * i,
                    water[i][j + 1] + height,
                    spacing * (j + 1));
                gl.texCoord2f(tx, ty);
                gl.vertex3f(spacing * i, water[i][j] + height, spacing * j);
                gl.texCoord2f(tx + td, ty + td);
                gl.vertex3f(
                    spacing * (i + 1),
                    water[i + 1][j + 1] + height,
                    spacing * (j + 1));
            }
        }
        gl.end();
        
        gl.disable(GL.BLEND);
        gl.disable(GL.TEXTURE_2D);
        gl.disable(GL.DEPTH_TEST);
        gl.enable(GL.CULL_FACE);
    }
}
