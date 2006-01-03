package com.jme.input.util;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

/**
 * This is a utility class to simulate an axis from two buttons (or keys). For a usage example see TestInputHandler.
 */
public class TwoButtonAxis extends SyntheticAxis {

    /**
     * @param name the name of this new axis
     */
    public TwoButtonAxis(String name) {
        super( name );
        getDecreaseAction().setSpeed( 1 );
        getIncreaseAction().setSpeed( 1 );
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
                trigger(increase, '\0', TwoButtonAxis.this.value, false, null );
            }
            else
            {
                float increase = -getSpeed() * evt.getTime();
                value += increase;
                trigger(increase, '\0', TwoButtonAxis.this.value, false, null );
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
                trigger(increase, '\0', TwoButtonAxis.this.value, false, null );
            }
            else
            {
                float increase = getSpeed() * evt.getTime();
                value += increase;
                trigger(increase, '\0', TwoButtonAxis.this.value, false, null );
            }
        }
    };

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
