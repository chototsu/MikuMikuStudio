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

package jme.geometry.model;

import jme.math.Matrix;



/**
 * A Joint stores everything about an animated bone (a joint that knows
 * about its parent joint - the two endpoints make up a bone).
 *
 * @author naj
 * @version 0.1
 */
public class Joint {

    /**
     * The name of the joint in MS3D.
     */
    public String name;

    /**
     * The name of the parent joint in MS3D.
     */
    public String parentName;

    /**
     * The flags in MS3D.
     */
    public int flags;

    /**
     * The local translation of the joint in 3D space.
     */
    public float posx, posy, posz;

    /**
     * The local rotation of the joint in 3D space.
     */
    public float rotx, roty, rotz;

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
    public Matrix relativeMatrix = new Matrix();

    /**
     * The original transformation of the joint.
     */
    public Matrix absoluteMatrix = new Matrix();

    /**
     * The helper matrix for calculating the final matrix.
     */
    public Matrix relativeFinalMatrix = new Matrix();

    /**
     * The final result of all transformations in the skeleton.
     */
    public Matrix finalMatrix = new Matrix();

    /**
     * The parent joint index.
     */
    public int parentIndex;

}