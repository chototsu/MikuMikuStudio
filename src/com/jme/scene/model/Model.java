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
package com.jme.scene.model;

import java.net.URL;

import com.jme.scene.Controller;
import com.jme.scene.Node;

/**
 * <code>Model</code> defines a sub-class of <code>Node</code> that
 * maintains and loads it's own internal scene. A model typically 
 * consists of multiple children of meshes. These meshes are joined
 * to compose a model. <code>Model</code> is abstract and the type of 
 * scene a load will create is dependant on the type of model. 
 * @author Mark Powell
 * @version $Id: Model.java,v 1.3 2004-02-15 20:22:39 mojomonkey Exp $
 */
public abstract class Model extends Node{
	/**
	 * <code>load</code> takes a filename that corresponds to the 
	 * location of the model. This data is than parsed and used to
	 * generate a scenegraph node that is composed of the model's 
	 * mesh.
	 * @param filename the file that contains the model data.
	 */
	public abstract void load(String filename);
	
	/**
	 * <code>load</code> takes a url that corresponds to the 
	 * location of the model. This data is than parsed and used to
	 * generate a scenegraph node that is composed of the model's 
	 * mesh.
	 * @param filename the url of the file that contains the model data.
	 */
	public abstract void load(URL filename);
	
	/**
	 * <code>getAnimationController</code> retrieves the 
	 * controller that maintains the animation of the model.
	 * If the model has no animations, null is returned.
	 * @return the animation controller that defines the 
	 * 	animation of the model.
	 */
	public abstract Controller getAnimationController();
}
