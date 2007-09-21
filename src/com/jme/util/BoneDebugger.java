/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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
package com.jme.util;

import com.jme.animation.Bone;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;

/**
 *  BoneDebugger is responsible for visually representing a skeletal system (a
 *  heirarchy of Bone nodes). To visualize the skeleton, a Bone is represented
 *  as a red sphere and the connection between bones as a white cylinder. Additionally,
 *  hardpoints are presented as green boxes. 
 *  
 *  Standard usage requires the passing in of the model that contains a skeleton 
 *  to the drawBones method. This method will render the bones on screen as 
 *  required. 
 *  
 *  @see com.jme.animation.Bone
 *
 */
public final class BoneDebugger {

    private static Sphere boneSphere = new Sphere("boneSphere", new Vector3f(), 6, 6, .125f);
    private static Box hardpointBox = new Box("hardpoint", new Vector3f(), 0.125f, 0.125f, 0.125f);
    private static Cylinder boneCylinder = new Cylinder("boneCylinder", 3, 8, .03f, 1f);
    static {
        boneSphere.getBatch(0).setLightCombineMode(LightState.OFF);
        boneSphere.getBatch(0).setTextureCombineMode(TextureState.OFF);
        boneSphere.getBatch(0).setSolidColor(ColorRGBA.red.clone());
        boneSphere.getBatch(0).setRenderQueueMode(Renderer.QUEUE_SKIP);
        
        hardpointBox.getBatch(0).setLightCombineMode(LightState.OFF);
        hardpointBox.getBatch(0).setTextureCombineMode(TextureState.OFF);
        hardpointBox.getBatch(0).setSolidColor(ColorRGBA.green.clone());
        hardpointBox.getBatch(0).setRenderQueueMode(Renderer.QUEUE_SKIP);

        boneCylinder.getBatch(0).setLightCombineMode(LightState.OFF);
        boneCylinder.getBatch(0).setTextureCombineMode(TextureState.OFF);
        boneCylinder.getBatch(0).setSolidColor(ColorRGBA.white.clone());
        boneCylinder.getBatch(0).setRenderQueueMode(Renderer.QUEUE_SKIP);
    }

    private static boolean inited = false;
    private static Vector3f tempTrans = new Vector3f();
    private static Vector3f tempScale = new Vector3f();
    private static Quaternion tempRot = new Quaternion();
    private static Quaternion tempQ = new Quaternion();
    private static Vector3f tempA = new Vector3f();
    private static Vector3f tempB = new Vector3f();
    private static Vector3f tempC = new Vector3f();
    private static Vector3f tempD = new Vector3f();

    public static void drawBones(Spatial spat, Renderer r) {
        drawBones(spat, r, true);
    }

    public static void drawBones(Spatial spat, Renderer r, boolean drawChildren) {
        if (!inited) {
            TextureState noTextureState = r.createTextureState();
            noTextureState.setEnabled(false);
            boneSphere.getBatch(0).setRenderState(noTextureState);
            hardpointBox.getBatch(0).setRenderState(noTextureState);
            boneCylinder.getBatch(0).setRenderState(noTextureState);
            
            ZBufferState noBufferState = r.createZBufferState();
            noBufferState.setEnabled(true);
            noBufferState.setWritable(true);
            noBufferState.setFunction(ZBufferState.CF_ALWAYS);
            boneSphere.getBatch(0).setRenderState(noBufferState);
            hardpointBox.getBatch(0).setRenderState(noBufferState);
            boneCylinder.getBatch(0).setRenderState(noBufferState);

            boneSphere.updateRenderState();
            boneSphere.updateGeometricState(0, false);
            hardpointBox.updateRenderState();
            hardpointBox.updateGeometricState(0, false);
            boneCylinder.updateRenderState();
            boneCylinder.updateGeometricState(0, false);
            boneSphere.lockMeshes();
            hardpointBox.lockMeshes();
            boneCylinder.lockMeshes();
            inited = true;
        }
        
        if (spat instanceof Bone) {
            drawTheBones(null, (Bone)spat, r);
        }

        if ((spat instanceof Node) && drawChildren) {
            Node n = (Node) spat;
            for (int x = 0, count = n.getQuantity(); x < count; x++) {
                drawBones(n.getChild(x), r, true);
            }
        }
    }

    private static void drawTheBones(Spatial skin, Bone bone, Renderer r) {
        if(skin == null) {
            tempTrans.set(0,0,0);
            tempRot.set(0, 0, 0, 1);
            tempScale.set(1,1,1);
        } else {
            tempTrans.set(skin.getWorldTranslation());
            tempRot.set(skin.getWorldRotation());
            tempScale.set(skin.getWorldScale());
        }
        
        if(bone.isHardpoint()) {
            hardpointBox.getWorldTranslation().set(tempTrans).addLocal(tempRot.mult(bone.getWorldTranslation(), tempA));
            hardpointBox.getWorldRotation().set(tempRot).multLocal(bone.getWorldRotation());
            hardpointBox.getWorldScale().set(tempScale).multLocal(bone.getWorldScale());
            
            hardpointBox.getBatch(0).draw(r);
        } else {
            boneSphere.getWorldTranslation().set(tempTrans).addLocal(tempRot.mult(bone.getWorldTranslation(), tempA));
            boneSphere.getWorldRotation().set(tempRot).multLocal(bone.getWorldRotation());
            boneSphere.getWorldScale().set(tempScale).multLocal(bone.getWorldScale());
            
            boneSphere.getBatch(0).draw(r);
        }

        Vector3f here = tempA;
        Vector3f there = tempB;
        Vector3f diff = tempC;

        if (bone.getQuantity() > 0) {
            bone.localToWorld(Vector3f.ZERO, here);
            float hX, hY, hZ;
            hX = here.getX();
            hY = here.getY();
            hZ = here.getZ();
            for (int x = 0, count = bone.getQuantity(); x < count; x++) {
                Spatial child = bone.getChild(x);
                if (child instanceof Bone) {
                    child.localToWorld(Vector3f.ZERO, there);
                    diff.set(there).subtractLocal(here);
    
                    float distance = here.distance(there);
                    
                    boneCylinder.getWorldScale().set(1, 1, distance);
                    boneCylinder.getWorldTranslation().set(diff).multLocal(0.5f).addLocal(here);
                    tempD.set(boneCylinder.getWorldTranslation());
                    boneCylinder.getWorldTranslation().set(tempTrans).addLocal(tempRot.mult(tempD, tempD));
    
                    diff.normalizeLocal();
                    boneCylinder.getWorldRotation().set(bone.getWorldRotation()).lookAt(diff, Vector3f.UNIT_Z);
                    tempQ.set(boneCylinder.getWorldRotation());
                    boneCylinder.getWorldRotation().set(tempRot).multLocal(tempQ);
    
    
                    boneCylinder.getBatch(0).draw(r);
                    drawTheBones(skin, (Bone)child, r);
                    here.set(hX, hY, hZ);
                }
            }
        }
    }

}
