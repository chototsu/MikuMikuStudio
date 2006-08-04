/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme.app;

import java.util.concurrent.*;
import java.util.logging.*;
import java.util.prefs.*;

import com.jme.image.*;
import com.jme.input.*;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.renderer.pass.*;
import com.jme.scene.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.*;
import com.jmex.model.XMLparser.Converters.*;
import com.jmex.sound.openAL.*;

/**
 * <code>StandardGame</code> intends to be a basic implementation of a game that can be
 * utilized in games as a logical next step from <code>SimpleGame</code> and can be utilized
 * in production games.
 * 
 * @author Matthew D. Hicks
 */
public class StandardGame extends AbstractGame implements Runnable {
    private static final String FONT_LOCATION = "/com/jme/app/defaultfont.tga";
    
    public static enum GameType {
        GRAPHICAL,
        HEADLESS
    }
    
    private Thread gameThread;
    private String gameName;
    private GameType type;
    private GameSettings settings;
    private boolean started;
    
    private Text fps;
    private Node fpsNode;
    private Timer timer;
    private Camera camera;
    private ColorRGBA backgroundColor;
    private BasicPassManager passManager;
    
    private ConcurrentLinkedQueue<GameTask> updateQueue;
    private ConcurrentLinkedQueue<GameTask> renderQueue;
    
    public StandardGame(String gameName, GameType type) {
        this(gameName, type, null);
    }
    
    public StandardGame(String gameName, GameType type, PreferencesGameSettings settings) {
        this.gameName = gameName;
        this.type = type;
        this.settings = settings;
        backgroundColor = ColorRGBA.black;
        passManager = new BasicPassManager();
        
        // Instantiate our queues
        updateQueue = new ConcurrentLinkedQueue<GameTask>();
        renderQueue = new ConcurrentLinkedQueue<GameTask>();
    }

    public void start() {
        // Validate settings
        if (settings == null) {
            settings =  new PreferencesGameSettings(Preferences.userRoot().node(gameName));
        }
        
        gameThread = new Thread(this);
        gameThread.start();
        
        // Wait for main game loop before returning
        try {
            while (!isStarted()) {
                Thread.sleep(1);
            }
        } catch(InterruptedException exc) {
            exc.printStackTrace();
        }
    }
    
    public void run() {
        initSystem();
        assertDisplayCreated();
        initGame();
        if (type == GameType.GRAPHICAL) {
            timer = Timer.getTimer();
        } else if (type == GameType.HEADLESS) {
            timer = new NanoTimer();
        }

        // Configure frame rate
        int preferredFPS = settings.getFramerate();
        long preferredTicksPerFrame = -1;
        long frameStartTick = -1;
        long frames = 0;
        long frameDurationTicks = -1;
        if (preferredFPS >= 0) {
            preferredTicksPerFrame = Math.round((float)timer.getResolution() / (float)preferredFPS);
        }
        
        // Main game loop
        try {
            float tpf;
            started = true;
            while ((!finished) && (!display.isClosing())) {
                // Fixed framerate Start
                if (preferredTicksPerFrame >= 0) {
                    frameStartTick = timer.getTime();
                }
                
                timer.update();
                tpf = timer.getTimePerFrame();
                
                if (type == GameType.GRAPHICAL) {
                    InputSystem.update();
                }
                update(tpf);
                render(tpf);
                display.getRenderer().displayBackBuffer();
                
                // Fixed framerate End
                if (preferredTicksPerFrame >= 0) {
                    frames++;
                    frameDurationTicks = timer.getTime() - frameStartTick;
                    while (frameDurationTicks < preferredTicksPerFrame) {
                        long sleepTime = ((preferredTicksPerFrame - frameDurationTicks) * 1000) / timer.getResolution();
                        try {
                            Thread.sleep(sleepTime);
                        } catch(InterruptedException exc) {
                            LoggingSystem.getLogger().log(Level.SEVERE, "Interrupted while sleeping in fixed-framerate", exc);
                        }
                        frameDurationTicks = timer.getTime() - frameStartTick;
                    }
                    if (frames == Long.MAX_VALUE) frames = 0;
                }
                
                Thread.yield();
            }
            started = false;
        } catch(Throwable t) {
            LoggingSystem.getLogger().log(Level.SEVERE, "Main game loop broken by uncaught exception", t);
        }
        cleanup();
        quit();
    }

    protected void initSystem() {
        if (type == GameType.GRAPHICAL) {
            display = DisplaySystem.getDisplaySystem(settings.getRenderer());
            displayMins();
            display.createWindow(settings.getWidth(),
                                 settings.getHeight(),
                                 settings.getDepth(),
                                 settings.getFrequency(),
                                 settings.isFullscreen());
            camera = display.getRenderer().createCamera(display.getWidth(), display.getHeight());
            display.getRenderer().setBackgroundColor(backgroundColor);
            
            // Configure Camera
            cameraPerspective();
            cameraFrame();
            camera.update();
            display.getRenderer().setCamera(camera);
            
            display.setTitle(gameName);
            
            if ((settings.isMusic()) || (settings.isSFX())) {
                SoundSystem.init(camera, SoundSystem.OUTPUT_DEFAULT);
            }
        } else {
            display = new DummyDisplaySystem();
        }
    }
    
    private void displayMins() {
        display.setMinDepthBits(settings.getDepthBits());
        display.setMinStencilBits(settings.getStencilBits());
        display.setMinAlphaBits(settings.getAlphaBits());
        display.setMinSamples(settings.getSamples());
    }
    
    private void cameraPerspective() {
        camera.setFrustumPerspective(45.0f, (float)display.getWidth() / (float)display.getHeight(), 1.0f, 1000.0f);
        camera.setParallelProjection(false);
        camera.update();
    }
    
    private void cameraFrame() {
        Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0.0f, -1.0f);
        camera.setFrame(loc, left, up, dir);
    }
    
    protected void initGame() {
        if (type == GameType.GRAPHICAL) {
            // Frames Per Second stuff
            AlphaState as = display.getRenderer().createAlphaState();
            as.setBlendEnabled(true);
            as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
            as.setDstFunction(AlphaState.DB_ONE);
            as.setTestEnabled(true);
            as.setTestFunction(AlphaState.TF_GREATER);
            as.setEnabled(true);
            TextureState font = display.getRenderer().createTextureState();
            font.setTexture(
                    TextureManager.loadTexture(StandardGame.class.getResource(FONT_LOCATION),
                    Texture.MM_LINEAR,
                    Texture.FM_LINEAR));
            font.setEnabled(true);
            fps = new Text("FPS label", "");
            fps.setTextureCombineMode(TextureState.REPLACE);
            fpsNode = new Node("FPS node");
            fpsNode.attachChild(fps);
            fpsNode.setRenderState(font);
            fpsNode.setRenderState(as);
            fpsNode.updateGeometricState(0.0f, true);
            fpsNode.updateRenderState();
        }
        
        // Create the GameStateManager
        GameStateManager.create();
    }
    
    protected void update(float interpolation) {
        // Update the GameStates
        GameStateManager.getInstance().update(interpolation);
        
        // Execute updateQueue item
        execute(updateQueue);
        
        if (type == GameType.GRAPHICAL) {
            // Update PassManager
            passManager.updatePasses(interpolation);
            
            // Update FPS
            fps.print(Math.round(timer.getFrameRate()) + " fps");
            
            // Update music/sound
            if ((settings.isMusic()) || (settings.isSFX())) {
                SoundSystem.update(interpolation);
            }
        }
    }
    
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();
        GameStateManager.getInstance().render(interpolation);
        
        // Execute renderQueue item
        execute(renderQueue);
        
        // Render PassManager
        passManager.renderPasses(display.getRenderer());
        
        // Render FPS
        display.getRenderer().draw(fpsNode);
    }
    
    private void execute(ConcurrentLinkedQueue<GameTask> queue) {
        GameTask task = queue.poll();
        if (task == null) return;
        while (task.isCancelled()) {
            task = queue.poll();
            if (task == null) return;
        }
        task.invoke();
    }
    
    protected void reinit() {
        displayMins();
        SoundSystem.stopAllSamples();
        display.recreateWindow(settings.getWidth(),
                               settings.getHeight(),
                               settings.getDepth(),
                               settings.getFrequency(),
                               settings.isFullscreen());
        camera = display.getRenderer().createCamera(display.getWidth(), display.getHeight());
        display.getRenderer().setBackgroundColor(backgroundColor);
        if ((settings.isMusic()) || (settings.isSFX())) {
            SoundSystem.init(camera, SoundSystem.OUTPUT_DEFAULT);
        }
    }
    
    protected void cleanup() {
        GameStateManager.getInstance().cleanup();
    }
    
    
    protected void quit() {
        if (display != null) {
            display.reset();
            display.close();
        }
    }
    
    /**
     * This method adds <code>callable</code> to the queue to
     * be invoked in the update() method in the OpenGL thread.
     * The Future returned may be utilized to cancel the task
     * or wait for the return object.
     * 
     * @param callable
     * @return
     *      Future<V>
     */
    
    public <V> Future<V> update(Callable<V> callable) {
        GameTask<V> task = new GameTask<V>(callable);
        updateQueue.add(task);
        return task;
    }
    
    /**
     * This method adds <code>callable</code> to the queue to
     * be invoked in the render() method in the OpenGL thread.
     * The Future returned may be utilized to cancel the task
     * or wait for the return object.
     * 
     * @param callable
     * @return
     *      Future<V>
     */
    
    public <V> Future<V> render(Callable<V> callable) {
        GameTask<V> task = new GameTask<V>(callable);
        renderQueue.add(task);
        return task;
    }
    
    /**
     * The internally used <code>DisplaySystem</code> for this instance
     * of <code>StandardGame</code>
     * 
     * @return
     *      DisplaySystem
     * 
     * @see DisplaySystem
     */
    public DisplaySystem getDisplay() {
        return display;
    }
    
    /**
     * The internally used <code>Camera</code> for this instance of
     * <code>StandardGame</code>.
     * 
     * @return
     *      Camera
     *      
     * @see Camera
     */
    public Camera getCamera() {
        return camera;
    }
    
    /**
     * The <code>BasicPassManager</code> utilized in <code>StandardGame</code>.
     * This is optional to be utilized. If nothing is added to it, it is simply
     * ignored.
     * 
     * @return
     *      BasicPassManager
     *      
     * @see BasicPassManager
     */
    public BasicPassManager getPassManager() {
        return passManager;
    }
    
    
    
    /**
     * The <code>GameSettings</code> implementation being utilized in
     * this instance of <code>StandardGame</code>.
     * 
     * @return
     *      GameSettings
     *      
     * @see GameSettings
     */
    public GameSettings getSettings() {
        return settings;
    }
    
    /**
     * Override the background color defined for this game. The reinit() method
     * must be invoked if the game is currently running before this will take effect.
     * 
     * @param backgroundColor
     */
    public void setBackgroundColor(ColorRGBA backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    /**
     * Gracefully shutdown the main game loop thread. This is a synonym
     * for the finish() method but just sounds better.
     * 
     * @see #finish()
     */
    public void shutdown() {
        finish();
    }
    
    /**
     * Will return true if within the main game loop. This is particularly
     * useful to determine if the game has finished the initialization but
     * will also return false if the game has been terminated.
     * 
     * @return
     *      boolean
     */
    public boolean isStarted() {
        return started;
    }
}

class GameTask<V> implements Future<V> {
    private Callable<V> callable;
    private boolean cancelled;
    private V result;
    private ExecutionException exc;
    
    public GameTask(Callable<V> callable) {
        this.callable = callable;
    }
    
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (result != null) {
            return false;
        }
        cancelled = true;
        return true;
    }

    public synchronized V get() throws InterruptedException, ExecutionException {
        while ((result == null) && (exc == null)) {
            wait();
        }
        if (exc != null) throw exc;
        return result;
    }

    public synchronized V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if ((result == null) && (exc == null)) {
            unit.timedWait(this, timeout);
        }
        if (exc != null) throw exc;
        if (result == null) throw new TimeoutException("Object not returned in time allocated.");
        return result;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isDone() {
        return result != null;
    }
    
    public Callable<V> getCallable() {
        return callable;
    }
    
    public synchronized void invoke() {
        try {
            result = callable.call();
        } catch(Exception e) {
            exc = new ExecutionException(e);
        }
        notifyAll();
    }
}