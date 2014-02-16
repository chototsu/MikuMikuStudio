package com.jme3.system.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.jme3.input.*;
import com.jme3.input.dummy.DummyKeyInput;
import com.jme3.input.dummy.DummyMouseInput;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.gdx.GdxRenderer;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.SystemListener;
import com.jme3.system.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: kobayasi
 * Date: 13/10/07
 * Time: 20:59
 * To change this template use File | Settings | File Templates.
 */
public class GdxContext implements JmeContext {
    private SystemListener systemListener;
    private AppSettings settings;
    private GdxRenderer renderer;
    private boolean created = false;
    private boolean renderable = false;
    private boolean autoFlush = true;
    private GdxTimer timer;
    private boolean needInitialize = true;

    public GdxContext() {
        timer = new GdxTimer();
    }

    @Override
    public Type getType() {
        return Type.Display;
    }

    @Override
    public void setSettings(AppSettings appSettings) {
        System.out.println("GdxContext.setSettings");
        if (appSettings == null) {
            throw new RuntimeException("GdxContext.setSettings appSettings == null");
        }
        this.settings = appSettings;
    }

    @Override
    public void setSystemListener(SystemListener systemListener) {
        System.out.println("setSystemListener");
        this.systemListener = systemListener;
    }

    @Override
    public AppSettings getSettings() {
        return settings;
    }

    @Override
    public Renderer getRenderer() {
        return renderer;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MouseInput getMouseInput() {
        return new DummyMouseInput();
    }

    @Override
    public KeyInput getKeyInput() {
        return new DummyKeyInput();
    }

    @Override
    public JoyInput getJoyInput() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TouchInput getTouchInput() {
        GdxInput input = new GdxInput();
        Gdx.input.setInputProcessor(new GestureDetector(input));
        return input;
        //return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public void setTitle(String s) {
    }

    @Override
    public boolean isCreated() {
        return created;
    }

    @Override
    public boolean isRenderable() {
        return renderable;
    }

    @Override
    public void setAutoFlushFrames(boolean b) {
        this.autoFlush = b;
    }

    @Override
    public void create(boolean b) {
        System.out.println("GdxContext.create()");
        renderer = new GdxRenderer();
        renderable = true;
        created = true;
    }

    @Override
    public void restart() {
    }

    @Override
    public void destroy(boolean b) {
        renderable = false;
    }

    public void onDrawFrame() {
        if (needInitialize) {
            renderer.initialize();
            systemListener.initialize();
            needInitialize = false;
        }
        if (isRenderable()) {
            if (systemListener != null) {
                systemListener.update();
            }
            if (autoFlush) {
                renderer.onFrame();
            }
        }
    }
}
