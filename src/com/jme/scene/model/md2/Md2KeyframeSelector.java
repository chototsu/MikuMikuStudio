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
package com.jme.scene.model.md2;

import com.jme.animation.VertexKeyframeController;
import com.jme.scene.TriMesh;
import com.jme.system.JmeException;

/**
 * <code>Md2KeyframeSelector</code> provides a mechanism for selecting
 * animations from a <code>VertexKeyframeController</code> as used by the
 * MD2 model type. All the animations are viewed, using the start and end
 * index of the keyframes to determine when an animation should begin and
 * end. Calling the <code>setAnimation</code> method, this class sets the
 * start and end frames of the controller.
 * @author Mark Powell
 * @version $Id: Md2KeyframeSelector.java,v 1.3 2004-04-25 21:36:09 mojomonkey Exp $
 */
public class Md2KeyframeSelector {
	private VertexKeyframeController controller;
	private Md2Animations[] animations;

	/**
	 * Constructor instantiates a new <code>Md2KeyframeSelector</code>
	 * object. The controller that will be used for animation selection
	 * is passed during construction. This controller should be the
	 * same that is added to the MD2 model.
	 * @param controller the animation controller used for keyframe selection.
	 */
	public Md2KeyframeSelector(VertexKeyframeController controller) {
		this.controller = controller;
		initializeAnimations();
	}

	/**
	 *
	 * <code>setAnimation</code> sets the desired animation based on the
	 * index of the animation list.
	 * @param animation the index into the animation list.
	 */
	public void setAnimation(int animation) {
		if(animation < 0 || animation >= animations.length) {
			throw new JmeException("Animation index out of bounds. " + animation);
		}
		controller.setMaxFrame(animations[animation].endFrame);
		controller.setMinFrame(animations[animation].startFrame);
	}

	/**
	 *
	 * <code>setAnimation</code> sets the desired animation based on the
	 * name of the animation. If the name of the animation does not
	 * exist there is no change in the animation.
	 * @param animation the name of the animation to switch to.
	 */
	public void setAnimation(String animation) {
		for (int i = 0; i < animations.length; i++) {
			if (animation.equalsIgnoreCase(animations[i].name)) {
				controller.setMaxFrame(animations[i].endFrame);
				controller.setMinFrame(animations[i].startFrame);
				return;
			}
		}
	}

	/**
	 *
	 * <code>isValidAnimation</code> returns true or false if an
	 * animation name exists in the animation list. If the name is
	 * valid, true is returned, otherwise false is returned.
	 * @param animation the animation name to test.
	 * @return true if the animation name exists, false otherwise.
	 */
	public boolean isValidAnimation(String animation) {
		for (int i = 0; i < animations.length; i++) {
			if (animation.equalsIgnoreCase(animations[i].name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * <code>getAnimationList</code> returns a list of all the animation
	 * names for a given controller.
	 * @return the animation names list.
	 */
	public String[] getAnimationList() {
		String[] names = new String[animations.length];

		for (int i = 0; i < names.length; i++) {
			names[i] = animations[i].name;
		}

		return names;
	}

	/**
	 *
	 * <code>getNumberOfAnimations</code> returns the number of animations
	 * in the controller.
	 * @return the number of animations that exist.
	 */
	public int getNumberOfAnimations() {
		return animations.length;
	}

	/**
	 *
	 * <code>initializeAnimations</code> builds a list of animations
	 * that are unique. This is based on animation names. The last two
	 * characters are removed from the name (as this typically corresponds
	 * to the sequence number).
	 *
	 */
	private void initializeAnimations() {
		TriMesh[] frames = controller.getKeyframes();
		Md2Animations[] temp = new Md2Animations[frames.length];
		int animationCounter = 0;
		for (int i = 0; i < frames.length; i++) {
			String name = frames[i].getName();
			name = name.substring(0, name.length() - 2);
			temp[i] = new Md2Animations();
			temp[i].name = name;
			temp[i].startFrame = i;
			temp[i].endFrame = i;

			if (i == 0) {
				animationCounter++;
			} else if (!temp[i].name.equals(temp[i - 1].name)) {
				animationCounter++;
			}
		}

		animations = new Md2Animations[animationCounter];
		int count = 0;
		for (int i = 0; i < temp.length; i++) {
			if (i == 0) {
				animations[count] = new Md2Animations();
				animations[count].name = temp[i].name;
				animations[count].startFrame = i;
				count++;

			} else if (!temp[i].name.equals(temp[i - 1].name)) {
				animations[count] = new Md2Animations();
				animations[count - 1].endFrame = i - 1;
				animations[count].name = temp[i].name;
				animations[count].startFrame = i;
				count++;

			}
		}

		animations[animations.length - 1].endFrame = temp.length - 1;
	}

	/**
	 *
	 * <code>Md2Animations</code> holds information about a single
	 * animation.
	 */
	private class Md2Animations {
		String name;
		int startFrame;
		int endFrame;
	}
}
