package com.jme.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * ErrorManager handles the complete system for logging and handling debugging
 * information. ErrorManager is a singleton class requiring a call to
 * getInstance to retrieve the ErrorHandler instance. This can then be used to
 * submit errors. If setEnableStack is true, messages with a level of warning or
 * severe will be added to the error stack. This stack has an upper bound of
 * 100, if 100 is reached, the last error will be "Error stack full, no more
 * errors stored.", and new errors will no longer be stored, until the error
 * stack is processed. However, they will continue to be logged as normal and
 * e-mailed if set.
 * @deprecated use java's Logger as illustrated in other classes
 * @author Mark Powell
 */
public class ErrorManager {
//    private static final int MAX_ERRORS = 100;
//
//    // handler for the logger
//    private Handler handler;
//
//    private static ErrorManager instance;
//    private Stack<String> errorStack;
//    private Map<String, Throwable> stackTraceMap;
//
//    // the logger.
//    private final static Logger logger = Logger.getLogger("jme");
//
//    private boolean enableStack = false;
//
//    private boolean sendMail = false;
//    private ErrorNotifier notifier;
//    
//    String fileName = "debug.txt";
//
//    /**
//     * private contstructor only called by the getInstance method when needed.
//     * Sets up the handler for the logging.
//     */
//    private ErrorManager() {
//        try {
//            handler = new FileHandler(fileName);
//            handler.setFormatter(new SimpleFormatter());
//            logger.addHandler(handler);
//        } catch (IOException e) {
//            System.err
//                    .println("Could not start Logging System with logging to file '"
//                            + fileName + "': ");
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * getInstance returns the singleton instance of the ErrorManager.
//     * 
//     * @return the singleton instance of the ErrorManager.
//     */
//    public static ErrorManager getInstance() {
//        if (instance == null) {
//            instance = new ErrorManager();
//        }
//
//        return instance;
//    }
//
//    /**
//     * setFilename sets the name and location of the output file to store the
//     * debug file.
//     * 
//     * @param filename
//     */
//    public void setFilename(String filename) {
//        try {
//            handler = new FileHandler(fileName);
//            handler.setFormatter(new SimpleFormatter());
//            logger.addHandler(handler);
//        } catch (IOException e) {
//            System.err
//                    .println("Could not start Logging System with logging to file '"
//                            + fileName + "': ");
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * turns on the stack storing capabilities of the ErrorHandler. If this is
//     * set to true, then any error with a level of warning or severe will be
//     * stored on the stack. It is up to the client to get the errors off the
//     * stack when they are available. If over 100 errors are put on the stack
//     * new errors will be dropped.
//     * 
//     * @param enabled
//     *            true to turn on the stack storing capabilities of the error
//     *            handler. false to turn it off.
//     */
//    public void setEnableStack(boolean enabled) {
//        if (errorStack == null) {
//            errorStack = new Stack<String>();
//
//            stackTraceMap = new HashMap<String, Throwable>();
//        }
//        this.enableStack = enabled;
//
//    }
//
//    /**
//     * addError places a new error on the stack if the level is warning or
//     * severe and the stack is enabled. If mailing is enabled, and the level is
//     * severe an e-mail is sent. The message is then logged with the appropriate
//     * level setting.
//     * 
//     * @param level
//     *            the level of the message.
//     * @param message
//     *            the message to process.
//     */
//    public synchronized void addError(Level level, String message) {
//        addError(level, message, null);
//    }
//
//    /**
//     * addError places a new error on the stack if the level is warning or
//     * severe and the stack is enabled. If mailing is enabled, and the level is
//     * severe an e-mail is sent. The message is then logged with the appropriate
//     * level setting.
//     * 
//     * @param level
//     *            the level of the message.
//     * @param message
//     *            the message to process.
//     * @param stackTrace
//     *            the stack trace that will be tied to this message. This stack
//     *            trace will be e-mailed if e-mail is enabled.
//     */
//    public synchronized void addError(Level level, String message,
//            Throwable stackTrace) {
//        if (enableStack) {
//            if (stackTrace != null) {
//                stackTraceMap.put(message, stackTrace);
//            }
//            if (level.equals(Level.WARNING) || level.equals(Level.SEVERE)) {
//                if (errorStack.capacity() <= MAX_ERRORS) {
//                    errorStack.push(message);
//                } else if (errorStack.capacity() == MAX_ERRORS) {
//                    errorStack.push("Error stack full, no more errors stored.");
//                }
//            }
//
//            if (level.equals(Level.SEVERE)) {
//                if (sendMail) {
//                    sendMail(message);
//                }
//            }
//        }
//        logger.log(level, message);
//    }
//
//    /**
//     * setSendMail will enable or disable the sending of mail when a severe
//     * error is added to the manager. This would be for critical errors and
//     * notifying the programmers.
//     * 
//     * @param value
//     *            true will send e-mail when a severe error is encountered,
//     *            false will not.
//     */
//    public void setSendMail(boolean value) {
//        sendMail = value;
//    }
//
//    /**
//     * sendMail will build a proper MIME message with the body containing the
//     * error message and send it to the set recipients. It is required that the
//     * mail information be set prior to sending the message. Including mail
//     * host, receivers, etc.
//     * 
//     * @param message
//     *            the message to e-mail.
//     */
//    public void sendMail(String message) {
//        if(notifier != null) {
//            String trace = getStackTrace(message);
//            notifier.send(message, trace);
//        }
//    }
//
//    /**
//     * loggerOn will set the logger to processes message or not.
//     * 
//     * @param value
//     *            true will set the logger to log everything, false log nothing.
//     */
//    public void loggerOn(boolean value) {
//        if (value) {
//            logger.setLevel(Level.FINE);
//        } else {
//            logger.setLevel(Level.OFF);
//        }
//    }
//
//    /**
//     * setLevel allows you to set the level of the logger to allow a fine grain
//     * control of the logging levels.
//     * 
//     * @param level
//     */
//    public void setLevel(Level level) {
//        logger.setLevel(level);
//    }
//
//    /**
//     * hasErrors returns true if there are errors on the stack, false otherwise.
//     * 
//     * @return true if there are errors on the stack, false otherwise.
//     */
//    public synchronized boolean hasErrors() {
//        if (errorStack == null) {
//            return false;
//        }
//        return !errorStack.empty();
//    }
//
//    /**
//     * getError retrieves the last error stored on the queue.
//     * 
//     * @return the top error on the list.
//     */
//    public synchronized String getError() {
//        if (hasErrors()) {
//            return errorStack.pop();
//        } else {
//            return "";
//        }
//    }
//
//    /**
//     * getStackTrace returns any stack traces associated with this message. If
//     * there is no stack trace associated with this message than null is
//     * returned. The stack trace is returned as a String with the name of the
//     * exception, the message for the exception and the trace of the exception.
//     * 
//     * @param key
//     *            the message to use as the key to obtain the stack trace.
//     * @return the stack trace if it exists, null otherwise.
//     */
//    public synchronized String getStackTrace(String key) {
//        Throwable e = stackTraceMap.get(key);
//        if (e == null) {
//            return null;
//        }
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        e.printStackTrace(pw);
//
//        return sw.toString();
//    }
//
//    public ErrorNotifier getNotifier() {
//        return notifier;
//    }
//
//    public void setNotifier(ErrorNotifier notifier) {
//        this.notifier = notifier;
//    }
}
