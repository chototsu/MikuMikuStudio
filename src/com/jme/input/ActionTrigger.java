package com.jme.input;

import java.util.ArrayList;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

/**
 * Stores data about an action trigger. Subclasses provide the actual trigger functionality.
 * Triggers are used by {@link InputHandler} to decouple event
 * occurence and action invocation.
 * <br>
 * The most important methods of the trigger are {@link #activate()} and {@link #deactivate()}: they add and remove this
 * trigger from the list of active trigger in the InputHandler. For all active triggers the {@link #performAction}
 * method is called in the {@link InputHandler#update(float)} method.
 * <br>
 * A trigger also registers itself with an input handler and can be removed from the list of triggers via the
 * {@link #remove()} method.
 */
public abstract class ActionTrigger {
    protected final InputHandler inputHandler;

    /**
     * Create a new action trigger for a fixed input handler.
     *
     * @param inputHandler handler this trigger belongs to (cannot be changed)
     * @param triggerName  name of this trigger (usually a button or axis name)
     * @param action       action that is performed by this trigger
     * @param allowRepeats true to allow multiple action invocations per event
     */
    protected ActionTrigger( InputHandler inputHandler, String triggerName, InputAction action, boolean allowRepeats ) {
        this.inputHandler = inputHandler;
        this.action = action;
        this.allowRepeats = allowRepeats;
        this.name = triggerName;
        synchronized ( this ) {
            if ( inputHandler.allTriggers == null ) {
                inputHandler.allTriggers = new ArrayList();
            }
            inputHandler.allTriggers.add( this );
        }
    }

    /**
     * Remove this trigger.
     */
    protected void remove() {
        synchronized ( inputHandler ) {
            deactivate();
            inputHandler.allTriggers.remove( this );
        }
    }

    protected final String name;
    protected final boolean allowRepeats;
    protected final InputAction action;

    /**
     * Invoked to activate or deactivate a trigger on specific event. The data in the
     * parameters depend on the kind of trigger. Defaults for each parameter (set if value for parameter
     * is unknown or not applicable) are given below. The trigger should activate or deactivate itself
     * if appropriate.
     *
     * @param character some character data associated with the event, default '\0'.
     *                  <br>example: keyboard character
     * @param index     index of the device part that caused the event, default -1, >= 0 if valid
     *                  <br>example: mouse button index, joystick axis index
     * @param position  new position of the device part that caused the event, default NaN, common range [-1;1]
     *                  <br>example: joystick axis position
     * @param delta     position delta of the device part that caused the event, default NaN, common range [-1;1]
     *                  <br>example: joystick axis delta
     * @param pressed   indicates if a button was pressed or released, default: false
     *                  <br>example: true if joystick button is pressed, false if joystick button is released
     * @param data      any trigger specific data
     *                  <br>example: joystick triggers get the Joystick instance for fast comparison
     * @see #activate()
     * @see #deactivate()
     */
    public abstract void checkActivation( char character, int index,
                                          float position, float delta, boolean pressed, Object data );

    /**
     * Called by InputHandler to fill info about the trigger into an event. Commonly overwritten by trigger
     * implementations to provide additional info.
     *
     * @param event where to put the information
     */
    protected void putTriggerInfo( InputActionEvent event ) {
        event.setTriggerName( name );
        event.setTriggerAllowsRepeats( allowRepeats );
        event.setTriggerDevice( getDeviceName() );
        event.setTriggerCharacter( '\0' );
        event.setTriggerDelta( 0 );
        event.setTriggerIndex( 0 );
        event.setTriggerPosition( 0 );
        event.setTriggerPressed( false );
    }

    /**
     * @return name of the device this trigger belongs to
     */
    protected abstract String getDeviceName();

    /**
     * true while in the active triggers list of the InputHandler.
     */
    private boolean active;

    /**
     * add this trigger to the list of active trigger in the InputHandler.
     */
    protected final void activate() {
        synchronized ( inputHandler ) {
            if ( !active ) {
                active = true;
                ActionTrigger firstActiveTrigger = inputHandler.activeTriggers;
                inputHandler.activeTriggers = this;
                this.setNext( firstActiveTrigger );
            }
        }
    }

    /**
     * remove this trigger from the list of active trigger in the InputHandler.
     */
    protected final void deactivate() {
        synchronized ( inputHandler ) {
            if ( active ) {
                active = false;
                ActionTrigger firstActiveTrigger = inputHandler.activeTriggers;
                if ( firstActiveTrigger == this ) {
                    inputHandler.activeTriggers = getNext();
                    setNext( null );
                }
                else {
                    this.getPrevious().previous.setNext( this.getNext() );
                }
            }
        }
    }

    /**
     * Used to maintain a linked list of active triggers.
     */
    private ActionTrigger next;

    /**
     * Used to maintain a linked list of active triggers.
     *
     * @return current value of the field next
     */
    ActionTrigger getNext() {
        return this.next;
    }

    /**
     * Used to maintain a linked list of active triggers.
     *
     * @param value new value for field next
     * @return true if next was changed
     */
    boolean setNext( final ActionTrigger value ) {
        final ActionTrigger oldValue = this.next;
        boolean changed = false;
        if ( oldValue != value ) {
            if ( oldValue != null ) {
                this.next = null;
                oldValue.setPrevious( null );
            }
            this.next = value;
            if ( value != null ) {
                value.setPrevious( this );
            }
            changed = true;
        }
        return changed;
    }

    /**
     * Used to maintain a linked list of active triggers.
     */
    private ActionTrigger previous;

    /**
     * @return current value of the field previous
     */
    ActionTrigger getPrevious() {
        return this.previous;
    }

    /**
     * Used to maintain a linked list of active triggers.
     *
     * @param value new value for field previous
     * @return true if previous was changed
     */
    boolean setPrevious( final ActionTrigger value ) {
        final ActionTrigger oldValue = this.previous;
        boolean changed = false;
        if ( oldValue != value ) {
            if ( oldValue != null ) {
                this.previous = null;
                oldValue.setNext( null );
            }
            this.previous = value;
            if ( value != null ) {
                value.setNext( this );
            }
            changed = true;
        }
        return changed;
    }

    /**
     * Perform the action and deactivate the trigger if it does not allow repeats.
     *
     * @param event info about the event that caused the action
     */
    public void performAction( InputActionEvent event ) {
        putTriggerInfo( event );
        action.performAction( event );
        if ( !allowRepeats ) {
            deactivate();
        }
    }

    /**
     * @return true if the trigger was activated
     * @see ActionTrigger
     */
    public final boolean isActive() {
        return active;
    }

    /**
     * Trigger implementation for using {@link KeyBindingManager} as trigger.
     */
    static class CommandTrigger extends ActionTrigger {
        protected CommandTrigger( InputHandler handler, String triggerName, InputAction action, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            activate();
        }

        public void performAction( InputActionEvent event ) {
            if ( name == null ||
                    KeyBindingManager.getKeyBindingManager().isValidCommand( name, allowRepeats ) ) {
                super.performAction( event );
            }
        }

        public void checkActivation( char character, int index, float position, float delta, boolean pressed, Object data ) {
            //is a trigger that is checked each frame
        }

        protected String getDeviceName() {
            return "command";
        }
    }

}
