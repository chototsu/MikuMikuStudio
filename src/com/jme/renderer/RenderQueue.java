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

/**
 * This optional class supports queueing of rendering states that are drawn when
 * displayBackBuffer is called on the renderer.  All spatials in the opaque bucket
 * are rendered first in order closest to farthest.  Then all spatials in the opaque
 * bucket are rendered in order farthest to closest.  Finally all spatials in the
 * ortho bucket are rendered in ortho mode from highest to lowest Z order.
 * @author renanse
 * @author Jack Lindamood (javadoc + SpatialList only)
 *
 */
public class RenderQueue {

  /** List of all transparent object to render. */
  private SpatialList transparentBucket;
  /** List of all opaque object to render. */
  private SpatialList opaqueBucket;
  /** List of all ortho object to render. */
  private SpatialList orthoBucket;
  /** The renderer. */
  private Renderer renderer;

  /**
   * Creates a new render queue that will work with the given renderer.
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
    opaqueBucket = new SpatialList();
    transparentBucket = new SpatialList();
    orthoBucket = new SpatialList();
  }

  /**
   * Add a given Spatial to the RenderQueue
   * @param s Spatial to add.
   * @param bucket A bucket type to add to.
   * @see com.jme.renderer.Renderer#QUEUE_OPAQUE
   * @see com.jme.renderer.Renderer#QUEUE_ORTHO
   * @see com.jme.renderer.Renderer#QUEUE_TRANSPARENT
   */
  public void addToQueue(Spatial s, int bucket) {
    switch (bucket) {
      case Renderer.QUEUE_OPAQUE:
        opaqueBucket.add(s);
        break;
      case Renderer.QUEUE_TRANSPARENT:
        transparentBucket.add(s);
        break;
      case Renderer.QUEUE_ORTHO:
        orthoBucket.add(s);
        break;
    }
  }

  /**
   * Calculates the distance from a spatial to the camera.  Distance is a squared distance.
   * @param spat Spatial to distancize.
   * @return Distance from Spatial to camera.
   */
  private float distanceToCam(Spatial spat) {
    if (spat.queueDistance != Float.NEGATIVE_INFINITY) return spat.queueDistance;
    Camera cam = renderer.getCamera();
    if (Vector3f.isValidVector(cam.getLocation()) &&
        Vector3f.isValidVector(spat.getWorldTranslation()))
      spat.queueDistance = cam.getLocation().distanceSquared(spat.getWorldTranslation());
    return spat.queueDistance;
  }

  /**
   * Renders the opaque, transparent, and ortho buckets in that order.
   */
  public void renderBuckets() {
    renderOpaqueBucket();
    renderTransparentBucket();
    renderOrthoBucket();
  }


  /**
   * Renders the opaque buckets.  Those closest to the camera are rendered first.
   */
  private void renderOpaqueBucket() {
      opaqueBucket.sortCam(0,opaqueBucket.listSize-1);
      for (int i=0;i<opaqueBucket.listSize;i++){
          opaqueBucket.list[i].onDraw(renderer);
          opaqueBucket.list[i].queueDistance=Float.NEGATIVE_INFINITY;
      }
      opaqueBucket.clear();
  }

  /**
   * Renders the transparent buckets.  Those farthest from the camera are rendered first.
   */
  private void renderTransparentBucket() {
      transparentBucket.sortCam(0,transparentBucket.listSize-1);
      for (int i=transparentBucket.listSize-1;i>=0;i--){
          transparentBucket.list[i].onDraw(renderer);
          transparentBucket.list[i].queueDistance=Float.NEGATIVE_INFINITY;
      }
    transparentBucket.clear();
  }

  /**
   * Renders the ortho buckets.  Those will the highest ZOrder are rendered first.
   */
  private void renderOrthoBucket() {
    renderer.setOrtho();
      orthoBucket.sortOrtho(0,orthoBucket.listSize-1);
      for (int i=orthoBucket.listSize-1;i>=0;i--){
          orthoBucket.list[i].onDraw(renderer);
      }
      orthoBucket.clear();
    renderer.unsetOrtho();
  }

  /**
   * This class is a special function list of Spatial objects for render queueing.  It
   * supports quicksorting with median of 3.
   * @author Jack Lindamood
   */
  private class SpatialList{
      Spatial [] list;
      int listSize;
      private static final int DEFAULT_SIZE = 32;
      SpatialList(){
          listSize=0;
          list=new Spatial[DEFAULT_SIZE];
      }
      /**
       * Adds a spatial to the list.  Lise size is doubled if there is no room.
       * @param s The spatial to add.
       */
      void add(Spatial s){
          if (listSize==list.length){
              Spatial[] temp=new Spatial[listSize*2];
              System.arraycopy(list,0,temp,0,listSize);
              list=temp;
          }
          list[listSize++]=s;
      }
      /**
       * Resets list size to 0.
       */
      void clear(){
          listSize=0;
      }
      /**
       * Sorts spatial list acording to ZOrder from start to end index inclusive.
       * @param start Start index.
       * @param end End index.
       */
      void sortOrtho(int start,int end){
          if (end-start<5){
	        for (int i=start+1; i<=end; i++)
    		    for (int j=i; j>start && list[j].getZOrder()<list[j-1].getZOrder(); j--)
        		    swap(j, j-1);
        	    return;
          }
          int middle=(start+end)/2;
          med3ortho(start,middle,end);
          float partDist=list[middle].getZOrder();
          int i=start+1,j=end-1;
          while(true){
              while (list[i].getZOrder() < partDist)
                  i++;
              while (list[j].getZOrder() > partDist)
                  j--;
              if (i>=j) break;
              swap(i,j);
          }
          sortOrtho(start,i-1);
          sortOrtho(i+1,end);
      }

      /**
       * Sorts spatial list acording to distanceToCam from start to end index inclusive.
       * @param start Start index.
       * @param end End index.
       */
      void sortCam(int start,int end){
          if (end-start<5){
	        for (int i=start+1; i<=end; i++)
    		    for (int j=i; j>start && distanceToCam(list[j])<distanceToCam(list[j-1]); j--)
        		    swap(j, j-1);
        	    return;
          }
          int middle=(start+end)/2;
          med3Cam(start,middle,end);
          float partDist=distanceToCam(list[middle]);
          int i=start+1,j=end-1;
          while(true){
              while (distanceToCam(list[i]) < partDist)
                  i++;
              while (distanceToCam(list[j]) > partDist)
                  j--;
              if (i>=j) break;
              swap(i,j);
          }
          sortCam(start,i-1);
          sortCam(i+1,end);
      }

      /**
       * Sorts 3 elements acording to distanceToCam
       * @param start Index 1.
       * @param middle Index 2.
       * @param end Index 3.
       */
      private void med3Cam(int start, int middle, int end) {
          if (distanceToCam(list[start]) > distanceToCam(list[middle]))
              swap(start,middle);
          if (distanceToCam(list[start]) > distanceToCam(list[end]))
              swap(start,end);
          if (distanceToCam(list[middle]) > distanceToCam(list[end]))
              swap(middle,end);
      }

      /**
       * Sorts 3 elements acording to ZOrder
       * @param start Index 1.
       * @param middle Index 2.
       * @param end Index 3.
       */
      private void med3ortho(int start, int middle, int end) {
          if (list[start].getZOrder() > list[middle].getZOrder())
              swap(start,middle);
          if (list[start].getZOrder() > list[end].getZOrder())
              swap(start,end);
          if (list[middle].getZOrder() > list[end].getZOrder())
              swap(middle,end);
      }

      /**
       * Swaps index i and j.
       * @param i First index.
       * @param j Second index.
       */
      private void swap(int i, int j) {
          Spatial temp=list[i];
          list[i]=list[j];
          list[j]=temp;
      }
  }
}