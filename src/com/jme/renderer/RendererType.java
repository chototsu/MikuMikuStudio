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

package com.jme.renderer;

import com.jme.util.JmeType;

/**
 * <code>RendererType</code> contains the different RendererTypes jME
 * supports, eg, LWJGL, JOGL, etc.
 * @author Gregg Patton
 * @version $Id: RendererType.java,v 1.4 2005-09-15 17:14:53 renanse Exp $
 */
public class RendererType extends JmeType {

    public final static RendererType NONE = new RendererType("NONE");
    public final static RendererType LWJGL = new RendererType("LWJGL");

    /**
     * Creates a new RenderType.
     * @param name The string name of this render type.
     */
    private RendererType(String name) {
        super(name);
    }

    /** <code>getType</code> returns a strongly typed enumeration of the given name for
     * this type.
     * @param name The name to get a type from (IE "LWJGL")
     * @return The RenderType for the given name.
     * @see com.jme.util.JmeType#getType(java.lang.String)
     */
    public JmeType getType(String name) {
        JmeType type = null;

        if (NONE.name.equals(name)) {
            type = NONE;
        } else if (LWJGL.name.equals(name)) {
            type = LWJGL;
        }

        return type;
    }

}