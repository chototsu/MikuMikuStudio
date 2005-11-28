package com.jme.input.util;

import com.jme.input.ActionTrigger;
import com.jme.input.InputHandler;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

import java.util.ArrayList;

/**
 * This is a utility class to simulate an axis from two buttons (or keys). For a usage example see TestInputHandler.
 */
public class TwoButtonAxis {
    /**
     * name of this axis.
     */
    private final String name;

    /**
     * @return the name of this axis
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of this new axis
     */
    public TwoButtonAxis(String name) {
        this.name = name;
        UtilInputHandlerDevice.get().addAxis( this );
        getDecreaseAction().setSpeed( 1 );
        getIncreaseAction().setSpeed( 1 );
    }

    /**
     * trigger for simulating axis
     */
    protected class AxisTrigger extends ActionTrigger {

        public AxisTrigger( InputHandler handler, String triggerName, InputAction action, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            TwoButtonAxis.this.add( this );
            if ( allowRepeats ) {
                activate();
            }
        }

        protected void remove() {
            super.remove();
            TwoButtonAxis.this.remove( this );
        }

        private float delta;
        private float position;

        protected void putTriggerInfo( InputActionEvent event ) {
            super.putTriggerInfo( event );
            event.setTriggerIndex( getIndex() );
            event.setTriggerDelta( delta );
            event.setTriggerPosition( position );
        }

        protected String getDeviceName() {
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


    /**
     * @return index of this axis (used when registering with InputHandler)
     * @see #getDeviceName()
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * store the index in util device
     */
    private int index;

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
    public String getDeviceName() {
        return UtilInputHandlerDevice.DEVICE_UTIL;
    }

    /**
     * list of triggers
     */
    private ArrayList axisTriggers = new ArrayList();

    /**
     * @param trigger what to add to list of triggers
     */
    void add( AxisTrigger trigger ) {
        axisTriggers.add( trigger );
    }

    /**
     * @param trigger what to remove from list of triggers
     */
    void remove( AxisTrigger trigger ) {
        axisTriggers.remove( trigger );
    }

    /**
     * current value of this axis
     */
    private float value = 0;

    /**
     * @return current value/position of this axis
     */
    public float getValue() {
        return value;
    }

    /**
     * @see #isDiscreet()
     */
    private boolean discreet = false;

    /**
     * @return true if a single keystoke changes the axis value by the action speed, false if continuous
     * (multiplied with frame time)
     */
    public boolean isDiscreet() {
        return discreet;
    }

    /**
     * @param discreet true if a single keystoke should change the axis value by the action speed, false if continuous
     * (multiplied with frame time)
     */
    public void setDiscreet(boolean discreet) {
        this.discreet = discreet;
    }

    /**
     * action to decrease the value
     */
    private final InputAction decreaseAction = new InputAction() {
        public void performAction(InputActionEvent evt) {
            if ( discreet )
            {
                float increase = -getSpeed();
                value += increase;
                trigger(increase);
            }
            else
            {
                float increase = -getSpeed() * evt.getTime();
                value += increase;
                trigger(increase);
            }
        }
    };

    /**
     * action to increase the value
     */
    private final InputAction increaseAction = new InputAction() {
        public void performAction(InputActionEvent evt) {
            if ( discreet )
            {
                float increase = getSpeed();
                value += increase;
                trigger(increase);
            }
            else
            {
                float increase = getSpeed() * evt.getTime();
                value += increase;
                trigger(increase);
            }
        }
    };

    /**
     * @param increase trigger axis event
     */
    private void trigger(float increase) {
        for ( int i = axisTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = (ActionTrigger) axisTriggers.get( i );
            trigger.checkActivation( '\0', getIndex(), value, increase, false, null );
        }
    }

    /**
     * The returned action should be subscribed with an InputHandler to be invoked any time the axis should decrease.
     * When {@link #isDiscreet()} is true it is commonly registed with allowRepeats==false, while allowRepeats should be
     * true when {@link #isDiscreet()} is false.
     * @return the action that decreases the axis value
     */
    public InputAction getDecreaseAction() {
        return decreaseAction;
    }

    /**
     * The returned action should be subscribed with an InputHandler to be invoked any time the axis should increase.
     * When {@link #isDiscreet()} is true it is commonly registed with allowRepeats==false, while allowRepeats should be
     * true when {@link #isDiscreet()} is false.
     * @return the action that increases the axis value
     */
    public InputAction getIncreaseAction() {
        return increaseAction;
    }
}
