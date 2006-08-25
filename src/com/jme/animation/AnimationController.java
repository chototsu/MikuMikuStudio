package com.jme.animation;

import java.io.IOException;
import java.util.ArrayList;

import com.jme.scene.Controller;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

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

    public void setActiveAnimation(String name) {
        if (animationSets != null) {
            for (int i = 0; i < animationSets.size(); i++) {
                if (animationSets.get(i).getName().equals(name)) {
                    activeAnimation = animationSets.get(i);
                }
            }
        }
    }

    public void setActiveAnimation(BoneAnimation bac) {
        if (animationSets != null) {
            for (int i = 0; i < animationSets.size(); i++) {
                if (animationSets.get(i) == bac) {
                    activeAnimation = animationSets.get(i);
                }
            }
        }
    }

    public void setActiveAnimation(int index) {
        if (animationSets != null) {
            for (int i = 0; i < animationSets.size(); i++) {
                if (i == index) {
                    activeAnimation = animationSets.get(i);
                }
            }
        }
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
        if (isActive()) {
             if(activeAnimation != null) {
                 activeAnimation.update(time, getRepeatType(), getSpeed());
             }
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
