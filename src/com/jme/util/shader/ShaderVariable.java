/*
 * Copyright (c) 2003-2009 jMonkeyEngine
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

package com.jme.util.shader;

import java.io.IOException;

import com.jme.util.export.*;

/**
 * An utily class to store shader's uniform variables content.
 */
public class ShaderVariable implements Savable {

    /** Name of the uniform variable. * */
    public String name;

    public static final int UNINITIALIZED = -1;

    public static final int UNAVAILABLE = -2;

    /** ID of uniform. * */
    public int variableID = UNINITIALIZED;

    /** Needs to be refreshed */
    public boolean needsRefresh = true;

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ShaderVariable) {
            final ShaderVariable temp = (ShaderVariable) obj;
            if (name.equals(temp.name)) {
                return true;
            }
        }
        return false;
    }

    public void write(final JMEExporter e) throws IOException {
        final OutputCapsule capsule = e.getCapsule(this);

        capsule.write(name, "name", "");
        capsule.write(variableID, "variableID", UNINITIALIZED);
    }

    public void read(final JMEImporter e) throws IOException {
        final InputCapsule capsule = e.getCapsule(this);

        name = capsule.readString("name", "");
        variableID = capsule.readInt("variableID", UNINITIALIZED);
    }

    public Class getClassTag() {
        return this.getClass();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}