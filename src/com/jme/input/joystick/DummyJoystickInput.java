package com.jme.input.joystick;

/**
 * Dummy JoystickInput to disable joystick support.
 */
public class DummyJoystickInput extends JoystickInput {
    private DummyJoystick dummyJoystick = new DummyJoystick();

    /**
     * @return number of attached game controllers
     */
    public int getJoystickCount() {
        return 0;
    }

    /**
     * Game controller at specified index.
     *
     * @param index index of the controller (0 <= index <= {@link #getJoystickCount()})
     * @return game controller
     */
    public Joystick getJoystick( int index ) {
        return null;
    }

    /**
     * This is a method to obtain a single joystick. It's simple to used but not
     * recommended (user may have multiple joysticks!).
     *
     * @return what the implementation thinks is the main joystick, not null!
     */
    public Joystick getDefaultJoystick() {
        return dummyJoystick;
    }

    protected void destroy() {

    }

    /**
     * Poll data for this input system part (update the values) and send events to all listeners
     * (events will not be generated if no listeners were added via addListener).
     */
    public void update() {

    }

    public static class DummyJoystick implements Joystick {
        public void rumble( int axis, float intensity ) {
        }

        public String[] getAxisNames() {
            return new String[0];
        }

        public int getAxisCount() {
            return 0;
        }

        public float getAxisValue( int axis ) {
            return 0;
        }

        public int getButtonCount() {
            return 0;
        }

        public boolean isButtonPressed( int button ) {
            return false;
        }

        public String getName() {
            return "Dummy";
        }

        public void setDeadZone( int axis, float value ) {

        }
    }
}
