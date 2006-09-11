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

package com.jme.renderer;

/**
 * A basic, extensible class for holding rendering stats.
 * 
 * @author Joshua Slack
 */
public class RenderStatistics {

    public long numberOfVerts;
    public long numberOfPoints;
    public long numberOfLines;
    public long numberOfTris;
    public long numberOfMesh;
    public long numberOfQuads;
    

    public Class getClassTag() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * <code>clearStatistics</code> resets the statistics information
     * counters.
     */
    public void clearStatistics() {
        numberOfVerts = 0;
        numberOfTris = 0;
        numberOfPoints = 0;
        numberOfLines = 0;
        numberOfMesh = 0;
        numberOfQuads = 0;
    }

    public long getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(long numberOfLines) {
        this.numberOfLines = numberOfLines;
    }

    public long getNumberOfMesh() {
        return numberOfMesh;
    }

    public void setNumberOfMesh(long numberOfMesh) {
        this.numberOfMesh = numberOfMesh;
    }

    public long getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(long numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public long getNumberOfTris() {
        return numberOfTris;
    }

    public void setNumberOfTris(long numberOfTris) {
        this.numberOfTris = numberOfTris;
    }
    
    public long getNumberOfQuads() {
        return numberOfQuads;
    }

    public void setNumberOfQuads(long numberOfQuads) {
        this.numberOfQuads = numberOfQuads;
    }

    public long getNumberOfVerts() {
        return numberOfVerts;
    }

    public void setNumberOfVerts(long numberOfVerts) {
        this.numberOfVerts = numberOfVerts;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        append(buf);
        return buf.toString();
    }

    public void append(StringBuffer buf) {
        buf.append("Counts:");
        boolean showing = false;
        if (numberOfMesh != 0) {
            buf.append(" Mesh(");
            buf.append(numberOfMesh);
            buf.append(")");
            showing = true;
        }
        if (numberOfVerts != 0) {
            buf.append(" Vert(");
            buf.append(numberOfVerts);
            buf.append(")");
            showing = true;
        }
        if (numberOfTris != 0) {
            buf.append(" Tri(");
            buf.append(numberOfTris);
            buf.append(")");
            showing = true;
        }
        if (numberOfQuads != 0) {
            buf.append(" Quad(");
            buf.append(numberOfQuads);
            buf.append(")");
            showing = true;
        }
        if (numberOfLines != 0) {
            buf.append(" Line(");
            buf.append(numberOfLines);
            buf.append(")");
            showing = true;
        }
        if (numberOfPoints != 0) {
            buf.append(" Pnt(");
            buf.append(numberOfPoints);
            buf.append(")");
            showing = true;
        }
        if (!showing) {
            buf.append(" nothing displaying.");
        }
    }
}
