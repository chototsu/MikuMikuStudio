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

package jme.locale.external;

import java.util.logging.Level;

import jme.exception.MonkeyRuntimeException;
import jme.locale.external.data.AbstractHeightMap;
import jme.entity.camera.Camera;
import jme.texture.TextureManager;
import jme.utility.LoggingSystem;

import org.lwjgl.opengl.GL;

/**
 * <code>Geomipmap</code> implements a <code>Terrain</code> class
 * for rendering of <code>HeightMaps</code> using the geomipmap CLOD 
 * scheme. <code>Geomipmap</code> uses the ideas developed by 
 * Willem H. de Boer and presented by Trent Polack. 
 * 
 * A heightmap is divided into patches, where each patch is a number
 * of vertices. These patches are then organized into LOD levels, where
 * the lowest 0, contains all vertices in the patch and the highest N 
 * only contains the four corners. 
 * 
 * The LOD chosen is dependant on the distance from the patch to the
 * camera. So, the highest LOD N will be the furthest from the camera.
 * 
 * This improves the speed of the rendering because patches that are
 * far away do not require as much detail because you wouldn't be able
 * to see it, therefore by lowering it's detail you save passing in those
 * extra, unneeded, vertices.
 * 
 * Patches that fall outside the <code>ViewFrustum</code> will also be 
 * culled with in turn saves much unnecessary rendering.
 * 
 * Special Note: Geomipmapping requires the heightmap to be a 
 * 2^N + 1 square size. For example, 1025x1025. Therefore, a 
 * <code>MidPointHeightMap</code> would not work for this.
 * 
 * @author Mark Powell
 * @version $Id: Geomipmap.java,v 1.1.1.1 2003-10-29 10:58:15 Anakan Exp $
 */
public class Geomipmap extends Terrain {
	private Camera camera;
	private int patchSize;
	private int numPatchesPerSide;
	private Patch[] patches;
	private int maxLOD;
	private float minDistance = 50;
	private int patchesRendered = 0;

	/**
	 * Constructor instantiates a new <code>Geomipmap</code> object.
	 * It calculates the level of details based on a patch size and
	 * the size of the terrain.
	 * @param patchSize the size of a single patch.
	 * @param camera the camera used for the viewport.
	 * @throws MonkeyRuntimeException if the patchSize is not greater than zero.
	 */
	public Geomipmap(
		AbstractHeightMap heightMap,
		int patchSize,
		Camera camera) {
		if (patchSize <= 0) {
			throw new MonkeyRuntimeException("patchSize must be greater than 0.");
		}

		this.heightData = heightMap;
		this.terrainSize = heightData.getSize();
		this.patchSize = patchSize;
		this.camera = camera;
		numPatchesPerSide = terrainSize / patchSize;

		patches = new Patch[numPatchesPerSide * numPatchesPerSide];

		for (int i = 0; i < patches.length; i++) {
			patches[i] = new Patch();
		}

		//calculate the max level of detail.
		int divisor = patchSize - 1;
		int LOD = 0;

		while (divisor > 2) {
			divisor = divisor >> 1;
			LOD++;
		}

		maxLOD = LOD;

		//initialize the patches
		for (int z = 0; z < numPatchesPerSide; z++) {
			for (int x = 0; x < numPatchesPerSide; x++) {
				patches[getPatchNumber(x, z)].LOD = maxLOD;
				patches[getPatchNumber(x, z)].isVisible = true;
			}
		}

		LoggingSystem.getLoggingSystem().getLogger().log(
			Level.INFO,
			"Created Geomipmap terrain system.");
	}

	/**
	 * <code>getNumPatches</code> returns the total number of terrain patches
	 * in the terrain.
	 * @return the total number of patches.
	 */
	public int getNumPatches() {
		return numPatchesPerSide * numPatchesPerSide;
	}

	/**
	 * <code>getNumPatchesRendered</code> returns the number of patches that
	 * have been rendered for a given frame.
	 * @return the total number of patches rendered for a given frame.
	 */
	public int getNumPatchesRendered() {
		return patchesRendered;
	}

	/**
	 * <code>update</code> updates the geomipmap patch values to 
	 * reflect any movement in the camera. 
	 * @param time time between frames.
	 */
	public void update(float time) {

		float patchX;
		float patchY;
		float patchZ;
		int patch;

		//update the LOD based on the camera's movement.
		for (int z = 0; z < numPatchesPerSide; z++) {
			for (int x = 0; x < numPatchesPerSide; x++) {
				patch = getPatchNumber(x, z);

				patchX = (x * patchSize) + (patchSize / 2.0f);
				patchY = heightData.getScaledHeightAtPoint(x, z);
				patchZ = (z * patchSize) + (patchSize / 2.0f);

				patchX *= xScale;
				patchZ *= zScale;

				if (camera
					.getFrustum()
					.containsSphere(
						patchX,
						patchY,
						patchZ,
						patchSize * xScale)) {
					patches[patch].isVisible = true;
				} else {
					patches[patch].isVisible = false;
				}

				if (patches[patch].isVisible || camera.hasMoved()) {
					patches[patch].distance =
						(float)Math.sqrt(
							(patchX - camera.getPosition().x)
								* (patchX - camera.getPosition().x)
								+ (patchY - camera.getPosition().y)
									* (patchY - camera.getPosition().y)
								+ (patchZ - camera.getPosition().z)
									* (patchZ - camera.getPosition().z));
					patches[patch].LOD = distanceToLOD(patches[patch].distance);
				}
			}
		}
	}

	/**
	 * <code>render</code> renders each patch if it is visible. The number of
	 * triangle fans rendered for each fan is dependant on the level of detail
	 * for the particular patch. 
	 */
	public void render() {

		GL.glActiveTextureARB(GL.GL_TEXTURE0_ARB);
		GL.glEnable(GL.GL_TEXTURE_2D);
		GL.glEnable(GL.GL_DEPTH_TEST);
		if(useDistanceFog || useVolumeFog) {
			GL.glEnable(GL.GL_FOG);
		}
		TextureManager.getTextureManager().bind(terrainTexture);

		if (isDetailed) {
			GL.glActiveTextureARB(GL.GL_TEXTURE1_ARB);
			GL.glEnable(GL.GL_TEXTURE_2D);
			TextureManager.getTextureManager().bind(detailId);
			GL.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_COMBINE_ARB);
			GL.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_RGB_SCALE_ARB, 2);
		}

		patchesRendered = 0;
		for (int z = 0; z < numPatchesPerSide; z++) {
			for (int x = 0; x < numPatchesPerSide; x++) {
				if (patches[getPatchNumber(x, z)].isVisible) {
					patchesRendered++;
					renderPatch(x, z);
				}
			}
		}

		GL.glActiveTextureARB(GL.GL_TEXTURE1_ARB);
		GL.glDisable(GL.GL_TEXTURE_2D);
		GL.glBindTexture(GL.GL_TEXTURE_2D, 0);

		GL.glActiveTextureARB(GL.GL_TEXTURE0_ARB);
		GL.glDisable(GL.GL_TEXTURE_2D);
		GL.glBindTexture(GL.GL_TEXTURE_2D, 0);
		GL.glDisable(GL.GL_DEPTH_TEST);
		if(useDistanceFog || useVolumeFog) {
			GL.glDisable(GL.GL_FOG);
		}
	}

	/**
	 * <code>setMinimumDistance</code> sets the distance for the most detail. 
	 * If any patch is within this distance, it will be rendered at full detail. 
	 * By default, minDistance is set to 50.
	 * @param minDistance the distance to render in full detail.
	 */
	public void setMinimumDistance(float minDistance) {
		this.minDistance = minDistance;
	}

	/**
	 * <code>renderVertex</code> renders a single vertex with the given
	 * coordinate, texture coordinate. The proper color is determined and
	 * applied.
	 * @param x the x value of the coordinate.
	 * @param z the z value of the coordinate.
	 * @param u the u value of the texture coordinate.
	 * @param v the v value of the texture coordinate.
	 */
	private void renderVertex(float x, float z, float u, float v) {
		float red = 1.0f;
		float green = 1.0f;
		float blue = 1.0f;
		float shade;

		//set up the lighting.
		if (isLit) {
			shade = lightMap.getShade((int) x, (int) z);
			red = shade * lightMap.getColor().x;
			green = shade * lightMap.getColor().y;
			blue = shade * lightMap.getColor().z;
		} else {
			red = 1.0f;
			green = 1.0f;
			blue = 1.0f;
		}
		GL.glColor3f(red, green, blue);

		//set up the texture coordinates.
		GL.glMultiTexCoord2fARB(GL.GL_TEXTURE0_ARB, u, v);
		if (isDetailed) {
			GL.glMultiTexCoord2fARB(
				GL.GL_TEXTURE1_ARB,
				u * repeatDetailMap,
				v * repeatDetailMap);
		}

		if (useVolumeFog) {
			setVolumetricFogCoord(
				heightData.getScaledHeightAtPoint((int) x, (int) z));
		}
		//render the point.
		GL.glVertex3f(
			x * xScale,
			heightData.getScaledHeightAtPoint((int) x, (int) z),
			z * zScale);
	}

	/**
	 * <code>renderFan</code> renders a given triangle with the LOD set up 
	 * using the <code>neighbor</code> class. 
	 * @param x the x component of the center of the fan.
	 * @param z the y component of the center of the fan.
	 * @param size the size of the side of the fan.
	 * @param neighbor determines how many vertices to render.
	 */
	private void renderFan(float x, float z, float size, Neighbor neighbor) {

		//Holds texture coordinates.
		float texLeft;
		float texBottom;
		float midX;
		float midZ;
		float texRight;
		float texTop;
		float halfSize = size / 2.0f;
		//set up the texture coordinates for the four corners of the fan.
		texLeft = (Math.abs(x - halfSize) / terrainSize);
		texBottom = (Math.abs(z - halfSize) / terrainSize);
		texRight = (Math.abs(x + halfSize) / terrainSize);
		texTop = (Math.abs(z + halfSize) / terrainSize);

		//set up the coordinate of the middle of the fan.
		midX = ((texLeft + texRight) / 2);
		midZ = ((texBottom + texTop) / 2);

		GL.glBegin(GL.GL_TRIANGLE_FAN);

		//a fan is rendered by drawing a line from the center to the
		//four corners. If a neighbor side is true, we also draw a 
		//line from the center to that side.
		renderVertex(x, z, midX, midZ);
		renderVertex(x - halfSize, z - halfSize, texLeft, texBottom);

		//left neighbor
		if (neighbor.left) {
			renderVertex(x - halfSize, z, texLeft, midZ);
		}

		renderVertex(x - halfSize, z + halfSize, texLeft, texTop);

		//upper neighbor
		if (neighbor.up) {
			renderVertex(x, z + halfSize, midX, texTop);
		}

		renderVertex(x + halfSize, z + halfSize, texRight, texTop);

		//right neighbor
		if (neighbor.right) {
			renderVertex(x + halfSize, z, texRight, midZ);
		}

		renderVertex(x + halfSize, z - halfSize, texRight, texBottom);

		//lower neighbor
		if (neighbor.down) {
			renderVertex(x, z - halfSize, midX, texBottom);
		}

		renderVertex(x - halfSize, z - halfSize, texLeft, texBottom);

		GL.glEnd();
	}

	/**
	 * <code>renderPatch</code> renders an entire patch of terrain. 
	 * It's main concern is first setting up the patch neighbors. This
	 * tells us what patches that surround the patch are of a different
	 * LOD. Next, the fan neighbors are set up. This is based on the LOD
	 * of the patch, and will denote how many fans to render.
	 * 
	 * @param x the x component of the center of the patch
	 * @param z the z component of the center of the patch.
	 */
	private void renderPatch(int x, int z) {
		Neighbor patchNeighbor = new Neighbor();
		Neighbor fanNeighbor = new Neighbor();
		int patch = getPatchNumber(x, z);

		//set up patch neighbors. Used to prevent cracking.
		if (x == 0) {
			patchNeighbor.left = true;
		} else if (
			patches[getPatchNumber(x - 1, z)].LOD <= patches[patch].LOD) {
			patchNeighbor.left = true;
		} else {
			patchNeighbor.left = false;
		}

		if (z == numPatchesPerSide - 1) {
			patchNeighbor.up = true;
		} else if (
			patches[getPatchNumber(x, z + 1)].LOD <= patches[patch].LOD) {
			patchNeighbor.up = true;
		} else {
			patchNeighbor.up = false;
		}

		if (x == numPatchesPerSide - 1) {
			patchNeighbor.right = true;
		} else if (
			patches[getPatchNumber(x + 1, z)].LOD <= patches[patch].LOD) {
			patchNeighbor.right = true;
		} else {
			patchNeighbor.right = false;
		}

		if (z == 0) {
			patchNeighbor.down = true;
		} else if (
			patches[getPatchNumber(x, z - 1)].LOD <= patches[patch].LOD) {
			patchNeighbor.down = true;
		} else {
			patchNeighbor.down = false;
		}

		//set up the neighbors for the fans. This will be based on the
		//LOD level.
		float fsize = patchSize;
		int divisor = patchSize - 1;
		int LOD = patches[patch].LOD;

		while (LOD-- >= 0) {
			divisor = divisor >> 1;
		}

		fsize /= divisor;

		float halfSize = fsize / 2.0f;
		for (float fanz = halfSize;
			((int) fanz + halfSize) < patchSize + 1;
			fanz += fsize) {
			for (float fanx = halfSize;
				((int) fanx + halfSize) < patchSize + 1;
				fanx += fsize) {
				if (fanx == halfSize) {
					fanNeighbor.left = patchNeighbor.left;
				} else {
					fanNeighbor.left = true;
				}

				if (fanz == halfSize) {
					fanNeighbor.down = patchNeighbor.down;
				} else {
					fanNeighbor.down = true;
				}

				if (fanx >= (patchSize - halfSize)) {
					fanNeighbor.right = patchNeighbor.right;
				} else {
					fanNeighbor.right = true;
				}

				if (fanz >= (patchSize - halfSize)) {
					fanNeighbor.up = patchNeighbor.up;
				} else {
					fanNeighbor.up = true;
				}

				renderFan(
					(x * patchSize) + fanx,
					(z * patchSize) + fanz,
					fsize,
					fanNeighbor);
			}
		}

	}

	/**
	 * <code>distanceToLOD</code> takes a distance from the camera to the
	 * center of a patch and calculates what LOD the patch should be.
	 * @param distance the distance between the camera and the center of
	 * 		a patch.
	 * @return the LOD appropriate for the distance.
	 */
	private int distanceToLOD(float distance) {
		float factor = minDistance;
		for (int i = 0; i < maxLOD; i++) {

			if (distance < factor) {
				return i;
			}
			factor *= 2.5;
		}
		return maxLOD;
	}

	/**
	 * Given the x, z coordinate of a patch, the index into the patch
	 * array is returned.
	 * @param x coordinate of the patch.
	 * @param z coordinate of the patch.
	 * @return index of the patch entry.
	 */
	private int getPatchNumber(int x, int z) {
		return (z * numPatchesPerSide + x);
	}

	/**
	 * <code>Patch</code> is an inner class that contains needed data
	 * for an individual patch of posts.
	 */
	private class Patch {
		/**
		 * <code>distance</code> defines the distance from the camera
		 * to the center of the patch.
		 */
		public float distance;
		/**
		 * <code>LOD</code> defines the patches current level of detail.
		 */
		public int LOD;
		/**
		 * <code>isVisible</code> denotes whether the patch is within the
		 * view frustum or not.
		 */
		public boolean isVisible;
	}

	/**
	 * <code>Neighbor</code> contains information about the neighbors 
	 * surrounding a patch. The class keeps track if a neighbor is of a 
	 * different LOD or not.  
	 */
	private class Neighbor {
		/**
		 * <code>left</code> denotes if the left neighbor has a different
		 * LOD.
		 */
		public boolean left;
		/**
		 * <code>right</code> denotes if the right neighbor has a different
		 * LOD.
		 */
		public boolean right;
		/**
		 * <code>up</code> denotes if the upper neighbor has a different
		 * LOD.
		 */
		public boolean up;
		/**
		 * <code>down</code> denotes if the lower neighbor has a different
		 * LOD.
		 */
		public boolean down;
	}

}
