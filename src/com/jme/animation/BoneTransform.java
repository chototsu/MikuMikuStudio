package com.jme.animation;

import java.io.IOException;
import java.io.Serializable;

import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * BoneTransform contains a Bone/Transform array pairing. This pairing defines
 * the bone that will be transformed (translate, rotate), and the transformations
 * for a given frame. The bone is updated during a call to the update method that
 * defines two keyframes and the interpolation value between them. 
 *
 */
public class BoneTransform implements Serializable, Savable {

    private static final long serialVersionUID = -6037680427670917355L;
    
    //temp variables to reduce garbage collection
    private static Quaternion tempQuat1 = new Quaternion();
    private static Quaternion tempQuat2 = new Quaternion();
    private static Vector3f tempVec1 = new Vector3f();
    private static Vector3f tempVec2 = new Vector3f();
    
    private Matrix4f[] transforms;
    private Bone bone;
    private String boneId;
    
    private int oldFrame;
    
    /**
     * Default constructor creates a new BoneTransform with no data
     * set.
     *
     */
    public BoneTransform() {
        
    }
    
    /**
     * Constructor defines the bone that will be transformed as well as how
     * many transform keyframes that exist. These keyframes are not set until
     * setTransform(int, Matrix4f) is called.
     * @param bone the bone to transform.
     * @param frames the number of keyframes for this animation.
     */
    public BoneTransform(Bone bone, int frames) {
        this.bone = bone;
        transforms = new Matrix4f[frames];
    }
    
    /**
     * Constructor defines the bone and the list of transforms to use. This
     * constructor builds a complete BoneTransform ready for use.
     * @param bone the bone to transform. 
     * @param transforms the transforms to use.
     */
    public BoneTransform(Bone bone, Matrix4f[] transforms) {
        this.bone = bone;
        this.transforms = transforms;
    }
    
    /**
     * setCurrentFrame will set the current frame from the bone. The frame
     * supplied will define how to transform the bone. It is the responsibility
     * of the caller to insure the frame supplied is valid.
     * @param frame the frame to set the bone's transform to.
     */
    public void setCurrentFrame(int frame) {
        if(oldFrame == frame) {
            return;
        }
        oldFrame = frame;
        if(bone != null) {
            bone.setLocalRotation(transforms[frame].toRotationQuat());
            bone.setLocalTranslation(transforms[frame]
                  .toTranslationVector());
        } 
    }

    /**
     * update sets the transform of the bone to a given interpolation between two
     * given frames. 
     * @param prevFrame the initial frame.
     * @param currentFrame the goal frame.
     * @param interpType the type of interpolation
     * @param result the time between frames
     */
    public void update(int prevFrame, int currentFrame, int interpType, float time) {
        if(bone == null) {
            return;
        }
        interpolate(transforms[prevFrame],
                transforms[currentFrame],
                interpType,
                time);
        bone.getLocalRotation().set(tempQuat1);
        bone.getLocalTranslation().set(tempVec1);
        bone.propogateBoneChange(true);
    }

    /**
     * setTransforms sets a transform for a given frame. It is the responsibility
     * of the caller to insure that the index is valid.
     * @param index the index of the transform to set.
     * @param transform the transform to set at the index.
     */
    public void setTransform(int index, Matrix4f transform) {
        transforms[index] = transform;
    }
    
    /**
     * sets the transforms array for the keyframes. This array should be the same
     * size as the times array and the types array. This is left to the
     * user to insure, if they are not the same, an ArrayIndexOutOfBounds
     * exception will be thrown during update.
     * @param transforms the transforms to set.
     */
    public void setTransforms(Matrix4f[] transforms) {
        this.transforms = transforms;
    }
    
    /**
     * defines the bone that the controller will be affecting.
     * @param b the bone that will be controlled.
     */
    public void setBone(Bone b) {
        bone = b;
    }
    
    /**
     * interpolates two quaternions and two vectors based on a time.
     */
    private Quaternion interpolate(Matrix4f start, Matrix4f end,
            int type, float time) {
        // if interpolation type is not support just return the start quaternion
        start.toRotationQuat(tempQuat1);
        start.toTranslationVector(tempVec1);
        if (type == BoneAnimation.LINEAR) {
            end.toRotationQuat(tempQuat2);
            tempQuat1.slerp(tempQuat2, time);
            
            end.toTranslationVector(tempVec2);
            tempVec1.multLocal(1 - time);
            tempVec2.multLocal(time);
            tempVec1.addLocal(tempVec2);
        }
        return tempQuat1;
    }

    /**
     * returns the bone that this BoneTransform is responsible for updating.
     * @return the bone this BoneTransform is responsible for updating.
     */
    public Bone getBone() {
        return bone;
    }

    /**
     * returns the transform array this BoneTransform is reponsible for applying
     * to the bone.
     * @return the transform array this BoneTransform is reponsible for applying
     * to the bone.
     */
    public Matrix4f[] getTransforms() {
        return transforms;
    }

    public String getBoneId() {
        return boneId;
    }

    public void setBoneId(String boneId) {
        this.boneId = boneId;
    }
    
    public boolean findBone(Bone b) {
        if(boneId == null) {
            return false;
        }
        
        if(boneId.equals(b.getName())) {
            bone = b;
            return true;
        } else {
            for(int i = 0; i < b.getQuantity(); i++) {
                if(b.getChild(i) instanceof Bone) {
                    if(this.findBone((Bone)b.getChild(i))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule cap = e.getCapsule(this);
        cap.write(boneId, "name", null);
        cap.write(transforms, "transforms", new Matrix4f[0]);
        cap.write(bone, "bone", null);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule cap = e.getCapsule(this);
        boneId = cap.readString("name", null);
        
        Savable[] savs = cap.readSavableArray("transforms", new Matrix4f[0]);
        if (savs == null)
            transforms = null;
        else {
            transforms = new Matrix4f[savs.length];
            for (int x = 0; x < savs.length; x++) {
                transforms[x] = (Matrix4f)savs[x];
            }
        }
        
        bone = (Bone)cap.readSavable("bone", null);
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}
