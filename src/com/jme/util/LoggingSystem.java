/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.jme.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.jme.system.JmeException;


/**
 * <code>LoggingSystem</code> maintains a system for logging using the Logging
 * API in JDK 1.4 and higher. <code>LoggingSystem</code> is a singleton and
 * is created via the <code>getLoggingSystem</code> method. This initializes a
 * default logger that can be retrieved via the <code>getLogger</code> method.
 * The logger object is final so can not be directly altered. To change the
 * attributes of the logger, use the built in methods in <code>LoggingSystem</code>.
 * Once the <code>LoggingSystem</code> is created, the logging object can be
 * used normally.
 *
 * @see java.util.logging.Logger
 *
 * @author Mark Powell
 * @version $Id: LoggingSystem.java,v 1.3 2004-04-22 22:27:09 renanse Exp $
 */
public class LoggingSystem {
    //Singleton object for the logging sytem.
    private static LoggingSystem logSystem = null;

    //the logger.
    private final static Logger logger = Logger.getLogger("jme");

    //handler for the logger
    private Handler handler;

    /**
     * Private constructor is called by the <code>getLoggingSystem</code> method.
     * Since this is the initial creation of the logger, it's attributes are set
     * to a default of: All levels, output to debug.txt and a simple formatter.
     */
    private LoggingSystem() {
        loggerOn(true);

        try {
            handler = new FileHandler("debug.txt");
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            throw new JmeException("Could not start Logging System");
        }
    }

    /**
     * <code>getLogger</code> returns the logger object maintained by the
     * <code>LoggingSystem</code>. This reference is null until the
     * <code>LoggingSystem</code> is initialized with a call to
     * <code>getLoggingSystem</code>.
     *
     * @return logger the logging object.
     */
    public static Logger getLogger() {
        if(null == logSystem) {
            logSystem = new LoggingSystem();
        }
        return logger;
    }

    /**
     *
     * <code>loggerOn</code> turns the logger on and off. If true is passed
     * the filter level is set to fine allowing all message to display, false
     * filters all messages.
     * @param value true allows messages to display, false does not.
     */
    public void loggerOn(boolean value) {
        if(value) {
           logger.setLevel(Level.FINE);
        } else {
           logger.setLevel(Level.OFF);
        }
    }

    /**
     * <code>setHandler</code> sets the <code>Handler</code> of the logger.
     * By default the Handler is a simple <code>FileHandler</code> that
     * writes to debug.txt.
     *
     * @param handler the new handler to use for log handling.
     */
    public void setHandler(Handler handler) {
        this.handler = handler;
        logger.addHandler(handler);
    }

    /**
     * <code>setLevel</code> sets the filtering level to be used for this
     * logger. By default it is set to ALL.
     *
     * @param level the new level to set the logger to.
     */
    public void setLevel(Level level) {
        logger.setLevel(level);
    }

    /**
     * <code>setFormatter</code> sets the formatter to use for this logger.
     * By default it is set to simple formatter.
     *
     * @param formatter the new formatter to use for the logger.
     */
    public void setFormatter(Formatter formatter) {
        handler.setFormatter(formatter);
    }

    /**
     * <code>getLoggingSystem</code> is the entry point for the
     * <code>LoggingSystem</code> class. This creates a new
     * <code>LoggingSystem</code> object if need be, or returns the reference
     * if one is already created.
     *
     * @return the singleton reference to the <code>LoggingSystem</code>
     */
    public static LoggingSystem getLoggingSystem() {
        if(null == logSystem) {
                return logSystem = new LoggingSystem();
        } else {
            return logSystem;
        }
    }
}
