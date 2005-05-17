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
package com.jmex.model.animation;

import com.jme.scene.TriMesh;
import com.jme.scene.Controller;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.util.LoggingSystem;
import com.jmex.model.EmptyTriMesh;
import com.jmex.model.ModelCloneCreator;

import java.util.ArrayList;
import java.util.logging.Level;
import java.io.*;

/**
 * Started Date: Jun 12, 2004 <br>
 * <br>
 * 
 * Class can do linear interpolation of a TriMesh between units of time. Similar
 * to <code>VertexKeyframeController</code> but interpolates float units of
 * time instead of integer keyframes. <br>
 * <br>
 * Controller.setSpeed(float) sets a speed relative to the defined speed. For
 * example, the default is 1. A speed of 2 would run twice as fast and a speed
 * of .5 would run half as fast <br>
 * <br>
 * Controller.setMinTime(float) and Controller.setMaxTime(float) both define the
 * bounds that KeyframeController should follow. It is the programmer's
 * responsiblity to make sure that the MinTime and MaxTime are within the span
 * of the defined <code>setKeyframe</code><br>
 * <br>
 * Controller functions RepeatType and isActive are both defined in their
 * default way for KeyframeController <br>
 * <br>
 * When this controller is saved/loaded to XML format, it assumes that the mesh
 * it morphs is the TriMesh it belongs to, so it is recomended to only attach
 * this controller to the TriMesh it animates.
 * 
 * @author Jack Lindamood
 * Parts by kevglass
 * @version $Id: KeyframeController.java,v 1.2 2005-05-17 04:19:05 Mojomonkey Exp $
 */
public class KeyframeController extends Controller {

    private static final long serialVersionUID = 1L;

    /**
     * An array of <code>PointInTime</code> s that defines the animation
     */
    transient public ArrayList keyframes;

    /**
     * A special array used with SmoothTransform to store temporary smooth
     * transforms
     */
    transient private ArrayList prevKeyframes;

    /**
     * The mesh that is actually morphed
     */
    private TriMesh morphMesh;

    /**
     * The current time in the animation
     */
    transient private float curTime;

    /**
     * The current frame of the animation
     */
    transient private int curFrame;

    /**
     * The frame of animation we're heading towards
     */
    transient private int nextFrame;
    
    /**
     * The PointInTime before <code>curTime</code>
     */
    transient private PointInTime before;

    /**
     * The PointInTime after <code>curTime</code>
     */
    transient private PointInTime after;

    /**
     * If true, the animation is moving forward, if false the animation is
     * moving backwards
     */
    transient private boolean movingForward;

    /**
     * Used with SmoothTransform to signal it is doing a smooth transform
     */
    transient private boolean isSmooth;

    /**
     * Used with SmoothTransform to hold the new beinging and ending time once
     * the transform is complete
     */
    transient private float tempNewBeginTime;

    transient private float tempNewEndTime;

    /** If true, the model's bounding volume will update every frame. */
    private boolean updatePerFrame;

    /**
     * Default constructor. Speed is 1, MinTime is 0 MaxTime is 0. Both MinTime
     * and MaxTime are automatically adjusted by setKeyframe if the setKeyframe
     * time is less than MinTime or greater than MaxTime. Default RepeatType is
     * RT_WRAP.
     */
    public KeyframeController() {
        this.setSpeed(1);
        keyframes = new ArrayList();
        curFrame = 0;
        this.setRepeatType(Controller.RT_WRAP);
        movingForward = true;
        this.setMinTime(0);
        this.setMaxTime(0);
        updatePerFrame = true;
    }

    /**
     * Sets the Mesh that will be physically changed by this KeyframeController
     * 
     * @param morph
     *            The new mesh to morph
     */
    public void setMorphingMesh(TriMesh morph) {
        morphMesh = morph;
        keyframes.clear();
        keyframes.add(new PointInTime(0, null));
    }

    public void shallowSetMorphMesh(TriMesh morph) {
        morphMesh = morph;
    }

    /**
     * Tells the controller to change its morphMesh to <code>shape</code> at
     * <code>time</code> seconds. Time must be >=0 and shape must be non-null
     * and shape must have the same number of vertexes as the current shape. If
     * not, then nothing happens. It is also required that
     * <code>setMorphingMesh(TriMesh)</code> is called before
     * <code>setKeyframe</code>. It is assumed that shape.indices ==
     * morphMesh.indices, otherwise morphing may look funny
     * 
     * @param time
     *            The time for the change
     * @param shape
     *            The new shape at that time
     */
    public void setKeyframe(float time, TriMesh shape) {
        if (morphMesh == null || time < 0
                || shape.getVertices().length != morphMesh.getVertices().length)
                return;
        for (int i = 0; i < keyframes.size(); i++) {
            PointInTime lookingTime = (PointInTime) keyframes.get(i);
            if (lookingTime.time == time) {
                lookingTime.newShape = shape;
                return;
            }
            if (lookingTime.time > time) {
                keyframes.add(i, new PointInTime(time, shape));
                return;
            }
        }
        keyframes.add(new PointInTime(time, shape));
        if (time > this.getMaxTime()) this.setMaxTime(time);
        if (time < this.getMinTime()) this.setMinTime(time);
    }

    /**
     * This function will do a smooth translation between a keframe's current
     * look, to the look directly at newTimeToReach. It takes translationLen
     * time (in sec) to do that translation, and once translated will animate
     * like normal between newBeginTime and newEndTime <br>
     * <br>
     * This would be usefull for example when a figure stops running and tries
     * to raise an arm. Instead of "teleporting" to the raise-arm animation
     * begining, a smooth translation can occur.
     * 
     * @param newTimeToReach
     *            The time to reach.
     * @param translationLen
     *            How long it takes
     * @param newBeginTime
     *            The new cycle begining time
     * @param newEndTime
     *            The new cycle ending time.
     */
    public void setSmoothTranslation(float newTimeToReach,
            float translationLen, float newBeginTime, float newEndTime) {
        if (!isActive() || isSmooth) return;
        if (newBeginTime < 0
                || newBeginTime > ((PointInTime) keyframes
                        .get(keyframes.size() - 1)).time) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Attempt to set invalid begintime:" + newBeginTime);
            return;
        }
        if (newEndTime < 0
                || newEndTime > ((PointInTime) keyframes
                        .get(keyframes.size() - 1)).time) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Attempt to set invalid endtime:" + newEndTime);
            return;
        }
        EmptyTriMesh begin = null, end = null;
        if (prevKeyframes == null) {
            prevKeyframes = new ArrayList();
            begin = new EmptyTriMesh();
            end = new EmptyTriMesh();
        } else {
            begin = (EmptyTriMesh) ((PointInTime) prevKeyframes.get(0)).newShape;
            end = (EmptyTriMesh) ((PointInTime) prevKeyframes.get(1)).newShape;
            prevKeyframes.clear();
        }

        getCurrent(begin);

        curTime = newTimeToReach;
        curFrame = 0;
        setMinTime(0);
        setMaxTime(((PointInTime) keyframes.get(keyframes.size() - 1)).time);
        update(0);
        getCurrent(end);

        swapKeyframeSets();
        curTime = 0;
        curFrame = 0;
        setMinTime(0);
        setMaxTime(translationLen);
        setKeyframe(0, begin);
        setKeyframe(translationLen, end);
        isSmooth = true;
        tempNewBeginTime = newBeginTime;
        tempNewEndTime = newEndTime;
    }

    /**
     * Swaps prevKeyframes and keyframes
     */
    private void swapKeyframeSets() {
        ArrayList temp = keyframes;
        keyframes = prevKeyframes;
        prevKeyframes = temp;
    }

    /**
     * Sets the new animation boundaries for this controller. This will start at
     * newBeginTime and proceed in the direction of newEndTime (either forwards
     * or backwards). If both are the same, then the animation is set to their
     * time and turned off, otherwise the animation is turned on to start the
     * animation acording to the repeat type. If either BeginTime or EndTime are
     * invalid times (less than 0 or greater than the maximum set keyframe time)
     * then a warning is set and nothing happens. <br>
     * It is suggested that this function be called if new animation boundaries
     * need to be set, instead of setMinTime and setMaxTime directly.
     * 
     * @param newBeginTime
     *            The starting time
     * @param newEndTime
     *            The ending time
     */
    public void setNewAnimationTimes(float newBeginTime, float newEndTime) {
        if (isSmooth) return;
        if (newBeginTime < 0
                || newBeginTime > ((PointInTime) keyframes
                        .get(keyframes.size() - 1)).time) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Attempt to set invalid begintime:" + newBeginTime);
            return;
        }
        if (newEndTime < 0
                || newEndTime > ((PointInTime) keyframes
                        .get(keyframes.size() - 1)).time) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Attempt to set invalid endtime:" + newEndTime);
            return;
        }
        setMinTime(newBeginTime);
        setMaxTime(newEndTime);
        setActive(true);
        if (newBeginTime <= newEndTime) { // Moving forward
            movingForward = true;
            curTime = newBeginTime;
            if (newBeginTime == newEndTime) {
                update(0);
                setActive(false);
            }
        } else { // Moving backwards
            movingForward = false;
            curTime = newEndTime;
        }
    }

    /**
     * Saves whatever the current morphMesh looks like into the dataCopy
     * 
     * @param dataCopy
     *            The copy to save the current mesh into
     */
    private void getCurrent(TriMesh dataCopy) {
        if (morphMesh.getColors() != null) {
            ColorRGBA[] newColors = null;
            if (dataCopy.getColors().length != morphMesh.getColors().length)
                newColors = new ColorRGBA[morphMesh.getColors().length];
            else
                newColors = dataCopy.getColors();
            for (int i = 0; i < newColors.length; i++)
                newColors[i] = new ColorRGBA(morphMesh.getColors()[i]);
            dataCopy.setColors(newColors);
        }
        if (morphMesh.getVertices() != null) {
            Vector3f[] newVerts = null;
            if (dataCopy.getVertices() == null
                    || dataCopy.getVertices().length != morphMesh.getVertices().length)
                newVerts = new Vector3f[morphMesh.getVertices().length];
            else
                newVerts = dataCopy.getVertices();
            for (int i = 0; i < newVerts.length; i++)
                newVerts[i] = new Vector3f(morphMesh.getVertices()[i]);
            dataCopy.setVertices(newVerts);
        }
        if (morphMesh.getNormals() != null) {
            Vector3f[] newNorms = null;
            if (dataCopy.getNormals() == null
                    || dataCopy.getNormals().length != morphMesh.getNormals().length)
                newNorms = new Vector3f[morphMesh.getNormals().length];
            else
                newNorms = dataCopy.getNormals();
            for (int i = 0; i < newNorms.length; i++)
                newNorms[i] = new Vector3f(morphMesh.getNormals()[i]);
            dataCopy.setNormals(newNorms);
        }
        if (morphMesh.getIndices() != null) {
            int[] newInds = null;
            if (dataCopy.getIndices() == null
                    || dataCopy.getIndices().length != morphMesh.getIndices().length)
                newInds = new int[morphMesh.getIndices().length];
            else
                newInds = dataCopy.getIndices();
            System.arraycopy(morphMesh.getIndices(), 0, newInds, 0,
                    newInds.length);
            dataCopy.setIndices(newInds);
        }
        if (morphMesh.getTextures() != null) {
            Vector2f[] newTex = null;
            if (dataCopy.getTextures() == null
                    || dataCopy.getNormals().length != morphMesh.getTextures().length)
                newTex = new Vector2f[morphMesh.getTextures().length];
            else
                newTex = dataCopy.getTextures();
            for (int i = 0; i < newTex.length; i++)
                newTex[i] = new Vector2f(morphMesh.getTextures()[i]);
            dataCopy.setTextures(newTex);
        }
    }

    /**
     * As defined in Controller
     * 
     * @param time
     *            as defined in Controller
     */
    public void update(float time) {
        if (!this.isActive()) return;
        if (easyQuit()) return;
        if (movingForward)
            curTime += time * this.getSpeed();
        else
            curTime -= time * this.getSpeed();
        
        findFrame();
        before = ((PointInTime) keyframes.get(curFrame));
        // Change this bit so the next frame we're heading towards isn't always going
        // to be one frame ahead since now we coule be animating from the last to first
        // frames.
        //after = ((PointInTime) keyframes.get(curFrame + 1));
        after = ((PointInTime) keyframes.get(nextFrame));
        
        float delta = (curTime - before.time) / (after.time - before.time);
        
        // If we doing that wrapping bit then delta should be caculated based 
        // on the time before the start of the animation we are. 
        if (nextFrame < curFrame) {
        	delta = 1 - (getMinTime()-curTime);
        }
        
        TriMesh oldShape = before.newShape;
        TriMesh newShape = after.newShape;
        
        Vector3f[] verts = morphMesh.getVertices();
        Vector3f[] norms = morphMesh.getNormals();
        Vector2f[] texts = morphMesh.getTextures();
        ColorRGBA[] colors = morphMesh.getColors();

        Vector3f[] oldverts = oldShape.getVertices();
        Vector3f[] oldnorms = oldShape.getNormals();
        Vector2f[] oldtexts = oldShape.getTextures();
        ColorRGBA[] oldcolors = oldShape.getColors();

        Vector3f[] newverts = newShape.getVertices();
        Vector3f[] newnorms = newShape.getNormals();
        Vector2f[] newtexts = newShape.getTextures();
        ColorRGBA[] newcolors = newShape.getColors();
        if (verts == null || oldverts == null || newverts == null) return;
        boolean hitnorms = false, hittexts = false, hitcolors = false;
        for (int i = 0; i < verts.length; i++) {
            verts[i].interpolate(oldverts[i], newverts[i], delta);
            //            morphMesh.setVertex(i,verts[i]);
            if (norms != null && oldnorms != null && newnorms != null) {
                norms[i].interpolate(oldnorms[i], newnorms[i], delta);
                hitnorms = true;
                //                morphMesh.setNormal(i,norms[i]);
            }
            if (texts != null && oldtexts != null && newtexts != null) {
                texts[i].interpolate(oldtexts[i], newtexts[i], delta);
                hittexts = true;
                //                morphMesh.setTexture(i,texts[i]);
            }
            if (colors != null && oldcolors != null && newcolors != null) {
                colors[i].interpolate(oldcolors[i], newcolors[i], delta);
                hitcolors = true;
                //                morphMesh.setColor(i,colors[i]);
            }
        }
        morphMesh.updateVertexBuffer();
        if (hitnorms) morphMesh.updateNormalBuffer();
        if (hittexts) morphMesh.updateTextureBuffer();
        if (hitcolors) morphMesh.updateColorBuffer();
        if (updatePerFrame) morphMesh.updateModelBound();
        //          Both methods seem equivalent in speed
        // Renanse says : depends on machine, update***Buffer will have less of
        // a hit on some machines.
    }

    /**
     * If both min and max time are equal and the model is already updated, then
     * it's an easy quit, or if it's on CLAMP and I've exceeded my time it's
     * also an easy quit.
     * 
     * @return true if update doesn't need to be called, false otherwise
     */
    private boolean easyQuit() {
        if (getMaxTime() == getMinTime() && curTime != getMinTime())
            return true;
        else if (getRepeatType() == RT_CLAMP
                && (curTime > getMaxTime() || curTime < getMinTime()))
            return true;
        else if (keyframes.size() < 2) return true;
        return false;
    }

    /**
     * If true, the model's bounding volume will be updated every frame. If
     * false, it will not.
     * 
     * @param update
     *            The new update model volume per frame value.
     */
    public void setModelUpdate(boolean update) {
        updatePerFrame = update;
    }

    /**
     * Returns true if the model's bounding volume is being updated every frame.
     * 
     * @return True if bounding volume is updating.
     */
    public boolean getModelUpdate() {
        return updatePerFrame;
    }

    /**
     * This is used by update(float). It calculates PointInTime
     * <code>before</code> and <code>after</code> as well as makes
     * adjustments on what to do when <code>curTime</code> is beyond the
     * MinTime and MaxTime bounds
     */
    private void findFrame() {
    	// If we're in our special wrapping case then just ignore changing
    	// frames. Once we get back into the actual series we'll revert back
    	// to the normal process
    	if ((curTime < getMinTime()) && (nextFrame < curFrame)) {
    		return;
    	}
    	
    	// Update the rest to maintain our new nextFrame marker as one infront
    	// of the curFrame in all cases. The wrap case is where the real work 
    	// is done.
        if (curTime > this.getMaxTime()) {
            if (isSmooth) {
                swapKeyframeSets();
                isSmooth = false;
                curTime = tempNewBeginTime;
                curFrame = 0;
                nextFrame = 1;
                setNewAnimationTimes(tempNewBeginTime, tempNewEndTime);
                return;
            }
            if (this.getRepeatType() == Controller.RT_WRAP) {
            	float delta = 1;
                curTime = this.getMinTime() - delta;
                curFrame++;
                
                for (nextFrame = 0; nextFrame < keyframes.size() - 1; nextFrame++) {
                    if (getMinTime() <= ((PointInTime) keyframes.get(nextFrame)).time)
                            break;
                }
                return;
            } else if (this.getRepeatType() == Controller.RT_CLAMP) {
                return;
            } else { // Then assume it's RT_CYCLE
                movingForward = false;
                curTime = this.getMaxTime();
            }
        } else if (curTime < this.getMinTime()) {
            if (this.getRepeatType() == Controller.RT_WRAP) {
                curTime = this.getMaxTime();
                curFrame = 0;
            } else if (this.getRepeatType() == Controller.RT_CLAMP) {
                return;
            } else { // Then assume it's RT_CYCLE
                movingForward = true;
                curTime = this.getMinTime();
            }
        }

    	nextFrame = curFrame+1;
    	
        if (curTime > ((PointInTime) keyframes.get(curFrame)).time) {
            if (curTime < ((PointInTime) keyframes.get(curFrame + 1)).time) {
            	nextFrame = curFrame+1;
                return;
            }
            else {
                for (; curFrame < keyframes.size() - 1; curFrame++) {
                    if (curTime <= ((PointInTime) keyframes.get(curFrame + 1)).time) {
                    		nextFrame = curFrame+1;
                            return;
                    }
                }
                // This -should- be unreachable because of the above
                curTime = this.getMinTime();
                curFrame = 0;
            	nextFrame = curFrame+1;
                return;
            }
        } else {
            for (; curFrame >= 0; curFrame--) {
                if (curTime >= ((PointInTime) keyframes.get(curFrame)).time) {
                	nextFrame = curFrame+1;
                	return; 
                }
            }
            // This should be unreachable because curTime>=0 and
            // keyframes[0].time=0;
            curFrame = 0;
        	nextFrame = curFrame+1;
            return;
        }
    }

    /**
     * This class defines a point in time that states <code>morphShape</code>
     * should look like <code>newShape</code> at <code>time</code> seconds
     */
    public static class PointInTime implements Serializable {

        private static final long serialVersionUID = 1L;

        public TriMesh newShape;

        public float time;

        public PointInTime(float time, TriMesh shape) {
            this.time = time;
            this.newShape = shape;
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        keyframes = (ArrayList) in.readObject();
        movingForward = true;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (isSmooth)
            out.writeObject(prevKeyframes);
        else
            out.writeObject(keyframes);
    }

    public TriMesh getMorphMesh() {
        return morphMesh;
    }
    
    /**
     * This function should be overridden by any Spatial objects that want their
     * Controller cloned by a CloneCreator. It stores into "store" the
     * properties of this Controller.
     *
     * @param store
     *            The Controller to store properties into. If null, null is
     *            returned.
     * @param properties
     *            The CloneCreator controlling how things should be copied into
     *            the store Controller.
     * @return The store controller, after a copy.
     */
    public Controller putClone(Controller store, ModelCloneCreator properties) {
       KeyframeController toStore;
        if (store == null) {
           toStore = new KeyframeController();
        } else {
           toStore = (KeyframeController) store;
        }
       
        super.putClone(toStore,properties);
       
        toStore.keyframes = new ArrayList(keyframes);
        toStore.morphMesh = morphMesh;
        if (prevKeyframes != null) {
           toStore.prevKeyframes = new ArrayList(prevKeyframes);
        }
       
        properties.queueKeyframeController(toStore);
       
        return toStore;
    } 

}