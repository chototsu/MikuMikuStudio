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
package com.jme.widget.input.mouse;

import java.util.Observer;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface WidgetMouseHandlerInterface {

	public void addMouseButtonDownObserver(Observer o);
	public void deleteMouseButtonDownObserver(Observer o);
	public void doMouseButtonDown();
	public void handleMouseButtonDown();

	public void addMouseButtonUpObserver(Observer o);
	public void deleteMouseButtonUpObserver(Observer o);
	public void doMouseButtonUp();
	public void handleMouseButtonUp();

	public void addMouseMoveObserver(Observer o);
	public void deleteMouseMoveObserver(Observer o);
	public void doMouseMove();
	public void handleMouseMove();

	public void addMouseDragObserver(Observer o);
	public void deleteMouseDragObserver(Observer o);
	public void doMouseDrag();
	public void handleMouseDrag();

	public void addMouseEnterObserver(Observer o);
	public void deleteMouseEnterObserver(Observer o);
	public void doMouseEnter();
	public void handleMouseEnter();

	public void addMouseExitObserver(Observer o);
	public void deleteMouseExitObserver(Observer o);
	public void doMouseExit();
	public void handleMouseExit();

}
