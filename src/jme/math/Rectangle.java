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
package jme.math;

/**
 * <code>Rectangle</code>
 * @author Mark Powell
 * @version $Id: Rectangle.java,v 1.1 2003-08-27 20:48:48 mojomonkey Exp $
 */
public class Rectangle {
    private Vector origin;
    private Vector firstEdge;
    private Vector secondEdge;
    
    public Rectangle() {
        origin = new Vector();
        firstEdge = new Vector();
        secondEdge = new Vector();
    }

    public Rectangle(Vector origin, Vector firstEdge, Vector secondEdge) {
        this.origin = origin;
        this.firstEdge = firstEdge;
        this.secondEdge = secondEdge;
    }
    
    /**
     * @return
     */
    public Vector getFirstEdge() {
        return firstEdge;
    }

    /**
     * @param firstEdge
     */
    public void setFirstEdge(Vector firstEdge) {
        this.firstEdge = firstEdge;
    }

    /**
     * @return
     */
    public Vector getOrigin() {
        return origin;
    }

    /**
     * @param origin
     */
    public void setOrigin(Vector origin) {
        this.origin = origin;
    }

    /**
     * @return
     */
    public Vector getSecondEdge() {
        return secondEdge;
    }

    /**
     * @param secondEdge
     */
    public void setSecondEdge(Vector secondEdge) {
        this.secondEdge = secondEdge;
    }

    
    
    

}
