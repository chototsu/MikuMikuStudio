package com.jmex.model.XMLparser;

/**
 * Started Date: Jun 6, 2004
 *
 * Is implimented by jME objects to allow them to be saved/loaded to/from jME
 * All implimenting classes must have a default constructor, or they will be
 * unable to be processed.  It is guaranteed that loadFromXML(String args) will
 * be called directly after default construction.  The user should make sure that the String
 * returned from writeToXML() will duplicate the current object when loadFromXML(String) is
 * called.
 *
 * <br>
 *
 * Note: If the implementing item is a Spatial, then it's name, translation, rotation, scale atts are set automatically
 * and shouldn't be included in either String
 *
 * <br>
 *
 * @author Jack Lindamood
 */
public interface XMLloadable {
    /**
     *<code>writeToXML</code> will return a String that when passed to <code>loadFromXML</code>
     * directly after a call to the default constructor, will reconstruct the current class.
     * @return String to later be given to <code>loadFromXML</code>
     */
    public String writeToXML();
    /**
     * Given a String from a previous object of the same class, <code>loadFromXML</code> will
     * duplicate that class.
     * @param args The string args given to reconstruct this class
     * @return The object that <code>JmeBinaryReader</code> will use.  This can either be the same object that
     * loadFromXML was called on, or a new object.  Only the returned object will be used. 
     */
    public Object loadFromXML(String args);
}