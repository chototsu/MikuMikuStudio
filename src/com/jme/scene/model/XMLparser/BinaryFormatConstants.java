package com.jme.scene.model.XMLparser;

/**
 * Started Date: Jun 23, 2004<br><br>
 *
 * This interface is used by jME's binary format to identify sections
 *
 * @author Jack Lindamood
 */
public interface BinaryFormatConstants {
    // -- File identifier
    public static final long BEGIN_FILE=1234567l;

    // -- Block identifiers.  Each of these need to be different
    public static final byte BEGIN_TAG=0;
    public static final byte END_TAG=1;
    public static final byte END_FILE=2;
    // -- Block identifiers

    // -- Data type identifiers.  Each of these need to be different
    public static final byte DATA_V3FARRAY=0;
    public static final byte DATA_V2FARRAY=1;
    public static final byte DATA_COLORARRAY=2;
    public static final byte DATA_STRING=3;
    public static final byte DATA_INTARRAY=4;
    public static final byte DATA_V3F = 5;
    public static final byte DATA_QUAT = 6;
    public static final byte DATA_FLOAT = 7;
    public static final byte DATA_COLOR = 8;
    public static final byte DATA_URL = 9;
    public static final byte DATA_INT = 10;
    // -- Data type identifiers


}
