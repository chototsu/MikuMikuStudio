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
package jme.geometry.hud;

import java.util.ArrayList;

import jme.math.Vector;

/**
 * <code>AbstractComponent</code> defines a base level of implementation of the
 * <code>Component</code> interface. The <code>dispose</code> and
 * <code>render</code> methods must be implmented by the subclass.
 * 
 * @author Mark Powell
 */
public abstract class AbstractComponent implements Component {
	protected int locationX;
	protected int locationY;
	protected float height;
	protected float width;
	protected Vector color = new Vector(1,1,1);
	protected float alpha;
	
	protected ArrayList children = new ArrayList();

    /**
     * <code>add</code> places the subcomponent in the array list
     * of children. This list is not sorted in anyway.
     * 
     * @param subComponent the child to add to this component.
     * @see jme.geometry.hud.Component#add(jme.geometry.hud.Component)
     */
    public void add(Component subComponent) {
        children.add(subComponent);
    }

	/**
	 * <code>remove</code> removes thes specified subComponent from
	 * this component. This effectively disposes the subcomponent as
	 * well as any children this child may have had.
	 * 
	 * @param subComponent the child to remove.
	 * @see jme.geometry.hud.Component#remove(jme.geometry.hud.Component)
	 */
    public void remove(Component subComponent) {
        children.remove(subComponent);
		subComponent.dispose();
    }

    /**
     * <code>contains</code> reports true if the supplied point is within
     * the constraints of the component. If the point falls outside 
     * of the component false is returned.
     * 
     * @param x the x value of the component.
     * @param y the y value of the component.
     * @return true if the point is within the component, false otherwise.
     * @see jme.geometry.hud.Component#contains(int,int)
     */
    public boolean contains(int x, int y) {
        if((x >= locationX && x <= locationX + width ) &&
			(x >= locationY && x <= locationY + height )) {
				return true;
			}
        return false;
    }

    /**
     * <code>setColor</code> sets the overall color of the component.
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     * @see jme.geometry.hud.Component#setColor(float,float,float)
     */
    public void setColor(float r, float g, float b) {
        color.x = r;
        color.y = g;
        color.z = b;
    }

    /**
     * <code>setTransparency</code> sets the alpha channel of the component.
     * @param a the alpha value.
     * @see jme.geometry.hud.Component#setTransparency(float)
     */
    public void setTransparency(float a) {
        alpha = a;
    }

    /* (non-Javadoc)
     * @see jme.geometry.hud.Component#setTexture(java.lang.String)
     */
    public void setTexture(String texture) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jme.geometry.hud.Component#setSize(float, float)
     */
    public void setSize(float width, float height) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jme.geometry.hud.Component#setLocation(float, float)
     */
    public void setLocation(float x, float y) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jme.geometry.hud.Component#isVisible(boolean)
     */
    public void isVisible(boolean value) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jme.geometry.hud.Component#getWidth()
     */
    public int getWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see jme.geometry.hud.Component#getHeight()
     */
    public int getHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see jme.geometry.hud.Component#getX()
     */
    public int getX() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see jme.geometry.hud.Component#getY()
     */
    public int getY() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see jme.geometry.hud.Component#dispose()
     */
    public abstract void dispose();
    
    public abstract void render();

}
