package com.jme.scene.model.XMLparser;

import java.util.Hashtable;

/**
 * Started Date: Jun 6, 2004
 *
 * Is implimented by jME objects to allow them to be saved/loaded to/from XML
 * All implimenting classes must have a default constructor, or they will be
 * unable to be constructed.  It is guaranteed that loadFromXML(String args) will
 * be called after default construction.  The user should make sure that the String
 * returned from writeToXML() will duplicate the current object when loadFromXML(String) is
 * called.
 *
 * *IMPORTANT* loadFromXML will not load the 'current' object from the string args, instead
 * it will return a new object created from those args
 * <br>
 *
 * Note: If the implementing item is a Spatial, then it's name, translation, rotation, scale atts are set automatically
 * and shouldn't be included in either String
 *
 * <br>
 *
 * //TODO: There is probably a better way to do this.  Find it!
 * @author Jack Lindamood
 */
public interface XMLloadable {
    public String writeToXML();
    public Object loadFromXML(String args);
}