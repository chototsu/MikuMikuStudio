/*
 * Copyright (c) 2003-2008 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.util.stat.graph;

import java.nio.FloatBuffer;

import com.jme.image.Texture2D;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.system.DisplaySystem;
import com.jme.util.stat.StatCollector;

public class GraphFactory {

    public static LineGrapher makeLineGraph(int width, int height, Quad q) {
        LineGrapher grapher = new LineGrapher(width, height);
        grapher.setThreshold(1);
        StatCollector.addStatListener(grapher);
        Texture2D graphTex = new Texture2D();
        graphTex.setMinificationFilter(MinificationFilter.NearestNeighborNoMipMaps);
        graphTex.setMagnificationFilter(MagnificationFilter.Bilinear);
        grapher.setTexture(graphTex);
        
        q.setTextureCombineMode(Spatial.TextureCombineMode.Replace);  
        q.setLightCombineMode(Spatial.LightCombineMode.Off);
        q.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        q.setZOrder(-1);
        
        float dW = (float)width / grapher.texRenderer.getWidth();
        float dH = (float)height / grapher.texRenderer.getHeight();
        FloatBuffer tbuf = q.getTextureCoords(0).coords;
        tbuf.clear();
        tbuf.put(0).put(dH);
        tbuf.put(0).put(0);
        tbuf.put(dW).put(0);
        tbuf.put(dW).put(dH);
        tbuf.rewind();

        q.setDefaultColor(new ColorRGBA(1, 1, 1, .70f));

        TextureState texState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        texState.setTexture(graphTex);
        q.setRenderState(texState);

        BlendState blend = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        blend.setBlendEnabled(true);
        blend.setSourceFunction(SourceFunction.SourceAlpha);
        blend.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
        q.setRenderState(blend);

        return grapher;
    }

    public static TimedAreaGrapher makeTimedGraph(int width, int height, Quad q) {
        TimedAreaGrapher grapher = new TimedAreaGrapher(width, height);
        grapher.setThreshold(1);
        StatCollector.addStatListener(grapher);
        Texture2D graphTex = new Texture2D();
        graphTex.setMinificationFilter(MinificationFilter.NearestNeighborNoMipMaps);
        graphTex.setMagnificationFilter(MagnificationFilter.Bilinear);
        grapher.setTexture(graphTex);
        
        q.setTextureCombineMode(Spatial.TextureCombineMode.Replace);  
        q.setLightCombineMode(Spatial.LightCombineMode.Off);
        q.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        q.setZOrder(-1);
        
        float dW = (float)width / grapher.texRenderer.getWidth();
        float dH = (float)height / grapher.texRenderer.getHeight();
        FloatBuffer tbuf = q.getTextureCoords(0).coords;
        tbuf.clear();
        tbuf.put(0).put(dH);
        tbuf.put(0).put(0);
        tbuf.put(dW).put(0);
        tbuf.put(dW).put(dH);
        tbuf.rewind();

        q.setDefaultColor(new ColorRGBA(1, 1, 1, .70f));

        TextureState texState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        texState.setTexture(graphTex);
        q.setRenderState(texState);

        BlendState blend = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        blend.setBlendEnabled(true);
        blend.setSourceFunction(SourceFunction.SourceAlpha);
        blend.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
        q.setRenderState(blend);

        return grapher;
    }

    public static TabledLabelGrapher makeTabledLabelGraph(int width, int height, Quad q) {
        TabledLabelGrapher grapher = new TabledLabelGrapher(width, height);
        grapher.setThreshold(1);
        StatCollector.addStatListener(grapher);
        Texture2D graphTex = new Texture2D();
        graphTex.setMinificationFilter(MinificationFilter.NearestNeighborNoMipMaps);
        graphTex.setMagnificationFilter(MagnificationFilter.Bilinear);
        grapher.setTexture(graphTex);
        
        q.setTextureCombineMode(Spatial.TextureCombineMode.Replace);  
        q.setLightCombineMode(Spatial.LightCombineMode.Off);
        q.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        q.setZOrder(-1);
        
        float dW = (float)width / grapher.texRenderer.getWidth();
        float dH = (float)height / grapher.texRenderer.getHeight();
        FloatBuffer tbuf = q.getTextureCoords(0).coords;
        tbuf.clear();
        tbuf.put(0).put(dH);
        tbuf.put(0).put(0);
        tbuf.put(dW).put(0);
        tbuf.put(dW).put(dH);
        tbuf.rewind();
        
        q.setDefaultColor(new ColorRGBA(1, 1, 1, .70f));

        TextureState texState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        texState.setTexture(graphTex);
        q.setRenderState(texState);

        BlendState blend = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        blend.setBlendEnabled(true);
        blend.setSourceFunction(SourceFunction.SourceAlpha);
        blend.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
        q.setRenderState(blend);

        return grapher;
    }
    
}
