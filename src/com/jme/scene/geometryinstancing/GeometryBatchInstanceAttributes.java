package com.jme.scene.geometryinstancing;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.geometryinstancing.instance.GeometryInstanceAttributes;

/**
 * <code>GeometryBatchInstanceAttributes</code> specifies the attributes for a
 * <code>GeometryBatchInstance</code>
 *
 * @author Patrik Lindegrén
 */
public class GeometryBatchInstanceAttributes
        extends GeometryInstanceAttributes {
    protected ColorRGBA color;

    public GeometryBatchInstanceAttributes(Vector3f translation, Vector3f scale,
                                           Vector3f rotation, ColorRGBA color) {
        super(translation, scale, rotation);
        this.color = color;
    }

    /** <code>buildMatrices</code> updates the world and rotation matrix */
    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }
}