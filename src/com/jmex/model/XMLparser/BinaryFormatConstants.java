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

package com.jmex.model.XMLparser;

/**
 * Started Date: Jun 23, 2004<br><br>
 *
 * This interface is used by jME's binary format to identify sections.
 *
 * @author Jack Lindamood
 */
public interface BinaryFormatConstants {
    // -- File identifier
    static final long BEGIN_FILE=1234567l;

    // -- Block identifiers.  Each of these need to be different
    static final byte BEGIN_TAG=0;
    static final byte END_TAG=1;
    static final byte END_FILE=2;
    // -- Block identifiers

    // -- Data type identifiers.  Each of these need to be different
    static final byte DATA_V3FARRAY=0;
    static final byte DATA_V2FARRAY=1;
    static final byte DATA_COLORARRAY=2;
    static final byte DATA_STRING=3;
    static final byte DATA_INTARRAY=4;
    static final byte DATA_V3F = 5;
    static final byte DATA_QUAT = 6;
    static final byte DATA_FLOAT = 7;
    static final byte DATA_COLOR = 8;
    static final byte DATA_URL = 9;
    static final byte DATA_INT = 10;
    static final byte DATA_BOOLEAN = 11;
    static final byte DATA_QUATARRAY = 12;
    static final byte DATA_BYTEARRAY = 13;
    static final byte DATA_SHORTARRAY = 14;
    static final byte DATA_V2F = 15;
    static final byte DATA_MATRIX3 = 16;
    // -- Data type identifiers

    float XYZ_SCALE = 1/64.0f;

}
