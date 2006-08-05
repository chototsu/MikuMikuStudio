package com.jme.util;

import java.util.ArrayList;

/**
 * 
 * @author Joshua Slack
 * 
 * @deprecated Replaced by {@link GameTaskQueue}
 */
public class RenderThreadActionQueue {
    
    protected static final ArrayList<RenderThreadExecutable> queue = new ArrayList<RenderThreadExecutable>();

    public static void addToQueue(RenderThreadExecutable qItem) {
        queue.add(qItem);
    }

    public static boolean isEmpty() {
        return queue.isEmpty();
    }

    public static void processQueueItem() {
        if (!isEmpty())
            queue.remove(0).doAction();
    }
}
