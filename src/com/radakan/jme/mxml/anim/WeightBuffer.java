/*
 * Copyright (c) 2008, OgreLoader
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the Gibbon Entertainment nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY 'Gibbon Entertainment' "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL 'Gibbon Entertainment' BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.radakan.jme.mxml.anim;

import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.util.geom.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * WeightBuffer contains associations of vertexes to bones and their weights.
 * The WeightBuffer can be sent to a shader or processed on the CPU 
 * to do skinning.
 */
public final class WeightBuffer {

    /**
     * Each 4 bytes in the boneIndex buffer are assigned to a vertex.
     * 
     */
    final ByteBuffer indexes;
    
    /**
     * The weight of each bone specified in the index buffer
     */
    final FloatBuffer weights;
    
    /**
     * The maximum number of weighted bones used by the vertices
     * Can be 1-4. The indexes and weights still have 4 components per vertex,
     * regardless of this value.
     */
    int maxWeightsPerVert = 0;
    
    public WeightBuffer(int vertexCount){
        indexes = BufferUtils.createByteBuffer(vertexCount * 4);
        weights = BufferUtils.createFloatBuffer(vertexCount * 4);
    }
    
    public WeightBuffer(ByteBuffer indexes, FloatBuffer weights){
        this.indexes = indexes;
        this.weights = weights;
    }
      
    public void sendToShader(GLSLShaderObjectsState shader){
        indexes.rewind();
        shader.setAttributePointer("indexes", 4, false, true, 0, indexes);
        
        if (maxWeightsPerVert > 1){
            weights.rewind();
            shader.setAttributePointer("weights", 4, true, 0, weights);
        }
    }
    
    /**
     * Normalizes weights if needed and finds largest amount of weights used
     * for all vertices in the buffer.
     */
    public void initializeWeights(){
        int nVerts = weights.capacity() / 4;
        weights.rewind();
        for (int v = 0; v < nVerts; v++){
            float w0 = weights.get(),
                  w1 = weights.get(),
                  w2 = weights.get(),
                  w3 = weights.get();
            
            if (w3 > 0.01f){
                maxWeightsPerVert = Math.max(maxWeightsPerVert, 4);
            }else if (w2 > 0.01f){
                maxWeightsPerVert = Math.max(maxWeightsPerVert, 3);
            }else if (w1 > 0.01f){
                maxWeightsPerVert = Math.max(maxWeightsPerVert, 2);
            }else if (w0 > 0.01f){
                maxWeightsPerVert = Math.max(maxWeightsPerVert, 1);
            }
            
            float sum = w0 + w1 + w2 + w3;
            if (sum != 1f){
                weights.position(weights.position()-4);
                weights.put(w0 / sum);
                weights.put(w1 / sum);
                weights.put(w2 / sum);
                weights.put(w3 / sum);
            }
        }
        weights.rewind();
    }
    
}
