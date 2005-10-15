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
import java.util.HashMap;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.input.action.MouseInputAction;
import com.jme.util.LoggingSystem;

/**
 * <code>InputHandler</code> handles mouse and key inputs. Inputs are added
 * and whenever update is called whenever action needs to take place (usually
 * every frame). Mouse actions are performed every update call. Keyboard actions
 * are performed only if the correct key is pressed.
 *
 * @author Mark Powell
 * @author Jack Lindamood - (javadoc only)
 * @version $Id: InputHandler.java,v 1.28 2005-10-15 18:04:45 irrisor Exp $
 */
public class InputHandler extends AbstractInputHandler {

    /**
     * List of Triggers
     */
    private ArrayList triggers = new ArrayList();

    protected static abstract class Trigger {
        protected Trigger( String triggerName, InputAction action, boolean allowRepeats ) {
            this.action = action;
            this.allowRepeats = allowRepeats;
            this.name = triggerName;
        }

        protected String name;
        protected boolean allowRepeats;
        protected InputAction action;

        public abstract boolean isTriggered();
    }

    static class CommandTrigger extends Trigger {
        protected CommandTrigger( String triggerName, InputAction action, boolean allowRepeats ) {
            super( triggerName, action, allowRepeats );
        }

        public boolean isTriggered() {
            return name == null ||
                    KeyBindingManager.getKeyBindingManager().isValidCommand( name, allowRepeats );
        }
    }

    /**
     * The mouse where valid mouse actions are taken from in update.
     */
    protected Mouse mouse;

    /**
     * event that will be used to call each action this frame
     */
    protected InputActionEvent event = new InputActionEvent();

    /**
     * Creates a new input handler. By default, there are no keyboard actions or
     * mouse actions defined.
     */
    public InputHandler() {
    }

    /**
     * @return keyboard manager.
     * @deprecated use {@link KeyBindingManager#getKeyBindingManager()}
     */
    public KeyBindingManager getKeyBindingManager() {
        //todo: remove this method in .11
        return KeyBindingManager.getKeyBindingManager();
    }

    /**
     * Sets the mouse to receive mouse inputs from.
     *
     * @param mouse This handler's new mouse.
     */
    public void setMouse( Mouse mouse ) {
        this.mouse = mouse;
    }

    /**
     * Returns the mouse currently receiving inputs by this handler.
     *
     * @return This handler's mouse.
     */
    public Mouse getMouse() {
        return mouse;
    }

    /**
     * @see com.jme.input.action.InputAction#setSpeed(float)
     * @deprecated InputHander does not distinguish between key and mouse actions any more
     *             - use {@link #setActionSpeed} to change speed of all actions
     */
    public void setKeySpeed( float speed ) {
        //todo: remove this method in .11
        LoggingSystem.getLogger().warning( "use of deprecated method that changed behaviour!" );
        setActionSpeed( speed );
    }

    /**
     * @see com.jme.input.action.InputAction#setSpeed(float)
     * @deprecated InputHander does not distinguish between key and mouse actions any more
     *             - use {@link #setActionSpeed} to change speed of all actions
     */
    public void setMouseSpeed( float speed ) {
        //todo: remove this method in .11
        LoggingSystem.getLogger().warning( "use of deprecated method that changed behaviour!" );
        setActionSpeed( speed );
    }

    /**
     * Sets the speed of all actions currently registered with this handler to
     * the given value.
     *
     * @param speed The new speed for all currently registered actions.
     * @see com.jme.input.action.InputAction#setSpeed(float)
     */
    public void setActionSpeed( float speed ) {
        for ( int i = triggers.size() - 1; i >= 0; i-- ) {
            ( (Trigger) triggers.get( i ) ).action.setSpeed( speed );
        }
    }

    /**
     * @deprecated use {@link #addAction(com.jme.input.action.InputAction, String, boolean)} to specify needed parameters
     * @noinspection deprecation
     */
    public void addAction( KeyInputAction inputAction ) {
        addAction( inputAction, inputAction.getKey(), inputAction.allowsRepeats() );
    }

    /**
     * Adds an input action to be invoked by this handler during update.
     *
     * @param inputAction    the input action to be added
     * @param triggerCommand the command to trigger this action (registered with {@link KeyBindingManager}), if null
     *                       the action is invoked on each call of {@link #update}
     * @param allowRepeats   true to invoke the action every call of update the trigger is lit, false to invoke
     *                       the action only once every time the trigger is lit
     * @noinspection deprecation
     */
    public void addAction( InputAction inputAction, String triggerCommand, boolean allowRepeats ) {
        triggers.add( new CommandTrigger( triggerCommand, inputAction, allowRepeats ) );
    }

    /**
     * Registeres a single key as command in {@link KeyBindingManager} and adds an input
     * action to be invoked by this handler during update.
     *
     * @param inputAction    the input action to be added
     * @param triggerCommand the command to trigger this action, may not be null (unlike in
     *                       {@link #addAction(com.jme.input.action.InputAction, String, boolean)})
     * @param keyCode        the keyCode to register at {@link KeyBindingManager} for the command
     * @param allowRepeats   true to invoke the action every call of update the trigger is lit, false to invoke
     *                       the action only once every time the trigger is lit
     * @noinspection deprecation
     */
    public void addAction( InputAction inputAction, String triggerCommand, int keyCode, boolean allowRepeats ) {
        if ( triggerCommand == null )
        {
            throw new NullPointerException( "triggerCommand may not be null" );
        }
        KeyBindingManager.getKeyBindingManager().add( triggerCommand,  keyCode );
        addAction( inputAction, triggerCommand,  allowRepeats );
    }

    /**
     * @noinspection deprecation
     * @deprecated use {@link #addAction(com.jme.input.action.InputAction, String, boolean)} to specify needed parameters
     */
    public void addKeyboardAction( String command, int keyInputValue, KeyInputAction action ) {
        KeyBindingManager.getKeyBindingManager().set( command, keyInputValue );
        addAction( action, command, action.allowsRepeats() );
    }

    /**
     * Adds a mouse input action to be polled by this handler during update.
     *
     * @param mouseAction The input action to be added
     */
    public void addAction( MouseInputAction mouseAction ) {
        //todo: mouse actions are called every frame - that should be changed
        addAction( mouseAction, null, true );
    }

    /**
     * Removes a keyboard input action from the list of keyActions that are
     * polled during update.
     *
     * @param inputAction The action to remove.
     */
    public void removeAction( InputAction inputAction ) {
        for ( int i = triggers.size() - 1; i >= 0; i-- ) {
            if ( ( (Trigger) triggers.get( i ) ).action == inputAction ) {
                triggers.remove( i );
            }
        }
    }

    /**
     * Clears all keyboard actions currently stored.
     *
     * @deprecated InputHander does not distinguish between key and mouse actions any more
     *             - use {@link #clearActions} to remove all actions
     */
    public void clearKeyboardActions() {
        //todo: remove this method in .11
        throw new UnsupportedOperationException( "InputHander does not distinguish between key and mouse actions any more" );
    }

    /**
     * Clears all mouse actions currently stored.
     *
     * @deprecated InputHander does not distinguish between key and mouse actions any more
     *             - use {@link #clearActions} to remove all actions
     */
    public void clearMouseActions() {
        //todo: remove this method in .11
        throw new UnsupportedOperationException( "InputHander does not distinguish between key and mouse actions any more" );
    }

    /**
     * Clears all actions currently registered.
     */
    public void clearActions() {
        triggers.clear();
    }

    /**
     * Checks all key and mouse actions to see if they are valid commands. If
     * so, performAction is called on the command with the given time.
     * <br>
     * This method can be invoked while the handler is disabled. Thus the method should
     * check {@link #isEnabled()} and return immediately if it evaluates to false.
     * <br>
     * This method should normally not be overwritten by subclasses. If an InputHandler needs to
     * execute something in each update register an action with triggerCommand = null. Exception to this
     * is an InputHandler that checks additional input types.
     * @see #addAction(com.jme.input.action.InputAction, String, boolean)
     * @param time The time to pass to every key and mouse action that is active.
     */
    public void update( float time ) {
        if ( !isEnabled() ) {
            return;
        }

        event.setTime( time );

        if ( mouse != null ) {
            mouse.update();
        }

        for ( int i = 0; i < triggers.size(); i++ ) {
            Trigger trigger = ( (Trigger) triggers.get( i ) );
            if ( trigger.isTriggered() ) {
                event.setTriggerName( trigger.name );
                trigger.action.performAction( event );
            }
        }

        for ( int i = this.sizeOfAttachedHandlers() - 1; i >= 0; i-- ) {
            InputHandler handler = this.getFromAttachedHandlers( i );
            if ( handler.isEnabled() ) {
                handler.update( time );
            }
        }
    }

    //todo: provide a list of events that occur this frame (update call)

    public static float getFloatProp( HashMap props, String key, float defaultVal ) {
        if ( props == null || props.get( key ) == null ) {
            return defaultVal;
        }
        else {
            return Float.parseFloat( props.get( key ).toString() );
        }
    }

    public static int getIntProp( HashMap props, String key, int defaultVal ) {
        if ( props == null || props.get( key ) == null ) {
            return defaultVal;
        }
        else {
            return Integer.parseInt( props.get( key ).toString() );
        }
    }

    public static boolean getBooleanProp( HashMap props, String key, boolean defaultVal ) {
        if ( props == null || props.get( key ) == null ) {
            return defaultVal;
        }
        else {
            return "true".equalsIgnoreCase( props.get( key ).toString() );
        }
    }

    public static Object getObjectProp( HashMap props, String key, Object defaultVal ) {
        if ( props == null || props.get( key ) == null ) {
            return defaultVal;
        }
        else {
            return props.get( key );
        }
    }


    /**
     * @return true if this handler is currently enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * store the value for field enabled
     */
    private boolean enabled = true;

    /**
     * Enable/disable the handler: disabled handler do not invoke actions and do not update attached handlers.
     *
     * @param value true to enable the handler, false to disable the handler
     */
    public void setEnabled( final boolean value ) {
        final boolean oldValue = this.enabled;
        if ( oldValue != value ) {
            this.enabled = value;
        }
    }

    /**
     * enabled/disables all attached handlers but this handler keeps its status.
     * @param enabled true to enable all attached handlers, false to disable them
     */
    public void setEnabledOfAttachedHandlers( boolean enabled )
    {
        for ( int i = this.sizeOfAttachedHandlers() - 1; i >= 0; i-- ) {
            InputHandler handler = this.getFromAttachedHandlers( i );
            handler.setEnabled( false );
        }
    }

    /**
     * List of InputHandlers
     */
    private ArrayList attachedHandlers;

    /**
     * Attach a handler which should be updated in this handlers update method.
     *
     * @param value handler to attach
     * @return true if handler was not attached before
     */
    public boolean addToAttachedHandlers( InputHandler value ) {
        if ( value != null ) {
            return value.setParent( this );
        }
        else {
            return false;
        }
    }

    /**
     * Attach a handler which should be updated in this handlers update method.
     *
     * @param value handler to attach
     * @return true if handler was not attached before
     */
    private boolean addToAttachedHandlers_internal( InputHandler value ) {
        boolean changed = false;
        if ( value != null ) {
            if ( this.attachedHandlers == null ) {
                this.attachedHandlers = new ArrayList();
            }
            else if ( this.attachedHandlers.contains( value ) ) {
                return false;
            }
            changed = this.attachedHandlers.add( value );
            if ( changed ) {
                value.setParent( this );
            }
        }
        return changed;
    }

    /**
     * Get an element from the attachedHandlers association.
     *
     * @param index index of element to be retrieved
     * @return the element, null if index out of range
     */
    public InputHandler getFromAttachedHandlers( int index ) {
        if ( attachedHandlers != null && index >= 0 && index < attachedHandlers.size() ) {
            return (InputHandler) attachedHandlers.get( index );
        }
        else {
            return null;
        }
    }

    public void removeAllFromAttachedHandlers() {
        for ( int i = this.sizeOfAttachedHandlers() - 1; i >= 0; i-- ) {
            InputHandler handler = this.getFromAttachedHandlers( i );
            this.removeFromAttachedHandlers( handler );
        }
    }

    public boolean removeFromAttachedHandlers( InputHandler value ) {
        boolean changed = false;
        if ( ( this.attachedHandlers != null ) && ( value != null ) ) {
            changed = this.attachedHandlers.remove( value );
            if ( changed ) {
                value.setParent( null );
            }
        }
        return changed;
    }

    /**
     * @return number of attached handlers
     */
    public int sizeOfAttachedHandlers() {
        return ( ( this.attachedHandlers == null )
                ? 0
                : this.attachedHandlers.size() );
    }

    /**
     * store value for field parent
     */
    private InputHandler parent;

    /**
     * Query parent handler.
     *
     * @return current parent
     */
    public InputHandler getParent() {
        return this.parent;
    }

    /**
     * @param value new value for field parent
     * @return true if parent was changed
     */
    protected boolean setParent( InputHandler value ) {
        boolean changed = false;
        final InputHandler oldValue = this.parent;
        if ( oldValue != value ) {
            if ( oldValue != null ) {
                this.parent = null;
                oldValue.removeFromAttachedHandlers( this );
            }
            this.parent = value;
            if ( value != null ) {
                value.addToAttachedHandlers_internal( this );
            }
            changed = true;
        }
        return changed;
    }
}