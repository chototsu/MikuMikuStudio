package com.jme.input.joystick;

/**
 * Listener receiving event for every axis- and button-change of all the joysticks.
 * @see JoystickInput
 * @author Matthew D. Hicks, Irrisor
 */
public interface JoystickInputListener {
    /**
     * Invoked when a button was pressed or released.
     * @param controller joystick the button belongs to
     * @param button index of the button
     * @param pressed true if button was pressed, false if released
     */
    public void onButton( Joystick controller, int button, boolean pressed );

    /**
     * Invoked when an axis has changed it's value.
     * @param controller joystick the axis belongs to
     * @param axis index of the axis
     * @param axisValue new value of the axis
     */
    public void onAxis( Joystick controller, int axis, float axisValue );
}
