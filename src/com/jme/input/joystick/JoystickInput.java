package com.jme.input.joystick;

import java.util.ArrayList;

import com.jme.input.joystick.lwjgl.LWJGLJoystickInput;
import com.jme.input.InputSystem;
import com.jme.input.Input;

/**
 * Manager for attached Joysticks. Singleton - use the {@link #get()} method.
 * Joysticks can be polled by calling {@link #update()}.
 * The startUpdateThread(int) method is commented out because of probable threading problems.
 *
 * @author Matthew D. Hicks, Irrisor
 */
public abstract class JoystickInput extends Input {

    /**
     * Only instance.
     */
    private static JoystickInput instance;

    /**
     * Initialize (if needed) and return the JoystickInput.
     * Implementation is determined by querying {@link #getProvider()}.
     * @return the only instance of the joystick manager
     */
    public static JoystickInput get() {
        if ( instance == null ) {
            if ( InputSystem.INPUT_SYSTEM_LWJGL.equals( getProvider() ) )
            {
                instance = new LWJGLJoystickInput(){};
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported provider: " + getProvider() );
            }
        }
        return instance;
    }

    /**
     * Protect contructor to avoid direct invocation.
     */
    protected JoystickInput() {
    }

    /**
     * Query current provider for input.
     *
     * @return currently selected provider
     */
    public static String getProvider() {
        return provider;
    }

    /**
     * store the value for field provider
     */
    private static String provider = InputSystem.INPUT_SYSTEM_LWJGL;

    /**
     * Change the provider used for joystick input. Default is {@link InputSystem.INPUT_SYSTEM_LWJGL}.
     *
     * @param value new provider
     * @throws IllegalStateException if called after first call of {@link #get()}. Note that get is called when
     * creating the DisplaySystem.
     */
    public static void setProvider( final String value ) {
        if ( instance != null )
        {
            throw new IllegalStateException( "Provider may only be changed before input is created!" );
        }
        provider = value;
    }

//    /**
//     * Thread for asynchronous polling.
//     */
//    protected class UpdateThread extends Thread {
//        /**
//         * @param updateInterval number milliseconds between each update
//         * @see Thread#Thread(String)
//         */
//        public UpdateThread( int updateInterval ) {
//            super( "joystick update thread" );
//            this.updateInterval = updateInterval;
//        }
//
//        /**
//         * sleep time.
//         */
//        private int updateInterval;
//
//        /**
//         * @see Thread#run()
//         */
//        public void run() {
//            final int updateInterval = this.updateInterval;
//            if ( getJoystickCount() > 0 ) {
//                while ( !isInterrupted() ) {
//                    update();
//                    try {
//                        Thread.sleep( updateInterval );
//                    } catch ( InterruptedException exc ) {
//                        //ok then just leave normally
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Thread for asynchronous update.
//     */
//    private UpdateThread thread;
//
//    /**
//     * Spawn a deamon thread to call the {@link #update} method in regular intervals.
//     * The thread calls the method, sleeps <i>updateInterval</i> milliseconds and repeats.
//     * @param updateInterval interval time for updating the joysticks in milliseconds
//     */
//    public void startUpdateThread( int updateInterval ) {
//        if ( thread == null || !thread.isAlive() ) {
//            thread = new UpdateThread( updateInterval );
//            thread.setDaemon( true );
//            thread.start();
//        }
//    }

    /**
     * list of event listeners.
     */
    protected ArrayList listeners;

    /**
     * Subscribe a listener to receive joystick events. Enable event generation.
     * @param listener to be subscribed
     */
    public void addListener( JoystickInputListener listener ) {
        if ( listeners == null ) {
            listeners = new ArrayList();
        }

        listeners.add( listener );
    }

    /**
     * Unsubscribe a listener. Disable event generation if no more listeners.
     * @see #addListener(JoystickInputListener)
     * @param listener to be unsuscribed
     */
    public void removeListener( JoystickInputListener listener ) {
        if ( listeners != null ) {
            listeners.remove( listener );
        }
    }

    /**
     * Remove all listeners and disable event generation.
     */
    public void removeListeners() {
        if ( listeners != null ) {
            listeners.clear();
        }
    }

    /**
     * @return number of attached game controllers
     */
    public abstract int getJoystickCount();

    /**
     * Game controller at specified index.
     * @param index index of the controller (0 <= index <= {@link #getJoystickCount()})
     * @return game controller
     */
    public abstract Joystick getJoystick( int index );

    /**
     * This is a method to obtain a single joystick. It's simple to used but not
     * recommended (user may have multiple joysticks!).
     * @return what the implementation thinks is the main joystick, not null!
     */
    public abstract Joystick getDefaultJoystick();

    /**
     * Destroy the input if it was initialized.
     */
    public static void destroyIfInitalized() {
        if ( instance != null )
        {
            instance.destroy();
            instance = null;
        }
    }

    protected abstract void destroy();
}