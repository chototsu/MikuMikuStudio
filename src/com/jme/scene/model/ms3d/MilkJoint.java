package com.jme.scene.model.ms3d;

import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.math.TransformMatrix;


/**
 * Helper class for MilkLoader to hold and process Joint
 * state information before it is passed to an <code>MilkAnimation</code>
 *
 * @author Jack Lindamood 
 */
class MilkJoint{
    String name;
    int parentIndex;
    Quaternion[] keyframeRot;
    Vector3f[] keyframePos;
    TransformMatrix inverseChainMatrix;
    TransformMatrix localRefMatrix=new TransformMatrix();


    public String toString() {
        return "MilkJoint{" +
                ", name='" + name + '\'' +
                ", parentIndex=" + parentIndex +
                '}';
    }
    public void processMe(){
        // Interpolates missing rotations and positions

        interpolateQuats();
        interpolateTranslations();
    }

    private void interpolateTranslations() {
        // 1) Find first OK Translation
        int startIndex;
        for (startIndex=0;startIndex<keyframePos.length;startIndex++)
            if (keyframePos[startIndex]!=null) break;
        if (startIndex==keyframePos.length){    // if all null
            keyframePos[0]=new Vector3f();
            keyframePos[startIndex-1]=new Vector3f();
            fillPos(0,startIndex-1);
            return;
        } else
        if (startIndex!=0){
            keyframePos[0]=new Vector3f(keyframePos[startIndex]);
            fillPos(0,startIndex);
        }
        // 2 ) Find last OK Translation
        int endIndex;
        for (endIndex=keyframePos.length-1;endIndex>=0;endIndex--)
            if (keyframePos[endIndex]!=null) break;
        if (endIndex!=keyframePos.length-1){
            keyframePos[keyframePos.length-1]=new Vector3f(keyframePos[endIndex]);
            fillPos(endIndex,keyframePos.length-1);
        }
        // 3) Now that tails are taken care of, fill gaps
        for (int i=startIndex+1;i<=endIndex;i++){
            if (keyframePos[i]!=null){
                fillPos(startIndex,i);
                startIndex=i;
            }
        }

    }

    private void interpolateQuats() {
        // 1) Find first OK Rotation
        int startIndex=0;
        for (startIndex=0;startIndex<keyframeRot.length;startIndex++)
            if (keyframeRot[startIndex]!=null) break;
        if (startIndex==keyframeRot.length){    // if all null
            keyframeRot[0]=new Quaternion(keyframeRot[startIndex]);
            fillQuats(0,startIndex);
            return;
        } else
        if (startIndex!=0){
            keyframeRot[0]=new Quaternion(keyframeRot[startIndex]);
            fillQuats(0,startIndex);
        }
        // 2 ) Find last OK Rotation
        int endIndex;
        for (endIndex=keyframeRot.length-1;endIndex>=0;endIndex--)
            if (keyframeRot[endIndex]!=null) break;
        if (endIndex!=keyframeRot.length-1){
            keyframeRot[keyframeRot.length-1]=new Quaternion(keyframeRot[endIndex]);
            fillQuats(endIndex,keyframeRot.length-1);
        }
        // 3) Now that tails are taken care of, fill gaps
        for (int i=startIndex+1;i<=endIndex;i++){
            if (keyframeRot[i]!=null){
                fillQuats(startIndex,i);
                startIndex=i;
            }
        }
    }

    private void fillQuats(int startIndex, int endIndex) {
        for (int i=startIndex+1;i<endIndex;i++){
            keyframeRot[i]=new Quaternion(keyframeRot[startIndex]);
            keyframeRot[i].slerp(keyframeRot[endIndex],((float)i-startIndex)/(endIndex-startIndex));
        }
    }
    private void fillPos(int startIndex, int endIndex) {
        for (int i=startIndex+1;i<endIndex;i++){
            keyframePos[i]=new Vector3f(keyframePos[startIndex]);
            keyframePos[i].interpolate(keyframePos[endIndex],((float)i-startIndex)/(endIndex-startIndex));
        }
    }
}