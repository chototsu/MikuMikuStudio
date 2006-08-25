package com.jme.animation;

import java.io.IOException;
import java.io.Serializable;

import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;


/**
 * The Influence class defines a pairing between a vertex and a bone. This
 * pairing is given a weight to define how much the bone affects the vertex.
 */
public class BoneInfluence implements Serializable, Savable {

    private static final long serialVersionUID = 5904348001742899839L;
    
    public float weight;
    public Bone bone;
    public String boneId;
    public Vector3f vOffset;
    public Vector3f nOffset;

    public BoneInfluence() {
    }

    public BoneInfluence(Bone boneIndex, float weight) {
        this.bone = boneIndex;
        this.weight = weight;
    }
    
    public void assignBone(Bone b) {
        if(boneId == null || b == null) {
            return;
        }
        
        if(boneId.equals(b.getName())) {
            bone = b;
        } else {
            for(int i = 0; i < b.getQuantity(); i++) {
                if(b.getChild(i) instanceof Bone) {
                    this.assignBone((Bone)b.getChild(i));
                }
            }
        }
    }
    
    @Override
    public boolean equals(Object arg0) {
    		if (!(arg0 instanceof BoneInfluence)) return false;
    		
    		BoneInfluence other = (BoneInfluence)arg0;
    		
    		if (boneId != null) {
    			if (!boneId.equals(other.boneId)) return false;
    		}
    		
    		return true;
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule cap = e.getCapsule(this);
        cap.write(weight, "weight", 0);
        cap.write(bone, "bone", null);
        cap.write(boneId, "boneId", null);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule cap = e.getCapsule(this);
        weight = cap.readFloat("weight", 0);
        bone = (Bone)cap.readSavable("bone", null);
        boneId = cap.readString("boneId", null);
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}
