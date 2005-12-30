package com.jme.input.util;

import java.util.ArrayList;

import com.jme.input.InputHandler;
import com.jme.input.ActionTrigger;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

/**
 * This class can be used to create synthetic axes for {@link InputHandler}s. As an example see {@link TwoButtonAxis}.
 */
public abstract class SyntheticAxis {
    /**
     * name of this axis.
     */
    protected final String name;
    /**
     * store the index in util device
     */
    private int index;
    /**
     * list of triggers
     */
    private ArrayList axisTriggers = new ArrayList();

    public SyntheticAxis( String name ) {
        this.name = name;
        UtilInputHandlerDevice.get().addAxis( this );
    }

    /**
     * @return the name of this axis
     */
    public String getName() {
        return name;
    }

    /**
     * @return index of this axis (used when registering with InputHandler)
     * @see #getDeviceName()
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * setter for field axis
     */
    void setIndex(final int value) {
        this.index = value;
    }

    /**
     * @return name of the virtual device this axis is attached to (used when registering with InputHandler)
     * @see #getIndex()
     */
    public final String getDeviceName() {
        return UtilInputHandlerDevice.DEVICE_UTIL;
    }

    protected void createTrigger( InputHandler inputHandler, InputAction action, boolean allowRepeats ) {
        new AxisTrigger( inputHandler, action, allowRepeats );
    }

    /**
     * @param trigger what to add to list of triggers
     */
    private void add( AxisTrigger trigger ) {
        axisTriggers.add( trigger );
    }

    /**
     * @param trigger what to remove from list of triggers
     */
    private void remove( AxisTrigger trigger ) {
        axisTriggers.remove( trigger );
    }

    /**
     * check all triggers
     * @see com.jme.input.ActionTrigger#checkActivation(char, int, float, float, boolean, Object)
     */
    protected void trigger( float delta, char character, float value, boolean pressed, Object data ) {
        for ( int i = axisTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = (ActionTrigger) axisTriggers.get( i );
            trigger.checkActivation( character, getIndex(), value, delta, pressed, data );
        }
    }

    /**
     * trigger for simulating axis
     */
    protected class AxisTrigger extends ActionTrigger {

        public AxisTrigger( InputHandler handler, InputAction action, boolean allowRepeats ) {
            super( handler, SyntheticAxis.this.getName(), action, allowRepeats );
            SyntheticAxis.this.add( this );
            if ( allowRepeats ) {
                activate();
            }
        }

        protected void remove() {
            super.remove();
            SyntheticAxis.this.remove( this );
        }

        private float delta;
        private float position;

        protected void putTriggerInfo( InputActionEvent event ) {
            super.putTriggerInfo( event );
            event.setTriggerIndex( getIndex() );
            event.setTriggerDelta( delta );
            event.setTriggerPosition( position );
        }

        protected final String getDeviceName() {
            return UtilInputHandlerDevice.DEVICE_UTIL;
        }

        public void checkActivation( char character, int axisIndex, float position, float delta, boolean pressed, Object data ) {
            if ( axisIndex == getIndex() ) {
                this.delta = delta;
                this.position = position;
                if ( !allowRepeats ) {
                    activate();
                }
            }
        }
    }
}
