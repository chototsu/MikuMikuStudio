package com.jme.scene.model.XMLparser.Converters;

import java.util.HashMap;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Started Date: Jul 1, 2004<br><br>
 *
 * This class is a base for all format converters and provides a generic framework
 * to convert to jME format from any other format.
 *
 * @author Jack Lindamood
 */
abstract public class FormatConverter {

    /**Contains a map of properties that tell the converter how to convert the format */
    HashMap properties=new HashMap();

    /**
     * Reads a given <code>format</code> and writes it to <code>jMEFormat</code> in the jME binary format
     * @param format InputStream representing the format to read
     * @param jMEFormat OutputStream to write the jME binary equivalent too
     * @throws IOException If anything goes wrong during the writting
     */
    public abstract void convert(InputStream format,OutputStream jMEFormat) throws IOException;

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
