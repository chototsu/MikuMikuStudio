/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.util.export.binary;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import com.jme.util.export.ByteUtils;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

public class BinaryInputCapsule implements InputCapsule {

    protected BinaryImporter importer;
    protected BinaryClassObject cObj;
    protected HashMap<Byte, Object> fieldData; 

    protected int index = 0;

    public BinaryInputCapsule(BinaryImporter importer, BinaryClassObject bco) {
        this.importer = importer;
        this.cObj = bco;
    }

    public void setContent(byte[] content) {
        fieldData = new HashMap<Byte, Object>();
        for (index = 0; index < content.length; ) {
            byte alias = content[index];
            index++;
            
            try {
                byte type = cObj.aliasFields.get(alias).type;
                switch(type) {
                    case BinaryClassField.BITSET: {
                        BitSet value = readBitSet(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.BOOLEAN: {
                        boolean value = readBoolean(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.BOOLEAN_1D: {
                        boolean[] value = readBooleanArray(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.BOOLEAN_2D: {
                        boolean[][] value = readBooleanArray2D(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.BYTE: {
                        byte value = readByte(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.BYTE_1D: {
                        byte[] value = readByteArray(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.BYTE_2D: {
                        byte[][] value = readByteArray2D(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.BYTEBUFFER: {
                        ByteBuffer value = readByteBuffer(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.DOUBLE: {
                        double value = readDouble(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.DOUBLE_1D: {
                        double[] value = readDoubleArray(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.DOUBLE_2D: {
                        double[][] value = readDoubleArray2D(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.FLOAT: {
                        float value = readFloat(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.FLOAT_1D: {
                        float[] value = readFloatArray(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.FLOAT_2D: {
                        float[][] value = readFloatArray2D(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.FLOATBUFFER: {
                        FloatBuffer value = readFloatBuffer(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.FLOATBUFFER_ARRAYLIST: {
                        ArrayList value = readFloatBufferArrayList(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.INT: {
                        int value = readInt(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.INT_1D: {
                        int[] value = readIntArray(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.INT_2D: {
                        int[][] value = readIntArray2D(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.INTBUFFER: {
                        IntBuffer value = readIntBuffer(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.LONG: {
                        long value = readLong(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.LONG_1D: {
                        long[] value = readLongArray(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.LONG_2D: {
                        long[][] value = readLongArray2D(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.SAVABLE: {
                        Savable value = readSavable(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.SAVABLE_1D: {
                        Savable[] value = readSavableArray(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.SAVABLE_2D: {
                        Savable[][] value = readSavableArray2D(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.SAVABLE_ARRAYLIST: {
                        ArrayList value = readSavableArrayList(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.SAVABLE_ARRAYLIST_1D: {
                        ArrayList[] value = readSavableArrayListArray(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.SAVABLE_ARRAYLIST_2D: {
                        ArrayList[][] value = readSavableArrayListArray2D(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.SHORT: {
                        short value = readShort(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.SHORT_1D: {
                        short[] value = readShortArray(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.SHORT_2D: {
                        short[][] value = readShortArray2D(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.SHORTBUFFER: {
                        ShortBuffer value = readShortBuffer(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.STRING: {
                        String value = readString(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.STRING_1D: {
                        String[] value = readStringArray(content);
                        fieldData.put(alias, value);
                        break;
                    }
                    case BinaryClassField.STRING_2D: {
                        String[][] value = readStringArray2D(content);
                        fieldData.put(alias, value);
                        break;
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public BitSet readBitSet(String name, BitSet defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (BitSet)fieldData.get(field.alias);
    }

    public boolean readBoolean(String name, boolean defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return ((Boolean)fieldData.get(field.alias)).booleanValue();
    }

    public boolean[] readBooleanArray(String name, boolean[] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (boolean[])fieldData.get(field.alias);
    }

    public boolean[][] readBooleanArray2D(String name, boolean[][] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (boolean[][])fieldData.get(field.alias);
    }

    public byte readByte(String name, byte defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return ((Byte)fieldData.get(field.alias)).byteValue();
    }

    public byte[] readByteArray(String name, byte[] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (byte[])fieldData.get(field.alias);
    }

    public byte[][] readByteArray2D(String name, byte[][] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (byte[][])fieldData.get(field.alias);
    }

    public ByteBuffer readByteBuffer(String name, ByteBuffer defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (ByteBuffer)fieldData.get(field.alias);
    }

    public double readDouble(String name, double defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return ((Double)fieldData.get(field.alias)).doubleValue();
    }

    public double[] readDoubleArray(String name, double[] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (double[])fieldData.get(field.alias);
    }

    public double[][] readDoubleArray2D(String name, double[][] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (double[][])fieldData.get(field.alias);
    }

    public float readFloat(String name, float defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return ((Float)fieldData.get(field.alias)).floatValue();
    }

    public float[] readFloatArray(String name, float[] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (float[])fieldData.get(field.alias);
    }

    public float[][] readFloatArray2D(String name, float[][] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (float[][])fieldData.get(field.alias);
    }

    public FloatBuffer readFloatBuffer(String name, FloatBuffer defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (FloatBuffer)fieldData.get(field.alias);
    }

    public ArrayList readFloatBufferArrayList(String name, ArrayList<FloatBuffer> defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (ArrayList)fieldData.get(field.alias);
    }

    public int readInt(String name, int defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return ((Integer)fieldData.get(field.alias)).intValue();
    }

    public int[] readIntArray(String name, int[] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (int[])fieldData.get(field.alias);
    }

    public int[][] readIntArray2D(String name, int[][] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (int[][])fieldData.get(field.alias);
    }

    public IntBuffer readIntBuffer(String name, IntBuffer defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (IntBuffer)fieldData.get(field.alias);
    }

    public long readLong(String name, long defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return ((Long)fieldData.get(field.alias)).longValue();
    }

    public long[] readLongArray(String name, long[] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (long[])fieldData.get(field.alias);
    }

    public long[][] readLongArray2D(String name, long[][] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (long[][])fieldData.get(field.alias);
    }

    public Savable readSavable(String name, Savable defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (Savable)fieldData.get(field.alias);
    }

    public Savable[] readSavableArray(String name, Savable[] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (Savable[])fieldData.get(field.alias);
    }

    public Savable[][] readSavableArray2D(String name, Savable[][] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (Savable[][])fieldData.get(field.alias);
    }

    public ArrayList readSavableArrayList(String name, ArrayList defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (ArrayList)fieldData.get(field.alias);
    }

    public ArrayList[] readSavableArrayListArray(String name, ArrayList[] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (ArrayList[])fieldData.get(field.alias);
    }

    public ArrayList[][] readSavableArrayListArray2D(String name, ArrayList[][] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (ArrayList[][])fieldData.get(field.alias);
    }

    public short readShort(String name, short defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return ((Short)fieldData.get(field.alias)).shortValue();
    }

    public short[] readShortArray(String name, short[] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (short[])fieldData.get(field.alias);
    }

    public short[][] readShortArray2D(String name, short[][] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (short[][])fieldData.get(field.alias);
    }

    public ShortBuffer readShortBuffer(String name, ShortBuffer defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (ShortBuffer)fieldData.get(field.alias);
    }

    public String readString(String name, String defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (String)fieldData.get(field.alias);
    }

    public String[] readStringArray(String name, String[] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (String[])fieldData.get(field.alias);
    }

    public String[][] readStringArray2D(String name, String[][] defVal) throws IOException {
        BinaryClassField field = cObj.nameFields.get(name);
        if (field == null || !fieldData.containsKey(field.alias)) return defVal;
        return (String[][])fieldData.get(field.alias);
    }

    
    
    
    // byte primitive
    
    protected byte readByte(byte[] content) throws IOException {
        byte value = content[index];
        index++;
        return value;        
    }
    protected byte[] readByteArray(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        byte[] value = new byte[length];
        for (int x = 0; x < length; x++)
            value[x] = readByte(content);
        return value;
    }
    protected byte[][] readByteArray2D(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        byte[][] value = new byte[length][];
        for (int x = 0; x < length; x++)
            value[x] = readByteArray(content);
        return value;
    }

    
    // byte primitive
    
    protected int readInt(byte[] content) throws IOException {
        byte[] bytes = inflateFrom(content, index);
        index+=1+bytes.length;
        bytes = ByteUtils.rightAlignBytes(bytes, 4);
        int value = ByteUtils.convertIntFromBytes(bytes);
        if (value == BinaryOutputCapsule.NULL_OBJECT || value == BinaryOutputCapsule.DEFAULT_OBJECT)
            index -= 4;
        return value;        
    }
    protected int[] readIntArray(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        int[] value = new int[length];
        for (int x = 0; x < length; x++)
            value[x] = readInt(content);
        return value;
    }
    protected int[][] readIntArray2D(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        int[][] value = new int[length][];
        for (int x = 0; x < length; x++)
            value[x] = readIntArray(content);
        return value;
    }

    
    // float primitive
    
    protected float readFloat(byte[] content) throws IOException {
        float value = ByteUtils.convertFloatFromBytes(content, index);
        index+=4;
        return value;      
    }
    protected float[] readFloatArray(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        float[] value = new float[length];
        for (int x = 0; x < length; x++)
            value[x] = readFloat(content);
        return value;
    }
    protected float[][] readFloatArray2D(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        float[][] value = new float[length][];
        for (int x = 0; x < length; x++)
            value[x] = readFloatArray(content);
        return value;
    }

    
    // double primitive
    
    protected double readDouble(byte[] content) throws IOException {
        double value = ByteUtils.convertDoubleFromBytes(content, index);
        index+=8;
        return value;      
    }
    protected double[] readDoubleArray(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        double[] value = new double[length];
        for (int x = 0; x < length; x++)
            value[x] = readDouble(content);
        return value;
    }
    protected double[][] readDoubleArray2D(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        double[][] value = new double[length][];
        for (int x = 0; x < length; x++)
            value[x] = readDoubleArray(content);
        return value;
    }

    
    // long primitive
    
    protected long readLong(byte[] content) throws IOException {
        byte[] bytes = inflateFrom(content, index);
        index+=1+bytes.length;
        bytes = ByteUtils.rightAlignBytes(bytes, 8);
        long value = ByteUtils.convertLongFromBytes(bytes);
        return value;        
    }
    protected long[] readLongArray(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        long[] value = new long[length];
        for (int x = 0; x < length; x++)
            value[x] = readLong(content);
        return value;
    }
    protected long[][] readLongArray2D(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        long[][] value = new long[length][];
        for (int x = 0; x < length; x++)
            value[x] = readLongArray(content);
        return value;
    }

    
    // short primitive
    
    protected short readShort(byte[] content) throws IOException {
        short value = ByteUtils.convertShortFromBytes(content, index);
        index+=2;
        return value;      
    }
    protected short[] readShortArray(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        short[] value = new short[length];
        for (int x = 0; x < length; x++)
            value[x] = readShort(content);
        return value;
    }
    protected short[][] readShortArray2D(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        short[][] value = new short[length][];
        for (int x = 0; x < length; x++)
            value[x] = readShortArray(content);
        return value;
    }

    
    // boolean primitive
    
    protected boolean readBoolean(byte[] content) throws IOException {
        boolean value = ByteUtils.convertBooleanFromBytes(content, index);
        index+=1;
        return value;        
    }
    protected boolean[] readBooleanArray(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        boolean[] value = new boolean[length];
        for (int x = 0; x < length; x++)
            value[x] = readBoolean(content);
        return value;
    }
    protected boolean[][] readBooleanArray2D(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        boolean[][] value = new boolean[length][];
        for (int x = 0; x < length; x++)
            value[x] = readBooleanArray(content);
        return value;
    }

    
    // String
    
    protected String readString(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        byte[] bytes = new byte[length];
        for (int x = 0; x < length; x++)
            bytes[x] = content[index++];
        return new String(bytes);
    }
    protected String[] readStringArray(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        String[] value = new String[length];
        for (int x = 0; x < length; x++)
            value[x] = readString(content);
        return value;
    }
    protected String[][] readStringArray2D(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        String[][] value = new String[length][];
        for (int x = 0; x < length; x++)
            value[x] = readStringArray(content);
        return value;
    }

    
    // BitSet
    
    protected BitSet readBitSet(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        BitSet value = new BitSet(length);
        for (int x = 0; x < length; x++)
            value.set(x, readBoolean(content));
        return value;
    }
    
    
    // INFLATOR for int and long

    protected static byte[] inflateFrom(byte[] contents, int index) {
        byte firstByte = contents[index];
        if (firstByte == BinaryOutputCapsule.NULL_OBJECT)
            return ByteUtils.convertToBytes(BinaryOutputCapsule.NULL_OBJECT);
        else if (firstByte == BinaryOutputCapsule.DEFAULT_OBJECT)
            return ByteUtils.convertToBytes(BinaryOutputCapsule.DEFAULT_OBJECT);
        else if (firstByte == 0)
            return new byte[0];
        else {
            byte[] rVal = new byte[firstByte];
            for (int x = 0; x < rVal.length; x++)
                rVal[x] = contents[x+1+index];
            return rVal;
        }
    }
    
    // BinarySavable
    
    protected Savable readSavable(byte[] content) throws IOException {
        int id = readInt(content);
        if(id == BinaryOutputCapsule.NULL_OBJECT) {
            return null;
        } else {
            return importer.readObject(id);
        }
    }
    
    
    // BinarySavable array
    
    protected Savable[] readSavableArray(byte[] content) throws IOException {
        int elements = readInt(content);
        if (elements == BinaryOutputCapsule.NULL_OBJECT) return null;
        Savable[] rVal = new Savable[elements];
        for (int x = 0; x < elements; x++) {
            rVal[x] = readSavable(content);
        }
        return rVal;
    }
    protected Savable[][] readSavableArray2D(byte[] content) throws IOException {
        int elements = readInt(content);
        if (elements == BinaryOutputCapsule.NULL_OBJECT) return null;
        Savable[][] rVal = new Savable[elements][];
        for (int x = 0; x < elements; x++) {
            rVal[x] = readSavableArray(content);
        }
        return rVal;
    }
    
    
    // ArrayList<BinarySavable>
    
    protected ArrayList readSavableArrayList(byte[] content) throws IOException {
        int length = readInt(content);
        if(length == BinaryOutputCapsule.NULL_OBJECT) {
            return null;
        }
        ArrayList<Savable> rVal = new ArrayList<Savable>(length);
        for (int x = 0; x < length; x++) {
            rVal.add(readSavable(content));
        }
        return rVal;
    }
    protected ArrayList[] readSavableArrayListArray(byte[] content) throws IOException {
        int length = readInt(content);
        if(length == BinaryOutputCapsule.NULL_OBJECT) {
            return null;
        }
        ArrayList[] rVal = new ArrayList[length];
        for (int x = 0; x < length; x++) {
            rVal[x] = readSavableArrayList(content);
        }
        return rVal;
    }
    protected ArrayList[][] readSavableArrayListArray2D(byte[] content) throws IOException {
        int length = readInt(content);
        if(length == BinaryOutputCapsule.NULL_OBJECT) {
            return null;
        }
        ArrayList[][] rVal = new ArrayList[length][];
        for (int x = 0; x < length; x++) {
            rVal[x] = readSavableArrayListArray(content);
        }
        return rVal;
    }

    
    // ArrayList<FloatBuffer>

    protected ArrayList readFloatBufferArrayList(byte[] content) throws IOException {
        int length = readInt(content);
        if(length == BinaryOutputCapsule.NULL_OBJECT) {
            return null;
        }
        ArrayList<FloatBuffer> rVal = new ArrayList<FloatBuffer>(length);
        for (int x = 0; x < length; x++) {
            rVal.add(readFloatBuffer(content));
        }
        return rVal;
    }

    // NIO BUFFERS
    // float buffer
    
    protected FloatBuffer readFloatBuffer(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        FloatBuffer value = BufferUtils.createFloatBuffer(length);
        for (int x = 0; x < length; x++) {
            value.put(readFloat(content));
        }
        value.rewind();
        return value;      
    }

    
    // int buffer
    
    protected IntBuffer readIntBuffer(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        IntBuffer value = BufferUtils.createIntBuffer(length);
        for (int x = 0; x < length; x++) {
            value.put(readInt(content));
        }
        value.rewind();
        return value;      
    }


    // byte buffer
    
    protected ByteBuffer readByteBuffer(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        ByteBuffer value = BufferUtils.createByteBuffer(length);
        for (int x = 0; x < length; x++) {
            value.put(readByte(content));
        }
        value.rewind();
        return value;      
    }


    // short buffer
    
    protected ShortBuffer readShortBuffer(byte[] content) throws IOException {
        int length = readInt(content);
        if (length == BinaryOutputCapsule.NULL_OBJECT) return null;
        ShortBuffer value = BufferUtils.createShortBuffer(length);
        for (int x = 0; x < length; x++) {
            value.put(readShort(content));
        }
        value.rewind();
        return value;      
    }
}
