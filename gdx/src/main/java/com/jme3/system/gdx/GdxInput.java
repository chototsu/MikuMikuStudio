package com.jme3.system.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.jme3.input.RawInputListener;
import com.jme3.input.TouchInput;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;
import com.jme3.util.RingBuffer;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by kobayasi on 2013/12/27.
 */
public class GdxInput implements TouchInput, InputProcessor, GestureDetector.GestureListener {
    private static final Logger logger = Logger.getLogger(GdxInput.class.getName());
    final private static int MAX_EVENTS = 1024;

    public boolean mouseEventsEnabled = true;
    public boolean mouseEventsInvertX = false;
    public boolean mouseEventsInvertY = false;
    public boolean keyboardEventsEnabled = false;
    public boolean dontSendHistory = false;
    private RawInputListener listener = null;
    final private RingBuffer<TouchEvent> eventQueue = new RingBuffer<TouchEvent>(MAX_EVENTS);
    final private RingBuffer<TouchEvent> eventPoolUnConsumed = new RingBuffer<TouchEvent>(MAX_EVENTS);
    final private RingBuffer<TouchEvent> eventPool = new RingBuffer<TouchEvent>(MAX_EVENTS);
    final private HashMap<Integer, Vector2f> lastPositions = new HashMap<Integer, Vector2f>();
    private int lastX = -1;
    private int lastY = -1;
    private boolean isInitialized = false;

    @Override
    public void setSimulateMouse(boolean b) {
        mouseEventsEnabled = b;
    }

    @Override
    public void setSimulateKeyboard(boolean b) {
        keyboardEventsEnabled = b;
    }

    @Override
    public void setOmitHistoricEvents(boolean b) {
        dontSendHistory = b;
    }

    @Override
    public void initialize() {
        TouchEvent item;
        for (int i = 0; i < MAX_EVENTS; i++)
        {
            item = new TouchEvent();
            eventPool.push(item);
        }
        isInitialized = true;
    }

    @Override
    public void update() {
        if (listener != null) {
            TouchEvent event;
            MouseButtonEvent btn;
            int newX;
            int newY;
            while (!eventQueue.isEmpty()) {
                synchronized (eventQueue) {
                    event = eventQueue.pop();
                }
                if (event != null) {
                    listener.onTouchEvent(event);
                    if (mouseEventsEnabled) {
                        if (mouseEventsInvertX)
                            newX = Gdx.graphics.getWidth() - (int) event.getX();
                        else
                            newX = (int) event.getX();

                        if (mouseEventsInvertY)
                            newY = Gdx.graphics.getHeight() - (int) event.getY();
                        else
                            newY = (int) event.getY();
                        switch (event.getType()) {
                            case DOWN:
                                // Handle mouse down event
                                btn = new MouseButtonEvent(0, true, newX, newY);
                                btn.setTime(event.getTime());
                                listener.onMouseButtonEvent(btn);
                                // Store current pos
                                lastX = -1;
                                lastY = -1;
                                //lastX = newX;
                                //lastY = newY;
//                                System.err.println("DOWN x = " + btn.getX() + " y = " + btn.getY());
                                break;

                            case UP:
                                // Handle mouse up event
                                btn = new MouseButtonEvent(0, false, newX, newY);
                                btn.setTime(event.getTime());
                                listener.onMouseButtonEvent(btn);
                                // Store current pos
                                lastX = -1;
                                lastY = -1;
                                //lastX = newX;
                                //lastY = newY;
//                                System.err.println("UP x = " + btn.getX() + " y = " + btn.getY());
                                break;

                            case MOVE:
                                int dx;
                                int dy;
                                if (lastX != -1) {
                                    dx = newX - lastX;
                                    dy = newY - lastY;
                                } else {
                                    dx = 1;
                                    dy = 0;
                                }
                                MouseMotionEvent mot = new MouseMotionEvent(newX, newY, dx, dy, 0, 0);
                                mot.setTime(event.getTime());
                                listener.onMouseMotionEvent(mot);
                                lastX = newX;
                                lastY = newY;
                                break;
                        }
                    }
                }
                if (event.isConsumed() == false) {
                    synchronized (eventPoolUnConsumed) {
                        try {
                            eventPoolUnConsumed.push(event);
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                } else {
                    synchronized (eventPool) {
                        try {
                            eventPool.push(event);
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public void setInputListener(RawInputListener rawInputListener) {
        this.listener = rawInputListener;
    }

    @Override
    public long getInputTimeNanos() {
        return System.nanoTime();
    }

    private void processEvent(TouchEvent event) {
        synchronized (eventQueue) {
            try {
                eventQueue.push(event);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private TouchEvent getNextFreeTouchEvent() {
        return getNextFreeTouchEvent(false);
    }
    private TouchEvent getNextFreeTouchEvent(boolean wait)
    {
        TouchEvent evt = null;
        synchronized(eventPoolUnConsumed)
        {
            int size = eventPoolUnConsumed.size();
            while (size > 0)
            {
                evt = eventPoolUnConsumed.pop();
                if (!evt.isConsumed())
                {
                    eventPoolUnConsumed.push(evt);
                    evt = null;
                }
                else
                {
                    break;
                }
                size--;
            }
        }


        if (evt == null)
        {
            if (eventPool.isEmpty() && wait)
            {
                logger.warning("eventPool buffer underrun");
                boolean isEmpty;
                do
                {
                    synchronized(eventPool)
                    {
                        isEmpty = eventPool.isEmpty();
                    }
                    try { Thread.sleep(50); } catch (InterruptedException e) { }
                }
                while (isEmpty);
                synchronized(eventPool)
                {
                    evt = eventPool.pop();
                }
            }
            else if (eventPool.isEmpty())
            {
                evt = new TouchEvent();
                logger.warning("eventPool buffer underrun");
            }
            else
            {
                synchronized(eventPool)
                {
                    evt = eventPool.pop();
                }
            }
        }
        return evt;
    }

    // InputProcessor methods.

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //mouseMoved2(screenX, screenY);
        TouchEvent event = getNextFreeTouchEvent();
        //event.set(TouchEvent.Type.MOVE, screenX, Gdx.graphics.getHeight() - screenY, 0, 0);
        //event.setPointerId(0);
        //event.setTime(System.nanoTime());
        //processEvent(event);

        //event = getNextFreeTouchEvent();
        event.set(TouchEvent.Type.DOWN, screenX, Gdx.graphics.getHeight() - screenY, 0, 0);
        event.setPointerId(0);
        event.setTime(System.nanoTime());
        processEvent(event);

        Vector2f lastPos = lastPositions.get(pointer);
        if (lastPos == null)
        {
            lastPos = new Vector2f(screenX, Gdx.graphics.getHeight() - screenY);
            lastPositions.put(pointer, lastPos);
        }
        lastPos.set(screenX, Gdx.graphics.getHeight() - screenY);

        System.err.println("touchDown x = " + screenX + " y = " + screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        TouchEvent event = getNextFreeTouchEvent();
        event.set(TouchEvent.Type.UP, screenX, Gdx.graphics.getHeight() - screenY, 0, 0);
        event.setPointerId(pointer);
        event.setTime(System.nanoTime());
        processEvent(event);
        System.err.println("touchUp x = " + screenX + " y = " + screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (screenX < 0 || screenY < 0) {
            return false;
        }
        Vector2f lastPos = lastPositions.get(pointer);
        if (lastPos == null)
        {
            lastPos = new Vector2f(screenX, Gdx.graphics.getHeight() - screenY);
            lastPositions.put(pointer, lastPos);
        }
        TouchEvent event = getNextFreeTouchEvent();
        event.set(TouchEvent.Type.MOVE, screenX, Gdx.graphics.getHeight() - screenY, screenX - lastPos.x, Gdx.graphics.getHeight() - screenY - lastPos.y);
        event.setPointerId(pointer);
        event.setTime(System.nanoTime());
        processEvent(event);
        System.err.println("touchDragged x = " + screenX + " y = " + screenY);
        lastPos.set(screenX, Gdx.graphics.getHeight() - screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return mouseMoved2(screenX, screenY);
    }
    public boolean mouseMoved2(int screenX, int screenY) {
        if (screenX < 0 || screenY < 0) {
            return false;
        }
        Vector2f lastPos = lastPositions.get(0);
        if (lastPos == null)
        {
            lastPos = new Vector2f(screenX, Gdx.graphics.getHeight() - screenY);
            lastPositions.put(0, lastPos);
        }


        TouchEvent event = getNextFreeTouchEvent();
        event.set(TouchEvent.Type.MOVE, screenX, Gdx.graphics.getHeight() - screenY, screenX - lastPos.x, Gdx.graphics.getHeight() - screenY - lastPos.y);
        event.setPointerId(0);
        event.setTime(System.nanoTime());
        processEvent(event);
//        System.err.println("mouseMoved x = " + screenX + " y = " + screenY);
        lastPos.set(screenX, Gdx.graphics.getHeight() - screenY);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
//        System.err.println("scrolled amount = " + amount);
        return false;
    }
    public boolean isMouseEventsInvertY() {
        return mouseEventsInvertY;
    }

    public void setMouseEventsInvertY(boolean mouseEventsInvertY) {
        this.mouseEventsInvertY = mouseEventsInvertY;
    }

    public boolean isMouseEventsInvertX() {
        return mouseEventsInvertX;
    }

    public void setMouseEventsInvertX(boolean mouseEventsInvertX) {
        this.mouseEventsInvertX = mouseEventsInvertX;
    }

    // GestureListener

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        System.err.println("touchDown2");
        return touchDown((int)x, (int)y, pointer, button);
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        System.err.println("tap");
        return touchUp((int)x, (int)y, count, button);
    }

    @Override
    public boolean longPress(float x, float y) {
        System.err.println("longPress");
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        System.err.println("fling");
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        System.err.println("pan");
        return touchDragged((int)x, (int)y, 0);
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        System.err.println("panStop");
        return touchUp((int)x,(int)y,pointer, button);
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        System.err.println("zoom "+initialDistance+" "+distance);
        TouchEvent event = getNextFreeTouchEvent();
        event.set(TouchEvent.Type.SCALE_MOVE, initialDistance, distance, 0, 0);
        event.setPointerId(0);
        event.setTime(System.nanoTime());
        processEvent(event);
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        System.err.println("pinch");
        return false;
    }
}
