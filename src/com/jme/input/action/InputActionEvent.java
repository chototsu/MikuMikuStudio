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
package com.jme.input.action;

import java.util.ArrayList;

import com.jme.input.KeyInput;
import com.jme.input.MouseInput;

/**
 * <code>InputActionEvent</code> defines an event that generates the
 * processing of a given InputAction. This event contains information about the
 * triggers that caused the event to take places as well as the list of names of
 * the other Actions that were to be processed at the same time.
 * 
 * @author Mark Powell
 * @version $Id: InputActionEvent.java,v 1.1 2004-10-14 01:22:58 mojomonkey Exp $
 */
public class InputActionEvent {

    //The key list contains the current state of the keyboard.
    private KeyInput keys;
    
    //The mouse information
    private MouseInput mouse;

    //the event list contains the names of all actions called for this event.
    private ArrayList eventList;

    //the time of the event.
    private float time;

    /**
     * instantiates a default InputActionEvent object. The keys, eventList and
     * time are set to null or 0.
     *  
     */
    public InputActionEvent() {
        this(null, null, null, 0);
    }

    /**
     * instantiates a new InputActionEvent object. The keys, eventList and time
     * are set during creation.
     * 
     * @param keys
     *            the key state.
     * @param eventList
     *            the list of called actions.
     * @param time
     *            the time of the event.
     */
    public InputActionEvent(KeyInput keys, MouseInput mouse, ArrayList eventList, float time) {
        this.keys = keys;
        this.eventList = eventList;
        this.time = time;
    }

    /**
     * returns the event list. This list contains all actions called for this
     * event.
     * 
     * @return Returns the eventList.
     */
    public ArrayList getEventList() {
        return eventList;
    }

    /**
     * sets the event list. This list contains all actions called for this
     * event.
     * 
     * @param eventList
     *            The eventList to set.
     */
    public void setEventList(ArrayList eventList) {
        this.eventList = eventList;
    }

    /**
     * returns the time the event occured.
     * 
     * @return Returns the time.
     */
    public float getTime() {
        return time;
    }

    /**
     * sets the time the event occured.
     * 
     * @param time
     *            The time to set.
     */
    public void setTime(float time) {
        this.time = time;
    }

    /**
     * returns the list of keys.
     * 
     * @return Returns the keys.
     */
    public KeyInput getKeys() {
        return keys;
    }

    /**
     * sets the list of keys.
     * 
     * @param keys
     *            The keys to set.
     */
    public void setKeys(KeyInput keys) {
        this.keys = keys;
    }

    /**
     * 
     * <code>isKeyDown</code> determines if a particular key is pressed at the
     * time of the event. The key is defined as a constant from KeyInput. If the
     * key is pressed, true is returned else, false is returned.
     * 
     * @param key
     *            the key to check (defined as a constant in KeyInput).
     * @return true if the supplied key is pressed, false otherwise.
     */
    public boolean isKeyDown(int key) {
        return keys.isKeyDown(key);
    }

    /**
     * 
     * <code>containsEvent</code> determines if a event defined by its name is
     * currently contained in the list.
     * 
     * @param event
     *            the event to check.
     * @return true if the event is in the list, false otherwise.
     */
    public boolean containsEvent(String event) {
        return eventList.contains(event);
    }
    /**
     * @return Returns the mouse.
     */
    public MouseInput getMouse() {
        return mouse;
    }
    /**
     * @param mouse The mouse to set.
     */
    public void setMouse(MouseInput mouse) {
        this.mouse = mouse;
    }
}