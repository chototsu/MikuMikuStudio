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
/*
 * Created on Apr 21, 2004
 */
package com.jme.effects.lenseflare;
import java.net.URL;
import java.util.ArrayList;

import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BillboardNode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
/**
 * @author Ahmed
 *
 */
public class LenseFlare extends BillboardNode {
    private ArrayList quadStack;
    private float subtractBy;
    /**
     * @param name,
     *            the name of the node
     */
    public LenseFlare(String name, float subtractBy) {
        super(name);
        quadStack = new ArrayList();
        this.subtractBy = subtractBy;
    }
    public void addType(URL urlToImage) {
        // no stack position or width of quad.
        // stack pos = -1, allocate to next available position
        // quad dimensions = null, 5x5
        initialise(-1, urlToImage, null);
    }
    public void addType(int stackPos, URL urlToImage) {
        // no dimensions, so:
        // quad dimensions = null, 5x5
        initialise(stackPos, urlToImage, null);
    }
    public void addType(URL urlToImage, Vector2f dimensions) {
        // no stack position, allocate to next
        // available position
        initialise(-1, urlToImage, dimensions);
    }
    public void addType(int stackPos, URL urlToImage, Vector2f dimensions) {
        // everything is available, call whatever they want
        initialise(stackPos, urlToImage, dimensions);
    }
    private void initialise(int stackPos, URL urlToImage, Vector2f dimension) {
        // if the url supplied is wrong, dont bother doing
        // anything
        if (urlToImage != null) {
            // obtain a texture state for the URL provided
            TextureState ts = DisplaySystem.getDisplaySystem().getRenderer()
                    .getTextureState();
            Texture tex = TextureManager.loadTexture(urlToImage,
                    Texture.MM_LINEAR, Texture.FM_LINEAR, true);
            ts.setTexture(tex);
            ts.setEnabled(true);
            // get an alphastate that would remove all black
            // from the image and replace it with transperant
            AlphaState as = DisplaySystem.getDisplaySystem().getRenderer()
                    .getAlphaState();
            as.setBlendEnabled(true);
            as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
            as.setDstFunction(AlphaState.DB_ONE);
            as.setTestEnabled(true);
            as.setTestFunction(AlphaState.TF_GREATER);
            as.setEnabled(true);
            // create a quad and if the dimesion provided is
            // null, then the width/height = 5x5
            Quad q = new Quad(this.getName() + " Quad");
            if (dimension != null) {
                q.initialize(dimension.x, dimension.y);
            } else {
                q.initialize(5, 5);
            }
            // create the colors for the quad
            ColorRGBA colors[] = new ColorRGBA[q.getVertices().length];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = new ColorRGBA(1f, 0.95f, 0f, 1f);
            }
            // set the properties of the quad
            q.setColors(colors);
            q.setRenderState(ts);
            q.setRenderState(as);
            this.attachChild(q);
            // if the stackposition is not available,
            // set it to the next available position
            if (stackPos != -1) {
                quadStack.add(stackPos, q);
            } else {
                quadStack.add(q);
            }
        }
    }
    public void updateWorldData(float time) {
        float sub = subtractBy;
        //super.updateWorldData(time);
        // position of camera
        Vector3f camPos = DisplaySystem.getDisplaySystem().getRenderer()
                .getCamera().getLocation();

        // position of node
        Vector3f curPos = this.getLocalTranslation();
        // difference is camera and node
        Vector3f difPos = new Vector3f();
        camPos.subtract(curPos, difPos);
        // temporary color array;
        for (int i = 0; i < quadStack.size(); i++) {
            ColorRGBA color = ((Quad)quadStack.get(i)).getColors()[0];
            if (Math.abs(difPos.x) > 0) {
                color.a = 1 - (sub * Math.abs(difPos.x));
            }
            ((Quad)quadStack.get(i)).setSolidColor(color);
            ((Quad)quadStack.get(i)).setLocalScale(difPos.x /7.5f);
        }
    }
}