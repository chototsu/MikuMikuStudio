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

package com.jme.animation;

import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;

/**
 * <code>DeformationJoint</code> defines a joint that contains a 
 * collection of position keyframes and rotation keyframes. A joint 
 * controller will then maintain a collection of these joints, causing
 * the joint to affect a vertex based on the current keyframe.
 * @author Mark Powell
 * @version $Id: DeformationJoint.java,v 1.1 2004-02-01 07:51:06 mojomonkey Exp $
 */
public class DeformationJoint {

	/**
	 * The name of the joint.
	 */
	public String name;

	/**
	 * The name of the parent joint.
	 */
	public String parentName;

	/**
	 * The local translation of the joint in 3D space.
	 */
	public Vector3f pos = new Vector3f();

	/**
	 * The local rotation of the joint in 3D space.
	 */
	public Vector3f rot = new Vector3f();

	/**
	 * The number of position keyframes for the joint.
	 */
	public int numberPosistionKeyframes;

	/**
	 * The number of position keyframes for the joint.
	 */
	public int numberRotationKeyframes;

	/**
	 * The tranlation keyframes of the animation.
	 */
	public Keyframe[] positionKeys;

	/**
	 * The rotation keyframes of the animation.
	 */
	public Keyframe[] rotationKeys;

	/**
	 * The transformation of a joint from its parent.
	 */
	public Matrix4f relativeMatrix = new Matrix4f();

	/**
	 * The original transformation of the joint.
	 */
	public Matrix4f absoluteMatrix = new Matrix4f();

	/**
	 * The helper Matrix4f for calculating the final Matrix4f.
	 */
	public Matrix4f relativeFinalMatrix = new Matrix4f();

	/**
	 * The final result of all transformations in the skeleton.
	 */
	public Matrix4f finalMatrix = new Matrix4f();

	/**
	 * The parent joint index.
	 */
	public int parentIndex;
}