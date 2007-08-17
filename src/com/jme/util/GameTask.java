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
package com.jme.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>GameTask</code> is used in <code>GameTaskQueue</code> to manage tasks that have
 * yet to be accomplished.
 * 
 * @author Matthew D. Hicks
 */
class GameTask<V> implements Future<V> {
    private static final Logger logger = Logger.getLogger(GameTask.class
            .getName());
    
    private Callable<V> callable;
    private boolean cancelled;
    private boolean completed;
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
        while ((!completed) && (exc == null)) {
            wait();
        }
        if (exc != null) throw exc;
        return result;
    }

    public synchronized V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if ((!completed) && (exc == null)) {
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
        return completed;
    }
    
    public Callable<V> getCallable() {
        return callable;
    }
    
    public synchronized void invoke() {
        try {
            result = callable.call();
            completed = true;
        } catch(Exception e) {
        	logger.logp(Level.SEVERE, this.getClass().toString(), "invoke()", "Exception", e);
            exc = new ExecutionException(e);
        }
        notifyAll();
    }
}
