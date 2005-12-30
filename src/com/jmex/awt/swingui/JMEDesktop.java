package com.jmex.awt.swingui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.LoggingSystem;
import com.jmex.awt.input.AWTKeyInput;
import com.jmex.awt.input.AWTMouseInput;

/**
 * A quad that displays a {@link JDesktopPane} as texture. It also converts jME mouse and keyboard events to Swing
 * events. The latter does work for ortho mode only. There are some issues with using multiple of this desktops.
 *
 * @see ImageGraphics
 */
public class JMEDesktop extends Quad {
    private ImageGraphics graphics;
    private JDesktopPane desktop;
    private Texture texture;
    private boolean initialized;
    private int width;
    private int height;

    private boolean showingJFrame = false;
    private final Frame awtWindow;
    private int desktopWidth;
    private int desktopHeight;
    private static final int DOUBLE_CLICK_TIME = 300;
    private final InputHandler inputHandler;
    private JMEDesktop.XUpdateAction xUpdateAction;
    private JMEDesktop.YUpdateAction yUpdateAction;
    private WheelUpdateAction wheelUpdateAction;
    private JMEDesktop.ButtonAction allButtonsUpdateAction;
    private InputAction keyUpdateAction;

    /**
     * @see #setShowingJFrame
     */
    public boolean isShowingJFrame() {
        return showingJFrame;
    }

    /**
     * @param showingJFrame true to display the desktop in a JFrame instead on this quad.
     * @deprecated for debuggin only
     */
    public void setShowingJFrame( boolean showingJFrame ) {
        this.showingJFrame = showingJFrame;
        awtWindow.setVisible( showingJFrame );
        awtWindow.repaint();
    }

    /**
     * Allows to disable input for the whole desktop and to add custom input actions.
     * @return this desktops input hander for input bindings
     * @see #getXUpdateAction()
     * @see #getYUpdateAction()
     * @see #getWheelUpdateAction()
     * @see #getButtonUpdateAction(int)
     * @see #getKeyUpdateAction()
     */
    public InputHandler getInputHandler() {
        return inputHandler;
    }

    /**
     * Create a quad with a Swing-Texture. Creates the quad and the JFrame but do not setup the rest.
     * Call {@link #setup(int, int, boolean, InputHandler)} to finish setup.
     *
     * @param name name of this desktop
     */
    public JMEDesktop( String name ) {
        super( name );

        inputHandler = new InputHandler();

        awtWindow = new Frame() {
            public boolean isShowing() {
                return true;
            }

            public boolean isVisible() {
                if ( awtWindow.isFocusableWindow()
                        && new Throwable().getStackTrace()[1].getMethodName().startsWith( "requestFocus" ) ) {
                    return false;
                }
                return initialized || super.isVisible();
            }

            public Graphics getGraphics() {
                if ( !showingJFrame ) {
                    return graphics == null ? super.getGraphics() : graphics.create();
                }
                else {
                    return super.getGraphics();
                }
            }
        };
        awtWindow.setFocusableWindowState( false );
        Container contentPane = awtWindow;
        awtWindow.setUndecorated( true );
        dontDrawBackground( contentPane );
//            ( (JComponent) contentPane ).setOpaque( false );

        desktop = new JDesktopPane() {
            public void paint( Graphics g ) {
                if ( !isShowingJFrame() ) {
                    g.clearRect( 0, 0, getWidth(), getHeight() );
                }
                super.paint( g );
            }

            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };

        final Color transparent = new Color( 0, 0, 0, 0 );
        desktop.setBackground( transparent );
        desktop.setFocusable( true );
        desktop.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                desktop.requestFocusInWindow();
            }
        } );

        // this internal frame is a workaround for key binding problems in JDK1.5
        final JInternalFrame internalFrame = new JInternalFrame();
        internalFrame.setUI( new BasicInternalFrameUI( internalFrame ) {
            protected void installComponents() {
            }
        } );
        internalFrame.setOpaque( false );
        internalFrame.setBackground( null );
        internalFrame.getContentPane().setLayout( new BorderLayout() );
        internalFrame.getContentPane().add( desktop, BorderLayout.CENTER );
        internalFrame.setVisible( true );
        internalFrame.setBorder( null );
        contentPane.add( internalFrame );
        // this would have suited for JDK1.4:
//        contentPane.add( desktop );

        awtWindow.pack();

        RepaintManager.currentManager( null ).setDoubleBufferingEnabled( false );
    }

    /**
     * Create a quad with a Swing-Texture.
     * Note that for the texture a width and height that is a power of 2 is used if the graphics card does
     * not support the specified size for textures. E.g. this results in a 1024x512
     * texture for a 640x480 desktop (consider using a 512x480 desktop in that case).
     *
     * @param name   name of the spatial
     * @param width  desktop width
     * @param height desktop height     
     * @param inputHandlerParent InputHandler where the InputHandler of this desktop should be added as subhandler,
     * may be null to provide custom input handling or later adding of InputHandler(s)
     * @see #getInputHandler()
     */
    public JMEDesktop( String name, final int width, final int height, InputHandler inputHandlerParent ) {
        this( name, width, height, false, inputHandlerParent );
    }

    /**
     * Create a quad with a Swing-Texture.
     * Note that for the texture a width and height that is a power of 2 is used if the graphics card does
     * not support the specified size for textures or mipMapping is true. E.g. this results in a 1024x512
     * texture for a 640x480 desktop (consider using a 512x480 desktop in that case).
     *
     * @param name       name of the spatial
     * @param width      desktop width
     * @param height     desktop hieght
     * @param mipMapping true to compute mipmaps for the desktop (not recommended), false for creating
     *                   a single image texture
     * @param inputHandlerParent InputHandler where the InputHandler of this desktop should be added as subhandler,
     * may be null to provide custom input handling or later adding of InputHandler(s)
     * @see #getInputHandler()
     */
    public JMEDesktop( String name, final int width, final int height, boolean mipMapping, InputHandler inputHandlerParent ) {
        this( name );

        setup( width, height, mipMapping, inputHandlerParent );
    }

    /**
     * Set up the desktop quad - may be called only once.
     * Note that for the texture a width and height that is a power of 2 is used if the graphics card does
     * not support the specified size for textures or mipMapping is true. E.g. this results in a 1024x512
     * texture for a 640x480 desktop (consider using a 512x480 desktop in that case).
     *
     * @param width      desktop width
     * @param height     desktop hieght
     * @param mipMapping true to compute mipmaps for the desktop (not recommended), false for creating
     *                   a single image texture
     * @param inputHandlerParent InputHandler where the InputHandler of this desktop should be added as subhandler,
     * may be null to provide custom input handling or later adding of InputHandler(s)
     * @see #getInputHandler()
     */
    public void setup( int width, int height, boolean mipMapping, InputHandler inputHandlerParent ) {
        reconstruct( null, null, null, null );
        if ( inputHandlerParent != null ) {
            inputHandlerParent.addToAttachedHandlers( inputHandler );
        }

        if ( initialized ) {
            throw new IllegalStateException( "may be called only once" );
        }
        initialize( powerOf2SizeIfNeeded( width, mipMapping ), powerOf2SizeIfNeeded( height, mipMapping ) );

        this.width = powerOf2SizeIfNeeded( width, mipMapping );
        this.height = powerOf2SizeIfNeeded( height, mipMapping );
        setModelBound( new OrientedBoundingBox() );
        updateModelBound();

        desktop.setPreferredSize( new Dimension( width, height ) );
        desktopWidth = width;
        desktopHeight = height;
        awtWindow.pack();

        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        texture = new Texture();
        texture.setCorrection( Texture.CM_PERSPECTIVE );
        texture.setFilter( Texture.FM_LINEAR );
        texture.setMipmapState( mipMapping ? Texture.MM_LINEAR_LINEAR : Texture.MM_LINEAR );
        texture.setWrap( Texture.WM_WRAP_S_WRAP_T );

        graphics = ImageGraphics.createInstance( this.width, this.height, mipMapping ? 2 : 0 );
        enableAntiAlias( graphics );
        graphics.translate( ( this.width - width ) * 0.5f, ( this.height - height ) * 0.5f );
        texture.setImage( graphics.getImage() );

        texture.setScale( new Vector3f( 1, -1, 1 ) );
        ts.setTexture( texture );
        ts.apply();
        this.setRenderState( ts );

        AlphaState alpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        alpha.setEnabled( true );
        alpha.setBlendEnabled( true );
        alpha.setSrcFunction( AlphaState.SB_SRC_ALPHA );
        alpha.setDstFunction( AlphaState.DB_ONE_MINUS_SRC_ALPHA );
        alpha.setTestEnabled( true );
        alpha.setTestFunction( AlphaState.TF_GREATER );
        this.setRenderState( alpha );

//        Toolkit.getDefaultToolkit().addAWTEventListener( new AWTEventListener() {
//            public void eventDispatched( AWTEvent event ) {
//                if ( isShowingJFrame() ) {
//                    System.out.println( event );
//                }
//            }
//        }, 0xFFFFFFFFFFFFFFFFl );


        xUpdateAction = new XUpdateAction();
        yUpdateAction = new YUpdateAction();
        wheelUpdateAction = new WheelUpdateAction();
        wheelUpdateAction.setSpeed( AWTMouseInput.WHEEL_AMP );
        allButtonsUpdateAction = new ButtonAction( InputHandler.BUTTON_ALL );
        keyUpdateAction = new KeyUpdateAction();

        setupDefaultInputBindings();

        PopupFactory.setSharedInstance( new MyPopupFactory() );

        initialized = true;

        setSynchronizingThreadsOnUpdate( true );
    }

    protected void setupDefaultInputBindings() {
        getInputHandler().addAction( getButtonUpdateAction( InputHandler.BUTTON_ALL ), InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_ALL,
                InputHandler.AXIS_NONE, false );
        getInputHandler().addAction( getXUpdateAction(), InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_NONE, 0, false );
        getInputHandler().addAction( getYUpdateAction(), InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_NONE, 1, false );
        getInputHandler().addAction( getWheelUpdateAction(), InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_NONE, 2, false );

        getInputHandler().addAction( getKeyUpdateAction(), InputHandler.DEVICE_KEYBOARD, InputHandler.BUTTON_ALL, InputHandler.AXIS_NONE, false );
    }

    //todo: reuse the runnables
    //todo: possibly reuse events, too?

    public void onKey( final char character, final int keyCode, final boolean pressed ) {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    sendAWTKeyEvent( keyCode, pressed, character );
                }
            } );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        }
    }

    public void onButton( final int button, final boolean pressed, final int x, final int y ) {
        convert( x, y, location );
        final int awtX = (int) location.x;
        final int awtY = (int) location.y;
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    sendAWTMouseEvent( awtX, awtY, pressed, button );
                }
            } );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        }
    }

    public void onWheel( final int wheelDelta, final int x, final int y ) {
        convert( x, y, location );
        final int awtX = (int) location.x;
        final int awtY = (int) location.y;
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    sendAWTWheelEvent( wheelDelta, awtX, awtY );
                }
            } );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        }
    }

    public void onMove( int xDelta, int yDelta, final int newX, final int newY ) {
        convert( newX, newY, location );
        final int awtX = (int) location.x;
        final int awtY = (int) location.y;
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    sendAWTMouseEvent( awtX, awtY, false, -1 );
                }
            } );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        }
    }

    private boolean synchronizingThreadsOnUpdate;

    /**
     * @return true if update and swing thread should be synchronized (avoids flickering, eats some performance)
     */
    public boolean isSynchronizingThreadsOnUpdate() {
        return synchronizingThreadsOnUpdate;
    }

    /**
     * Choose if update and swing thread should be synchronized (avoids flickering, eats some performance)
     *
     * @param synchronizingThreadsOnUpdate true to synchronize
     */
    public void setSynchronizingThreadsOnUpdate( boolean synchronizingThreadsOnUpdate ) {
        if ( this.synchronizingThreadsOnUpdate != synchronizingThreadsOnUpdate ) {
            this.synchronizingThreadsOnUpdate = synchronizingThreadsOnUpdate;
        }
    }

    private void enableAntiAlias( Graphics2D graphics ) {
        RenderingHints hints = graphics.getRenderingHints();
        if ( hints == null ) {
            hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        else {
            hints.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        graphics.setRenderingHints( hints );
    }

    /**
     * @return an action that should be invoked to generate an awt event when the mouse x-coordinate is changed
     */
    public XUpdateAction getXUpdateAction() {
        return xUpdateAction;
    }

    /**
     * @return an action that should be invoked to generate an awt event when the mouse y-coordinate is changed
     */
    public YUpdateAction getYUpdateAction() {
        return yUpdateAction;
    }

    /**
     * @return an action that should be invoked to generate an awt event when the mouse wheel position is changed
     */
    public WheelUpdateAction getWheelUpdateAction() {
        return wheelUpdateAction;
    }

    /**
     * @param swingButtonIndex button index sent in generated swing event, InputHandler.BUTTON_ALL for using trigger index
     * @return an action that should be invoked to generate an awt event for a pressed/released mouse button
     */
    public ButtonAction getButtonUpdateAction( int swingButtonIndex ) {
        if ( swingButtonIndex == InputHandler.BUTTON_ALL ) {
            return allButtonsUpdateAction;
        }
        else {
            return new ButtonAction( swingButtonIndex );
        }
    }

    /**
     * @return an action that should be invoked to generate an awt event for a pressed/released key
     */
    public InputAction getKeyUpdateAction() {
        return keyUpdateAction;
    }

    private static class LightWeightPopup extends Popup {
        public LightWeightPopup( JComponent desktop ) {
            this.desktop = desktop;
        }

        private final JComponent desktop;

        JPanel panel = new JPanel( new BorderLayout() );

        public void adjust( Component owner, Component contents, int x, int y ) {
            panel.setVisible( false );
            desktop.add( panel, 0 );
            panel.removeAll();
            panel.add( contents, BorderLayout.CENTER );
            if ( contents instanceof JComponent ) {
                JComponent jComponent = (JComponent) contents;
                jComponent.setDoubleBuffered( false );
            }
            panel.setSize( panel.getPreferredSize() );
            panel.setLocation( x, y );
            contents.invalidate();
            panel.validate();
        }

        public void show() {
            panel.setVisible( true );
        }

        public void hide() {
            Rectangle bounds = panel.getBounds();
            desktop.remove( panel );
            desktop.repaint( bounds );
        }
    }

    private void sendAWTKeyEvent( int keyCode, boolean pressed, char character ) {
        keyCode = AWTKeyInput.toAWTCode( keyCode );
        if ( keyCode != 0 ) {
            final Component focusOwner = getFocusOwner();
            if ( focusOwner != null ) {
                if ( pressed ) {
                    KeyEvent event = new KeyEvent( focusOwner, KeyEvent.KEY_PRESSED,
                            System.currentTimeMillis(), getCurrentModifiers( -1 ),
                            keyCode, character );
                    dispatchEvent( focusOwner, event );
                    anInt.value = keyCode;
                    Char c = (Char) characters.get( anInt );
                    if ( c == null ) {
                        characters.put( new Int( keyCode ), new Char( character ) );
                    }
                    else {
                        c.value = character;
                    }
                }
                if ( !pressed ) {
                    anInt.value = keyCode;
                    Char c = (Char) characters.get( anInt );
                    if ( c != null ) {
                        character = c.value;
                        //TODO: repeat input
                        if ( character != '\0' ) {
                            dispatchEvent( focusOwner, new KeyEvent( focusOwner, KeyEvent.KEY_TYPED,
                                    System.currentTimeMillis(), getCurrentModifiers( -1 ),
                                    0, character ) );
                        }
                    }
                    dispatchEvent( focusOwner, new KeyEvent( focusOwner, KeyEvent.KEY_RELEASED,
                            System.currentTimeMillis(), getCurrentModifiers( -1 ),
                            keyCode, character ) );
                }
            }
        }
    }

    private void dispatchEvent( final Component receiver, final AWTEvent event ) {
        if ( !SwingUtilities.isEventDispatchThread() ) {
            throw new IllegalStateException( "not in swing thread!" );
        }
        receiver.dispatchEvent( event );
    }

    private static Int anInt = new Int( 0 );

    private static class Int {
        public Int( int value ) {
            this.value = value;
        }

        public boolean equals( Object obj ) {
            if ( obj instanceof Int ) {
                return ( (Int) obj ).value == value;
            }
            else {
                return false;
            }
        }

        public int hashCode() {
            return value;
        }

        int value;
    }

    private static class Char {
        public Char( char value ) {
            this.value = value;
        }

        char value;
    }

    /**
     * From keyCode (Int) to character (Char)
     */
    private Map characters = new HashMap();

    private static void dontDrawBackground( Container container ) {
        if ( container != null ) {
            container.setBackground( null );
            if ( container instanceof JComponent ) {
                final JComponent component = ( (JComponent) container );
                component.setOpaque( false );
            }
            dontDrawBackground( container.getParent() );
        }
    }

    private static int powerOf2SizeIfNeeded( int size, boolean generateMipMaps ) {
        if ( generateMipMaps || !TextureState.isSupportingNonPowerOfTwoTextureSize() ) {
            int powerOf2Size = 1;
            while ( powerOf2Size < size ) {
                powerOf2Size <<= 1;
            }
            return powerOf2Size;
        }
        else {
            return size;
        }
    }

    private Component lastComponent;
    private Component grabbedMouse;
    private int grabbedMouseButton;
    private int downX = 0;
    private int downY = 0;
    private long lastClickTime = 0;
    private int clickCount = 0;
    private static final int MAX_CLICKED_OFFSET = 4;

    private Vector2f location = new Vector2f();

    private void sendAWTWheelEvent( int wheelDelta, int x, int y ) {
        Component comp = lastComponent != null ? lastComponent : componentAt( x, y, desktop, false );
        if ( comp == null ) {
            comp = desktop;
        }
        final Point pos = SwingUtilities.convertPoint( desktop, x, y, comp );
        final MouseWheelEvent event = new MouseWheelEvent( comp,
                MouseEvent.MOUSE_WHEEL,
                System.currentTimeMillis(), getCurrentModifiers( -1 ), pos.x, pos.y, 1, false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL,
                Math.abs( wheelDelta ), wheelDelta > 0 ? -1 : 1 );
        dispatchEvent( comp, event );
    }

    private void sendAWTMouseEvent( int x, int y, boolean pressed, int button ) {
        Component comp = componentAt( x, y, desktop, false );

        final int eventType;
        if ( button >= 0 ) {
            eventType = pressed ? MouseEvent.MOUSE_PRESSED : MouseEvent.MOUSE_RELEASED;
        }
        else {
            eventType = getButtonMask( -1 ) == 0 ? MouseEvent.MOUSE_MOVED : MouseEvent.MOUSE_DRAGGED;
        }

        final long time = System.currentTimeMillis();
        if ( lastComponent != comp ) {
            //enter/leave events
            while ( lastComponent != null && ( comp == null || !SwingUtilities.isDescendingFrom( comp, lastComponent ) ) )
            {
                final Point pos = SwingUtilities.convertPoint( desktop, x, y, lastComponent );
                sendExitedEvent( lastComponent, getCurrentModifiers( button ), pos );
                lastComponent = lastComponent.getParent();
            }
            final Point pos = SwingUtilities.convertPoint( desktop, x, y, lastComponent );
            if ( lastComponent == null ) {
                lastComponent = desktop;
            }
            sendEnteredEvent( comp, lastComponent, getCurrentModifiers( button ), pos );
            lastComponent = comp;
            downX = Integer.MIN_VALUE;
            downY = Integer.MIN_VALUE;
            lastClickTime = 0;
        }

        boolean clicked = false;
        if ( comp != null ) {
            if ( button >= 0 ) {
                if ( pressed ) {
                    grabbedMouse = comp;
                    grabbedMouseButton = button;
                    downX = x;
                    downY = y;
                    setFocusOwner( componentAt( x, y, desktop, true ) );
                }
                else if ( grabbedMouseButton == button && grabbedMouse != null ) {
                    comp = grabbedMouse;
                    grabbedMouse = null;
                    if ( Math.abs( downX - x ) <= MAX_CLICKED_OFFSET && Math.abs( downY - y ) < MAX_CLICKED_OFFSET ) {
                        if ( lastClickTime + DOUBLE_CLICK_TIME > time ) {
                            clickCount++;
                        }
                        else {
                            clickCount = 1;
                        }
                        clicked = true;
                        lastClickTime = time;
                    }
                    downX = Integer.MIN_VALUE;
                    downY = Integer.MIN_VALUE;
                }
            }
            else if ( grabbedMouse != null ) {
                comp = grabbedMouse;
            }

            final Point pos = SwingUtilities.convertPoint( desktop, x, y, comp );
            final MouseEvent event = new MouseEvent( comp,
                    eventType,
                    time, getCurrentModifiers( button ), pos.x, pos.y, clickCount,
                    button == 1 && pressed, button >= 0 ? button : 0 );
            dispatchEvent( comp, event );
            if ( clicked ) {
                // CLICKED seems to need special glass pane handling o_O
                comp = componentAt( x, y, desktop, true );
                final Point clickedPos = SwingUtilities.convertPoint( desktop, x, y, comp );

                final MouseEvent clickedEvent = new MouseEvent( comp,
                        MouseEvent.MOUSE_CLICKED,
                        time, getCurrentModifiers( button ), clickedPos.x, clickedPos.y, clickCount,
                        false, button );
                dispatchEvent( comp, clickedEvent );
            }
        }
        else if ( pressed ) {
            // clicked no component at all
            setFocusOwner( null );
        }
    }

    public void setFocusOwner( Component comp ) {
        if ( comp == null || comp.isFocusable() ) {
            awtWindow.setFocusableWindowState( true );
            Component oldFocusOwner = getFocusOwner();
            if ( comp == desktop ) {
                comp = null;
            }
            if ( oldFocusOwner != comp ) {
                if ( oldFocusOwner != null ) {
                    dispatchEvent( oldFocusOwner, new FocusEvent( oldFocusOwner,
                            FocusEvent.FOCUS_LOST, false, comp ) );
                }
                if ( comp != null ) {
                    dispatchEvent( comp, new FocusEvent( comp,
                            FocusEvent.FOCUS_GAINED, false, oldFocusOwner ) );
                }
                else {
                    if ( getFocusOwner() != null ) {
                        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                    }
                }
            }
            awtWindow.setFocusableWindowState( false );
        }
    }

    private int getCurrentModifiers( int button ) {
        int modifiers = 0;
        if ( isKeyDown( KeyInput.KEY_LMENU ) ) {
            modifiers |= KeyEvent.ALT_DOWN_MASK;
            modifiers |= KeyEvent.ALT_MASK;
        }
        if ( isKeyDown( KeyInput.KEY_RMENU ) ) {
            modifiers |= KeyEvent.ALT_GRAPH_DOWN_MASK;
            modifiers |= KeyEvent.ALT_GRAPH_MASK;
        }
        if ( isKeyDown( KeyInput.KEY_LCONTROL ) || isKeyDown( KeyInput.KEY_RCONTROL ) ) {
            modifiers |= KeyEvent.CTRL_DOWN_MASK;
            modifiers |= KeyEvent.CTRL_MASK;
        }
        if ( isKeyDown( KeyInput.KEY_LSHIFT ) || isKeyDown( KeyInput.KEY_RSHIFT ) ) {
            modifiers |= KeyEvent.SHIFT_DOWN_MASK;
            modifiers |= KeyEvent.SHIFT_MASK;
        }
        return modifiers | getButtonMask( button );
    }

    private boolean isKeyDown( int key ) {
        return KeyInput.get().isKeyDown( key );
    }

    private int getButtonMask( int button ) {
        int buttonMask = 0;
        if ( MouseInput.get().isButtonDown( 0 ) || button == 0 ) {
            buttonMask |= MouseEvent.BUTTON1_MASK;
            buttonMask |= MouseEvent.BUTTON1_DOWN_MASK;
        }
        if ( MouseInput.get().isButtonDown( 1 ) || button == 1 ) {
            buttonMask |= MouseEvent.BUTTON2_MASK;
            buttonMask |= MouseEvent.BUTTON2_DOWN_MASK;
        }
        if ( MouseInput.get().isButtonDown( 2 ) || button == 2 ) {
            buttonMask |= MouseEvent.BUTTON3_MASK;
            buttonMask |= MouseEvent.BUTTON3_DOWN_MASK;
        }
        return buttonMask;
    }

    private int lastXin = -1;
    private int lastXout = -1;
    private int lastYin = -1;
    private int lastYout = -1;

    private Ray pickRay = new Ray();
    private Vector3f bottomLeft = new Vector3f();
    private Vector3f topLeft = new Vector3f();
    private Vector3f topRight = new Vector3f();
    private Vector3f bottomRight = new Vector3f();
    private Vector3f tuv = new Vector3f();

    private void convert( int x, int y, Vector2f store ) {
        if ( lastXin == x && lastYin == y ) {
            store.x = lastXout;
            store.y = lastYout;
        }
        else {
            lastXin = x;
            lastYin = y;
            if ( getRenderQueueMode() == Renderer.QUEUE_ORTHO ) {
                //TODO: occlusion by other quads (JMEFrames)
                x = (int) ( x - getWorldTranslation().x + desktopWidth / 2 );
                y = (int) ( DisplaySystem.getDisplaySystem().getHeight() - y - getWorldTranslation().y + desktopHeight / 2 );
            }
            else {
                store.set( x, y );
                DisplaySystem.getDisplaySystem().getWorldCoordinates( store, 0, pickRay.origin );
                DisplaySystem.getDisplaySystem().getWorldCoordinates( store, 0.3f, pickRay.direction ).subtractLocal( pickRay.origin ).normalizeLocal();

                applyWorld( bottomLeft.set( -width * 0.5f, -height * 0.5f, 0 ) );
                applyWorld( topLeft.set( -width * 0.5f, height * 0.5f, 0 ) );
                applyWorld( topRight.set( width * 0.5f, height * 0.5f, 0 ) );
                applyWorld( bottomRight.set( width * 0.5f, -height * 0.5f, 0 ) );

                if ( pickRay.intersectWherePlanarQuad( topLeft, topRight, bottomLeft, tuv ) ) {
                    x = (int) ( ( tuv.y - 0.5f ) * width ) + desktopWidth / 2;
                    y = (int) ( ( tuv.z - 0.5f ) * height ) + desktopHeight / 2;
                }
                else {
                    x = -1;
                    y = -1;
                }
            }
            lastYout = y;
            lastXout = x;
            //debug:
//            Graphics g = desktop.getGraphics();
//            g.setColor( Color.RED );
//            g.fillRect( x, y, 1, 1 );
//            g.dispose();

            store.set( x, y );
        }
    }

    private void applyWorld( Vector3f point ) {
        getWorldRotation().multLocal( point.multLocal( getWorldScale() ) ).addLocal( getWorldTranslation() );
    }

    private Component componentAt( int x, int y, Component parent, boolean scanRootPanes ) {
        if ( scanRootPanes && parent instanceof JRootPane ) {
            JRootPane rootPane = (JRootPane) parent;
            parent = rootPane.getContentPane();
        }
        Component child = parent.getComponentAt( x, y );
        if ( child != null ) {
            if ( parent instanceof JTabbedPane && child != parent ) {
                child = ( (JTabbedPane) parent ).getSelectedComponent();
            }
            x -= child.getX();
            y -= child.getY();
        }
        return child != parent && child != null ? componentAt( x, y, child, scanRootPanes ) : child;
    }

    private void sendEnteredEvent( Component comp, Component lastComponent, int buttonMask, Point pos ) {
        if ( comp != null && comp != lastComponent ) {
            sendEnteredEvent( comp.getParent(), lastComponent, buttonMask, pos );

            pos = SwingUtilities.convertPoint( lastComponent, pos.x, pos.y, comp );
            final MouseEvent event = new MouseEvent( comp,
                    MouseEvent.MOUSE_ENTERED,
                    System.currentTimeMillis(), buttonMask, pos.x, pos.y, 0, false, 0 );
            dispatchEvent( comp, event );
        }

    }

    private void sendExitedEvent( Component lastComponent, int buttonMask, Point pos ) {
        final MouseEvent event = new MouseEvent( lastComponent,
                MouseEvent.MOUSE_EXITED,
                System.currentTimeMillis(), buttonMask, pos.x, pos.y, 1, false, 0 );
        dispatchEvent( lastComponent, event );
    }

    private final LockRunnable paintLockRunnable = new LockRunnable();

    public void draw( Renderer r ) {
        if ( graphics.isDirty() ) {
            final boolean synchronizingThreadsOnUpdate = this.synchronizingThreadsOnUpdate;
            if ( synchronizingThreadsOnUpdate ) {
                synchronized ( paintLockRunnable ) {
                    try {
                        paintLockRunnable.wait = true;
                        SwingUtilities.invokeLater( paintLockRunnable );
                        paintLockRunnable.wait( 100 );
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                if ( graphics != null ) {
                    graphics.update( texture );
                }
            } finally {

                if ( synchronizingThreadsOnUpdate ) {
                    synchronized ( paintLockRunnable ) {
                        paintLockRunnable.notifyAll();
                    }
                }
            }
        }
        super.draw( r );
    }

    public JDesktopPane getJDesktop() {
        return desktop;
    }

    public Component getFocusOwner() {
        return this.awtWindow.getFocusOwner();
    }

    private class LockRunnable implements Runnable {
        private boolean wait = false;

        public void run() {
            synchronized ( paintLockRunnable ) {
                notifyAll();
                if ( wait ) {
                    try {
                        //wait for repaint to finish
                        wait = false;
                        paintLockRunnable.wait( 200 );
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static class MyPopupFactory extends PopupFactory {
        public Popup getPopup( Component owner, Component contents, int x, int y ) throws IllegalArgumentException {
            while ( owner.getParent() != null && !( owner.getParent() instanceof Frame ) ) {
                owner = owner.getParent();
            }
            if ( owner instanceof JDesktopPane ) {
                JMEDesktop.LightWeightPopup popup = new JMEDesktop.LightWeightPopup( (JComponent) owner );
                popup.adjust( owner, contents, x, y );
                return popup;
            }
            else {
                LoggingSystem.getLogger().severe( "Popup creation failed - desktop not found in component hierarchy of " + owner );
                return null;
            }
        }
    }

    private class ButtonAction extends InputAction {
        private final int swingButtonIndex;

        /**
         * @param swingButtonIndex button index sent in generated swing event, InputHandler.BUTTON_ALL for using trigger index
         */
        public ButtonAction( int swingButtonIndex ) {
            this.swingButtonIndex = swingButtonIndex;
        }

        public void performAction( InputActionEvent evt ) {
            onButton( swingButtonIndex != InputHandler.BUTTON_ALL ? swingButtonIndex : evt.getTriggerIndex(), evt.getTriggerPressed(),
                    lastXin, lastYin );
        }

    }

    private class XUpdateAction extends InputAction {
        public XUpdateAction() {
            setSpeed( 1 );
        }

        public void performAction( InputActionEvent evt ) {
            int screenWidth = DisplaySystem.getDisplaySystem().getWidth();
            onMove( (int) ( screenWidth * evt.getTriggerDelta() * getSpeed() ), 0,
                    (int) ( screenWidth * evt.getTriggerPosition() * getSpeed() ), lastYin );
        }
    }

    private class YUpdateAction extends InputAction {
        public YUpdateAction() {
            setSpeed( 1 );
        }

        public void performAction( InputActionEvent evt ) {
            int screenHeight = DisplaySystem.getDisplaySystem().getHeight();
            onMove( 0, (int) ( screenHeight * evt.getTriggerDelta() * getSpeed() ), lastXin,
                    (int) ( screenHeight * evt.getTriggerPosition() * getSpeed() ) );
        }
    }

    private class WheelUpdateAction extends InputAction {
        public WheelUpdateAction() {
            setSpeed( 1 );
        }

        public void performAction( InputActionEvent evt ) {
            onWheel( (int) ( evt.getTriggerDelta() * getSpeed() ), lastXin, lastYin );
        }
    }

    private class KeyUpdateAction extends InputAction {
        public void performAction( InputActionEvent evt ) {
            onKey( evt.getTriggerCharacter(), evt.getTriggerIndex(), evt.getTriggerPressed() );
        }
    }
}
