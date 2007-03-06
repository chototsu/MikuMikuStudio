/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

package com.jme.scene.batch;

import java.io.IOException;
import java.io.Serializable;
import java.nio.IntBuffer;

import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * LineBatch provides an extension of GeomBatch adding the capabilities for line
 * characteristics. These batch elements are usually contained in the Line
 * object, while Geometry contains GeomBatch elements.
 * 
 * @author Joshua Slack
 */
public class LineBatch extends GeomBatch implements Serializable, Savable {
	private static final long serialVersionUID = 1L;

    private float lineWidth = 1.0f;
    private int mode = Line.SEGMENTS;
    private short stipplePattern = (short)0xFFFF;
    private int stippleFactor = 1;
    private boolean antialiased = false;

    protected transient IntBuffer indexBuffer;
    
    /**
     * Default constructor that creates a new TriangleBatch.
     *
     */
    public LineBatch() {
    	super();
    }

    /**
     * @return true if points are to be drawn antialiased
     */
    public boolean isAntialiased() {
        return antialiased;
    }
    
    /**
     * Sets whether the point should be antialiased. May decrease performance. If
     * you want to enabled antialiasing, you should also use an alphastate with
     * a source of SB_SRC_ALPHA and a destination of DB_ONE_MINUS_SRC_ALPHA or
     * DB_ONE.
     * 
     * @param antiAliased
     *            true if the line should be antialiased.
     */
    public void setAntialiased(boolean antialiased) {
        this.antialiased = antialiased;
    }

    /**
     * @return either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public int getMode() {
        return mode;
    }

    /**
     * @param mode
     *            either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * @return the width of this line.
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets the width of the line when drawn. Non anti-aliased line widths are
     * rounded to the nearest whole number by opengl.
     * 
     * @param lineWidth
     *            The lineWidth to set.
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
    
    /**
     * @return the set stipplePattern. 0xFFFF means no stipple.
     */
    public short getStipplePattern() {
        return stipplePattern;
    }

    /**
     * The stipple or pattern to use when drawing this line. 0xFFFF is a solid
     * line.
     * 
     * @param stipplePattern
     *            a 16bit short whose bits describe the pattern to use when
     *            drawing this line
     */
    public void setStipplePattern(short stipplePattern) {
        this.stipplePattern = stipplePattern;
    }
    
    /**
     * @return the set stippleFactor.
     */
    public int getStippleFactor() {
        return stippleFactor;
    }

    /**
     * @param stippleFactor
     *            magnification factor to apply to the stipple pattern.
     */
    public void setStippleFactor(int stippleFactor) {
        this.stippleFactor = stippleFactor;
    }

	public IntBuffer getIndexBuffer() {
		return indexBuffer;
	}

	public void setIndexBuffer(IntBuffer indices) {
		this.indexBuffer = indices;
	}

    
    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @see java.io.Serializable
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getIndexBuffer() == null)
            s.writeInt(0);
        else {
            s.writeInt(getIndexBuffer().limit());
            getIndexBuffer().rewind();
            for (int x = 0, len = getIndexBuffer().limit(); x < len; x++)
                s.writeInt(getIndexBuffer().get());
        }
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        int len = s.readInt();
        if (len == 0) {
            setIndexBuffer(null);
        } else {
            IntBuffer buf = BufferUtils.createIntBuffer(len);
            for (int x = 0; x < len; x++)
                buf.put(s.readInt());
            setIndexBuffer(buf);            
        }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(lineWidth, "lineWidth", 1);
        capsule.write(mode, "mode", Line.SEGMENTS);
        capsule.write(stipplePattern, "stipplePattern", (short)0xFFFF);
        capsule.write(antialiased, "antialiased", false);
        capsule.write(indexBuffer, "indexBuffer", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        lineWidth = capsule.readFloat("lineWidth", 1);
        mode = capsule.readInt("mode", Line.SEGMENTS);
        stipplePattern = capsule.readShort("stipplePattern", (short)0xFFFF);
        antialiased = capsule.readBoolean("antialiased", false);
        indexBuffer = capsule.readIntBuffer("indexBuffer", null);
    }
    
    public void draw(Renderer r) {
        if(!isEnabled()) {
            return;
        }
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
                return;
        }

        super.draw(r);
        r.draw(this);
    }
}
