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
 * Started Date: Jun 6, 2004
 *
 * Is implimented by jME objects to allow them to be saved/loaded to/from jME
 * All implimenting classes must have a default constructor, or they will be
 * unable to be processed.  It is guaranteed that loadFromXML(String args) will
 * be called directly after default construction.  The user should make sure that the String
 * returned from writeToXML() will duplicate the current object when loadFromXML(String) is
 * called.
 *
 * <br>
 *
 * Note: If the implementing item is a Spatial, then it's name, translation, rotation, scale atts are set automatically
 * and shouldn't be included in either String
 *
 * <br>
 *
 * @author Jack Lindamood
 */
public interface XMLloadable {
    /**
     *<code>writeToXML</code> will return a String that when passed to <code>loadFromXML</code>
     * directly after a call to the default constructor, will reconstruct the current class.
     * @return String to later be given to <code>loadFromXML</code>
     */
    public String writeToXML();
    /**
     * Given a String from a previous object of the same class, <code>loadFromXML</code> will
     * duplicate that class.
     * @param args The string args given to reconstruct this class
     * @return The object that <code>JmeBinaryReader</code> will use.  This can either be the same object that
     * loadFromXML was called on, or a new object.  Only the returned object will be used. 
     */
    public Object loadFromXML(String args);
}