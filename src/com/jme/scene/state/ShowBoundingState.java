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
package com.jme.scene.state;

/**
 * <code>ShowBoundingState</code>
 * @author Mark Powell
 * @version $Id: ShowBoundingState.java,v 1.1 2004-03-12 17:40:30 mojomonkey Exp $
 */
public class ShowBoundingState extends RenderState {
	private static boolean shown;

	/** <code>getType</code> 
	 * @return
	 * @see com.jme.scene.state.RenderState#getType()
	 */
	public int getType() {
		return RS_SHOW_BOUNDINGS;
	}

	/** <code>set</code> 
	 * 
	 * @see com.jme.scene.state.RenderState#set()
	 */
	public void set() {
		if (isEnabled()) {
			shown = true;
		} else {
			shown = false;
		}
	}

	/** <code>unset</code> 
	 * 
	 * @see com.jme.scene.state.RenderState#unset()
	 */
	public void unset() {
		if (isEnabled()) {
			shown = false;
		}
	}
    
    public static boolean isShown() {
        return shown;
    }
}
