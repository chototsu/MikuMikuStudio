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

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

/**
 * <code>Joint</code>
 * @author Mark Powell
 * @version $Id: Joint.java,v 1.1 2004-01-26 01:30:12 mojomonkey Exp $
 */
public class Joint {
    private String name;
    private String parentName;
    private int parentIndex;
    
    private Vector3f localTranslation;
    private Vector3f localRotation;
    //the vertices and mesh to update.
    private TriMesh mesh;
    private int[] vertexIndex;

    private Keyframe[] positionFrames;
    private Keyframe[] rotationFrames;
    private int lastKeyframe, nextKeyframe, currentKeyframe;

    private Matrix3f relativeMatrix;
    private Matrix3f absoluteMatrix;
    private Matrix3f relativeFinalMatrix;
    private Matrix3f finalMatrix;
    
    /**
     * <code>getFinalMatrix</code>
     * @return
     */
    public Matrix3f getFinalMatrix() {
        return finalMatrix;
    }

    /**
     * <code>setFinalMatrix</code>
     * @param finalMatrix
     */
    public void setFinalMatrix(Matrix3f finalMatrix) {
        this.finalMatrix = finalMatrix;
    }

    /**
     * <code>getAbsoluteMatrix</code>
     * @return
     */
    public Matrix3f getAbsoluteMatrix() {
        return absoluteMatrix;
    }

    /**
     * <code>setAbsoluteMatrix</code>
     * @param absoluteMatrix
     */
    public void setAbsoluteMatrix(Matrix3f absoluteMatrix) {
        this.absoluteMatrix = absoluteMatrix;
    }

    /**
     * <code>getRelativeMatrix</code>
     * @return
     */
    public Matrix3f getRelativeMatrix() {
        return relativeMatrix;
    }

    /**
     * <code>setRelativeMatrix</code>
     * @param relativeMatrix
     */
    public void setRelativeMatrix(Matrix3f relativeMatrix) {
        this.relativeMatrix = relativeMatrix;
    }

    public Joint() {
        relativeMatrix = new Matrix3f();
        absoluteMatrix = new Matrix3f();
        relativeFinalMatrix = new Matrix3f();
        finalMatrix = new Matrix3f();    
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
    
    public String getParentName() {
        return parentName;
    }
    
    public void setParentIndex(int index) {
        this.parentIndex = index;
    }
    
    public int getParentIndex() {
        return parentIndex;
    }
    
    public void setLocalTranslation(Vector3f translation) {
        this.localTranslation = translation;
    }
    
    public Vector3f getLocalTranslation() {
        return localTranslation;
    }
    
    public void setLocalRotations(Vector3f rotation) {
        this.localRotation = rotation;
    }
    
    public Vector3f getLocalRotations() {
        return localRotation;
    }
    
    public void setPositionFrames(Keyframe[] positionFrames) {
        this.positionFrames = positionFrames;
    }
    
    public void setRotationFrames(Keyframe[] rotationFrames) {
        this.rotationFrames = rotationFrames;
    }

    public void update(float time) {
        //calculate current keyframe
        //update matrices
        //update all vertices related to this joint.
    }
    
    public void initializeJoint() {
        
    }
}
