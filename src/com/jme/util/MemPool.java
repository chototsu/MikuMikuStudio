package com.jme.util;

import com.jme.math.Vector3f;
import com.jme.math.Matrix3f;
import com.jme.math.Vector2f;

/**
 *
 * This is a class that contains temporary objects usable in functions during jME's
 * execution.  This class and its variables are <b>NOT</b> thread safe.
 *
 * @author Jack Lindamood
 */
public class MemPool {

    private MemPool(){}
    public static Vector3f v3a=new Vector3f();
    public static Vector3f v3b=new Vector3f();
    public static Vector3f v3c=new Vector3f();
    public static Vector3f v3d=new Vector3f();
    public static Vector3f v3e=new Vector3f();
    public static Matrix3f m3a=new Matrix3f();
    public static Matrix3f m3b=new Matrix3f();
    public static Vector2f v2a=new Vector2f();
    public static Vector2f v2b=new Vector2f();
    public static Vector2f v2c=new Vector2f();
    public static Vector2f v2d=new Vector2f();
}
