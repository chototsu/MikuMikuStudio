/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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

import java.util.ArrayList;

import com.jme.renderer.Renderer;

/**
 * <code>HeadlessDelegate</code> provides an interface between JMEComponents
 * and the GL renderers.
 *
 * @author Joshua Slack
 * @version $Id: HeadlessDelegate.java,v 1.1 2004-11-09 19:59:02 renanse Exp $
 */

public class HeadlessDelegate {
	private static ArrayList lookupTable = new ArrayList();

  public HeadlessDelegate() {
  }

	/**
	 * Check to see if the pair associated with this renderer is flagged as
	 * needing an OpenGL update.  (ie, the AWT component has just updated itself
	 * and will want a new pic next time it updates.)
	 *
	 * @param r Renderer
	 * @return boolean
	 */
	public static boolean needsRender(Renderer r) {
		MapClass res = lookup(r);
		if (res != null)
			return res.needsRefresh;
		else return false;
	}

	/**
	 * Sets the needs render flag for the Renderer/JMEComponent pair associated
	 * with the given JMEComponent.  See <code>needsRender(Renderer)</code> for
	 * more on this flag's use.
	 *
	 * @param comp JMEComponent
	 * @param needsRefresh boolean
	 */
	public static void setNeedsRender(JMEComponent comp, boolean needsRefresh) {
		MapClass res = lookup(comp);
		if (res == null) return;
		res.needsRefresh = needsRefresh;
	}

	/**
	 * Copies the OpenGL contents currently in the matching OpenGL context into
	 * the given JMEComponent's IntBuffer field.
	 *
	 * @param comp JMEComponent
	 */
	public static void copyContents(JMEComponent comp) {
		// Look up appropriate renderer
		MapClass res = lookup(comp);
		if (res == null) return;
		// Copy renderer's context to the component's image buffer.
		res.r.grabScreenContents(comp.getBuffer(), 0, 0, res.r.getWidth(), res.r.getHeight());
	}

	/**
	 * Adds a new Renderer, JMEComponent pair to be tracked.  You should do this
	 * after creating a JMEComponent so that the OpenGL and AWT sides can both
	 * communicate effectively.
	 *
	 * @param r Renderer
	 * @param comp JMEComponent
	 * @return boolean
	 */
	public static boolean add(Renderer r, JMEComponent comp) {
		MapClass res = new MapClass(r, comp);
		if (lookupTable.contains(comp)) return false;
		return lookupTable.add(res); // always true, per ArrayList.add(Object)
	}

	/**
	 * Check to see if a MapClass is already listed in the lookupTable
	 * matching the given JMEComponent.
	 *
	 * @param comp JMEComponent
	 * @return boolean
	 */
	public static boolean isListed(JMEComponent comp) {
		return (lookup(comp) != null);
	}

	/**
	 * Check to see if a MapClass is already listed in the lookupTable
	 * matching the given Renderer.
	 *
	 * @param r Renderer
	 * @return boolean
	 */
	public static boolean isListed(Renderer r) {
		return (lookup(r) != null);
	}

	/**
	 * Remove the MapClass matching a given JMEComponent from the lookupTable
	 * ArrayList.  This is useful for cleaning things up.
	 *
	 * @param comp JMEComponent
	 * @return boolean
	 */
	public static boolean remove(JMEComponent comp) {
		MapClass res = lookup(comp);
		if (res != null) {
			return lookupTable.remove(res);
		} else return false;
	}

	/**
	 * Remove the MapClass matching a given Renderer from the lookupTable
	 * ArrayList.  This is useful for cleaning things up.
	 *
	 * @param r Renderer
	 * @return boolean
	 */
	public static boolean remove(Renderer r) {
		MapClass res = lookup(r);
		if (res != null) {
			return lookupTable.remove(res);
		} else return false;
	}

	/**
	 * Private lookup function used to find the MapClass pair in the lookupTable
	 * ArrayList having a matching JMEComponent field.
	 *
	 * @param comp JMEComponent
	 * @return MapClass
	 */
	private static MapClass lookup(JMEComponent comp) {
		for (int x = lookupTable.size(); --x >= 0; ) {
			MapClass mc = (MapClass)lookupTable.get(x);
			if (mc.comp == comp) return mc;
		}
		return null;
	}

	/**
	 * Private lookup function used to find the MapClass pair in the lookupTable
	 * ArrayList having a matching Renderer field.
	 *
	 * @param r Renderer
	 * @return MapClass
	 */
	private static MapClass lookup(Renderer r) {
		for (int x = lookupTable.size(); --x >= 0; ) {
			MapClass mc = (MapClass)lookupTable.get(x);
			if (mc.r == r) return mc;
		}
		return null;
	}

}


/**
 * <code>MapClass</class> is a locally used matching class for associating
 * JMEComponents with Renderers and a boolean refresh flag.
 */
class MapClass {

	Renderer r;
	JMEComponent comp;
	boolean needsRefresh;

	public MapClass (Renderer r, JMEComponent comp) {
		this.r = r;
		this.comp = comp;
		needsRefresh = true;
	}

	public boolean equals(Object other) {
		if (other == this) {
		 return true;
	 }
	 if (!(other instanceof MapClass)) {
		 return false;
	 }
	 MapClass that = (MapClass)other;
	 if (this.comp != that.comp) return false;
	 if (this.r != that.r) return false;
	 return true;
 }
}
