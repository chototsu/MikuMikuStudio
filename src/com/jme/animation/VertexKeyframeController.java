/* 
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding 
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

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.TriMesh;

/**
 * <code>VertexKeyframeController</code>
 * @author Mark Powell
 * @version $Id: VertexKeyframeController.java,v 1.1 2004-02-06 21:14:47 mojomonkey Exp $
 */
public class VertexKeyframeController extends Controller {
	TriMesh[] keyframes;
	TriMesh displayedMesh;
	float currentTime;
	int minFrame = 0;
	int maxFrame;
	int currentFrame;
	int nextFrame = 1;
	int cycleModifier = 1;
	
	
	
	/** <code>update</code> 
	 * @param time
	 * @see com.jme.scene.Controller#update(float)
	 */
	public void update(float time) {
		if(getRepeatType() == Controller.RT_CLAMP) {
			currentTime += time;
			if(currentTime >= 1) {
				currentFrame++;
				nextFrame++;
				if(currentFrame >= maxFrame) {
					currentFrame = maxFrame;
				}
				
				if(nextFrame >= maxFrame) {
					nextFrame = maxFrame;
				}
				
				currentTime = 0;
			}
			
		} else if(getRepeatType()== Controller.RT_WRAP) {
			currentTime += time;
			if(currentTime >= 1) {
				currentFrame++;
				nextFrame++;
				if(currentFrame >= maxFrame) {
					currentFrame = minFrame;
				}
				
				if(nextFrame >= maxFrame) {
					nextFrame = minFrame;
				}
				
				
				currentTime = 0;
			}
		} else if(getRepeatType() == Controller.RT_CYCLE) {
			currentTime += time;
			if(currentTime >= 1) {
				
				currentFrame += cycleModifier;
				nextFrame += cycleModifier;
				if(currentFrame >= maxFrame) {
					currentFrame = maxFrame;
					cycleModifier = -1;
				} else if(currentFrame <= minFrame) {
					currentFrame = minFrame;
					cycleModifier = 1;
				}
				
				if(nextFrame >= maxFrame) {
					nextFrame = maxFrame - 2;
					cycleModifier = -1;
				} else if (currentFrame <= minFrame) {
					nextFrame = minFrame + 1;
					cycleModifier = 1;
				}
				
				
				currentTime = 0;
			}
		}
		
		
		Vector3f[] verts = displayedMesh.getVertices();
		Vector3f[] norms = displayedMesh.getNormals();
		for(int i = 0; i < verts.length; i++) {
			
			verts[i].x = keyframes[currentFrame].getVertices()[i].x + currentTime * (keyframes[nextFrame].getVertices()[i].x - keyframes[currentFrame].getVertices()[i].x);
			verts[i].y = keyframes[currentFrame].getVertices()[i].y + currentTime * (keyframes[nextFrame].getVertices()[i].y - keyframes[currentFrame].getVertices()[i].y);
			verts[i].z = keyframes[currentFrame].getVertices()[i].z + currentTime * (keyframes[nextFrame].getVertices()[i].z - keyframes[currentFrame].getVertices()[i].z);
		
			norms[i].x = keyframes[currentFrame].getNormals()[i].x + currentTime * (keyframes[nextFrame].getNormals()[i].x - keyframes[currentFrame].getNormals()[i].x);
			norms[i].y = keyframes[currentFrame].getNormals()[i].y + currentTime * (keyframes[nextFrame].getNormals()[i].y - keyframes[currentFrame].getNormals()[i].y);
			norms[i].z = keyframes[currentFrame].getNormals()[i].z + currentTime * (keyframes[nextFrame].getNormals()[i].z - keyframes[currentFrame].getNormals()[i].z);
			
		}
		displayedMesh.updateVertexBuffer();
		displayedMesh.updateNormalBuffer();
	}
	
	/**
	 * @return Returns the displayedMesh.
	 */
	public TriMesh getDisplayedMesh() {
		return displayedMesh;
	}

	/**
	 * @param displayedMesh The displayedMesh to set.
	 */
	public void setDisplayedMesh(TriMesh displayedMesh) {
		this.displayedMesh = displayedMesh;
	}

	/**
	 * @return Returns the keyframes.
	 */
	public TriMesh[] getKeyframes() {
		return keyframes;
		
	}

	/**
	 * @param keyframes The keyframes to set.
	 */
	public void setKeyframes(TriMesh[] keyframes) {
		this.keyframes = keyframes;
		maxFrame = keyframes.length-1;
		
	}
	
	

	/**
	 * @return Returns the maxFrame.
	 */
	public int getMaxFrame() {
		return maxFrame;
	}

	/**
	 * @param maxFrame The maxFrame to set.
	 */
	public void setMaxFrame(int maxFrame) {
		this.maxFrame = maxFrame;
	}

	/**
	 * @return Returns the minFrame.
	 */
	public int getMinFrame() {
		return minFrame;
	}

	/**
	 * @param minFrame The minFrame to set.
	 */
	public void setMinFrame(int minFrame) {
		this.minFrame = minFrame;
	}

}
