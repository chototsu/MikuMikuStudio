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
package com.jme.util;

/**
 * <code>JmeType</code> JmeType derived classes are used to create 
 * strongly typed enumerations.  Using a strongly typed enumeration 
 * has benefits over using intrinsic types or their wrappers, 
 * ie int, boolean, String, etc.  For instance, the strongly typed 
 * enumeration can only be compared against the same type, no accidental
 * crossover of type comparison.  Strongly typed enumerations also allow
 * effective method overloading, performFoo(FooType1 type) has a different
 * signature than performFoo(FooType2 type).
 * @author Gregg Patton
 * @version $Id: JmeType.java,v 1.1 2004-02-09 12:21:50 greggpatton Exp $
 */
public abstract class JmeType {

    protected String name;

    protected JmeType(String name) {
        this.name = name;
    }

    /**
     * <code>getName</code> returns the name of this jME type
     * @return the name of this jME type
     */
    public String getName() {
        return name;
    }

    /**
     * <code>getType</code> returns an instance of the JmeType
     * @param name the String representation of the type
     * @return an instance of the JmeType
     */
    public abstract JmeType getType(String name);

    /** <code>toString</code> 
     * @return the String representation of the JmeType
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
    }

}
