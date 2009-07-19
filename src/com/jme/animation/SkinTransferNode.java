/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.animation;

import java.util.ArrayList;

import com.jme.math.Matrix4f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.geom.VertMap;

/**
 * A non-parented SkinNode which is enforced to be used only for holding
 * SkinNode Geometry skins.
 *
 * Specifically, this SkinNode will throw if you attempt to parent it, or if
 * you attempt to set skeleton, controller, animation, or other
 * non-skin-geometry properties.
 *
 * This class is intended to be used for classes dedicated for usage as a
 * SkinNode.assimilate() targets.
 *
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 * @see SkinNode.assimilate(SkinNode)
 */
public class SkinTransferNode extends SkinNode {
    private static final long serialVersionUID = 1L;
    /**
     * Empty Constructor to be used internally only.
     */
    public SkinTransferNode() {
    }

    public SkinTransferNode(String name) {
        super(name);
    }

    /**
     * Overrides Spatial.setParent().
     *
     * @see Spatial.setParent(Node)
     */
    protected void setParent(Node parent) {
        throw new IllegalStateException(
            SkinTransferNode.class.getName() + "s may not be parented");
    }

    /**
     * addBoneInfluence defines how a vertex will be affected by a bone. This is
     * given with four values, the geometry child the vertex is found, the index
     * to the vertex in the geometry, the index of the bone that has been or
     * will be set via setBones or addBone and the weight that this indexed bone
     * affects the vertex.
     * 
     * @param geomIndex
     *            the geometry child that contains the vertex to be affected.
     * @param vert
     *            the index to the vertex.
     * @param bone
     *            the bone that affects the vertex.
     * @param weight
     *            the weight that the bone will affect the vertex.
     */
    public void addBoneInfluence(int geomIndex, int vert, Bone bone,
            float weight) {
        throw new IllegalStateException(
                "Only the 'boneId' variant of 'addBoneInfluence' may be used "
                + "for " + SkinTransferNode.class.getName());
    }
    
    public void setAnimation(BoneAnimation anim) {
        throw new IllegalStateException(
            "Animation may not be set for " + SkinTransferNode.class.getName());
    }
    
    public void setAnimation(int index) {
        throw new IllegalStateException(
            "Animation may not be set for " + SkinTransferNode.class.getName());
    }
    
    public void setAnimation(String name) {
        throw new IllegalStateException(
            "Animation may not be set for " + SkinTransferNode.class.getName());
    }
    
    public String getAnimationString() {
    	return null;
    	
   	}
    
    public void setSkeleton(Bone b) {
        throw new IllegalStateException(
            "Skeleton may not be set for " + SkinTransferNode.class.getName());
    }

    public Bone getSkeleton() {
        return null;
    }

    public void assignSkeletonBoneInfluences() {
        throw new IllegalStateException(
            SkinTransferNode.class.getName() + "s do not have Skeletons");
    }
    
    public void regenInfluenceOffsets() {
        throw new IllegalStateException(
            SkinTransferNode.class.getName() + "s do not have Skeletons");
    }

    public synchronized void updateSkin() {
        throw new IllegalStateException(
            SkinTransferNode.class.getName() + "s do not have Skeletons");
    }   

    public void setBindMatrix(Matrix4f mat) {
        // TODO:  I think we should prohibit bind matrixes
        bindMatrix = mat;
    }
    
    public void remapInfluences(VertMap mappings, int geomIndex) {
        throw new IllegalStateException(
            SkinTransferNode.class.getName() + "s do not have Skeletons");
    }

    protected void assimilate(
            Geometry newSkinGeo, ArrayList<BoneInfluence>[] newInfluences) {
        throw new IllegalStateException(
                "Can only assimilate FROM a " + SkinTransferNode.class.getName());
    }
}
