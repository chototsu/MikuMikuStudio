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
package com.jme.scene;

import com.jme.math.Vector3f;

/**
 * <code>DistanceSwitchModel</code>
 * @author Mark Powell
 * @version $Id: DistanceSwitchModel.java,v 1.2 2004-03-13 18:07:56 mojomonkey Exp $
 */
public class DistanceSwitchModel implements SwitchModel {
	private float[] modelMin;
	private float[] modelMax;
	private float[] worldMin;
	private float[] worldMax;
	
	private int numChildren;
	
	private float worldScaleSquared;
	private Vector3f diff;
	
	public DistanceSwitchModel(int numChildren) {
		this.numChildren = numChildren;
		modelMin = new float[numChildren];
		modelMax = new float[numChildren];
		worldMin = new float[numChildren];
		worldMax = new float[numChildren];
	}
	
	public void setModelMinDistance (int index, float minDist) {
		
		modelMin[index] = minDist;
	}
	
	public void setModelMaxDistance (int index, float maxDist) {
		modelMax[index] = maxDist;
	}
	
	public void setModelDistance (int index, float minDist, float maxDist) {
		
		modelMin[index] = minDist;
		modelMax[index] = maxDist;
	}
	
	public void set(float value) {
		worldScaleSquared = value;
	}
	
	public void set(Vector3f value) {
		diff = value;
	}
	
	public int getSwitchChild(){
		for ( int i = 0; i < numChildren; i++) {
			float value = modelMin[i];
			worldMin[i] = worldScaleSquared*value*value;

			value = modelMax[i];
			worldMax[i] = worldScaleSquared*value*value;
       }

		// select the LOD child
		if ( numChildren > 0 ) {
			float fSqrDist = diff.lengthSquared();

            for ( int i = 0; i < numChildren; i++) {
				if ( worldMin[i] <= fSqrDist
						&&   fSqrDist < worldMax[i] ) {
                    return i;
				}
			}
		}
		
		return SwitchNode.SN_INVALID_CHILD;
	}
}
