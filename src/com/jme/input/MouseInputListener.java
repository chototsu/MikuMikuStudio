package com.jme.input;

/**
 * This interface is used to receive mouse events from {@link com.jme.input.MouseInput#update()}.
 */
public interface MouseInputListener {
    /**
     * Called in {@link com.jme.input.KeyInput#update()} whenever a mouse button is pressed or released.
     * @param button index of the mouse button that was pressed/released
     * @param pressed true if button was pressed, false if released
     * @param x x position of the mouse while button was pressed/released
     * @param y y position of the mouse while button was pressed/released
     */
    void onButton( int button, boolean pressed, int x, int y );

    /**
     * Called in {@link com.jme.input.KeyInput#update()} whenever the mouse wheel is rotated.
     * @param wheelDelta steps the wheel was rotated
     * @param x x position of the mouse while wheel was rotated
     * @param y y position of the mouse while wheel was rotated
     */
    void onWheel( int wheelDelta, int x, int y );

    /**
     * Called in {@link com.jme.input.KeyInput#update()} whenever the mouse is moved.
     * @param xDelta delta of the x coordinate since the last mouse movement event
     * @param yDelta delta of the y coordinate since the last mouse movement event
     * @param newX x position of the mouse after the mouse was moved
     * @param newY y position of the mouse after the mouse was moved
     */
    void onMove( int xDelta, int yDelta, int newX, int newY );
}
