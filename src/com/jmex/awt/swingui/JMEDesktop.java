package com.jmex.awt.swingui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jmex.awt.input.AWTKeyInput;

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
    private final JFrame swingFrame;
    private int desktopWidth;
    private int desktopHeight;

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
        swingFrame.setVisible( showingJFrame );
        swingFrame.repaint();
    }

    /**
     * Create a quad with a Swing-Texture. Creates the quad and the JFrame but do not setup the rest.
     * Call {@link #setup(int, int, boolean)} to finish setup.
     *
     * @param name name of this desktop
     */
    public JMEDesktop( String name ) {
        super( name );

        swingFrame = new JFrame() {
            public boolean isShowing() {
                return true;
            }

            public boolean isVisible() {
//                if ( new Exception().getStackTrace()[1].getMethodName().indexOf( "Focus" ) > 0 )
//                {
//                    return false;
//                }
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
        swingFrame.setUndecorated( true );

        final Color transparent = new Color( 0, 0, 0, 0 );
        desktop = new JDesktopPane() {
            public void paint( Graphics g ) {
                if ( !isShowingJFrame() ) {
                    g.clearRect( 0, 0, getWidth(), getHeight() );
                }
                super.paint( g );
            }
        };
        dontDrawBackground( swingFrame.getContentPane() );
        desktop.setBackground( transparent );
        desktop.setFocusable( true );
        desktop.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                desktop.requestFocusInWindow();
            }
        } );

        swingFrame.getContentPane().add( desktop );
        ( (JComponent) swingFrame.getContentPane() ).setOpaque( false );

        swingFrame.pack();
        initialized = true;
        try {
            desktop.requestFocus();
        } finally {
            initialized = false;
        }

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
     * @param height desktop hieght
     */
    public JMEDesktop( String name, final int width, final int height ) {
        this( name, width, height, false );
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
     */
    public JMEDesktop( String name, final int width, final int height, boolean mipMapping ) {
        this( name );

        setup( width, height, mipMapping );
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
     */
    public void setup( int width, int height, boolean mipMapping ) {
        reconstruct( null, null, null, null );

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
        swingFrame.pack();

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

        MouseInput.get().addListener( new MouseInputListener() {
            public void onButton( int button, boolean pressed, int x, int y ) {
                sendAWTMouseEvent( x, y, pressed, button );
            }

            public void onWheel( int wheelDelta, int x, int y ) {
                sendAWTWheelEvent( wheelDelta, x, y );
            }

            public void onMove( int xDelta, int yDelta, int newX, int newY ) {
                sendAWTMouseEvent( newX, newY, false, -1 );
            }
        } );

        KeyInput.get().addListener( new KeyInputListener() {
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
        } );

        //TODO: make static popup factory to allow multiple desktops

        PopupFactory.setSharedInstance( new PopupFactory() {
            public Popup getPopup( Component owner, Component contents, int x, int y ) throws IllegalArgumentException {
                LightWeightPopup popup = new LightWeightPopup();
                popup.adjust( owner, contents, x, y );
                return popup;
            }
        } );

        initialized = true;
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

    private class LightWeightPopup extends Popup {
        JPanel panel = new JPanel( new BorderLayout() );

        public void adjust( Component owner, Component contents, int x, int y ) {
            panel.setVisible( false );
            desktop.add( panel, 0 );
            panel.removeAll();
            panel.add( contents, BorderLayout.CENTER );
            panel.setSize( panel.getPreferredSize() );
            panel.setLocation( x, y );
            panel.doLayout();
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
            final Component focusOwner = swingFrame.getFocusOwner();
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
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                receiver.dispatchEvent( event );
            }
        } );
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

    private Vector2f location = new Vector2f();

    private void sendAWTWheelEvent( int wheelDelta, int x, int y ) {
        convert( x, y, location );
        x = (int) location.x;
        y = (int) location.y;
        Component comp = lastComponent != null ? lastComponent : componentAt( x, y, desktop );
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
        convert( x, y, location );
        x = (int) location.x;
        y = (int) location.y;
        Component comp = componentAt( x, y, desktop );

        final int eventType;
        if ( button >= 0 ) {
            eventType = pressed ? MouseEvent.MOUSE_PRESSED : MouseEvent.MOUSE_RELEASED;
        }
        else {
            eventType = getButtonMask( -1 ) == 0 ? MouseEvent.MOUSE_MOVED : MouseEvent.MOUSE_DRAGGED;
        }

        if ( lastComponent != comp ) {
            //enter/leave events
            while ( lastComponent != null && ( comp == null || !SwingUtilities.isDescendingFrom( comp, lastComponent ) ) ) {
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
        }

        if ( comp != null ) {
            if ( button >= 0 ) {
                if ( pressed ) {
                    grabbedMouse = comp;
                    grabbedMouseButton = button;
                }
                else if ( grabbedMouseButton == button && grabbedMouse != null ) {
                    comp = grabbedMouse;
                    grabbedMouse = null;
                }
            }
            else if ( grabbedMouse != null ) {
                comp = grabbedMouse;
            }

            final Point pos = SwingUtilities.convertPoint( desktop, x, y, comp );
            final MouseEvent event = new MouseEvent( comp,
                    eventType,
                    System.currentTimeMillis(), getCurrentModifiers( button ), pos.x, pos.y, 1,
                    button == 1 && pressed, button >= 0 ? button : 0 );
            dispatchEvent( comp, event );
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

    private Component componentAt( int x, int y, Component parent ) {
        Component child = parent.getComponentAt( x, y );
        if ( child != null ) {
            if ( parent instanceof JTabbedPane && child != parent ) {
                child = ( (JTabbedPane) parent ).getSelectedComponent();
            }
            x -= child.getX();
            y -= child.getY();
        }
        return child != parent && child != null ? componentAt( x, y, child ) : child;
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

    public void draw( Renderer r ) {
        synchronized ( swingFrame ) {
            if ( graphics != null ) {
                graphics.update( texture );
            }
        }
        super.draw( r );
    }

    public JDesktopPane getJDesktop() {
        return desktop;
    }
}
