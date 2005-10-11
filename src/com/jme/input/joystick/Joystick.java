package com.jme.input.joystick;

/**
 * Represents a single joystick device.
 * @author Matthew D. Hicks, Irrisor
 */
public interface Joystick {
    /**
     * Query the names of the joysticks axes. Indices correspond with {@link #getAxisValue(int)}.
     * @return an array of axis names
     */
    String[] getAxisNames();

    /**
     * @return number of axes this joystick has
     */
    int getAxisCount();

    /**
     * Query the current position of a single axis.
     * Remember to call {@link com.jme.input.joystick.JoystickInput#update()} prior to using these method.
     * @param axis index of the axis of interest
     * @return the current position of the axis between -1 and 1
     */
    float getAxisValue( int axis );

    /**
     * @return number of buttons this joystick has
     */
    int getButtonCount();

    /**
     * Query state of a button.
     * Remember to call {@link com.jme.input.joystick.JoystickInput#update()} prior to using these method.
     * @param button index of a button (0 <= index < {@link #getButtonCount()})
     * @return true if button is currently pressed
     */
    boolean isButtonPressed( int button );

    /**
     * @return name of this joystick
     */
    String getName();

    /**
     * Cause the rumbler (if existent) for specified axis to change force.
     * @param axis index of the axis to be rumbled
     * @param intensity new force intensity
     */
    void rumble( int axis, float intensity );
}
