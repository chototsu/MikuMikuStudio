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
package com.jme.renderer;

import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.Geometry;
import com.jme.system.JmeException;

import java.util.Comparator;
import java.util.Arrays;

/**
 * This optional class supports queueing of rendering states that are drawn when
 * displayBackBuffer is called on the renderer. All spatials in the opaque
 * bucket are rendered first in order closest to farthest. Then all spatials in
 * the opaque bucket are rendered in order farthest to closest. Finally all
 * spatials in the ortho bucket are rendered in ortho mode from highest to
 * lowest Z order. As a user, you shouldn't need to use this class directly. All
 * you'll need to do is call Spatial.setRenderQueueMode .
 * 
 * @author renanse
 * @author Jack Lindamood (javadoc + SpatialList only)
 * @see com.jme.scene.Spatial#setRenderQueueMode(int)
 *  
 */
public class RenderQueue {

    /** List of all transparent object to render. */
    private SpatialList transparentBucket;

    /** List of all opaque object to render. */
    private SpatialList opaqueBucket;

    /** List of all ortho object to render. */
    private SpatialList orthoBucket;

    /** List of all clone object to render. */
    private SpatialList cloneBucket;

    /** The renderer. */
    private Renderer renderer;

    /**
     * Creates a new render queue that will work with the given renderer.
     * 
     * @param r
     */
    public RenderQueue(Renderer r) {
        this.renderer = r;
        setupBuckets();
    }

    /**
     * Creates the buckets needed.
     */
    private void setupBuckets() {
        opaqueBucket = new SpatialList(new OpaqueComp());
        transparentBucket = new SpatialList(new TransparentComp());
        orthoBucket = new SpatialList(new OrthoComp());
        cloneBucket = new SpatialList(new CloneComp());
    }

    /**
     * Add a given Spatial to the RenderQueue. This is how jME adds data tothe
     * render queue. As a user, in 99% of casees you'll want to use the function
     * Spatail.setRenderQueueMode and let jME add the item to the queue itself.
     * 
     * @param s
     *            Spatial to add.
     * @param bucket
     *            A bucket type to add to.
     * @see com.jme.scene.Spatial#setRenderQueueMode(int)
     * @see com.jme.renderer.Renderer#QUEUE_OPAQUE
     * @see com.jme.renderer.Renderer#QUEUE_ORTHO
     * @see com.jme.renderer.Renderer#QUEUE_TRANSPARENT
     */
    public void addToQueue(Spatial s, int bucket) {
        switch (bucket) {
        case Renderer.QUEUE_OPAQUE:
            if (s instanceof Geometry && ((Geometry) s).getCloneID() != -1) {
                cloneBucket.add(s);
            } else {
                opaqueBucket.add(s);
            }
            break;
        case Renderer.QUEUE_TRANSPARENT:
            transparentBucket.add(s);
            break;
        case Renderer.QUEUE_ORTHO:
            orthoBucket.add(s);
            break;
        default:
            throw new JmeException("Illeagle Render queue order of " + bucket);
        }
    }

    /**
     * Calculates the distance from a spatial to the camera. Distance is a
     * squared distance.
     * 
     * @param spat
     *            Spatial to distancize.
     * @return Distance from Spatial to camera.
     */
    private float distanceToCam(Spatial spat) {
        if (spat.queueDistance != Float.NEGATIVE_INFINITY)
                return spat.queueDistance;
        Camera cam = renderer.getCamera();
        spat.queueDistance = 0;
        if (Vector3f.isValidVector(cam.getLocation())
                && Vector3f.isValidVector(spat.getWorldTranslation()))
                spat.queueDistance = cam.getLocation().distanceSquared(
                        spat.getWorldTranslation());
        return spat.queueDistance;
    }

    /**
     * Renders the opaque, clone, transparent, and ortho buckets in that order.
     */
    public void renderBuckets() {
        renderOpaqueBucket();
        renderCloneBucket();
        renderTransparentBucket();
        renderOrthoBucket();
    }

    /**
     * Draw Clone buckets. Those with the smallest Z value are drawn first.
     */
    private void renderCloneBucket() {
        cloneBucket.sort();
        for (int i = 0; i < cloneBucket.listSize; i++) {
            cloneBucket.list[i].draw(renderer);
        }
        cloneBucket.clear();
    }

    /**
     * Renders the opaque buckets. Those closest to the camera are rendered
     * first.
     */
    private void renderOpaqueBucket() {
        opaqueBucket.sort();
        for (int i = 0; i < opaqueBucket.listSize; i++) {
            opaqueBucket.list[i].draw(renderer);
            opaqueBucket.list[i].queueDistance = Float.NEGATIVE_INFINITY;
        }
        opaqueBucket.clear();
    }

    /**
     * Renders the transparent buckets. Those farthest from the camera are
     * rendered first.
     */
    private void renderTransparentBucket() {
        transparentBucket.sort();
        for (int i = 0; i < transparentBucket.listSize; i++) {
            transparentBucket.list[i].draw(renderer);
            transparentBucket.list[i].queueDistance = Float.NEGATIVE_INFINITY;
        }
        transparentBucket.clear();
    }

    /**
     * Renders the ortho buckets. Those will the highest ZOrder are rendered
     * first.
     */
    private void renderOrthoBucket() {
        renderer.setOrtho();
        orthoBucket.sort();
        for (int i = 0; i < orthoBucket.listSize; i++) {
            orthoBucket.list[i].draw(renderer);
        }
        orthoBucket.clear();
        renderer.unsetOrtho();
    }

    /**
     * This class is a special function list of Spatial objects for render
     * queueing. It supports quicksorting with median of 3.
     * 
     * @author Jack Lindamood
     */
    private class SpatialList {

        Spatial[] list;

        int listSize;

        private static final int DEFAULT_SIZE = 32;

        private Comparator c;

        SpatialList(Comparator c) {
            listSize = 0;
            list = new Spatial[DEFAULT_SIZE];
            this.c = c;
        }

        /**
         * Adds a spatial to the list. Lise size is doubled if there is no room.
         * 
         * @param s
         *            The spatial to add.
         */
        void add(Spatial s) {
            if (listSize == list.length) {
                Spatial[] temp = new Spatial[listSize * 2];
                System.arraycopy(list, 0, temp, 0, listSize);
                list = temp;
            }
            list[listSize++] = s;
        }

        /**
         * Resets list size to 0.
         */
        void clear() {
            for (int i = 0; i < listSize; i++)
                list[i] = null;
            listSize = 0;
        }

        /**
         * Sorts the elements in the list acording to their Comparator.
         */
        void sort() {
            if (listSize > 1) Arrays.sort(list, 0, listSize, c);
        }
    }

    private class OpaqueComp implements Comparator {

        public int compare(Object o1, Object o2) {
            float d1 = distanceToCam((Spatial) o1);
            float d2 = distanceToCam((Spatial) o2);
            if (d1 <= d2)
                return -1;
            else
                return 1;
        }
    }

    private class TransparentComp implements Comparator {

        public int compare(Object o1, Object o2) {
            float d1 = distanceToCam((Spatial) o1);
            float d2 = distanceToCam((Spatial) o2);
            if (d1 <= d2)
                return 1;
            else
                return -1;
        }
    }

    private class OrthoComp implements Comparator {

        public int compare(Object o1, Object o2) {
            Spatial s1 = (Spatial) o1;
            Spatial s2 = (Spatial) o2;
            if (s1.getZOrder() < s2.getZOrder())
                return 1;
            else
                return -1;
        }
    }

    private class CloneComp implements Comparator {

        public int compare(Object o1, Object o2) {
            Geometry s1 = (Geometry) o1;
            Geometry s2 = (Geometry) o2;
            if (s1.getCloneID() < s2.getCloneID())
                return 1;
            else
                return -1;
        }
    }
}