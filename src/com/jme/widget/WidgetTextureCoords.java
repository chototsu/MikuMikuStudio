/* 
* Copyright (c) 2004, jMonkeyEngine - Mojo Monkey Coding 
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
package com.jme.widget;

/**
 * <code>WidgetTextureCoords</code>
 * @author Gregg Patton
 * @version $Id: WidgetTextureCoords.java,v 1.1 2004-03-04 03:26:09 greggpatton Exp $
 */
public class WidgetTextureCoords {
    public float u0 = .5f, u1 = .5f, v0 = .5f, v1 = .5f;
    public float maskU0 = .5f, maskU1 = .5f, maskV0 = .5f, maskV1 = .5f;
    
    /**
     * 
     */
    public WidgetTextureCoords() {
        super();
    }

    public WidgetTextureCoords(float u0, float v0, float u1, float v1) {
        super();
        
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
    }

    public WidgetTextureCoords(float u0, float v0, float u1, float v1,
                               float maskU0, float maskV0, float maskU1, float maskV1) {
        super();
        
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
        
        this.maskU0 = maskU0;
        this.maskV0 = maskV0;
        this.maskU1 = maskU1;
        this.maskV1 = maskV1;
    }

    /**
     * <code>getU0</code>
     * @return
     */
    public float getU0() {
        return u0;
    }

    /**
     * <code>setU0</code>
     * @param f
     */
    public void setU0(float f) {
        u0 = f;
    }

    /**
     * <code>getU1</code>
     * @return
     */
    public float getU1() {
        return u1;
    }

    /**
     * <code>setU1</code>
     * @param f
     */
    public void setU1(float f) {
        u1 = f;
    }

    /**
     * <code>getV0</code>
     * @return
     */
    public float getV0() {
        return v0;
    }

    /**
     * <code>setV0</code>
     * @param f
     */
    public void setV0(float f) {
        v0 = f;
    }

    /**
     * <code>getV1</code>
     * @return
     */
    public float getV1() {
        return v1;
    }

    /**
     * <code>setV1</code>
     * @param f
     */
    public void setV1(float f) {
        v1 = f;
    }

    /**
     * <code>getMaskU0</code>
     * @return
     */
    public float getMaskU0() {
        return maskU0;
    }

    /**
     * <code>setMaskU0</code>
     * @param f
     */
    public void setMaskU0(float f) {
        maskU0 = f;
    }

    /**
     * <code>getMaskU1</code>
     * @return
     */
    public float getMaskU1() {
        return maskU1;
    }

    /**
     * <code>setMaskU1</code>
     * @param f
     */
    public void setMaskU1(float f) {
        maskU1 = f;
    }

    /**
     * <code>getMaskV0</code>
     * @return
     */
    public float getMaskV0() {
        return maskV0;
    }

    /**
     * <code>setMaskV0</code>
     * @param f
     */
    public void setMaskV0(float f) {
        maskV0 = f;
    }

    /**
     * <code>getMaskV1</code>
     * @return
     */
    public float getMaskV1() {
        return maskV1;
    }

    /**
     * <code>setMaskV1</code>
     * @param f
     */
    public void setMaskV1(float f) {
        maskV1 = f;
    }

}
