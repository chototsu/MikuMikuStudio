package com.jme.scene.model.XMLparser.Converters;

import com.jme.util.LoggingSystem;

import java.util.HashMap;
import java.io.*;

/**
 * Started Date: Jul 1, 2004<br><br>
 *
 * This class is a base for all format converters and provides a generic framework
 * to convert to jME format from any other format.
 *
 * @author Jack Lindamood
 */
abstract public class FormatConverter {

    /**Contains a map of properties that tell the converter how to convert the format. */
    HashMap properties=new HashMap();

    /**
     * Reads a given <code>format</code> and writes it to <code>jMEFormat</code> in the jME binary format.
     * @param format InputStream representing the format to read
     * @param jMEFormat OutputStream to write the jME binary equivalent too
     * @throws IOException If anything goes wrong during the writting
     */
    public abstract void convert(InputStream format,OutputStream jMEFormat) throws IOException;

    /**
     * Given an array of string arguments representing two files, the first file
     * will be read and the second file will be deleted (if existing), then created
     * and written to. Sample Usage "FormatConverter runner.txt runner.jme"
     * @param args The array representing the from file and to file
     */
    public void attemptFileConvert(String[] args){
        LoggingSystem.getLoggingSystem().loggerOn(false);
        if (args.length!=2){
            System.err.println("Correct way to use is: <FormatFile> <jmeoutputfile>");
            System.err.println("For example: runner.txt runner.jme");
            return;
        }
        File inFile=new File(args[0]);
        File outFile=new File(args[1]);
        if (!inFile.canRead()){
            System.err.println("Cannot read input file " + inFile);
            return;
        }
        try {
            System.out.println("Converting file " + inFile.getCanonicalPath() + " to " + outFile.getCanonicalPath());
            convert(new FileInputStream(inFile),new FileOutputStream(outFile));
        } catch (IOException e) {
            System.err.println("Unable to convert:" + e);
            return;
        }
        System.out.println("Conversion complete!");
    }

    /**
     * Adds a property.  Properties can tell this how to process this format
     * and are used in a format specific way.  Look at the doc for the format to
     * find what keys are used.
     * @param key Key to add (For example "texdir")
     * @param property Property for that key to have (For example "c:\\blarg\\")
     */
    public void setProperty(String key, Object property) {
        properties.put(key,property);
    }

    /**
     * Removes a property.  Properties can tell this how to process this format
     * and are used in a format specific way.
     * @param key The property to remove
     */
    public void clearProperty(String key){
        properties.remove(key);
    }
}
