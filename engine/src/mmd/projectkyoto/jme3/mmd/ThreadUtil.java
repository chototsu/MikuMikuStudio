/*
 * Copyright (c) 2010-2014, Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package projectkyoto.jme3.mmd;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by kobayasi on 2014/05/21.
 */
public class ThreadUtil {
    private static ScheduledThreadPoolExecutor pool;
    public static synchronized ScheduledThreadPoolExecutor getPool() {
        if (pool == null) {
            int poolSize = Runtime.getRuntime().availableProcessors() + 4;
            pool = new ScheduledThreadPoolExecutor(poolSize);
        }
        return pool;
    }
    public static synchronized void shutdown() {
        if (pool == null) {
            return;
        }
        pool.shutdown();
        pool = null;
    }
    private static int remaining = 0;
    public static int getRemaining() {
        return remaining;
    }
    public static synchronized void addRemaining(int size) {
        if (size != 0) {
            remaining += size;
            for(ThreadPoolListener listener : listeners) {
                listener.remainingChanged(size);
            }
        }
    }
    public static interface ThreadPoolListener {
        public void remainingChanged(int remaining);
    }
    private static ArrayList<ThreadPoolListener> listeners = new ArrayList<ThreadPoolListener>();
    public static void addThreadPoolListener(ThreadPoolListener listener) {
        listeners.add(listener);
    }
    public static void removeThreadPoolListener(ThreadPoolListener listener) {
        listeners.remove(listener);
    }
    public static abstract class Job implements Callable{
        private int workSize = 0;
        public Job(int size) {
            workSize = size;
        }
        public final void addWorkSize(int size) {
            workSize += size;
            addRemaining(size);
        }
    }
    public static Future addJob(final Job job) {
        addRemaining(job.workSize);
        Callable c = new Callable() {
            @Override
            public Object call() throws Exception {
                try {
                    Object result = job.call();
                    return result;
                } finally {
                    try {
                        addRemaining(-job.workSize);
                    } finally {
                        job.workSize = 0;
                    }
                }
            }
        };
        return getPool().submit(c);
    }
}
