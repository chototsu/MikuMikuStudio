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

package com.jme.input;

import java.util.ArrayList;

import com.jme.input.lwjgl.LWJGLMouseInput;
import com.jmex.awt.input.AWTMouseInput;


/**
 * <code>MouseInput</code> defines an interface to communicate with the mouse
 * input device.
 * The status of spcific buttons can be queried via the {@link #isButtonDown}
 * method. Position data can be queried by various get methods.
 * For each button that is pressed or released as well as for movement of
 * mouse or wheel an event is generated which
 * can be received by a {@link MouseInputListener}, these are subsribed via
 * {@link #addListener(MouseInputListener)}. Handling of events is done inside the
 * {@link #update} method.
 * @author Mark Powell
 * @version $Id: MouseInput.java,v 1.10 2005-10-12 03:44:39 renanse Exp $
 */
public abstract class MouseInput extends Input {

    private static MouseInput instance;
    /**
     * list of event listeners.
     */
    protected ArrayList listeners;

    /**
     * @return the input instance, implementation is determined by querying {@link #getProvider()}
     */
    public static MouseInput get()
    {
        if ( instance == null )
        {
            if ( InputSystem.INPUT_SYSTEM_LWJGL.equalsIgnoreCase( getProvider() ) )
            {
                instance = new LWJGLMouseInput(){};
            }
            else if ( InputSystem.INPUT_SYSTEM_AWT.equalsIgnoreCase( getProvider() ) )
            {
                instance = new AWTMouseInput(){};
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported provider: " + getProvider() );
            }
        }
        return instance;
    }


    /**
     * Query current provider for input.
     *
     * @return currently selected provider
     */
    public static String getProvider() {
        return provider;
    }

    /**
     * store the value for field provider
     */
    private static String provider = InputSystem.INPUT_SYSTEM_LWJGL;

    /**
     * Change the provider used for mouse input. Default is {@link InputSystem.INPUT_SYSTEM_LWJGL}.
     *
     * @param value new provider
     * @throws IllegalStateException if called after first call of {@link #get()}. Note that get is called when
     * creating the DisplaySystem.
     */
    public static void setProvider( final String value ) {
        if ( instance != null )
        {
            throw new IllegalStateException( "Provider may only be changed before input is created!" );
        }
        provider = value;
    }

    /**
     * <code>destroy</code> cleans up the native mouse interface.
     * Destroy is protected now - please is {@link #destroyIfInitalized()}.
     */
    protected abstract void destroy();

    /**
     *
     * <code>getButtonIndex</code> gets the button code for a given button
     * name.
     * @param buttonName the name to get the code for.
     * @return the code for the given button name.
     */
    public abstract int getButtonIndex(String buttonName);

    /**
     *
     * <code>isButtonDown</code> returns true if a given button is pressed,
     * false if it is not pressed.
     * @param buttonCode the button code to check.
     * @return true if the button is pressed, false otherwise.
     */
    public abstract boolean isButtonDown(int buttonCode);

    /**
     *
     * <code>getButtonName</code> gets the button name for a given button
     * code.
     * @param buttonIndex the code to get the name for.
     * @return the name for the given button code.
     */
    public abstract String getButtonName(int buttonIndex);

    /**
     *
     * <code>isCreated</code> returns true if the mouse input is created,
     * false otherwise.
     * @return true if the mouse input is created, false otherwise.
     */
    public abstract boolean isCreated();

    /**
     *
     * <code>getWheelDelta</code> gets the change in the mouse wheel.
     * @return the change in the mouse wheel.
     */
    public abstract int getWheelDelta();

    /**
     *
     * <code>getXDelta</code> gets the change along the x axis.
     * @return the change along the x axis.
     */
    public abstract int getXDelta();

    /**
     *
     * <code>getYDelta</code> gets the change along the y axis.
     * @return the change along the y axis.
     */
    public abstract int getYDelta();

    /**
     *
     * <code>getXAbsolute</code> gets the absolute x axis value.
     * @return the absolute x axis value.
     */
    public abstract int getXAbsolute();

    /**
     *
     * <code>getYAbsolute</code> gets the absolute y axis value.
     * @return the absolute y axis value.
     */
    public abstract int getYAbsolute();

    /**
     * Updates the state of the mouse (position and button states). Invokes event listeners synchronously.
     */
    public abstract void update();

    //todo:
    /**
     * <code>setCursorVisible</code> sets the visiblity of the hardware cursor.
     * @param v true turns the cursor on false turns it off
     */
    public abstract void setCursorVisible(boolean v);

    /**
     * <code>isCursorVisible</code>
     * @return the visibility of the hardware cursor
     */
    public abstract boolean isCursorVisible();

    /**
     * Subscribe a listener to receive mouse events. Enable event generation.
     * @param listener to be subscribed
     */
    public void addListener( MouseInputListener listener ) {
        if ( listeners == null ) {
            listeners = new ArrayList();
        }

        listeners.add( listener );
    }

    /**
     * Unsubscribe a listener. Disable event generation if no more listeners.
     * @see #addListener(com.jme.input.MouseInputListener)
     * @param listener to be unsuscribed
     */
    public void removeListener( MouseInputListener listener ) {
        if ( listeners != null ) {
            listeners.remove( listener );
        }
    }

    /**
     * Remove all listeners and disable event generation.
     */
    public void removeListeners() {
        if ( listeners != null ) {
            listeners.clear();
        }
    }

    /**
     * Destroy the input if it was initialized.
     */
    public static void destroyIfInitalized() {
        if ( instance != null )
        {
            instance.destroy();
            instance = null;
        }
    }
}
