package com.jme.system.jogl;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLContext;
import javax.media.opengl.glu.GLU;

import com.jme.image.Image;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.renderer.RenderContext;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.TextureRenderer.Target;
import com.jme.renderer.jogl.JOGLRenderer;
import com.jme.renderer.jogl.JOGLTextureRenderer;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.WeakIdentityCache;
import com.jmex.awt.input.AWTKeyInput;
import com.jmex.awt.input.AWTMouseInput;

public class JOGLDisplaySystem extends DisplaySystem {
    private static final Logger logger =
            Logger.getLogger(JOGLDisplaySystem.class.getName());

    private JOGLRenderer renderer;

    private RenderContext currentContext;

    private WeakIdentityCache<Object, RenderContext> contextStore =
            new WeakIdentityCache<Object, RenderContext>();

    private Frame frame;

    private GLAutoDrawable autoDrawable;

    private boolean isClosing = false;

    @Override
    public Canvas createCanvas(int w, int h) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createHeadlessWindow(int w, int h, int bpp) {
        // TODOX Auto-generated method stub
    }

    @Override
    public TextureRenderer createTextureRenderer(final int width,
            final int height, final Target target) {
        if (!isCreated()) {
            return null;
        }

        return new JOGLTextureRenderer(width, height,
                (JOGLRenderer) getRenderer());
    }

    @Override
    public void createWindow(int width, int height, int bpp, int frq, boolean fs) {
        // confirm that the parameters are valid.
        if (width <= 0 || height <= 0) {
            throw new JmeException("Invalid resolution values: " + width + " "
                    + height);
        } else if ((bpp != 32) && (bpp != 16) && (bpp != 24)) {
            throw new JmeException("Invalid pixel depth: " + bpp);
        }

        // set the window attributes
        this.width = width;
        this.height = height;
        this.bpp = bpp;
        this.frq = frq;
        this.fs = fs;

        final GLCanvas glCanvas = new GLCanvas();
        glCanvas.setSize(width, height);
        glCanvas.setIgnoreRepaint(true);
        glCanvas.setFocusable(true);

        frame = new Frame();
        frame.add(glCanvas);

        if (fs) {
            frame.setUndecorated(true);

            final GraphicsDevice gd =
                    GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .getDefaultScreenDevice();
            gd.setFullScreenWindow(frame);

            DisplayMode displayMode = new DisplayMode(width, height, bpp, frq);
            gd.setDisplayMode(displayMode);
        } else {
            frame.pack();
        }
        frame.setVisible(true);

        while (glCanvas.getContext().makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        GLAutoDrawable drawable = glCanvas;
        drawable.setGL(new DebugGL(drawable.getGL()));

        renderer = new JOGLRenderer(drawable.getWidth(), drawable.getHeight());
        switchContext(this);

        // FIXME Hack?
        updateStates(renderer);

        // Put the window into orthographic projection mode with 1:1 pixel
        // ratio.
        // We haven't used GLU here to do this to avoid an unnecessary
        // dependency.
        final GL gl = drawable.getGL();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0.0, drawable.getWidth(), 0.0, drawable.getHeight(), -1.0,
                1.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glViewport(0, 0, drawable.getWidth(), drawable.getHeight());
        // Clear window to avoid the desktop "showing through"
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        // update();

        // FIXME All of this is boilerplate which should be a part of the
        // MouseInput, KeyInput, etc. classes if possible.
        MouseInput.setProvider(MouseInput.INPUT_AWT);
        ((AWTMouseInput) MouseInput.get()).setDragOnly(true);
        final MouseListener mouseListener = (MouseListener) MouseInput.get();
        glCanvas.addMouseListener(mouseListener);
        glCanvas.addMouseMotionListener((MouseMotionListener) MouseInput.get());
        glCanvas.addMouseWheelListener((MouseWheelListener) MouseInput.get());

        glCanvas.setCursor(new Cursor(Cursor.HAND_CURSOR));

        KeyInput.setProvider(KeyInput.INPUT_AWT);
        final KeyListener keyListener = (KeyListener) KeyInput.get();
        glCanvas.addKeyListener(keyListener);

        // TODO Look into JInput.
        // JoystickInput.setProvider(JoystickInput.INPUT_DUMMY);

        // glCanvas.setFocusable(true);
        // glCanvas.requestFocus();
        glCanvas.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent event) {
                ((AWTMouseInput) MouseInput.get()).setEnabled(true);
                ((AWTKeyInput) KeyInput.get()).setEnabled(true);
            }

            @Override
            public void focusLost(FocusEvent event) {
                ((AWTMouseInput) MouseInput.get()).setEnabled(false);
                ((AWTKeyInput) KeyInput.get()).setEnabled(false);
            }

        });

        // We are going to use jme's Input systems, so enable updating.
        // ((JMECanvas) glCanvas).setUpdateInput(true);

        // FIXME
        autoDrawable = glCanvas;
        // frame.setSize(width, height);

        // TODO Animator equivalent for JMonkey?
        // final Animator animator = new Animator(canvas);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                isClosing = true;
            }

        });

        created = true;
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public void setTitle(String title) {
        if (frame != null)
            frame.setTitle(title);
    }

    @Override
    public String getAdapter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RenderContext getCurrentContext() {
        return currentContext;
    }

    @Override
    public String getDisplayAPIVersion() {
        try {
            GL gl = GLU.getCurrentGL();
            return gl.glGetString(GL.GL_VERSION);
        } catch (Exception e) {
            return "Unable to retrieve API version.";
        }
    }

    @Override
    public String getDisplayRenderer() {
        try {
            GL gl = GLU.getCurrentGL();
            return gl.glGetString(GL.GL_RENDERER);
        } catch (Exception e) {
            return "Unable to retrieve adapter details.";
        }
    }

    @Override
    public String getDisplayVendor() {
        try {
            GL gl = GLU.getCurrentGL();
            return gl.glGetString(GL.GL_VENDOR);
        } catch (Exception e) {
            return "Unable to retrieve vendor.";
        }
    }

    @Override
    public String getDriverVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void initForCanvas(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isActive() {
        // TODO Auto-generated method stub
        // return false;
        return frame.hasFocus();
    }

    @Override
    public boolean isClosing() {
        return isClosing;
    }

    @Override
    public boolean isValidDisplayMode(int width, int height, int bpp, int freq) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void moveWindowTo(int locX, int locY) {
        // TODO Auto-generated method stub

    }

    @Override
    public void recreateWindow(int w, int h, int bpp, int frq, boolean fs) {
        // TODO Auto-generated method stub

    }

    @Override
    public RenderContext removeContext(Object contextKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setIcon(Image[] iconImages) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRenderer(final Renderer renderer) {
        if (renderer instanceof JOGLRenderer) {
            this.renderer = (JOGLRenderer) renderer;
        } else {
            logger.warning("Invalid Renderer type");
        }
    }

    @Override
    public void setVSyncEnabled(boolean enabled) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void updateDisplayBGC() {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        autoDrawable.getContext().release();
        if (frame != null) {
            frame.dispose();
        }
    }

    /**
     * Switches to another RenderContext identified by the contextKey or to a
     * new RenderContext if none is provided.
     *
     * @param contextKey
     *                key identifier
     * @return RenderContext identified by the contextKey or new RenderContext
     *         if none provided
     */
    private RenderContext switchContext(Object contextKey) {
        currentContext = contextStore.get(contextKey);
        if (currentContext == null) {
            currentContext = new RenderContext(contextKey);
            currentContext.setupRecords(getRenderer());
            contextStore.put(contextKey, currentContext);
        }

        return currentContext;
    }

}
