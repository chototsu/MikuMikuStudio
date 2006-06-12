package com.jmex.font3d;

import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * This is the interface for some peace of text in JME. The implementation, and
 * hence the rendering of the text depends on the kind of text (3D, 2D, etc.).
 * 
 * It is good practice to create an implementation of {@link TextFactory} and then
 * have that create instances of {@link JmeText}.
 *  
 * @author emanuel
 */
public interface JmeText {
    /**
     * @return the factory where this text was created, or null if it does not
     *         know.
     */
    TextFactory getFactory();

    /**
     * @return the string of text that this object is visualizing.
     */
    StringBuffer getText();

    /**
     * Sets the string of text that this object is visualizing, the geometry of
     * the object should change to reflect the change.
     * 
     * @param text
     */
    void setText(String text);

    /**
     * Append text to the string of text that this object is visualizing.
     * 
     * @param text
     */
    void appendText(String text);

    /**
     * @return the flags that were given when this text was created.
     */
    int getFlags();

    /**
     * @return the size of the text (normally size 12 refers to 12pt, in jme I
     *         guess it refers to jme-units).
     */
    float getSize();

    /**
     * change the size of the font, this will most likely be implemented with
     * scaling, so watch out when using this and setLocalScale(...).
     * 
     * @param size
     */
    void setSize(float size);

    // For compatability with Spatial/Geometry
    void setLocalRotation(Matrix3f rotation);

    void setLocalRotation(Quaternion quaternion);

    void setLocalScale(float localScale);

    void setLocalScale(Vector3f trans);

    void setLocalTranslation(Vector3f trans);
}
