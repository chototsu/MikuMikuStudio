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
package com.jme.widget.input.mouse;

import java.util.Observer;

/**
 * <code>WidgetMouseHandlerInterface</code>
 * @author Gregg Patton
 * @version $Id: WidgetMouseHandlerInterface.java,v 1.3 2004-04-22 22:27:19 renanse Exp $
 */
public interface WidgetMouseHandlerInterface {

	/**
     * <code>addMouseButtonDownObserver</code>
     * @param o
     */
    public void addMouseButtonDownObserver(Observer o);

    /**
     * <code>deleteMouseButtonDownObserver</code>
     * @param o
     */
    public void deleteMouseButtonDownObserver(Observer o);

    /**
     * <code>deleteMouseButtonDownObservers</code>
     *
     */
    public void deleteMouseButtonDownObservers();

	/**
     * <code>doMouseButtonDown</code>
     *
     */
    public void doMouseButtonDown();
	/**
     * <code>handleMouseButtonDown</code>
     *
     */
    public void handleMouseButtonDown();




	/**
     * <code>addMouseButtonUpObserver</code>
     * @param o
     */
    public void addMouseButtonUpObserver(Observer o);

    /**
     * <code>deleteMouseButtonUpObserver</code>
     * @param o
     */
    public void deleteMouseButtonUpObserver(Observer o);

    /**
     * <code>deleteMouseButtonUpObservers</code>
     *
     */
    public void deleteMouseButtonUpObservers();

	/**
     * <code>doMouseButtonUp</code>
     *
     */
    public void doMouseButtonUp();

	/**
     * <code>handleMouseButtonUp</code>
     *
     */
    public void handleMouseButtonUp();




	/**
     * <code>addMouseMoveObserver</code>
     * @param o
     */
    public void addMouseMoveObserver(Observer o);

    /**
     * <code>deleteMouseMoveObserver</code>
     * @param o
     */
    public void deleteMouseMoveObserver(Observer o);

    /**
     * <code>deleteMouseMoveObservers</code>
     *
     */
    public void deleteMouseMoveObservers();

	/**
     * <code>doMouseMove</code>
     *
     */
    public void doMouseMove();

	/**
     * <code>handleMouseMove</code>
     *
     */
    public void handleMouseMove();




	/**
     * <code>addMouseDragObserver</code>
     * @param o
     */
    public void addMouseDragObserver(Observer o);

    /**
     * <code>deleteMouseDragObserver</code>
     * @param o
     */
    public void deleteMouseDragObserver(Observer o);

    /**
     * <code>deleteMouseDragObservers</code>
     *
     */
    public void deleteMouseDragObservers();

	/**
     * <code>doMouseDrag</code>
     *
     */
    public void doMouseDrag();

	/**
     * <code>handleMouseDrag</code>
     *
     */
    public void handleMouseDrag();




	/**
     * <code>addMouseEnterObserver</code>
     * @param o
     */
    public void addMouseEnterObserver(Observer o);
    /**
     * <code>deleteMouseEnterObserver</code>
     * @param o
     */
    public void deleteMouseEnterObserver(Observer o);
    /**
     * <code>deleteMouseEnterObservers</code>
     *
     */
    public void deleteMouseEnterObservers();
	/**
     * <code>doMouseEnter</code>
     *
     */
    public void doMouseEnter();
	/**
     * <code>handleMouseEnter</code>
     *
     */
    public void handleMouseEnter();




	/**
     * <code>addMouseExitObserver</code>
     * @param o
     */
    public void addMouseExitObserver(Observer o);
    /**
     * <code>deleteMouseExitObserver</code>
     * @param o
     */
    public void deleteMouseExitObserver(Observer o);
    /**
     * <code>deleteMouseExitObservers</code>
     *
     */
    public void deleteMouseExitObservers();
	/**
     * <code>doMouseExit</code>
     *
     */
    public void doMouseExit();
	/**
     * <code>handleMouseExit</code>
     *
     */
    public void handleMouseExit();

}
