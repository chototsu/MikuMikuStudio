package com.jme.scene.model.XMLparser;

import com.jme.scene.Controller;
import com.jme.math.TransformMatrix;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.BitSet;

/**
 * Started Date: Jun 9, 2004<br>
 *
 * This controller animates a Node's JointMesh children acording to the joints stored inside <code>movementInfo</code>
 * 
 * @author Jack Lindamood
 */
public class JointController extends Controller {
    /**
     * It is JointController's responsibility to keep changePoints sorted by <code>time</code> at all times
     */
    int numJoints;

    /**
     * movementInfo[i] contains a float value time and an array of TransformMatrix.  At time <code>time</code>
     * the joint i is at movement <code>jointChange[i]</code>
     */
    ArrayList movementInfo;

    /**
     * parentIndex contains a list of who's parent a joint is.  -1 indicates a root joint with no parent
     */
    int[] parentIndex;
    /**
     * Local refrence matrix that can determine a joint's position in space relative to its parent
     */
    TransformMatrix[] localRefMatrix;
    public float FPS;

    /**
     * Array of all the meshes this controller should consider animating
     */
    ArrayList movingMeshes;


    /**
     * This controller's internal current time
     */
    private float curTime;

    /**
     * This controller's internal current PointInTime index
     */
    private int curTimePoint;


    /**
     * Used internally, they are updated every update(float) call to tell points how to change
     */
    private TransformMatrix[] jointMovements;

    /**
     * The inverse chain matrix of every joint.  Calculated once with prosessController()
     */
    private TransformMatrix[] inverseChainMatrix;

    // Internal worker classes
    private Quaternion unSyncbeginAngle=new Quaternion();
    private Vector3f unSyncbeginPos=new Vector3f();
    private TransformMatrix tempUnSyncd=new TransformMatrix();

    /**
     * Tells update that it should be called every <code>skipRate</code> seconds
     */
    public float skipRate;
    /**
     * Used with skipRate internally
     */
    private float currentSkip;

    JointController(int numJoints){
        this.numJoints=numJoints;
        parentIndex=new int[numJoints];
        localRefMatrix=new TransformMatrix[numJoints];
        movingMeshes=new ArrayList();
        jointMovements=new TransformMatrix[numJoints];
        inverseChainMatrix=new TransformMatrix[numJoints];
        for (int i=0;i<numJoints;i++){
            localRefMatrix[i]=new TransformMatrix();
            jointMovements[i]=new TransformMatrix();
            inverseChainMatrix[i]=new TransformMatrix();
        }
        movementInfo=new ArrayList();
        movementInfo.add(0,new PointInTime());  // Add a time=0
        curTime=0;
        curTimePoint=1;
        currentSkip=0;
        skipRate=.01f;
//        this.setSpeed(.1f);
    }

    /**
     * Tells JointController that at time <code>time</code> the joint <code>jointNumber</code> will translate
     * to x,y,z relative to its parent
     * @param jointNumber Index of joint to affect
     * @param time Which time the joint will take these values
     * @param x Joint's x translation
     * @param y Joint's y translation
     * @param z Joint's z translation
     */
    public void setTranslation(int jointNumber,float time,float x,float y,float z){
        findUpToTime(time).setTranslation(jointNumber,x,y,z);
    }

    /**
     * Tells JointController that at time <code>time</code> the joint <code>jointNumber</code> will translate
     * to x,y,z relative to its parent
     * @param jointNumber Index of joint to affect
     * @param time Which time the joint will take these values
     * @param trans Joint's translation

     */
    public void setTranslation(int jointNumber,float time,Vector3f trans){
        findUpToTime(time).setTranslation(jointNumber,trans);
    }

    /**
     * Tells JointController that at time <code>time</code> the joint <code>jointNumber</code> will rotate
     * acording to the euler angles x,y,z relative to its parent's rotation
     * @param jointNumber Index of joint to affect
     * @param time Which time the joint will take these values
     * @param x Joint's x rotation
     * @param y Joint's y rotation
     * @param z Joint's z rotation
     */
    public void setRotation(int jointNumber,float time,float x,float y,float z){
        findUpToTime(time).setRotation(jointNumber,x,y,z);
    }

    /**
     * Tells JointController that at time <code>time</code> the joint <code>jointNumber</code> will rotate
     * acording to <code>quaternion</code>
     * @param jointNumber Index of joint to affect
     * @param time Which time the joint will take these values
     * @param quaternion The joint's new rotation
     */
    public void setRotation(int jointNumber, float time, Quaternion quaternion) {
        findUpToTime(time).setRotation(jointNumber,quaternion);
    }

    /**
     * Used with setRotation and setTranslation.  This function finds a point in time for given time.  If one doesn't
     * exist then a new PointInTime is created and returned.
     * @param time
     * @return
     */
    private PointInTime findUpToTime(float time) {
        Iterator I=movementInfo.iterator();
        int index=0;
        while (I.hasNext()){
            float curTime=((PointInTime)I.next()).time;
            if (curTime>=time) break;
            index++;
        }
        PointInTime storedNext=null;
        if (index==movementInfo.size()){
            storedNext=new PointInTime();
            movementInfo.add(storedNext);
            storedNext.time = time;
        } else{
            if (((PointInTime)movementInfo.get(index)).time==time){
                storedNext=(PointInTime) movementInfo.get(index);
            } else{
                storedNext=new PointInTime();
                movementInfo.add(index,storedNext);
                storedNext.time = time;
            }
        }
        return storedNext;
    }
    public void update(float time) {
        if (!this.isActive()) return;
        curTime+=time*this.getSpeed();
        currentSkip+=time;
        if (currentSkip>=skipRate){
            currentSkip=0;
        } else{
            return;
        }

        PointInTime now=(PointInTime) movementInfo.get(curTimePoint);
        PointInTime then=null;
        if (now.time < curTime){
            curTimePoint++;
            if (curTimePoint==movementInfo.size()){
                curTimePoint=1;
                curTime=0;
                now=(PointInTime) movementInfo.get(1);
                then=(PointInTime) movementInfo.get(0);
            } else{
                then=now;
                now=(PointInTime) movementInfo.get(curTimePoint);
            }
        } else
            then=(PointInTime) movementInfo.get(curTimePoint-1);
        float delta=(curTime-then.time)/(now.time-then.time);
        createJointTransforms(delta);
        combineWithInverse();
        updateData();
    }

    /**
     * Used with update(float).  <code>updateData</code> moves every normal and vertex acording to its jointIndex
     */
    private void updateData(){
        for (int currentGroup=0;currentGroup<movingMeshes.size();currentGroup++){
            JointMesh updatingGroup=(JointMesh) movingMeshes.get(currentGroup);
            int currentBoneIndex;
            for (int j=0;j<updatingGroup.jointIndex.length;j++){
                currentBoneIndex=updatingGroup.jointIndex[j];
                unSyncbeginPos.set(updatingGroup.originalVertex[j]);
                updatingGroup.setVertex(j,jointMovements[currentBoneIndex].multPoint(unSyncbeginPos));
                unSyncbeginPos.set(updatingGroup.originalNormal[j]);
                updatingGroup.setNormal(j,jointMovements[currentBoneIndex].multNormal(unSyncbeginPos));
            }
            updatingGroup.updateModelBound();   //TODO: Why won't this work?
        }
    }

    /**
     * Used with update(float) to combine joints with their inverse to properly translate points.
     */
    private void combineWithInverse() {
        for (int i=0;i<numJoints;i++)
            jointMovements[i].multLocal(inverseChainMatrix[i]);
    }


    /**
     * Processes a JointController by filling holes and creating inverse matrixes.  Should only be called once per
     * JointController object lifetime
     */
    public void processController(){
        invertWithParents();
        fillHoles();
    }

    /**
     * Inverts joints with their parents.  Only called once per JointController lifetime during processing.
     */
    private void invertWithParents() {
        for (int i=0;i<numJoints;i++){
            inverseChainMatrix[i]=new TransformMatrix(localRefMatrix[i]);
            inverseChainMatrix[i].inverse();
            if (parentIndex[i]!=-1)
                inverseChainMatrix[i].multLocal(inverseChainMatrix[parentIndex[i]]);
        }
    }


    /**
     * Called with update to create the needed joint transforms for that point in time
     * @param changeAmnt The % diffrence (from 0-1) between two points in time
     */
    private void createJointTransforms(float changeAmnt) {
        PointInTime now=(PointInTime) movementInfo.get(curTimePoint);
        PointInTime then=(PointInTime) movementInfo.get(curTimePoint-1);
        for (int index=0;index<numJoints;index++){
            int theParentIndex=parentIndex[index];

            unSyncbeginAngle.set(then.jointRotation[index]);
            unSyncbeginPos.set(then.jointTranslation[index]);

            unSyncbeginAngle.slerp(now.jointRotation[index],changeAmnt);
            unSyncbeginPos.interpolate(now.jointTranslation[index],changeAmnt);

            tempUnSyncd.set(unSyncbeginAngle,unSyncbeginPos);
            jointMovements[index].set(localRefMatrix[index]);
            jointMovements[index].multLocal(tempUnSyncd);
            if (theParentIndex!=-1){
                tempUnSyncd.set(jointMovements[index]);
                jointMovements[index].set(jointMovements[theParentIndex]);
                jointMovements[index].multLocal(tempUnSyncd);
            }
        }
    }


    /**
     * Fills null rotations and translations for any joint at any point in time
     */
    private void fillHoles() {
        fillRots();
        fillTrans();
    }


    /**
     * Gives every point in time for every joint a valid rotation
     */
    private void fillRots() {
        for (int joint=0;joint<numJoints;joint++){
            // 1) Find first non-null rotation of joint <code>joint</code>
            int start;
            for (start=0;start<movementInfo.size();start++){
                if (((PointInTime)movementInfo.get(start)).jointRotation[joint]!=null) break;
            }
            if (start==movementInfo.size()){    // if they are all null then fill with identity
                for (int i=0;i<movementInfo.size();i++)
                    ((PointInTime)movementInfo.get(i)).jointRotation[joint]=new Quaternion();
                break;  // we're done so lets break
            }
            if (start!=0){  // if there -are- null elements at the begining, then fill with first non-null
                unSyncbeginAngle.set( ((PointInTime)movementInfo.get(start)).jointRotation[joint]);
                for (int i=0;i<start;i++)
                    ((PointInTime)movementInfo.get(i)).jointRotation[joint]=new Quaternion(unSyncbeginAngle);
            }
            int lastgood=start;
            for (int i=start+2;i<movementInfo.size();i++){
                if (((PointInTime)movementInfo.get(i)).jointRotation[joint]!=null){
                    fillQuats(joint,lastgood,i);    // fills gaps
                    lastgood=i;
                }
            }
            fillQuats(joint,lastgood,movementInfo.size()-1);  // fills tail
        }
    }


    /**
     * Gives every point in time for every joint a valid translation
     */
    private void fillTrans() {
        for (int joint=0;joint<numJoints;joint++){
            // 1) Find first non-null translation of joint <code>joint</code>
            int start;
            for (start=0;start<movementInfo.size();start++){
                if (((PointInTime)movementInfo.get(start)).jointTranslation[joint]!=null) break;
            }
            if (start==movementInfo.size()){    // if they are all null then fill with identity
                for (int i=0;i<movementInfo.size();i++)
                    ((PointInTime)movementInfo.get(i)).jointTranslation[joint]=new Vector3f(0,0,0);
                break;  // we're done so lets break
            }
            if (start!=0){  // if there -are- null elements at the begining, then fill with first non-null
                unSyncbeginPos.set( ((PointInTime)movementInfo.get(start)).jointTranslation[joint]);
                for (int i=0;i<start;i++)
                    ((PointInTime)movementInfo.get(i)).jointTranslation[joint]=new Vector3f(unSyncbeginPos);
            }
            int lastgood=start;
            for (int i=start+2;i<movementInfo.size();i++){
                if (((PointInTime)movementInfo.get(i)).jointTranslation[joint]!=null){
                    fillPos(joint,lastgood,i);    // fills gaps
                    lastgood=i;
                }
            }
            fillPos(joint,lastgood,movementInfo.size()-1);  // fills tail
        }
    }

    /**
     * Interpolates missing quats that weren't specified to the JointController
     * @param jointIndex Index of the joint that has missing quats
     * @param startRotIndex Begining index of a valid non-null quat
     * @param endRotIndex Ending index of a valid non-null quat
     */
    private void fillQuats(int jointIndex,int startRotIndex, int endRotIndex) {
        unSyncbeginAngle.set(((PointInTime)movementInfo.get(startRotIndex)).jointRotation[jointIndex]);
        for (int i=startRotIndex+1;i<endRotIndex;i++){
            ((PointInTime)movementInfo.get(i)).jointRotation[jointIndex]=new Quaternion(unSyncbeginAngle);
            ((PointInTime)movementInfo.get(i)).jointRotation[jointIndex].slerp(
                    ((PointInTime)movementInfo.get(endRotIndex)).jointRotation[jointIndex],
                    ((float)i-startRotIndex)/(endRotIndex-startRotIndex));
        }
    }

    /**
     * Interpolates missing vector that weren't specified to the JointController
     * @param jointIndex Index of the joint that has missing vector
     * @param startPosIndex Begining index of a valid non-null vector
     * @param endPosIndex Ending index of a valid non-null vector
     */
    private void fillPos(int jointIndex,int startPosIndex, int endPosIndex) {
        unSyncbeginPos.set(((PointInTime)movementInfo.get(startPosIndex)).jointTranslation[jointIndex]);
        for (int i=startPosIndex+1;i<endPosIndex;i++){
            ((PointInTime)movementInfo.get(i)).jointTranslation[jointIndex]=new Vector3f(unSyncbeginPos);
            ((PointInTime)movementInfo.get(i)).jointTranslation[jointIndex].interpolate(
                    ((PointInTime)movementInfo.get(endPosIndex)).jointTranslation[jointIndex],
                    ((float)i-startPosIndex)/(endPosIndex-startPosIndex));
        }
    }

    /**
     * Adds a jointmesh for this JointController to consider animating
     * @param child Child JointMesh to consider
     */
    public void addJointMesh(JointMesh child) {
        movingMeshes.add(child);
    }

    /**
     * At a point in time is defined by <b>time</b>.  JointController will change joint <b>i</b> to the
     * rotation <code>jointRotation[i]</code> and translation <code>jointTranslation[i]</code> at the point in time
     * <code>time</code>
     */
    class PointInTime{
        float time;
        Vector3f[] jointTranslation;
        Quaternion[] jointRotation;
        /**
         * The bitsets specify if the translation/rotation was specified externally, or if it was interpolated.  This
         * is useful to cut down on stored XML file size.
         */
        BitSet usedTrans;
        BitSet usedRot;
        PointInTime(){
            jointTranslation=new Vector3f[numJoints];
            usedRot = new BitSet(numJoints);
            usedTrans=new BitSet(numJoints);
            jointRotation=new Quaternion[numJoints];
        }
        void setRotation(int jointIndex,float x,float y,float z){
            if (jointRotation[jointIndex]==null) jointRotation[jointIndex]=new Quaternion();
            jointRotation[jointIndex].fromAngles(new float[]{x,y,z});
            usedRot.set(jointIndex);
        }

        void setTranslation(int jointIndex,float x,float y,float z){
            if (jointTranslation[jointIndex]==null) jointTranslation[jointIndex]=new Vector3f();
            jointTranslation[jointIndex].set(x,y,z);
            usedTrans.set(jointIndex);
        }

        void setTranslation(int jointIndex,Vector3f v){
            if (jointTranslation[jointIndex]==null) jointTranslation[jointIndex]=new Vector3f();
            jointTranslation[jointIndex].set(v);
            usedTrans.set(jointIndex);
        }

        public void setRotation(int jointIndex, Quaternion quaternion) {
            if (jointRotation[jointIndex]==null) jointRotation[jointIndex]=new Quaternion();
            jointRotation[jointIndex].set(quaternion);
            usedRot.set(jointIndex);
        }
    }
}