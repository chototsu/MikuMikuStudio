package com.jme.input;

import com.jme.input.action.InputAction;

/**
 * Devices are used in {@link InputHandler} to create different types of {@link ActionTrigger}s. The method
 * {@link #createTriggers}(...) is called by InputHandler when actions are registered via
 * {@link InputHandler#addAction(InputAction, String, int, int, boolean)}.
 *
 * @see com.jme.input.mouse.MouseInputHandlerDevice
 * @see com.jme.input.keyboard.KeyboardInputHandlerDevice
 * @see com.jme.input.joystick.JoystickInputHandlerDevice
 */
public abstract class InputHandlerDevice {
    /**
     * Store name of this device. The name may not change, because it is used as key.
     */
    private final String name;

    /**
     * @param name name of the device
     */
    protected InputHandlerDevice( String name ) {
        if ( name == null ) {
            throw new NullPointerException();
        }
        this.name = name;
    }

    /**
     * Query name of this device. Note: The name may not change, because it is used as key, that's why this method
     * is final (avoid overrriding and returning another name).
     *
     * @return name of this device
     */
    public final String getName() {
        return name;
    }

    /**
     * Creates device specific trigger(s) for specified axes and buttons (the triggers register themselves at the
     * inputHandler).
     *
     * @param action
     * @param axis
     * @param button
     * @param allowRepeats
     * @param inputHandler input handler for the triggers
     */
    protected abstract void createTriggers( InputAction action, int axis, int button, boolean allowRepeats, InputHandler inputHandler );
}
