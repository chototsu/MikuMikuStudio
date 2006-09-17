package com.jme.util;

import java.util.ArrayList;

/**
 * 
 * @author Joshua Slack
 * 
 * @deprecated Replaced by {@link GameTaskQueue}
 */
@SuppressWarnings({"Deprecation"})
public class RenderThreadActionQueue {

    /**
     * @deprecated Replaced by {@link GameTaskQueue}
     */
    protected static final ArrayList<RenderThreadExecutable> queue = new ArrayList<RenderThreadExecutable>();

    /**
     * @deprecated Replaced by {@link GameTaskQueue}
     * @param qItem -
     */
    public static void addToQueue(RenderThreadExecutable qItem) {
        queue.add(qItem);
    }

    /**
     * @deprecated v
     * @return -
     */
    public static boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * @deprecated Replaced by {@link GameTaskQueue}
     */
    public static void processQueueItem() {
        if (!isEmpty())
            queue.remove(0).doAction();
    }
}
