/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jme.animation;

import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.CloneCreator;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.math.TransformMatrixQuat;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Arrays;

/**
 * Started Date: Jul 9, 2004 <br>
 * <br>
 * 
 * This class animates spatials by interpolating between various
 * transformations. The user defines objects to be transformed and what
 * rotation/translation/scale to give each object at various points in time. The
 * user must call interpolateMissing() before using the controller in order to
 * interpolate unspecified translation/rotation/scale.
 * 
 * 
 * @author Jack Lindamood
 */
public class SpatialTransformer extends Controller {

    private static final long serialVersionUID = 1L;

    /** Number of objects this transformer changes. */
    private int numObjects;

    /** Refrences to the objects that will be changed. */
    public Spatial[] toChange;

    /** Used internally by update specifying how to change each object. */
    private TransformMatrixQuat[] pivots;

    /**
     * parentIndexes[i] states that toChange[i]'s parent is
     * toChange[parentIndex[i]].
     */
    public int[] parentIndexes;

    /** Interpolated array of keyframe states */
    public ArrayList keyframes;

    /** Current time in the animation */
    private float curTime;

    /** Time previous to curTime */
    private PointInTime beginPointTime;

    /** Time after curTime */
    private PointInTime endPointTime;

    /** Used internally in update to flag that a pivot has been updated */
    private boolean[] haveChanged;

    private float delta;

    private final static Vector3f unSyncbeginPos = new Vector3f();

    private final static Vector3f unSyncendPos = new Vector3f();

    private final static Quaternion unSyncbeginRot = new Quaternion();

    private final static Quaternion unSyncendRot = new Quaternion();

    /**
     * Constructs a new SpatialTransformer that will operate on
     * <code>numObjects</code> Spatials
     * 
     * @param numObjects
     *            The number of spatials to change
     */
    public SpatialTransformer(int numObjects) {
        this.numObjects = numObjects;
        toChange = new Spatial[numObjects];
        pivots = new TransformMatrixQuat[numObjects];
        parentIndexes = new int[numObjects];
        haveChanged = new boolean[numObjects];
        Arrays.fill(parentIndexes, -1);
        for (int i = 0; i < numObjects; i++)
            pivots[i] = new TransformMatrixQuat();
        keyframes = new ArrayList();
    }

    public void update(float time) {
        if (!isActive()) return;
        curTime += time * getSpeed();
        setBeginAndEnd();
        Arrays.fill(haveChanged, false);
        delta = endPointTime.time - beginPointTime.time;
        if (delta != 0f) delta = (curTime - beginPointTime.time) / delta;
        for (int i = 0; i < numObjects; i++) {
            updatePivot(i);
            pivots[i].applyToSpatial(toChange[i]);
        }
    }

    /**
     * Called by update, and itself recursivly. Will, when completed, change
     * toChange[objIndex] by pivots[objIndex]
     * 
     * @param objIndex
     *            The index to update.
     */
    private void updatePivot(int objIndex) {
        if (haveChanged[objIndex])
            return;
        else
            haveChanged[objIndex] = true;
        int parentIndex = parentIndexes[objIndex];
        if (parentIndex != -1) {
            updatePivot(parentIndex);
        }
        pivots[objIndex].interpolateTransforms(beginPointTime.look[objIndex],
                endPointTime.look[objIndex], delta);
        if (parentIndex != -1)
                pivots[objIndex].combineWithParent(pivots[parentIndex]);
    }

    /**
     * Called in update to calculate the correct beginPointTime and
     * endPointTime.
     */
    private void setBeginAndEnd() {
        for (int i = 1; i < keyframes.size(); i++) {
            if (curTime <= ((PointInTime) keyframes.get(i)).time) {
                beginPointTime = (PointInTime) keyframes.get(i - 1);
                endPointTime = (PointInTime) keyframes.get(i);
                return;
            }
        }
        beginPointTime = (PointInTime) keyframes.get(0);
        if (keyframes.size() == 1)
            endPointTime = beginPointTime;
        else
            endPointTime = (PointInTime) keyframes.get(1);
        curTime = ((PointInTime) keyframes.get(0)).time;
    }

    /**
     * Sets an object to animate. The object's index is <code>index</code> and
     * it's parent index is <code>parentIndex</code>. A parent index of -1
     * indicates it has no parent.
     * 
     * @param objChange
     *            The spatial that will be updated by this SpatialTransformer.
     * @param index
     *            The index of that spatial in this transformer's array
     * @param parentIndex
     *            The parentIndex in this transformer's array for this Spatial
     */

    public void setObject(Spatial objChange, int index, int parentIndex) {
        toChange[index] = objChange;
        pivots[index].setTranslation(objChange.getLocalTranslation());
        parentIndexes[index] = parentIndex;
    }

    /**
     * Returns the keyframe for <code>time</code>. If one doens't exist, a
     * new one is created
     * 
     * @param time
     *            The time to look for.
     * @return The keyframe refrencing <code>time</code>.
     */
    private PointInTime findTime(float time) {
        for (int i = 0; i < keyframes.size(); i++) {
            if (((PointInTime) keyframes.get(i)).time == time)
                    return (PointInTime) keyframes.get(i);
            if (((PointInTime) keyframes.get(i)).time > time) {
                PointInTime t = new PointInTime(time);
                keyframes.add(i, t);
                return t;
            }
        }
        PointInTime t = new PointInTime(time);
        keyframes.add(t);
        return t;
    }

    /**
     * Sets object with index <code>indexInST</code> to rotate by
     * <code>rot</code> at time <code>time</code>.
     * 
     * @param indexInST
     *            The index of the spatial to change
     * @param time
     *            The time for the spatial to take this rotation
     * @param rot
     *            The rotation to take
     */
    public void setRotation(int indexInST, float time, Quaternion rot) {
        PointInTime toAdd = findTime(time);
        toAdd.setRotation(indexInST, rot);
    }

    /**
     * Sets object with index <code>indexInST</code> to translate by
     * <code>position</code> at time <code>time</code>.
     * 
     * @param indexInST
     *            The index of the spatial to change
     * @param time
     *            The time for the spatial to take this translation
     * @param position
     *            The position to take
     */
    public void setPosition(int indexInST, float time, Vector3f position) {
        PointInTime toAdd = findTime(time);
        toAdd.setTranslation(indexInST, position);
    }

    /**
     * Sets object with index <code>indexInST</code> to scale by
     * <code>scale</code> at time <code>time</code>.
     * 
     * @param indexInST
     *            The index of the spatial to change
     * @param time
     *            The time for the spatial to take this scale
     * @param scale
     *            The scale to take
     */
    public void setScale(int indexInST, float time, Vector3f scale) {
        PointInTime toAdd = findTime(time);
        toAdd.setScale(indexInST, scale);
    }

    /**
     * This must be called one time, once all translations/rotations/scales have
     * been set. It will interpolate unset values to make the animation look
     * correct. Tail and head values are assumed to be the identity.
     */
    public void interpolateMissing() {
        if (keyframes.size() != 1) {
            fillTrans();
            fillRots();
            fillScales();
        }
        for (int objIndex = 0; objIndex < numObjects; objIndex++)
            pivots[objIndex].applyToSpatial(toChange[objIndex]);
    }

    /**
     * Called by interpolateMissing(), it will interpolate missing scale values.
     */
    private void fillScales() {
        for (int objIndex = 0; objIndex < numObjects; objIndex++) {
            // 1) Find first non-null scale of objIndex <code>objIndex</code>
            int start;
            for (start = 0; start < keyframes.size(); start++) {
                if (((PointInTime) keyframes.get(start)).usedScale
                        .get(objIndex)) break;
            }
            if (start == keyframes.size()) { // if they are all null then fill
                // with identity
                for (int i = 0; i < keyframes.size(); i++)
                    ((PointInTime) keyframes.get(i)).look[objIndex].setScale(1,
                            1, 1);
                continue; // we're done so lets break
            }

            if (start != 0) { // if there -are- null elements at the begining,
                // then fill with first non-null
                ((PointInTime) keyframes.get(start)).look[objIndex]
                        .getScale(unSyncbeginPos);
                for (int i = 0; i < start; i++)
                    ((PointInTime) keyframes.get(i)).look[objIndex]
                            .setScale(unSyncbeginPos);
            }
            int lastgood = start;
            for (int i = start + 1; i < keyframes.size(); i++) {
                if (((PointInTime) keyframes.get(i)).usedScale.get(objIndex)) {
                    fillScale(objIndex, lastgood, i); // fills gaps
                    lastgood = i;
                }
            }
            if (lastgood != keyframes.size() - 1) { // Make last ones equal to
                // last good
                ((PointInTime) keyframes.get(keyframes.size() - 1)).look[objIndex]
                        .setScale(((PointInTime) keyframes.get(lastgood)).look[objIndex]
                                .getScale(null));
            }
            ((PointInTime) keyframes.get(lastgood)).look[objIndex]
                    .getScale(unSyncbeginPos);

            for (int i = lastgood + 1; i < keyframes.size(); i++) {
                ((PointInTime) keyframes.get(i)).look[objIndex]
                        .setScale(unSyncbeginPos);
            }
        }
    }

    /**
     * Interpolates unspecified scale values for objectIndex from start to end.
     * 
     * @param objectIndex
     *            Index to interpolate.
     * @param startScaleIndex
     *            Starting scale index.
     * @param endScaleIndex
     *            Ending scale index.
     */
    private void fillScale(int objectIndex, int startScaleIndex,
            int endScaleIndex) {
        ((PointInTime) keyframes.get(startScaleIndex)).look[objectIndex]
                .getScale(unSyncbeginPos);
        ((PointInTime) keyframes.get(endScaleIndex)).look[objectIndex]
                .getScale(unSyncendPos);
        float startTime = ((PointInTime) keyframes.get(startScaleIndex)).time;
        float endTime = ((PointInTime) keyframes.get(endScaleIndex)).time;
        float delta = endTime - startTime;
        Vector3f tempVec = new Vector3f();

        for (int i = startScaleIndex + 1; i < endScaleIndex; i++) {
            float thisTime = ((PointInTime) keyframes.get(i)).time;
            tempVec.interpolate(unSyncbeginPos, unSyncendPos,
                    (thisTime - startTime) / delta);
            ((PointInTime) keyframes.get(i)).look[objectIndex]
                    .setScale(tempVec);
        }
    }

    /**
     * Called by interpolateMissing(), it will interpolate missing rotation
     * values.
     */
    private void fillRots() {
        for (int joint = 0; joint < numObjects; joint++) {
            // 1) Find first non-null rotation of joint <code>joint</code>
            int start;
            for (start = 0; start < keyframes.size(); start++) {
                if (((PointInTime) keyframes.get(start)).usedRot.get(joint))
                        break;
            }
            if (start == keyframes.size()) { // if they are all null then fill
                // with identity
                for (int i = 0; i < keyframes.size(); i++)
                    ((PointInTime) keyframes.get(i)).look[joint]
                            .setRotationQuaternion(new Quaternion());
                continue; // we're done so lets break
            }
            if (start != 0) { // if there -are- null elements at the begining,
                // then fill with first non-null

                ((PointInTime) keyframes.get(start)).look[joint]
                        .getRotation(unSyncbeginRot);
                for (int i = 0; i < start; i++)
                    ((PointInTime) keyframes.get(i)).look[joint]
                            .setRotationQuaternion(unSyncbeginRot);
            }
            int lastgood = start;
            for (int i = start + 1; i < keyframes.size(); i++) {
                if (((PointInTime) keyframes.get(i)).usedRot.get(joint)) {
                    fillQuats(joint, lastgood, i); // fills gaps
                    lastgood = i;
                }
            }
            //            fillQuats(joint,lastgood,keyframes.size()-1); // fills tail
            ((PointInTime) keyframes.get(lastgood)).look[joint]
                    .getRotation(unSyncbeginRot);

            for (int i = lastgood + 1; i < keyframes.size(); i++) {
                ((PointInTime) keyframes.get(i)).look[joint]
                        .setRotationQuaternion(unSyncbeginRot);
            }
        }
    }

    /**
     * Interpolates unspecified rot values for objectIndex from start to end.
     * 
     * @param objectIndex
     *            Index to interpolate.
     * @param startRotIndex
     *            Starting rot index.
     * @param endRotIndex
     *            Ending rot index.
     */
    private void fillQuats(int objectIndex, int startRotIndex, int endRotIndex) {
        ((PointInTime) keyframes.get(startRotIndex)).look[objectIndex]
                .getRotation(unSyncbeginRot);
        ((PointInTime) keyframes.get(endRotIndex)).look[objectIndex]
                .getRotation(unSyncendRot);
        float startTime = ((PointInTime) keyframes.get(startRotIndex)).time;
        float endTime = ((PointInTime) keyframes.get(endRotIndex)).time;
        float delta = endTime - startTime;
        Quaternion tempQuat = new Quaternion();

        for (int i = startRotIndex + 1; i < endRotIndex; i++) {
            float thisTime = ((PointInTime) keyframes.get(i)).time;
            tempQuat.slerp(unSyncbeginRot, unSyncendRot, (thisTime - startTime)
                    / delta);
            ((PointInTime) keyframes.get(i)).look[objectIndex]
                    .setRotationQuaternion(tempQuat);
        }
    }

    /**
     * Called by interpolateMissing(), it will interpolate missing translation
     * values.
     */
    private void fillTrans() {
        for (int objIndex = 0; objIndex < numObjects; objIndex++) {
            // 1) Find first non-null translation of objIndex
            // <code>objIndex</code>
            int start;
            for (start = 0; start < keyframes.size(); start++) {
                if (((PointInTime) keyframes.get(start)).usedTrans
                        .get(objIndex)) break;
            }
            if (start == keyframes.size()) { // if they are all null then fill
                // with identity
                for (int i = 0; i < keyframes.size(); i++)
                    pivots[objIndex].getTranslation( // pull original translation
                            ((PointInTime) keyframes.get(i)).look[objIndex]
                            .getTranslation()); // ...into object translation. 
                continue; // we're done so lets break
            }

            if (start != 0) { // if there -are- null elements at the begining,
                // then fill with first non-null
                ((PointInTime) keyframes.get(start)).look[objIndex]
                        .getTranslation(unSyncbeginPos);
                for (int i = 0; i < start; i++)
                    ((PointInTime) keyframes.get(i)).look[objIndex]
                            .setTranslation(unSyncbeginPos);
            }
            int lastgood = start;
            for (int i = start + 1; i < keyframes.size(); i++) {
                if (((PointInTime) keyframes.get(i)).usedTrans.get(objIndex)) {
                    fillVecs(objIndex, lastgood, i); // fills gaps
                    lastgood = i;
                }
            }
            if (lastgood != keyframes.size() - 1) { // Make last ones equal to
                // last good
                ((PointInTime) keyframes.get(keyframes.size() - 1)).look[objIndex]
                        .setTranslation(((PointInTime) keyframes.get(lastgood)).look[objIndex]
                                .getTranslation(null));
            }
            ((PointInTime) keyframes.get(lastgood)).look[objIndex]
                    .getTranslation(unSyncbeginPos);

            for (int i = lastgood + 1; i < keyframes.size(); i++) {
                ((PointInTime) keyframes.get(i)).look[objIndex]
                        .setTranslation(unSyncbeginPos);
            }
        }
    }

    /**
     * Interpolates unspecified translation values for objectIndex from start to
     * end.
     * 
     * @param objectIndex
     *            Index to interpolate.
     * @param startPosIndex
     *            Starting translation index.
     * @param endPosIndex
     *            Ending translation index.
     */
    private void fillVecs(int objectIndex, int startPosIndex, int endPosIndex) {
        ((PointInTime) keyframes.get(startPosIndex)).look[objectIndex]
                .getTranslation(unSyncbeginPos);
        ((PointInTime) keyframes.get(endPosIndex)).look[objectIndex]
                .getTranslation(unSyncendPos);
        float startTime = ((PointInTime) keyframes.get(startPosIndex)).time;
        float endTime = ((PointInTime) keyframes.get(endPosIndex)).time;
        float delta = endTime - startTime;
        Vector3f tempVec = new Vector3f();

        for (int i = startPosIndex + 1; i < endPosIndex; i++) {
            float thisTime = ((PointInTime) keyframes.get(i)).time;
            tempVec.interpolate(unSyncbeginPos, unSyncendPos,
                    (thisTime - startTime) / delta);
            ((PointInTime) keyframes.get(i)).look[objectIndex]
                    .setTranslation(tempVec);
        }
    }

    /**
     * Returns the number of Objects used by this SpatialTransformer
     * 
     * @return The number of objects.
     */
    public int getNumObjects() {
        return numObjects;
    }

    public Controller putClone(Controller store, CloneCreator properties) {
        if (!properties.isSet("spatialcontroller")) return null;
        SpatialTransformer toReturn = new SpatialTransformer(this.numObjects);
        super.putClone(toReturn, properties);

        toReturn.numObjects = this.numObjects;
        System.arraycopy(this.toChange, 0, toReturn.toChange, 0,
                toChange.length);
        System.arraycopy(this.pivots, 0, toReturn.pivots, 0, pivots.length);
        toReturn.parentIndexes = this.parentIndexes;
        toReturn.keyframes = this.keyframes;
        toReturn.curTime = this.curTime;
        toReturn.haveChanged = this.haveChanged;

        properties.queueSpatialTransformer(toReturn);

        return toReturn;
    }

    /**
     * Defines a point in time where at time <code>time</code>, ohject
     * <code>toChange[i]</code> will assume transformation
     * <code>look[i]</code>. BitSet's used* specify if the transformation
     * value was specified by the user, or interpolated
     */
    public class PointInTime {

        /** Bit i is true if look[i].rotation was user defined. */
        public BitSet usedRot;

        /** Bit i is true if look[i].translation was user defined. */
        public BitSet usedTrans;

        /** Bit i is true if look[i].scale was user defined. */
        public BitSet usedScale;

        /** The time of this TransformationMatrix. */
        public float time;

        /** toChange[i] looks like look[i] at time. */
        public TransformMatrixQuat[] look;

        /**
         * Constructs a new PointInTime with the time <code>time</code>
         * 
         * @param time
         *            The the for this PointInTime.
         */
        PointInTime(float time) {
            look = new TransformMatrixQuat[numObjects];
            usedRot = new BitSet(numObjects);
            usedTrans = new BitSet(numObjects);
            usedScale = new BitSet(numObjects);
            for (int i = 0; i < look.length; i++)
                look[i] = new TransformMatrixQuat();
            this.time = time;
        }

        /**
         * Sets the rotation for objIndex and sets usedRot to true for that
         * index
         * 
         * @param objIndex
         *            The object to take the rotation at this point in time.
         * @param rot
         *            The rotation to take.
         */
        void setRotation(int objIndex, Quaternion rot) {
            look[objIndex].setRotationQuaternion(rot);
            usedRot.set(objIndex);
        }

        /**
         * Sets the translation for objIndex and sets usedTrans to true for that
         * index
         * 
         * @param objIndex
         *            The object to take the translation at this point in time.
         * @param trans
         *            The translation to take.
         */
        void setTranslation(int objIndex, Vector3f trans) {
            look[objIndex].setTranslation(trans);
            usedTrans.set(objIndex);
        }

        /**
         * Sets the scale for objIndex and sets usedScale to true for that index
         * 
         * @param objIndex
         *            The object to take the scale at this point in time.
         * @param scale
         *            The scale to take.
         */
        void setScale(int objIndex, Vector3f scale) {
            look[objIndex].setScale(scale);
            usedScale.set(objIndex);
        }
    }
}