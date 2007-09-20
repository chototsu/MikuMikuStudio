/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

import java.io.IOException;
import java.util.ArrayList;

import com.jme.scene.Controller;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * A controller for modifying a BoneAnimation over time 
 */
public class AnimationController extends Controller implements Savable {
    private static final long serialVersionUID = 1L;

    private ArrayList<BoneAnimation> animationSets;
    private Bone skeleton;
    private BoneAnimation activeAnimation;
    
    public AnimationController() { }

    public void addAnimation(BoneAnimation bac) {
        if (bac == null) {
            return;
        }

        if (animationSets == null) {
            animationSets = new ArrayList<BoneAnimation>();
        }
        animationSets.add(bac);

        if (skeleton != null) {
            bac.assignSkeleton(skeleton);
        }
    }
    
    public void removeAnimation(BoneAnimation bac) {
        if (animationSets != null) {
            animationSets.remove(bac);
            
            if(animationSets.size() == 0) {
                activeAnimation = null;
            } else if(bac == activeAnimation) {
                activeAnimation = animationSets.get(0);
            }
        }
        
    }
    
    public void removeAnimation(int index) {
        if(index < 0 || index >= animationSets.size()) {
            return;
        }
        
        if (animationSets != null) {
            BoneAnimation bac = animationSets.get(index);
            animationSets.remove(index);
            if(animationSets.size() == 0) {
                activeAnimation = null;
            } else if(bac == activeAnimation) {
                activeAnimation = animationSets.get(0);
            }
        }
        
    }
    
    public BoneAnimation getActiveAnimation() {
        return activeAnimation;
    }

    public void setCurrentFrame(int frame) {
        if (activeAnimation != null) {
            activeAnimation.setCurrentFrame(frame);
        }
    }

    public void clearAnimations() {
        if (animationSets != null) {
            animationSets.clear();
        }
    }

    public BoneAnimation getAnimation(int i) {
        if (animationSets != null) {
            return animationSets.get(i);
        } else {
            return null;
        }
    }

    public ArrayList<BoneAnimation> getAnimations() {
        return animationSets;
    }
    
    public void clearActiveAnimation() {
    	activeAnimation = null;
    }

    public void setActiveAnimation(String name) {
        if (animationSets != null) {
            for (int i = 0; i < animationSets.size(); i++) {
                if (animationSets.get(i).getName().equals(name)) {
                    activeAnimation = animationSets.get(i);
                    return;
                }
            }
        }
        //Invalid animation, set active to null
        clearActiveAnimation();
    }

    public void setActiveAnimation(BoneAnimation bac) {
        if (animationSets != null) {
            for (int i = 0; i < animationSets.size(); i++) {
                if (animationSets.get(i) == bac) {
                    activeAnimation = animationSets.get(i);
                    return;
                }
            }
        }
        //Invalid animation, set active to null
        clearActiveAnimation();
    }

    public void setActiveAnimation(int index) {
        if (animationSets != null && index < animationSets.size()) {
            activeAnimation = animationSets.get(index);
            return;
        }
        //Invalid animation, set active to null
        clearActiveAnimation();
    }

    public void setSkeleton(Bone b) {
        this.skeleton = b;
        if(animationSets != null) {
            for (int i = 0; i < animationSets.size(); i++) {
                animationSets.get(i).assignSkeleton(skeleton);
            }
        }
    }

    @Override
    public void update(float time) {
         if(activeAnimation != null) {
             activeAnimation.update(time, getRepeatType(), getSpeed());
         }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule cap = e.getCapsule(this);
        cap.writeSavableArrayList(animationSets, "animationSets", null);
        cap.write(skeleton, "skeleton", null);
        cap.write(activeAnimation, "activeAnimation", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule cap = e.getCapsule(this);
        animationSets = cap.readSavableArrayList("animationSets", null);
        skeleton = (Bone)cap.readSavable("skeleton", null);
        activeAnimation = (BoneAnimation)cap.readSavable("activeAnimation", null);
    }
}
