/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jme.animation;

/**
 * <code>Keyframe</code> defines a positional or a rotational keyframe. The
 * keyframe defines a position a vertex should be at a given time. An animation
 * system uses a collection of these keyframes to build a sequence of
 * transitions to manipulate and animate a collection of vertices.
 * @author Mark Powell
 * @version $Id: Keyframe.java,v 1.4 2005-09-15 17:14:51 renanse Exp $
 */
public class Keyframe {

    /**
     * The time in milliseconds after the start of the animation for which
     * that keyframe occurs.
     */
    public float time;

    /**
     * For a position keyframe (x,y,z) are the coordinates to translate.
     * For a rotation keyframe (x,y,z) are angles, in radians, to rotate.
     */
    public float x, y, z;

    /**
     * Create a keyframe at a given time and vector.
     * @param time the time in milliseconds after the start of the animation
     * for which that keyframe occurs.
     * @param x the x value of the translation or rotation for the keyframe.
     * @param y the y value of the translation or rotation for the keyframe.
     * @param z the z value of the translation or rotation for the keyframe.
     */
    public Keyframe(float time, float x, float y, float z) {
        this.time = time;
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
