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
package com.jme.animation;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.TriMesh;
import com.jme.system.JmeException;

/**
 * <code>VertexKeyframeController</code> controls animation of a particular
 * model using a collection of keyframe models. Each model represents a
 * state of the displayed model at a certain (regular) time frame. Essentially,
 * the displayed model is morphed from one keyframe to another giving the
 * impression of movement.
 * @author Mark Powell
 * @version $Id: VertexKeyframeController.java,v 1.8 2004-05-12 00:14:45 renanse Exp $
 */
public class VertexKeyframeController extends Controller {
	private TriMesh[] keyframes;
	private TriMesh displayedMesh;
	private float currentTime;
	private int minFrame = 0;
	private int maxFrame;
	private int currentFrame;
	private int nextFrame = 1;
	private int cycleModifier = 1;

	/** <code>update</code> updates the displayed mesh by interpolating
	 * between two animation frames. These frames, denoted current frame
	 * and next frame, are combined to determine each in-between frame
	 * required to make smooth animation. All aspects of the tri mesh
	 * are interpolated if they are set in the keyframes. This means,
	 * you can morph colors, texture coordinates, normals and vertices.
	 * Most animation deals strictly with morphing of vertices, so all
	 * other aspects of the keyframe TriMeshes should be null.
	 * @param time the time between frames. This time is added to a counter
	 * 		to determine when the next keyframe should be loaded.
	 * @see com.jme.scene.Controller#update(float)
	 */
	public void update(float time) {
          time *= getSpeed();
		//determine repeat type. This defines how the current time
		//and keyframes are loaded.
		if (getRepeatType() == Controller.RT_CLAMP) {
			currentTime += time;
			while (currentTime >= 1) {
				currentFrame++;
				nextFrame++;
				if (currentFrame >= maxFrame) {
					currentFrame = maxFrame;
				}

				if (nextFrame >= maxFrame) {
					nextFrame = maxFrame;
				}

				currentTime--;
			}

		} else if (getRepeatType() == Controller.RT_WRAP) {
			currentTime += time;
			while (currentTime >= 1) {
				currentFrame++;
				nextFrame++;
				if (currentFrame >= maxFrame) {
					currentFrame = minFrame;
				}

				if (nextFrame >= maxFrame) {
					nextFrame = minFrame;
				}

				currentTime--;
			}
		} else if (getRepeatType() == Controller.RT_CYCLE) {
			currentTime += time;
			while (currentTime >= 1) {

				currentFrame += cycleModifier;
				nextFrame += cycleModifier;
				if (currentFrame >= maxFrame) {
					currentFrame = maxFrame;
					cycleModifier = -1;
				} else if (currentFrame <= minFrame) {
					currentFrame = minFrame;
					cycleModifier = 1;
				}

				if (nextFrame >= maxFrame) {
					nextFrame = maxFrame - 2;
					cycleModifier = -1;
				} else if (currentFrame <= minFrame) {
					nextFrame = minFrame + 1;
					cycleModifier = 1;
				}

				currentTime--;
			}
		}

                //Morph each aspect of the model for every vertex.
                boolean morphedVerts = false, morphedNorms = false, morphedTexts = false,
                    morphedColors = false;
                Vector3f[] verts = displayedMesh.getVertices();
                Vector3f[] norms = displayedMesh.getNormals();
                Vector2f[] texs = displayedMesh.getTextures();
                ColorRGBA[] colors = displayedMesh.getColors();
                Vector3f keyframeA, keyframeB;
                Vector2f keyframeTA, keyframeTB;
                ColorRGBA keyframeCA, keyframeCB;
                for (int i = 0; i < displayedMesh.getVertices().length; i++) {
                  //morph vertices
                  if (verts != null
                      && keyframes[currentFrame].getVertices() != null
                      && keyframes[nextFrame].getVertices() != null) {
                    keyframeA = keyframes[currentFrame].getVertices()[i];
                    keyframeB = keyframes[nextFrame].getVertices()[i];
                    verts[i].x =
                        keyframeA.x
                        + currentTime
                        * (keyframeB.x
                           - keyframeA.x);
                    verts[i].y =
                        keyframeA.y
                        + currentTime
                        * (keyframeB.y
                           - keyframeA.y);
                    verts[i].z =
                        keyframeA.z
                        + currentTime
                        * (keyframeB.z
                           - keyframeA.z);
                    morphedVerts = true;
                  }

                  //morph normals if appropriate
                  if (norms != null
                      && keyframes[currentFrame].getNormals() != null
                      && keyframes[nextFrame].getNormals() != null) {
                    keyframeA = keyframes[currentFrame].getNormals()[i];
                    keyframeB = keyframes[nextFrame].getNormals()[i];
                    norms[i].x =
                        keyframeA.x
                        + currentTime
                        * (keyframeB.x
                           - keyframeA.x);
                    norms[i].y =
                        keyframeA.y
                        + currentTime
                        * (keyframeB.y
                           - keyframeA.y);
                    norms[i].z =
                        keyframeA.z
                        + currentTime
                        * (keyframeB.z
                           - keyframeA.z);
                    morphedNorms = true;
                  }

                  //morph texture coordinates if appropriate.
                  if (texs != null
                      && keyframes[currentFrame].getTextures().length > 0
                      && keyframes[nextFrame].getTextures().length > 0) {
                    keyframeTA = keyframes[currentFrame].getTextures()[i];
                    keyframeTB = keyframes[nextFrame].getTextures()[i];
                    texs[i].x =
                        keyframeTA.x
                        + currentTime
                        * (keyframes[nextFrame].getTextures()[i].x
                           - keyframeTA.x);
                    texs[i].y =
                        keyframeTA.y
                        + currentTime
                        * (keyframeTB.y
                           - keyframeTA.y);
                    morphedTexts = true;
                  }

                  //morph colors if appropriate.
                  if (colors != null
                      && keyframes[currentFrame].getColors() != null
                      && keyframes[nextFrame].getColors() != null) {
                    keyframeCA = keyframes[currentFrame].getColors()[i];
                    keyframeCB = keyframes[nextFrame].getColors()[i];
                    colors[i].r =
                        keyframeCA.r
                        + currentTime
                        * (keyframeCB.r
                           - keyframeCA.r);
                    colors[i].g =
                        keyframeCA.g
                        + currentTime
                        * (keyframeCB.g
                           - keyframeCA.g);
                    colors[i].b =
                        keyframeCA.b
                        + currentTime
                        * (keyframeCB.b
                           - keyframeCA.b);
                    colors[i].a =
                        keyframeCA.a
                        + currentTime
                        * (keyframeCB.a
                           - keyframeCA.a);
                    morphedColors = true;
                  }
                }
                if (morphedVerts) displayedMesh.updateVertexBuffer();
                if (morphedNorms) displayedMesh.updateNormalBuffer();
                if (morphedTexts) displayedMesh.updateTextureBuffer();
                if (morphedColors) displayedMesh.updateColorBuffer();
              }

	/**
	 * <code>getDisplayedMesh</code> returns the mesh that is being morphed.
	 * This mesh is typically attached to a scene node.
	 * @return Returns the displayedMesh.
	 */
	public TriMesh getDisplayedMesh() {
		return displayedMesh;
	}

	/**
	 * <code>setDisplayedMesh</code> sets the mesh that is to be morphed.
	 * @param displayedMesh The displayedMesh to set.
	 */
	public void setDisplayedMesh(TriMesh displayedMesh) {
		this.displayedMesh = displayedMesh;
	}

	/**
	 * <code>getKeyframes</code> returns an array of triangle meshes that
	 * is used to define the keyframes of the animation.
	 * @return Returns the keyframes.
	 */
	public TriMesh[] getKeyframes() {
		return keyframes;

	}

	/**
	 * <code>setKeyframes</code> sets the array of triangle meshes that
	 * defines the keyframes of the animation.
	 * @param keyframes The keyframes to set.
	 */
	public void setKeyframes(TriMesh[] keyframes) {
		this.keyframes = keyframes;
		maxFrame = keyframes.length - 1;

	}

	/**
	 * <code>getMaxFrame</code> returns the current maximum frame used
	 * for the animation. This frame may be smaller than the total number
	 * of keyframes. This allows the keyframes to define a number of
	 * different animations, and setting the minimum and maximum frame
	 * allows the user to switch between animations.
	 * @return Returns the maxFrame.
	 */
	public int getMaxFrame() {
		return maxFrame;
	}

	/**
	 * <code>setMaxFrame</code> sets the maximum keyframe to display.
	 * This allows the user to define a subgroup of the total keyframes
	 * to display. Effectively allowing the keyframes to define multiple
	 * animations and the user to switch between different animations.
	 * @param maxFrame The maxFrame to set.
	 */
	public void setMaxFrame(int maxFrame) {
		if(maxFrame >= keyframes.length) {
			throw new JmeException("Max Frame out of bounds. " + maxFrame);
		}
		this.maxFrame = maxFrame;
		currentTime = 0;
	}

	/**
	 * <code>getMinFrame</code> returns the current minimum frame used
	 * for the animation. This frame may be larger than the initial
	 * keyframe. This allows the keyframes to define a number of
	 * different animations, and setting the minimum and maximum frame
	 * allows the user to switch between animations.
	 * @return Returns the minFrame.
	 */
	public int getMinFrame() {
		return minFrame;
	}

	/**
	 * <code>setMinFrame</code> sets the minimum keyframe to display.
	 * This allows the user to define a subgroup of the total keyframes
	 * to display. Effectively allowing the keyframes to define multiple
	 * animations and the user to switch between different animations.
	 * @param minFrame The minFrame to set.
	 */
	public void setMinFrame(int minFrame) {
		if(minFrame < 0) {
			throw new JmeException("Min Frame out of bounds. " + minFrame);
		}
		this.minFrame = minFrame;
		currentTime = 0;
		currentFrame = minFrame;
		nextFrame = minFrame + 1;
	}

}
