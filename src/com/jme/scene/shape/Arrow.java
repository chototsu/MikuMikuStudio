/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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

package com.jme.scene.shape;

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>Arrow</code> is basically a cylinder with a pyramid on top.
 * 
 * @author Joshua Slack
 * @version $Revision: 1.3 $
 */
public class Arrow extends TriMesh {
    private static final long serialVersionUID = 1L;

    protected float length = 1;
    protected float width = .25f;

    protected static final Quaternion rotator = new Quaternion();

    public Arrow() {}
    
    public Arrow(String name) {
        super(name);
    }

    public Arrow(String name, float length, float width) {
        super(name);
        this.length = length;
        this.width = width;
        
        buildArrow();
    }

    protected void buildArrow() {
        clearBatches();
        
        // Start with cylinders:
        Cylinder base = new Cylinder("base", 4, 16, width*.75f, length);
        rotator.fromAngles(90*FastMath.DEG_TO_RAD, 0, 0);
        base.getBatch(0).rotatePoints(rotator);
        base.getBatch(0).rotateNormals(rotator);
        addBatch(base.getBatch(0));

        Pyramid tip = new Pyramid("tip",  2*width, length/2f);
        tip.getBatch(0).translatePoints(0, length*.75f, 0);
        addBatch(tip.getBatch(0));
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(length, "length", 1);
        capsule.write(width, "width", .25f);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        length = capsule.readFloat("length", 1);
        width = capsule.readFloat("width", .25f);
        
    }

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}
    
    
}
