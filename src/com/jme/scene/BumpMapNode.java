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

import java.nio.FloatBuffer;

import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.light.Light;
import com.jme.light.PointLight;
import com.jme.math.Matrix3f;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * <code>BumpMapNode</code>
 *
 * @author Mark Powell
 * @version $id$
 */
public class BumpMapNode extends Node {
	private Node objects;
	private Texture normalMap;
	private Light light;
	private boolean modulate;
	private TextureState textureState;
	private TextureState textureStateModulated;
	private AlphaState alphaState;
	private boolean forceCullObjects;

	public BumpMapNode(Node objects, Texture normalMap, Light light,
			boolean modulate) {

		this.objects = objects;
		this.normalMap = normalMap;
		this.light = light;
		this.modulate = modulate;

		this.forceCullObjects = objects.isForceCulled();

		textureState = DisplaySystem.getDisplaySystem().getRenderer()
				.createTextureState();
		textureStateModulated = DisplaySystem.getDisplaySystem().getRenderer()
				.createTextureState();

		normalMap.setApply(Texture.AM_COMBINE);
		normalMap.setCombineFuncRGB(Texture.ACF_DOT3_RGB);
		normalMap.setCombineSrc0RGB(Texture.ACS_TEXTURE);
		normalMap.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
		normalMap.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);
		normalMap.setCombineOp1RGB(Texture.ACO_SRC_COLOR);

		textureState.setTexture(normalMap, 0);
		textureStateModulated.setTexture(normalMap, 0);

		Texture texture = new Texture();
		texture.setApply(Texture.AM_COMBINE);
		texture.setCombineFuncRGB(Texture.ACF_MODULATE);
		texture.setCombineSrc0RGB(Texture.ACS_CONSTANT);
		texture.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
		texture.setCombineSrc1RGB(Texture.ACS_PREVIOUS);
		texture.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
		texture.setBlendColor(light.getDiffuse());
		textureStateModulated.setTexture(texture, 1);

		Texture texture2 = new Texture();
		texture2.setApply(Texture.AM_COMBINE);
		texture2.setCombineFuncRGB(Texture.ACF_ADD);
		texture2.setCombineSrc0RGB(Texture.ACS_PREVIOUS);
		texture2.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
		texture2.setCombineSrc1RGB(Texture.ACS_CONSTANT);
		texture2.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
		texture2.setBlendColor(light.getAmbient());
		textureStateModulated.setTexture(texture2, 2);

		alphaState = DisplaySystem.getDisplaySystem().getRenderer()
				.createAlphaState();
		alphaState.setBlendEnabled(true);
		alphaState.setTestEnabled(false);
		alphaState.setSrcFunction(AlphaState.SB_DST_COLOR);
		alphaState.setDstFunction(AlphaState.DB_ZERO);

	}

	public Node getObjects() {
		return objects;
	}

	public Texture getNormalMap() {
		return normalMap;
	}

	public TextureState getTextureState() {
		if (modulate) {
			return textureStateModulated;
		} else {
			return textureState;
		}
	}

	public Light getLight() {
		return light;
	}

	public AlphaState getAlphaState() {
		return alphaState;
	}

	public boolean isModulated() {
		return modulate;
	}

	public void setCurrentAmbientMaterial(ColorRGBA color) {
		//This is used to capture material changes for bump mapping. Materials
		// are implemented by using additional texture units with a constant
		// color
		// as one of the inputs for a texture add.
		textureStateModulated.getTexture(2).setBlendColor(
				color.mult(light.getAmbient()));
	}

	public void setCurrentDiffuseMaterial(ColorRGBA color, int maxTextureUnits) {
		//		 This is used to capture material changes for bump mapping. Materials
		// are implemented by using additional texture units with a constant
		// color
		// as one of the inputs for a texture modulate. If there are enough
		// texture units, then ambient is added. If not, then it gets modulated
		// by L.N.
		if (textureStateModulated.getNumberOfUnits() > 2) {
			textureStateModulated.getTexture(1).setBlendColor(color
					.mult(light.getDiffuse()));
		} else {
			FloatBuffer fb = textureStateModulated.getTexture(2).getBlendColor();
			ColorRGBA c = new ColorRGBA(fb.get(), fb.get(), fb.get(), fb.get());
			textureStateModulated.getTexture(1).setBlendColor(color
					.mult(light.getDiffuse()).add(c));
		}
	}

	public void computeLightVectors(TriMesh mesh) {
		// Generate light direction vectors in the surface local space and store
		// them in the trimesh for interpolation via the rasterizer. This
		// assumes
		// the user has provided normalized normals.
		if (mesh.getNormals() == null || mesh.getTextures() == null)
			return;

		//assert( rkMesh.Colors() );

		// get the world light vector
		Vector3f lightVector;
		switch (light.getType()) {
			case Light.LT_DIRECTIONAL :
				lightVector = ((DirectionalLight) light).getDirection()
						.negate();
				break;
			case Light.LT_POINT :
			case Light.LT_SPOT :
				lightVector = ((PointLight) light).getLocation();
				break;
			default :
				// ambient light, nothing we can do to handle this
				return;
		}

		//	  transform the world light vector into model space
		Vector3f modelLightVector = lightVector.subtract(mesh
				.getWorldTranslation());
		modelLightVector = mesh.getWorldRotation().mult(modelLightVector);
		modelLightVector.divideLocal(mesh.getWorldScale());

		// The surface local space information is computed for all the vertices.
		// We iterate over the triangles because we need to know the
		// connectivity
		// information. The end result is assignment of the light vectors.
		Vector3f[] vertices = mesh.getVertices();
		Vector3f[] normals = mesh.getNormals();
		Vector2f[] textures = mesh.getTextures();

		// Set the light vectors to (0,0,0) as a flag that the quantity has not
		// yet been computed. The probability that a light vector is actually
		// (0,0,0) should be small, so the flag system should save computation
		// time overall.
		ColorRGBA[] lightVectors = mesh.getColors();
		for (int i = 0; i < lightVectors.length; i++) {
			lightVectors[i] = new ColorRGBA();
		}
		//memset(akLVec,0,mesh.GetVertexQuantity()*sizeof(ColorRGB));

		for (int tris = 0; tris < mesh.getTriangleQuantity(); tris++) {
			// get the triangle vertices and attributes
			int triIndex[] = new int[3];
			mesh.getTriangle(tris, triIndex);

			Vector3f[] vert = {vertices[triIndex[0]], vertices[triIndex[1]],
					vertices[triIndex[2]]};

			Vector3f[] norm = {normals[triIndex[0]], normals[triIndex[1]],
					normals[triIndex[2]]};

			Vector2f[] tex = {textures[triIndex[0]], textures[triIndex[1]],
					textures[triIndex[2]]};

			for (int i = 0; i < 3; i++) {
				ColorRGBA color = lightVectors[triIndex[i]];
				if (!color.equals(ColorRGBA.black))
					continue;

				// Compute the surface local space at each vertex.
				//
				// TO DO. If the geometry is static in model space, then we
				// should precompute the surface local space and store it.
				//
				// The normal vector N is the surface normal. The vertex normal
				// is used as an approximation to N.
				//
				// The tangent vector T is computed by thinking of the surface
				// in
				// parametric form P(u,v) for some scalar variables u and v. In
				// this case, a tangent is the partial derivative T = dP/du.
				// This
				// quantity is estimated from the triangle attributes. The
				// texture coordinates (u,v) are used as an approximation to the
				// parametric quantities.
				//
				// The binormal vector is B = Cross(N,T). If the estimation of
				// T is ill-posed, we try BLAH BLAH BLAH.

				//The tangent T is defined
				// by dSurf/dS, and the binormal B = Cross(N,T). We assume the
				// normals are normalized, and we assume the model is
				// parametrized so that the square patch assumption holds.
				//
				// We need to compute the tangent vector at the current vertex.
				// If we think of the surface as being a vector-valued function
				// P(u,v), then we want to set T to be dP/du. We try to compute
				// T, but if the parametrization is such that our direct
				// definition of T is not applicable, then we try for the
				// binormal
				// directly and then back out a reasonable T. If there is no
				// useful parametrization information, we just assume that the
				// light vector is in the same direction as the surface normal.

				int p = (i == 0) ? 2 : i - 1;
				int n = (i + 1) % 3;

				// compute edge vectors at the specified vertex
				Vector3f kDP1 = vert[n].subtract(vert[i]);
				Vector3f kDP2 = vert[p].subtract(vert[i]);

				// estimate a tangent surface vector
				Vector3f kTangent = null;
				boolean fDegenerate = false;
				float fEpsilon = 1e-05f;
				if (Math.abs(kDP1.length()) < fEpsilon
						|| Math.abs(kDP2.length()) < fEpsilon) {
					// The triangle is very small, call it degenerate.
					fDegenerate = true;
				} else {
					// difference of surface parameters along triangle edge
					float fDU1 = tex[n].x - tex[i].x;
					float fDV1 = tex[n].y - tex[i].y;

					if (Math.abs(fDV1) < fEpsilon) {
						// The triangle effectively has no variation in the v
						// texture coordinate.
						if (Math.abs(fDU1) < fEpsilon) {
							// The triangle effectively has no variation in the
							// u
							// coordinate. Since the texture coordinates do not
							// effectively vary on this triangle, treat it as a
							// degenerate parametric surface.
							fDegenerate = true;
						} else {
							// The variation is effectively all in u, so set the
							// tangent T = dP/du.
							kTangent = kDP1.divide(fDU1);
						}
					} else {
						// difference of surface parameters along triangle edge
						float fDU2 = tex[p].x - tex[i].x;
						float fDV2 = tex[p].y - tex[i].y;
						float fDet = fDV1 * fDU2 - fDV2 * fDU1;

						if (Math.abs(fDet) >= fEpsilon) {
							// The triangle vertices form three collinear points
							// in parameter space, so
							//   dP/du = (dv1*dP2-dv2*dP1)/(dv1*du2-dv2*du1)
							kTangent = (kDP2.mult(fDV1).subtract(
									kDP1.mult(fDV2)).divide(fDet));
						} else {
							// The triangle vertices are collinear in parameter
							// space.
							fDegenerate = true;
						}
					}
				}

				if (fDegenerate) {
					// The texture coordinate mapping is not properly defined
					// for
					// this. Just say that the tangent space light vector points
					// in the same direction as the surface normal.
					color.r = 0.0f;
					color.g = 0.0f;
					color.b = 1.0f;
					continue;
				}

				// Project T into the tangent plane by projecting out the
				// surface
				// normal, then make it unit length.
				kTangent = kTangent.subtract(norm[i]
						.mult(norm[i].dot(kTangent)));
				kTangent.normalize();

				// compute the binormal B, another tangent perpendicular to T
				Vector3f kBinormal = norm[i].cross(kTangent).normalize();

				// When generating bump/normal maps, folks usually work in a
				// left-handed screen space with the origin at the upper right,
				// u to the right, and v down, while we apply the textures with
				// the origin at the lower left, u right, v up, so we need to
				// flip
				// the binormal (v-axis) to get a proper transformation to the
				// surface local texture space.
				Matrix3f kRotTS = new Matrix3f(kTangent.x, kTangent.y,
						kTangent.z, -kBinormal.x, -kBinormal.y, -kBinormal.z,
						norm[i].x, norm[i].y, norm[i].z);

				// Compute the tangent space light vector. Conceptually
				//   kTSLight = kRotTS*(kMLight-Vertex[i])/|kMLight-Vertex[i]|
				Vector3f kTSLight = modelLightVector;

				// Subtract off the vertex position if we have a positional
				// light.
				if (light.getType() != Light.LT_DIRECTIONAL)
					kTSLight = kTSLight.subtract(vert[i]);

				kTSLight.normalize();
				kTSLight = kRotTS.mult(kTSLight);

				// Transform the light vector into [0,1]^3 to make it a valid
				// ColorRGB object.
				color.r = 0.5f * (kTSLight.x + 1.0f);
				color.g = 0.5f * (kTSLight.y + 1.0f);
				color.b = 0.5f * (kTSLight.z + 1.0f);
			}
		}
	}
	public void updateWorldBound() {
		worldBound = objects.getWorldBound();
	}

	public void draw(Renderer r) {
		objects.setForceCull(forceCullObjects);
		r.draw(this);
		objects.setForceCull(true);
	}
}
