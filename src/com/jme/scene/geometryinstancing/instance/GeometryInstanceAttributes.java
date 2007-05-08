package com.jme.scene.geometryinstancing.instance;

import com.jme.math.FastMath;
import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;

/**
 * <code>GeometryInstanceAttributes</code> specifies the attributes for a
 * <code>GeometryInstance</code>.
 *
 * @author Patrik Lindegrén
 */
public class GeometryInstanceAttributes {
    protected Vector3f scale;        // Scale
    protected Vector3f rotation;    // Rotation
    protected Vector3f translation;    // Translation
    protected Matrix4f mtNormal;    // Normal matrix (scale, rotation)
    protected Matrix4f mtWorld;        // Local to world matrix (scale, rotation, translation)

    public GeometryInstanceAttributes(Vector3f translation, Vector3f scale,
                                      Vector3f rotation) {
        this.scale = scale;
        this.rotation = rotation;
        this.translation = translation;
        mtWorld = new Matrix4f();
        mtNormal = new Matrix4f();
        buildMatrices();
    }

    /**
     * Vector used to store and calculate rotation in degrees Not needed when
     * radian rotation is implemented in Matrix4f
     */
    private Vector3f rotationDegrees = new Vector3f();

    /** <code>buildMatrices</code> updates the world and rotation matrix */
    public void buildMatrices() {
        // Scale (temporarily use mtWorld as storage)
        mtWorld.loadIdentity();
        mtWorld.m00 = scale.x;
        mtWorld.m11 = scale.y;
        mtWorld.m22 = scale.z;

        // Build rotation matrix (temporarily use mtNormal as storage)
        rotationDegrees.set(rotation).multLocal(FastMath.RAD_TO_DEG);
        mtNormal.loadIdentity();
        mtNormal.angleRotation(rotationDegrees);
        //mtNormal.radianRotation(rotation);		// Add a radian rotation function to Matrix4f (requested feature)

        // Build normal matrix (scale * rotation)
        mtNormal.multLocal(mtWorld);

        // Build world matrix (scale * rotation + translation)
        mtWorld.set(mtNormal);
        mtWorld.setTranslation(translation);
    }

    public Vector3f getScale() {
        return scale;
    }

    /**
     * After using the <code>setScale</code> function, user needs to call the
     * <code>buildMatrices</code> function
     *
     * @param scale
     */
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    /**
     * After using the <code>setTranslation</code> function, user needs to call
     * the <code>buildMatrices</code> function
     *
     * @param translation
     */
    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * After using the <code>setRotation</code> function, user needs to call the
     * <code>buildMatrices</code> function
     *
     * @param rotation
     */
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Matrix4f getWorldMatrix() {
        return mtWorld;
    }

    public Matrix4f getNormalMatrix() {
        return mtNormal;
    }
}