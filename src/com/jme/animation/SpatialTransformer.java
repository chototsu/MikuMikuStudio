package com.jme.animation;

import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.math.TransformMatrix;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Started Date: Jul 9, 2004<br><br>
 *
 *
 *
 * @author Jack Lindamood
 */
public class SpatialTransformer extends Controller{
    private int numObjects;
    public Spatial[] toChange;
    private TransformMatrix[] pivots;
    public int[] parentIndexes;

    public ArrayList keyframes;
    Vector3f unSyncbeginPos=new Vector3f();
    Vector3f unSyncendPos=new Vector3f();
    private Quaternion unSyncbeginRot=new Quaternion();
    private Quaternion unSyncendRot=new Quaternion();
    private float curTime;
    private PointInTime beginPointTime;
    private PointInTime endPointTime;

    public SpatialTransformer(int numObjects){
        this.numObjects=numObjects;
        toChange=new Spatial[numObjects];
        pivots=new TransformMatrix[numObjects];
        parentIndexes=new int[numObjects];
        for (int i=0;i<numObjects;i++){
            parentIndexes[i]=-1;
            pivots[i]=new TransformMatrix();
        }
        keyframes=new ArrayList();

    }


    public void update(float time) {
        if (!isActive()) return;
        curTime+=time*getSpeed();
        setBeginAndEnd();

        boolean[] haveChanged=new boolean[numObjects];
        for (int i=0;i<numObjects;i++){
            if (toChange[i] instanceof Spatial){
                updatePivot(i,haveChanged);
            }
        }
    }

    private void updatePivot(int objIndex, boolean[] haveChanged) {
        Spatial thisSpatial=(Spatial) toChange[objIndex];
        if (haveChanged[objIndex]){
            return;
        }
        pivots[objIndex].loadIdentity();
        if (parentIndexes[objIndex]!=-1){
            updatePivot(parentIndexes[objIndex],haveChanged);
            pivots[objIndex].set(pivots[parentIndexes[objIndex]]);
        }
        TransformMatrix temp=new TransformMatrix();
        float delta=endPointTime.time-beginPointTime.time;
        temp.interpolateTransforms(beginPointTime.look[objIndex],endPointTime.look[objIndex],
                (delta==0f) ? 0 :  (curTime-beginPointTime.time)/delta);
        pivots[objIndex].multLocal(temp);
        pivots[objIndex].applyToSpatial(thisSpatial);

        haveChanged[objIndex]=true;
    }


    private void setBeginAndEnd() {
        for (int i=1;i<keyframes.size();i++){
            if (curTime <= ((PointInTime)keyframes.get(i)).time){
                beginPointTime=(PointInTime)keyframes.get(i-1);
                endPointTime=(PointInTime)keyframes.get(i);
                return;
            }
        }
        beginPointTime=(PointInTime)keyframes.get(0);
        if (keyframes.size()==1)
            endPointTime=beginPointTime;
        else
            endPointTime=(PointInTime)keyframes.get(1);
        curTime=((PointInTime)keyframes.get(0)).time;
    }

    public void setObject(Spatial objChange, int index, int parentIndex) {
        toChange[index]=objChange;
        parentIndexes[index]=parentIndex;
    }


    private PointInTime findTime(float time) {
        for (int i=0;i<keyframes.size();i++){
            if (((PointInTime)keyframes.get(i)).time==time)
                return (PointInTime) keyframes.get(i);
            if (((PointInTime)keyframes.get(i)).time>time){
                PointInTime t=new PointInTime(time);
                keyframes.add(i,t);
                return t;
            }
        }
        PointInTime t=new PointInTime(time);
        keyframes.add(t);
        return t;
    }

    public void setRotation(int indexInST, float time, Quaternion rot) {
        PointInTime toAdd=findTime(time);
        toAdd.setRotation(indexInST,rot);
    }

    public void setPosition(int indexInST, float time, Vector3f position) {
        PointInTime toAdd=findTime(time);
        toAdd.setTranslation(indexInST,position);
    }

    public void setScale(int indexInST, float time, Vector3f scale) {
        PointInTime toAdd=findTime(time);
        toAdd.setScale(indexInST,scale);
    }

    public void setTimeFrame(float time) {
        int index;
        for (index=0;index<keyframes.size();index++)
            if (((PointInTime)keyframes.get(index)).time==time) break;
        if (index==keyframes.size()) return;
        PointInTime thisTime=(PointInTime) keyframes.get(index);
        boolean[] haveChanged=new boolean[numObjects];
        for (int i=0;i<thisTime.look.length;i++){
            if (toChange[i] instanceof Spatial){
                updatePivot(i,thisTime,haveChanged);
            }
        }
    }

    private void updatePivot(int objIndex, PointInTime thisTime,boolean []haveChanged) {
        if (haveChanged[objIndex]){
//            Spatial thisSpatial=(Spatial) toChange[objIndex];
//            thisSpatial.setLocalRotation(pivots[objIndex].getRotation((Matrix3f) null));
//            thisSpatial.setLocalTranslation(pivots[objIndex].getTranslation(null));
//            thisSpatial.setLocalScale(pivots[objIndex].getScale(null));
            return;
        }
        pivots[objIndex].loadIdentity();
        if (parentIndexes[objIndex]!=-1){
            updatePivot(parentIndexes[objIndex],thisTime,haveChanged);
            pivots[objIndex].set(pivots[parentIndexes[objIndex]]);
        }
        pivots[objIndex].multLocal(thisTime.look[objIndex]);
        Spatial thisSpatial=(Spatial) toChange[objIndex];
        pivots[objIndex].applyToSpatial(thisSpatial);
//        thisSpatial.setLocalRotation(pivots[objIndex].getRotation((Matrix3f) null));
//        thisSpatial.setLocalTranslation(pivots[objIndex].getTranslation(null));
//        thisSpatial.setLocalScale(pivots[objIndex].getScale(null));

        haveChanged[objIndex]=true;
    }

    public void interpolateMissing() {
        if (keyframes.size()==1)
            return;
        fillTrans();
        fillRots();
        fillScales();
    }

    private void fillScales() {
        for (int objIndex=0;objIndex<numObjects;objIndex++){
            // 1) Find first non-null scale of objIndex <code>objIndex</code>
            int start;
            for (start=0;start<keyframes.size();start++){
                if (((PointInTime)keyframes.get(start)).usedScale.get(objIndex)) break;
            }
            if (start==keyframes.size()){    // if they are all null then fill with identity
                for (int i=0;i<keyframes.size();i++)
                    ((PointInTime)keyframes.get(i)).look[objIndex].setScale(1,1,1);
                break;  // we're done so lets break
            }

            if (start!=0){  // if there -are- null elements at the begining, then fill with first non-null
                ((PointInTime)keyframes.get(start)).look[objIndex].getScale(unSyncbeginPos);
                for (int i=0;i<start;i++)
                    ((PointInTime)keyframes.get(i)).look[objIndex].setScale(unSyncbeginPos);
            }
            int lastgood=start;
            for (int i=start+1;i<keyframes.size();i++){
                if (((PointInTime)keyframes.get(i)).usedScale.get(objIndex)){
                    fillScale(objIndex,lastgood,i);    // fills gaps
                    lastgood=i;
                }
            }
            if (lastgood!=keyframes.size()-1){  // Make last ones equal to last good
                ((PointInTime)keyframes.get(keyframes.size()-1)).look[objIndex].setScale(
                        ((PointInTime)keyframes.get(lastgood)).look[objIndex].getScale(null)
                );
            }
            ((PointInTime)keyframes.get(lastgood)).look[objIndex].getScale(unSyncbeginPos);

            for (int i=lastgood+1;i<keyframes.size();i++){
                ((PointInTime)keyframes.get(i)).look[objIndex].setScale(unSyncbeginPos);
            }
        }
    }

    private void fillScale(int objectIndex, int startScaleIndex, int endScaleIndex) {
        ((PointInTime)keyframes.get(startScaleIndex)).look[objectIndex].getScale(unSyncbeginPos);
        ((PointInTime)keyframes.get(endScaleIndex)).look[objectIndex].getScale(unSyncendPos);
        float startTime=((PointInTime)keyframes.get(startScaleIndex)).time;
        float endTime=((PointInTime)keyframes.get(endScaleIndex)).time;
        float delta=endTime-startTime;
        Vector3f tempVec=new Vector3f();

        for (int i=startScaleIndex+1;i<endScaleIndex;i++){
            float thisTime=((PointInTime)keyframes.get(i)).time;
            tempVec.interpolate(unSyncbeginPos,unSyncendPos,(thisTime-startTime)/delta);
            ((PointInTime)keyframes.get(i)).look[objectIndex].setScale(tempVec);
        }
    }


    private void fillRots() {
        for (int joint=0;joint<numObjects;joint++){
            // 1) Find first non-null rotation of joint <code>joint</code>
            int start;
            for (start=0;start<keyframes.size();start++){
                if (((PointInTime)keyframes.get(start)).usedRot.get(joint)) break;
            }
            if (start==keyframes.size()){    // if they are all null then fill with identity
                for (int i=0;i<keyframes.size();i++)
                    ((PointInTime)keyframes.get(i)).look[joint].setRotationQuaternion(new Quaternion());
                break;  // we're done so lets break
            }
            if (start!=0){  // if there -are- null elements at the begining, then fill with first non-null

                ((PointInTime)keyframes.get(start)).look[joint].getRotation(unSyncbeginRot);
                for (int i=0;i<start;i++)
                    ((PointInTime)keyframes.get(i)).look[joint].setRotationQuaternion(unSyncbeginRot);
            }
            int lastgood=start;
            for (int i=start+1;i<keyframes.size();i++){
                if (((PointInTime)keyframes.get(i)).usedRot.get(joint)){
                    fillQuats(joint,lastgood,i);    // fills gaps
                    lastgood=i;
                }
            }
//            fillQuats(joint,lastgood,keyframes.size()-1);  // fills tail
            ((PointInTime)keyframes.get(lastgood)).look[joint].getRotation(unSyncbeginRot);

            for (int i=lastgood+1;i<keyframes.size();i++){
                ((PointInTime)keyframes.get(i)).look[joint].setRotationQuaternion(unSyncbeginRot);
            }
        }
    }

    private void fillQuats(int objectIndex,int startRotIndex, int endRotIndex) {
        ((PointInTime)keyframes.get(startRotIndex)).look[objectIndex].getRotation(unSyncbeginRot);
        ((PointInTime)keyframes.get(endRotIndex)).look[objectIndex].getRotation(unSyncendRot);
        float startTime=((PointInTime)keyframes.get(startRotIndex)).time;
        float endTime=((PointInTime)keyframes.get(endRotIndex)).time;
        float delta=endTime-startTime;
        Quaternion tempQuat=new Quaternion();

        for (int i=startRotIndex+1;i<endRotIndex;i++){
            float thisTime=((PointInTime)keyframes.get(i)).time;
            tempQuat.slerp(unSyncbeginRot,unSyncendRot,(thisTime-startTime)/delta);
            ((PointInTime)keyframes.get(i)).look[objectIndex].setRotationQuaternion(tempQuat);
        }
    }

    private void fillTrans() {
        for (int objIndex=0;objIndex<numObjects;objIndex++){
            // 1) Find first non-null translation of objIndex <code>objIndex</code>
            int start;
            for (start=0;start<keyframes.size();start++){
                if (((PointInTime)keyframes.get(start)).usedTrans.get(objIndex)) break;
            }
            if (start==keyframes.size()){    // if they are all null then fill with identity
                for (int i=0;i<keyframes.size();i++)
                    ((PointInTime)keyframes.get(i)).look[objIndex].setTranslation(0,0,0);
                break;  // we're done so lets break
            }

            if (start!=0){  // if there -are- null elements at the begining, then fill with first non-null
                ((PointInTime)keyframes.get(start)).look[objIndex].getTranslation(unSyncbeginPos);
                for (int i=0;i<start;i++)
                    ((PointInTime)keyframes.get(i)).look[objIndex].setTranslation(unSyncbeginPos);
            }
            int lastgood=start;
            for (int i=start+1;i<keyframes.size();i++){
                if (((PointInTime)keyframes.get(i)).usedTrans.get(objIndex)){
                    fillVecs(objIndex,lastgood,i);    // fills gaps
                    lastgood=i;
                }
            }
            if (lastgood!=keyframes.size()-1){  // Make last ones equal to last good
                ((PointInTime)keyframes.get(keyframes.size()-1)).look[objIndex].setTranslation(
                        ((PointInTime)keyframes.get(lastgood)).look[objIndex].getTranslation(null)
                );
            }
            ((PointInTime)keyframes.get(lastgood)).look[objIndex].getTranslation(unSyncbeginPos);

            for (int i=lastgood+1;i<keyframes.size();i++){
                ((PointInTime)keyframes.get(i)).look[objIndex].setTranslation(unSyncbeginPos);
            }
        }
    }

    private void fillVecs(int objectIndex,int startPosIndex, int endPosIndex) {
        ((PointInTime)keyframes.get(startPosIndex)).look[objectIndex].getTranslation(unSyncbeginPos);
        ((PointInTime)keyframes.get(endPosIndex)).look[objectIndex].getTranslation(unSyncendPos);
        float startTime=((PointInTime)keyframes.get(startPosIndex)).time;
        float endTime=((PointInTime)keyframes.get(endPosIndex)).time;
        float delta=endTime-startTime;
        Vector3f tempVec=new Vector3f();

        for (int i=startPosIndex+1;i<endPosIndex;i++){
            float thisTime=((PointInTime)keyframes.get(i)).time;
            tempVec.interpolate(unSyncbeginPos,unSyncendPos,(thisTime-startTime)/delta);
            ((PointInTime)keyframes.get(i)).look[objectIndex].setTranslation(tempVec);
        }
    }


    public class PointInTime{
        public BitSet usedRot;
        public BitSet usedTrans;
        public BitSet usedScale;
        public float time;
        public TransformMatrix[] look; // toChange[i] looks like look[i] at time

        PointInTime(float time){
            look=new TransformMatrix[numObjects];
            usedRot=new BitSet(numObjects);
            usedTrans=new BitSet(numObjects);
            usedScale=new BitSet(numObjects);
            for (int i=0;i<look.length;i++)
                look[i]=new TransformMatrix();
            this.time=time;
        }

        PointInTime(){
            look=new TransformMatrix[numObjects];
        }


        void setRotation(int objIndex,Quaternion rot){
            look[objIndex].setRotationQuaternion(rot);
            usedRot.set(objIndex);
        }
        void setTranslation(int objIndex,Vector3f trans){
            look[objIndex].setTranslation(trans);
            usedTrans.set(objIndex);
        }
        void setScale(int objIndex,Vector3f scale){
            look[objIndex].setScale(scale);
            usedScale.set(objIndex);
        }
    }

    public int getNumObjects(){
        return numObjects;
    }
}