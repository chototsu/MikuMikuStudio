package jmetest.awt.swingui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.jme.app.BaseGame;
import com.jme.app.SimpleGame;
import com.jme.input.FirstPersonHandler;
import com.jme.input.MouseInput;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.util.TextureManager;
import com.jme.image.Texture;
import com.jmex.awt.swingui.JMEDesktop;

/**
 * Example for using Swing within a jME game: Some frames, buttons and textfields are shown above
 * and on a spinning box.
 *
 * @see com.jmex.awt.swingui.JMEDesktop
 */
public class TestJMEDesktop extends SimpleGame {
    private JMEDesktop jmeDesktop;
    private Node desktopNode;

    protected void simpleUpdate() {
        input.setEnabled( true );// jmeFrame.getDesktop().isFocusOwner() );
    }

    public static void main( String[] args ) throws Exception {
        TestJMEDesktop testJMEDesktop = new TestJMEDesktop();
        testJMEDesktop.setDialogBehaviour( BaseGame.ALWAYS_SHOW_PROPS_DIALOG );
        testJMEDesktop.start();
    }

    /**
     * Called near end of initGame(). Must be defined by derived classes.
     */
    protected void simpleInitGame() {
        display.setTitle( "jME-Desktop test");
        display.getRenderer().setBackgroundColor( ColorRGBA.blue );

        jmeDesktop = new JMEDesktop( "test frame", display.getWidth(), display.getHeight(), false );
        jmeDesktop.setLightCombineMode( LightState.OFF );
        desktopNode = new Node( "desktop node" );
        desktopNode.attachChild( jmeDesktop );
        rootNode.attachChild( desktopNode );
        createBoxBorder();

        perspective();
//        fullScreen();

        jmeDesktop.getJDesktop().setBackground( new Color( 1, 1, 1, 0.2f ) );

        MouseInput.get().setCursorVisible( true );
        ( (FirstPersonHandler) input ).getMouseLookHandler().setEnabled( false );

        switchLookAndFeelAndCreateSwingStuff( 1 );

        create3DStuff();
    }

    private void createBoxBorder() {
        //create a border from boxes around the desktop
        float borderWidth = 10;
        float halfBorderWidth = borderWidth / 2;
        int halfDesktopWidth = jmeDesktop.getJDesktop().getWidth() / 2;
        int halfDesktopHeight = jmeDesktop.getJDesktop().getHeight() / 2;

        Box top = new Box( "top border", new Vector3f(),
                halfDesktopWidth + halfBorderWidth,
                halfBorderWidth, halfBorderWidth );
        top.getLocalTranslation().set( 0, - halfDesktopHeight, 0 );
        desktopNode.attachChild( top );

        Box bottom = new Box( "bottom border", new Vector3f(),
                halfDesktopWidth + halfBorderWidth,
                halfBorderWidth, halfBorderWidth );
        bottom.getLocalTranslation().set( 0, halfDesktopHeight, 0 );
        desktopNode.attachChild( bottom );

        Box left = new Box( "left border", new Vector3f(),
                halfBorderWidth,
                halfDesktopHeight + halfBorderWidth,
                halfBorderWidth );
        left.getLocalTranslation().set( - halfDesktopWidth, 0, 0 );
        desktopNode.attachChild( left );

        Box right = new Box( "right border", new Vector3f(),
                halfBorderWidth,
                halfDesktopHeight + halfBorderWidth,
                halfBorderWidth );
        right.getLocalTranslation().set( halfDesktopWidth, 0, 0 );
        desktopNode.attachChild( right );
    }

    private void perspective() {
        desktopNode.getLocalRotation().fromAngleNormalAxis( -0.7f, new Vector3f( 1, 0, 0 ) );
        desktopNode.setLocalScale( 0.03f );
        desktopNode.getLocalTranslation().set( 0, 0, 0 );
        desktopNode.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
    }

    private void fullScreen() {
        final DisplaySystem display = DisplaySystem.getDisplaySystem();

        desktopNode.getLocalRotation().set( 0, 0, 0, 1 );
        desktopNode.getLocalTranslation().set( display.getWidth() / 2, display.getHeight() / 2, 0 );
        desktopNode.getLocalScale().set( 1, 1, 1 );
        desktopNode.setRenderQueueMode( Renderer.QUEUE_ORTHO );
    }

    private JButton button1;
    private boolean moreStuffCreated;

    protected void createSwingStuff() {
        final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
        desktopPane.removeAll();

        createSwingInternalFrame( desktopPane, "My Frame 1", 10, 150 );
        createSwingInternalFrame( desktopPane, "My Frame 2", 20, 300 );
        createSwingInternalFrame( desktopPane, null, 400, 350 );

        final JButton button3 = new JButton( "more stuff" );
        button3.setLocation( 300, 100 );
        button3.setSize( button3.getPreferredSize() );
        desktopPane.add( button3 );
        button3.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                createMoreSwingStuff();
            }
        } );

        final JLabel label = new JLabel( "click scene to steer view (WASD+Arrows)" );
        label.setSize( label.getPreferredSize() );
        label.setLocation( display.getWidth() - (int) label.getSize().getWidth() - 10, 10 );
        desktopPane.add( label );

        moreStuffCreated = false;

        final JButton themeButton = new JButton( "change l&f" );
        themeButton.setLocation( 10, 400 );
        themeButton.setSize( themeButton.getPreferredSize() );
        desktopPane.add( themeButton );
        themeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                jmeDesktop.getJDesktop().removeAll();
                switchLookAndFeelAndCreateSwingStuff( theme + 1 );
            }
        } );

        JButton fullScreenButton = new JButton( "<html><big>toggle fullscreen</big></html>" );
        fullScreenButton.setSize( fullScreenButton.getPreferredSize() );
        fullScreenButton.setLocation( ( display.getWidth() - fullScreenButton.getWidth() ) / 2,
                display.getHeight() - 40 - fullScreenButton.getHeight() / 2 );
        desktopPane.add( fullScreenButton );
        fullScreenButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( desktopNode.getRenderQueueMode() == Renderer.QUEUE_ORTHO ) {
                    perspective();
                }
                else {
                    fullScreen();
                }
            }
        } );

        createRotateButton( desktopPane, 0.25f );
        createRotateButton( desktopPane, -0.25f );
        createRotateButton( desktopPane, 0.15f );
        createRotateButton( desktopPane, -0.15f );
        createRotateButton( desktopPane, 0.45f );
        createRotateButton( desktopPane, -0.45f );

        desktopPane.repaint();
    }

    private void createRotateButton( JDesktopPane parent, final float direction ) {
        JButton button = new JButton( direction < 0 ? "<" : ">" );
        button.setSize( button.getPreferredSize() );
        button.setLocation( (int) ( ( display.getWidth() - button.getWidth() ) / 2
                + direction * display.getWidth() ), display.getHeight() - 40 - button.getHeight() / 2 );
        parent.add( button );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( desktopNode.getRenderQueueMode() != Renderer.QUEUE_ORTHO ) {
                    desktopNode.addController( new Controller() {
                        float length = 1;
                        float endTime = timer.getTimeInSeconds() + length;
                        Quaternion start = new Quaternion().set( desktopNode.getLocalRotation() );
                        Quaternion finish = new Quaternion().set( desktopNode.getLocalRotation() ).multLocal(
                                new Quaternion().fromAngleNormalAxis( direction, new Vector3f( 0, 1, 0 ) ) );

                        public void update( float time ) {
                            if ( timer.getTimeInSeconds() > endTime ) {
                                desktopNode.removeController( this );
                            }
                            else {
                                desktopNode.getLocalRotation().slerp( finish, start, ( endTime - timer.getTimeInSeconds() ) / length );
                            }
                        }
                    } );
                }
            }
        } );
    }

    private void createMoreSwingStuff() {
        if ( moreStuffCreated ) {
            return;
        }
        moreStuffCreated = true;

        JDesktopPane desktopPane = jmeDesktop.getJDesktop();
        JPanel stuffPanel = new JPanel();
        stuffPanel.setLayout( new GridLayout( 0, 1 ) );
        JScrollPane scrollPane = new JScrollPane( stuffPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        scrollPane.setLocation( 400, 100 );
        desktopPane.add( scrollPane );

        stuffPanel.add( new JCheckBox( "check" ) );
        JComboBox comboBox = new JComboBox( new Object[]{"Item 1", "Item 2", "Item 3"} );
        comboBox.setEditable( true );
        stuffPanel.add( comboBox );
        JProgressBar progress = new JProgressBar( 0, 100 );
        stuffPanel.add( progress );
        progress.setValue( 30 );

        JSlider slider = new JSlider( 0, 100 );
        slider.setValue( 75 );
        stuffPanel.add( slider );
        stuffPanel.setDoubleBuffered( false );
        stuffPanel.setOpaque( false );

        ButtonGroup rGroup = new ButtonGroup();
        for ( int i = 0; i < 10; i++ ) {
            JRadioButton radio = new JRadioButton( "radio " + i );
            stuffPanel.add( radio );
            rGroup.add( radio );
        }

        scrollPane.setSize( (int) scrollPane.getPreferredSize().getWidth(), 200 );
        scrollPane.revalidate();

        JTabbedPane tabbedPane = new JTabbedPane();
        desktopPane.add( tabbedPane );
        tabbedPane.add( "a", new JButton( "abc" ) );
        tabbedPane.add( "d", new JButton( "def" ) );
        tabbedPane.add( "g", new JButton( "ghi" ) );
        tabbedPane.setLocation( 10, 30 );
        tabbedPane.setSize( 150, 100 );
        tabbedPane.revalidate();
    }

    int theme;

    private void createSwingInternalFrame( JDesktopPane desktopPane, String title, int x, int y ) {
        final JInternalFrame internalFrame = new JInternalFrame( title );
        if ( title == null ) {
            internalFrame.putClientProperty( "JInternalFrame.isPalette", Boolean.TRUE );
        }
        internalFrame.setLocation( x, y );
        desktopPane.add( internalFrame );
        internalFrame.setResizable( true );

        internalFrame.getContentPane().setLayout( new FlowLayout() );
        button1 = new JButton( "button in " + title );
        internalFrame.getContentPane().add( button1 );
        internalFrame.getContentPane().add( new JButton( "<html><i>test</i> <big>2</big></html>" ) );
        internalFrame.setVisible( true );
        internalFrame.pack();

        final JTextField textField = new JTextField( "type in here" );
        internalFrame.add( textField );
        internalFrame.pack();
    }

    protected void switchLookAndFeelAndCreateSwingStuff( final int theme ) {
        try {
            this.theme = theme;
            switch ( theme ) {
                case 1:
                    UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
                    break;
//                case 2:
//                    UIManager.setLookAndFeel( new OaLookAndFeel() );
//                    break;
//                case 3:
//                    SynthLookAndFeel laf = new SynthLookAndFeel();
//                    laf.load( TestJMEDesktop.class.getResourceAsStream( "test.xml" ), TestJMEDesktop.class );
//                    UIManager.setLookAndFeel( laf );
//                    break;
                default:
                    UIManager.setLookAndFeel( new MetalLookAndFeel() );
                    this.theme = 0;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        createSwingStuff();
    }

    private void create3DStuff() {
        // Normal Scene setup stuff...
        final Vector3f axis = new Vector3f(1, 1, 0.5f).normalizeLocal();

        final Box box = new Box("Box", new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5) );
        box.setModelBound(new OrientedBoundingBox() );
        box.updateModelBound();
        box.setLocalTranslation(new Vector3f(0, 0, -10));
        box.setRandomColors();
        box.setLightCombineMode( LightState.OFF );

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture( TextureManager.loadTexture(TestJMEDesktop.class
                .getClassLoader().getResource(
                "jmetest/data/images/Monkey.jpg"),
                Texture.MM_LINEAR, Texture.FM_LINEAR));
        box.setRenderState(ts);

        //let the box rotate
        box.addController( new Controller() {
            public void update( float time ) {
                box.getLocalRotation().fromAngleNormalAxis( timer.getTimeInSeconds(), axis );
            }
        } );

        rootNode.attachChild( box );
    }
}
