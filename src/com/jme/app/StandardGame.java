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

import java.util.logging.*;
import java.util.prefs.*;

import com.jme.image.*;
import com.jme.input.*;
import com.jme.math.*;
import com.jme.renderer.*;
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
    
    private static enum GameSetting {
        GAME_RENDERER,
        GAME_WIDTH,
        GAME_HEIGHT,
        GAME_DEPTH,
        GAME_FREQUENCY,
        GAME_FULLSCREEN,
        GAME_DEPTH_BITS,
        GAME_ALPHA_BITS,
        GAME_STENCIL_BITS,
        GAME_SAMPLES,
        GAME_MUSIC,
        GAME_SFX,
        GAME_FRAMERATE
    }
    
    private static final String DEFAULT_RENDERER = PropertiesIO.DEFAULT_RENDERER;
    private static final int DEFAULT_WIDTH = PropertiesIO.DEFAULT_WIDTH;
    private static final int DEFAULT_HEIGHT = PropertiesIO.DEFAULT_HEIGHT;
    private static final int DEFAULT_DEPTH = PropertiesIO.DEFAULT_DEPTH;
    private static final int DEFAULT_FREQUENCY = PropertiesIO.DEFAULT_FREQ;
    private static final boolean DEFAULT_FULLSCREEN = false; //PropertiesIO.DEFAULT_FULLSCREEN;
    private static final int DEFAULT_DEPTH_BITS = 8;
    private static final int DEFAULT_ALPHA_BITS = 0;
    private static final int DEFAULT_STENCIL_BITS = 0;
    private static final int DEFAULT_SAMPLES = 0;
    private static final boolean DEFAULT_MUSIC = true;
    private static final boolean DEFAULT_SFX = true;
    private static final int DEFAULT_FRAMERATE = -1;
    
    public static enum GameType {
        GRAPHICAL,
        HEADLESS
    }
    
    private Thread gameThread;
    private String gameName;
    private GameType type;
    private Preferences settings;
    private boolean started;
    
    private Text fps;
    private Node fpsNode;
    private Timer timer;
    private Camera camera;
    private ColorRGBA backgroundColor;
    
    public StandardGame(String gameName, GameType type, Preferences settings) {
        this.gameName = gameName;
        this.type = type;
        this.settings = settings;
        backgroundColor = ColorRGBA.black;
    }

    public void start() {
        // Validate settings
        if (settings == null) {
            settings = Preferences.userRoot().node(gameName);
        }
        
        gameThread = new Thread(this);
        gameThread.start();
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
        int preferredFPS = settings.getInt(GameSetting.GAME_FRAMERATE.toString(), DEFAULT_FRAMERATE);
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
            display = DisplaySystem.getDisplaySystem(settings.get(GameSetting.GAME_RENDERER.toString(), DEFAULT_RENDERER));
            displayMins();
            display.createWindow(settings.getInt(GameSetting.GAME_WIDTH.toString(), DEFAULT_WIDTH),
                                 settings.getInt(GameSetting.GAME_HEIGHT.toString(), DEFAULT_HEIGHT),
                                 settings.getInt(GameSetting.GAME_DEPTH.toString(), DEFAULT_DEPTH),
                                 settings.getInt(GameSetting.GAME_FREQUENCY.toString(), DEFAULT_FREQUENCY),
                                 settings.getBoolean(GameSetting.GAME_FULLSCREEN.toString(), DEFAULT_FULLSCREEN));
            camera = display.getRenderer().createCamera(display.getWidth(), display.getHeight());
            display.getRenderer().setBackgroundColor(backgroundColor);
            
            // Configure Camera
            cameraPerspective();
            cameraFrame();
            camera.update();
            display.getRenderer().setCamera(camera);
            
            display.setTitle(gameName);
            
            if ((settings.getBoolean(GameSetting.GAME_MUSIC.toString(), DEFAULT_MUSIC)) || (settings.getBoolean(GameSetting.GAME_SFX.toString(), DEFAULT_SFX))) {
                SoundSystem.init(camera, SoundSystem.OUTPUT_DEFAULT);
            }
        } else {
            display = new DummyDisplaySystem();
        }
    }
    
    private void displayMins() {
        display.setMinDepthBits(settings.getInt(GameSetting.GAME_DEPTH_BITS.toString(), DEFAULT_DEPTH_BITS));
        display.setMinStencilBits(settings.getInt(GameSetting.GAME_STENCIL_BITS.toString(), DEFAULT_STENCIL_BITS));
        display.setMinAlphaBits(settings.getInt(GameSetting.GAME_ALPHA_BITS.toString(), DEFAULT_ALPHA_BITS));
        display.setMinSamples(settings.getInt(GameSetting.GAME_SAMPLES.toString(), DEFAULT_SAMPLES));
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
        
        if (type == GameType.GRAPHICAL) {
            fps.print(Math.round(timer.getFrameRate()) + " fps");
            if ((settings.getBoolean(GameSetting.GAME_MUSIC.toString(), DEFAULT_MUSIC)) || (settings.getBoolean(GameSetting.GAME_SFX.toString(), DEFAULT_SFX))) {
                SoundSystem.update(interpolation);
            }
        }
    }
    
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();
        GameStateManager.getInstance().render(interpolation);
        display.getRenderer().draw(fpsNode);
    }
    
    protected void reinit() {
        displayMins();
        display.recreateWindow(settings.getInt(GameSetting.GAME_WIDTH.toString(), DEFAULT_WIDTH),
                               settings.getInt(GameSetting.GAME_HEIGHT.toString(), DEFAULT_HEIGHT),
                               settings.getInt(GameSetting.GAME_DEPTH.toString(), DEFAULT_DEPTH),
                               settings.getInt(GameSetting.GAME_FREQUENCY.toString(), DEFAULT_FREQUENCY),
                               settings.getBoolean(GameSetting.GAME_FULLSCREEN.toString(), DEFAULT_FULLSCREEN));
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