package com.jme.input;

/**
 * This interface is used to receive key events from {@link com.jme.input.KeyInput#update()}.
 */
public interface KeyInputListener {
    /**
     * Called in {@link com.jme.input.KeyInput#update()} whenever a key is pressed or released.
     * @param character character associated with pressed key, 0 if not applicable (e.g. if key released)
     * @param keyCode key code of the pressed/released key
     * @param pressed true if key was pressed, false if released
     */
    void onKey( char character, int keyCode, boolean pressed );
}
