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
import jme.geometry.model.Vertex;
import jme.texture.TextureManager;
import jme.utility.LoggingSystem;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.Window;

/**
 * <code>SkyDome</code> defines an implementation of the sky interface where
 * the sky is represented as a dome. The dome or half sphere is rendered over
 * the camera and gives a smooth look and allow for transitions of the texture
 * and color. This will allow for day/night effects and other natural 
 * phenomenon.
 * 
 * For more information see 
 * <a href="http://www.spheregames.com/files/SkyDomesPDF.zip">this</a> 
 * PDF file.
 * @author Mark Powell
 * @version $Id: SkyDome.java,v 1.1.1.1 2003-10-29 10:58:26 Anakan Exp $
 */
public class SkyDome implements Sky {

	private int texId;

	//attributes of the dome
	private float radius;
	private float dLon;
	private float dLat;
	private float hTile;
	private float vTile;

	//attributes for animation
	private float xSpeed;
    private float ySpeed;
	private float xTexAnimation = 0.0f;
	private float yTexAnimation = 0.0f;
	private float rotationSpeed;
	private float rotation;

	//dome vertices
	private Vertex vertices[];
	private int numVertices;

	private final static float DTOR = (float) java.lang.Math.PI / 180f;

	/**
	 * Constructor instantiates a new <code>SkyDome</code> object. All
	 * attributes are stored and the dome is generated.
	 * @param radius the radius of the sphere.
	 * @param dLon the spacing between longitude lines in degrees.
	 * @param dLat the spacing between latitude lines in degrees.
	 * @param hTile
	 * @param vTile
	 * @throws MonkeyGLException if the OpenGL context is not created before
	 * 		creation.
	 */
	public SkyDome(
		float radius,
		float dLon,
		float dLat,
		float hTile,
		float vTile) {

		if (!Window.isCreated()) {
			throw new MonkeyGLException(
				"Window must be created before SkyDome.");
		}

		this.radius = radius;
		this.dLon = dLon;
		this.dLat = dLat;
		this.hTile = hTile;
		this.vTile = vTile;

		generateDome();

		LoggingSystem.getLoggingSystem().getLogger().log(
			Level.INFO,
			"Created Skydome.");
	}

	/**
	 * <code>setTexture</code> assignes the texture that makes up the
	 * sky dome.
	 * @param filename the image file that represents the sky dome texture.
	 */
	public void setTexture(String filename) {
		texId =
			TextureManager.getTextureManager().loadTexture(
				filename,
				GL.GL_LINEAR_MIPMAP_LINEAR,
				GL.GL_LINEAR,
				true);
	}

	/**
	 * <code>getSize</code> returns the size of the sky dome, which is
	 * represented by the diameter of the dome.
	 * @return the radius of the dome.
	 */
	public float getSize() {
		return radius / 4;
	}

	/**
	 * <code>setTextureSpeed</code> sets the animation speed of the dome texture.
	 * 
	 * @param xSpeed the speed along the x axis.
	 * @param ySpeed the speed along the y axis.
	 */
	public void setTextureSpeed(float xSpeed, float ySpeed) {
		this.xSpeed = xSpeed / 100;
		this.ySpeed = ySpeed / 100;
	}

	/**
	 * <code>setDomeRotation</code> sets the speed at which the dome rotates
	 * around the camera.
	 * @param rotationSpeed the speed at which the dome rotates.
	 */
	public void setDomeRotation(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed / 1000;
	}

	/**
	 * <code>update</code> calculates the new position of the dome and
	 * the texture based on the speeds.
	 */
	public void update(float time) {
		xTexAnimation += xSpeed * time / 10000;
		yTexAnimation += ySpeed * time / 10000;
		rotation += rotationSpeed * time;

		if (rotation > 360 || rotation < 0) {
			rotation %= 360;
		}

		if (xTexAnimation > hTile) {
			xTexAnimation %= hTile;
		}

		if (yTexAnimation > vTile) {
			yTexAnimation %= vTile;
		}

	}

	/**
	 * <code>render</code> draws the dome around the camera and applies the 
	 * texture. 
	 */
	public void render() {
		GL.glEnable(GL.GL_TEXTURE_2D);
		GL.glDisable(GL.GL_DEPTH_TEST);
		GL.glCullFace(GL.GL_FRONT);
		GL.glBindTexture(GL.GL_TEXTURE_2D, texId);

		GL.glPushMatrix();
		GL.glRotatef(270, 1.0f, 0.0f, 0.0f);
		GL.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
		GL.glBegin(GL.GL_TRIANGLE_STRIP);

		for (int i = 0; i < numVertices; i++) {
			GL.glColor3f(1.0f, 1.0f, 1.0f);

			GL.glTexCoord2f(
				vertices[i].u + xTexAnimation,
				vertices[i].v + yTexAnimation);
			GL.glVertex3f(vertices[i].x, vertices[i].y, vertices[i].z);
		}

		GL.glEnd();

		GL.glPopMatrix();
		GL.glDisable(GL.GL_TEXTURE_2D);
		GL.glEnable(GL.GL_DEPTH_TEST);
		GL.glCullFace(GL.GL_BACK);
	}

	/**
	 * <code>generateDome</code> builds the list of vertices that make up
	 * the dome and sets the texture coordinates.
	 * NOTE: Code is from the sky dome tutorial at www.spheregames.com.
	 */
	private void generateDome() {

		numVertices = (int) ((360 / dLon) * (90 / dLat) * 4);
		vertices = new Vertex[numVertices];

		for (int i = 0; i < numVertices; i++) {
			vertices[i] = new Vertex();
		}

		float vx, vy, vz, mag;

		// Generate the dome
		int n = 0;

		for (int lat = 0; lat <= 90 - dLat; lat += (int) dLat) {
			for (int lon = 0; lon <= 360 - dLon; lon += (int) dLon) {
				vertices[n].x =
					radius
						* (float) Math.sin(lat * DTOR)
						* (float) Math.cos(DTOR * lon);
				vertices[n].y =
					radius
						* (float) Math.sin(lat * DTOR)
						* (float) Math.sin(DTOR * lon);
				vertices[n].z = radius * (float) Math.cos(lat * DTOR);

				vx = vertices[n].x;
				vy = vertices[n].y;
				vz = vertices[n].z;

				mag = (float) Math.sqrt((vx * vx) + (vy * vy) + (vz * vz));
				vx /= mag;
				vy /= mag;
				vz /= mag;

				vertices[n].u =
					hTile * (float) (Math.atan2(vx, vz) / (Math.PI * 2)) + 0.5f;
				vertices[n].v =
					vTile * (float) (Math.asin(vy) / Math.PI) + 0.5f;
				n++;

				vertices[n].x =
					radius
						* (float) Math.sin((lat + dLat) * DTOR)
						* (float) Math.cos(lon * DTOR);
				vertices[n].y =
					radius
						* (float) Math.sin((lat + dLat) * DTOR)
						* (float) Math.sin(lon * DTOR);
				vertices[n].z = radius * (float) Math.cos((lat + dLat) * DTOR);

				vx = vertices[n].x;
				vy = vertices[n].y;
				vz = vertices[n].z;

				mag = (float) Math.sqrt((vx * vx) + (vy * vy) + (vz * vz));
				vx /= mag;
				vy /= mag;
				vz /= mag;

				vertices[n].u =
					hTile * (float) (Math.atan2(vx, vz) / (Math.PI * 2)) + 0.5f;
				vertices[n].v =
					vTile * (float) (Math.asin(vy) / Math.PI) + 0.5f;
				n++;

				vertices[n].x =
					radius
						* (float) Math.sin(DTOR * lat)
						* (float) Math.cos(DTOR * (lon + dLon));
				vertices[n].y =
					radius
						* (float) Math.sin(DTOR * lat)
						* (float) Math.sin(DTOR * (lon + dLon));
				vertices[n].z = radius * (float) Math.cos(DTOR * lat);

				vx = vertices[n].x;
				vy = vertices[n].y;
				vz = vertices[n].z;

				mag = (float) Math.sqrt((vx * vx) + (vy * vy) + (vz * vz));
				vx /= mag;
				vy /= mag;
				vz /= mag;

				vertices[n].u =
					hTile * (float) (Math.atan2(vx, vz) / (Math.PI * 2)) + 0.5f;
				vertices[n].v =
					vTile * (float) (Math.asin(vy) / Math.PI) + 0.5f;
				n++;

				if (lat > -90 && lat < 90) {
					// Calculate the vertex at phi+dLat, theta+dLon
					vertices[n].x =
						radius
							* (float) Math.sin((lat + dLat) * DTOR)
							* (float) Math.cos(DTOR * (lon + dLon));
					vertices[n].y =
						radius
							* (float) Math.sin((lat + dLat) * DTOR)
							* (float) Math.sin(DTOR * (lon + dLon));
					vertices[n].z =
						radius * (float) Math.cos((lat + dLat) * DTOR);

					// Calculate the texture coordinates
					vx = vertices[n].x;
					vy = vertices[n].y;
					vz = vertices[n].z;

					mag = (float) Math.sqrt((vx * vx) + (vy * vy) + (vz * vz));
					vx /= mag;
					vy /= mag;
					vz /= mag;

					vertices[n].u =
						hTile * (float) (Math.atan2(vx, vz) / (Math.PI * 2))
							+ 0.5f;
					vertices[n].v =
						vTile * (float) (Math.asin(vy) / Math.PI) + 0.5f;
					n++;
				}
			}
		}

		//Deal with the seam issue.
		for (int i = 0; i < numVertices - 3; i++) {
			if (vertices[i].u - vertices[i + 1].u > 0.9f)
				vertices[i + 1].u += 1.0f;

			if (vertices[i + 1].u - vertices[i].u > 0.9f)
				vertices[i].u += 1.0f;

			if (vertices[i].u - vertices[i + 2].u > 0.9f)
				vertices[i + 2].u += 1.0f;

			if (vertices[i + 2].u - vertices[i].u > 0.9f)
				vertices[i].u += 1.0f;

			if (vertices[i + 1].u - vertices[i + 2].u > 0.9f)
				vertices[i + 2].u += 1.0f;

			if (vertices[i + 2].u - vertices[i + 1].u > 0.9f)
				vertices[i + 1].u += 1.0f;

			if (vertices[i].v - vertices[i + 1].v > 0.8f)
				vertices[i + 1].v += 1.0f;

			if (vertices[i + 1].v - vertices[i].v > 0.8f)
				vertices[i].v += 1.0f;

			if (vertices[i].v - vertices[i + 2].v > 0.8f)
				vertices[i + 2].v += 1.0f;

			if (vertices[i + 2].v - vertices[i].v > 0.8f)
				vertices[i].v += 1.0f;

			if (vertices[i + 1].v - vertices[i + 2].v > 0.8f)
				vertices[i + 2].v += 1.0f;

			if (vertices[i + 2].v - vertices[i + 1].v > 0.8f)
				vertices[i + 1].v += 1.0f;
		}
	}
}
