package com.jme.scene.model.ms3d;

import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.math.TransformMatrix;
import com.jme.scene.Controller;
import com.jme.bounding.BoundingBox;

/**
 * Class to support animations returned from a MilkLoader.
 * It is important to note that <code>setMinTime</code> and
 * <code>setMaxTime</code> actually set the begining and ending
 * keyframe not it's given time.
 *
 * @author Jack Lindamood
**/
public class MilkAnimation extends Controller{

    private Quaternion unSyncbeginAngle=new Quaternion();
    private Vector3f unSyncbeginPos=new Vector3f();
    private MilkFile movementFile;
    private TransformMatrix[] jointMovements;
    private TransformMatrix tempUnSyncd=new TransformMatrix();
    private int currentFrame;
    private float changeAmnt;
    float timeBetweenFrames;
    private float skipRate;
    private float totalSkip;

    MilkAnimation(MilkFile movementFile, long startTime, long endTime, float duration) {
        this.movementFile=movementFile;
        this.timeBetweenFrames=duration;
        jointMovements=new TransformMatrix[movementFile.nNumJoints];

        for (int i=0;i<jointMovements.length;i++)
            jointMovements[i]=new TransformMatrix();
        this.setMaxTime(endTime);
        this.setMinTime(startTime);
        this.setSpeed(1);
        this.setRepeatType(Controller.RT_CYCLE);
        skipRate=totalSkip=0;
    }

    /**
     * Sets the minimum <I>frame</I> the animation should start at.
     * If time is less than 0 or greater than maximum frame, call is ignored
     * @param frame New minimum frame
     */
    public void setMinTime(float frame){
        if (frame < 0 || frame > this.getMaxTime() || this.getMinTime()==frame) return;
        super.setMinTime(frame);
        currentFrame= (int) frame;
    }
    /**
     * Sets the maximum <I>frame</I> the animation should start at.
     * If time is more than total animation or greater than minimum frame, call is ignored
     * @param frame New maximum frame
     */
    public void setMaxTime(float frame){
        if (frame <= this.getMinTime() || frame>movementFile.iTotalFrames || this.getMaxTime()==frame) return;
        super.setMaxTime(frame);
        currentFrame = (int) this.getMinTime();
   }

    /**
     * To increase Frames per Second you can specify the amount of time
     * that should elapse between one update and another for this animation
     * @param skipRate The new skipRate.  The parameter is ignored if it is
     * larger than the time between frames specified in the ms3d, or less than 0.
     */
    public void setSkipRate(float skipRate){
        if (skipRate<0 || skipRate > this.timeBetweenFrames) return;
        this.skipRate=skipRate;
        this.totalSkip=0;
    }

    /**
     * Returns skipRate
     * @return
     */
    public float getSkipRate(){
        return this.skipRate;
    }

    public void update(float time) {
        if (!this.isActive()) return;
        changeAmnt+=time*this.getSpeed();
        if (skipRate!=0f){
            totalSkip+=time;
            if (totalSkip >= skipRate)
                totalSkip=0;
            else
                return;
        }

        if (changeAmnt>=this.timeBetweenFrames){
            changeAmnt=0;
            currentFrame++;
            if (currentFrame >= this.getMaxTime()){
                if (this.getRepeatType()!=Controller.RT_CLAMP)
                    currentFrame= (int) this.getMinTime();
                else
                    this.setActive(false);
            }
        }
        createJointTransforms(changeAmnt);
        combineWithInverse();
        updateData();
    }
    BoundingBox findBiggestFit(int currentGroup){
        Vector3f[] extents=new Vector3f[6];
        for (int i=0;i<extents.length;i++)
            extents[i]=new Vector3f();

        for (currentFrame=(int) this.getMinTime();
             currentFrame<this.getMaxTime();currentFrame++){
                for (float changeAmnt=0;changeAmnt<=1;changeAmnt+=.25){
                    createJointTransforms(changeAmnt);
                    combineWithInverse();
                    updateBox(extents,currentGroup);
                }
        }
        BoundingBox toReturn=new BoundingBox("Computed Largest Box");
        toReturn.computeFromPoints(extents);
        currentFrame=(int) this.getMinTime();
        return toReturn;
    }

    private void updateBox(Vector3f[] extents,int currentGroup) {
         MilkshapeGroup updatingGroup=movementFile.myGroups[currentGroup];
         int currentBoneIndex;
         for (int j=0;j<updatingGroup.numTriangles;j++){
             for (int s=0;s<3;s++){  // Each tri has 3 co-ords
                 int coordIndex=movementFile.myTri[
                             updatingGroup.triangleIndices[j]].
                         vertexIndices[s];
                 currentBoneIndex=movementFile.boneID[coordIndex];
                 if (currentBoneIndex==-1) continue;
                 unSyncbeginPos.set(movementFile.vertexes[coordIndex]);
                 jointMovements[currentBoneIndex].multPoint(unSyncbeginPos);
                 if (unSyncbeginPos.x < extents[0].x)
                     extents[0].set(unSyncbeginPos);
                 if (unSyncbeginPos.x > extents[1].x)
                     extents[1].set(unSyncbeginPos);
                 if (unSyncbeginPos.y < extents[2].y)
                     extents[2].set(unSyncbeginPos);
                 if (unSyncbeginPos.y > extents[3].y)
                     extents[3].set(unSyncbeginPos);
                 if (unSyncbeginPos.z < extents[4].z)
                     extents[4].set(unSyncbeginPos);
                 if (unSyncbeginPos.z > extents[5].z)
                     extents[5].set(unSyncbeginPos);
             }
         }
    }

    private void combineWithInverse() {
        for (int i=0;i<jointMovements.length;i++)
            jointMovements[i].multLocal(movementFile.myJoints[i].inverseChainMatrix);
    }
    private void createJointTransforms(float changeAmnt) {
        for (int index=0;index<jointMovements.length;index++){
            int theParentIndex=movementFile.myJoints[index].parentIndex;

            unSyncbeginAngle.set(movementFile.myJoints[index].keyframeRot[currentFrame]);
            unSyncbeginPos.set(movementFile.myJoints[index].keyframePos[currentFrame]);

            unSyncbeginAngle.slerp(movementFile.myJoints[index].keyframeRot[currentFrame+1],changeAmnt);
            unSyncbeginPos.interpolate(movementFile.myJoints[index].keyframePos[currentFrame+1],changeAmnt);
            tempUnSyncd.set(unSyncbeginAngle,unSyncbeginPos);
            jointMovements[index].set(movementFile.myJoints[index].localRefMatrix);
            jointMovements[index].multLocal(tempUnSyncd);
            if (theParentIndex!=-1){
                tempUnSyncd.set(jointMovements[index]);
                jointMovements[index].set(jointMovements[theParentIndex]);
                jointMovements[index].multLocal(tempUnSyncd);
            }
        }
    }

    private void updateData(){
        for (int currentGroup=0;currentGroup<movementFile.nNumGroups;currentGroup++){
            MilkshapeGroup updatingGroup=movementFile.myGroups[currentGroup];
            int currentBoneIndex;
            for (int j=0;j<updatingGroup.numTriangles;j++){
                for (int s=0;s<3;s++){  // Each tri has 3 co-ords
                    int coordIndex=movementFile.myTri[
                                updatingGroup.triangleIndices[j]].
                            vertexIndices[s];
                    currentBoneIndex=movementFile.boneID[coordIndex];
                    if (currentBoneIndex==-1) continue;
                    unSyncbeginPos.set(movementFile.vertexes[coordIndex]);
                    updatingGroup.setVertex(j*3+s,
                            jointMovements[currentBoneIndex].multPoint(unSyncbeginPos));

                    unSyncbeginPos.set(movementFile.myTri[updatingGroup.triangleIndices[j]].vertexNormals[s]);
                    updatingGroup.setNormal(j*3+s,
                            jointMovements[currentBoneIndex].multNormal(unSyncbeginPos));
                }
            }
        }
    }
}