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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.zip.GZIPInputStream;

import com.jme.math.FastMath;
import com.jme.util.export.ByteUtils;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;

public class BinaryImporter implements JMEImporter {

    //TODO: Provide better cleanup and reuse of this class -- Good for now.
    
    //Key - alias, object - bco
    protected HashMap<String, BinaryClassObject> classes;
    //Key - id, object - the savable
    protected HashMap<Integer, Savable> contentTable;
    //Key - savable, object - capsule
    protected IdentityHashMap<Savable, BinaryInputCapsule> capsuleTable;
    //Key - id, opject - location in the file
    protected HashMap<Integer, Integer> locationTable;
    
    protected static BinaryImporter instance = null;
    protected BinaryImporter importer;
    public static boolean debug = false;

    protected byte[] dataArray;
    protected int aliasWidth;
    
    protected BinaryImporter() {
        importer = this;
    }
    
    public static BinaryImporter getInstance() {
        if (instance == null) instance = new BinaryImporter();
        return instance;
    }

    public void cleanup() {
        instance = null;
    }

    public Savable load(InputStream is) throws IOException {
        contentTable = new HashMap<Integer, Savable>();
        GZIPInputStream zis = new GZIPInputStream(is);
        BufferedInputStream bis = new BufferedInputStream(zis);
        int numClasses = ByteUtils.readInt(bis);
        aliasWidth = ((int)FastMath.log(numClasses, 256) + 1);
        classes = new HashMap<String, BinaryClassObject>(numClasses);
        for(int i = 0; i < numClasses; i++) {
            String alias = readString(bis, aliasWidth);
            
            int classLength = ByteUtils.readInt(bis);
            String className = readString(bis, classLength);
            BinaryClassObject bco = new BinaryClassObject();
            bco.alias = alias.getBytes();
            bco.className = className;
            
            int fields = ByteUtils.readInt(bis);
            bco.nameFields = new HashMap<String, BinaryClassField>(fields);
            bco.aliasFields = new HashMap<Byte, BinaryClassField>(fields);
            for (int x = 0; x < fields; x++) {
                byte fieldAlias = (byte)bis.read();
                byte fieldType = (byte)bis.read();
                
                int fieldNameLength = ByteUtils.readInt(bis);
                String fieldName = readString(bis, fieldNameLength);
                BinaryClassField bcf = new BinaryClassField(fieldName, fieldAlias, fieldType);
                bco.nameFields.put(fieldName, bcf);
                bco.aliasFields.put(fieldAlias, bcf);
            }
            classes.put(alias, bco);
        }
        
        int numLocs = ByteUtils.readInt(bis);
        capsuleTable = new IdentityHashMap<Savable, BinaryInputCapsule>(numLocs);
        locationTable = new HashMap<Integer, Integer>(numLocs);
        for(int i = 0; i < numLocs; i++) {
            int id = ByteUtils.readInt(bis);
            int loc = ByteUtils.readInt(bis);
            locationTable.put(id, loc);
        }

        @SuppressWarnings("unused")
        int numbIDs = ByteUtils.readInt(bis); // XXX: NOT CURRENTLY USED
        
        int id = ByteUtils.readInt(bis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int size = -1;
        byte[] cache = new byte[5120];
        while((size = bis.read(cache)) != -1) {
            baos.write(cache, 0, size);
        }
        bis = null;
                
        dataArray = baos.toByteArray();
        baos = null;
        Savable rVal = readObject(id);
        if (debug) {
            System.err.println("Importer Stats: ");
            System.err.println("Tags: "+numClasses);
            System.err.println("Objects: "+numLocs);
            System.err.println("Data Size: "+dataArray.length);
        }
        dataArray = null;
        cleanup();
        return rVal;
    }
    
    public Savable load(URL f) throws IOException {
        InputStream is = f.openStream();
        Savable rVal = load(is);
        is.close();
        return rVal;
    }
    
    public Savable load(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        Savable rVal = load(fis);
        fis.close();
        return rVal;
    }
    
    public BinaryInputCapsule getCapsule(Savable id) {
        return capsuleTable.get(id);
    }

    protected String readString(InputStream f, int length) throws IOException {
        byte[] data = new byte[length];
        for(int j = 0; j < length; j++) {
            data[j] = (byte)f.read();
        }
        
        return new String(data);
    }
    
    protected String readString(int length, int offset) throws IOException {
        byte[] data = new byte[length];
        for(int j = 0; j < length; j++) {
            data[j] = dataArray[j+offset];
        }
        
        return new String(data);
    }
    
    public Savable readObject(int id) {
        
        if(contentTable.get(id) != null) {
            return contentTable.get(id);
        }
        
        try {
            int loc = locationTable.get(id);
            
            String alias = readString(aliasWidth, loc);
            loc+=aliasWidth;

            BinaryClassObject bco = classes.get(alias);

            
            
            Savable out = BinaryClassLoader.fromName(bco.className);
            
            if(out == null) {
                System.err.println("NULL " + alias);
            }
            
            int dataLength = ByteUtils.convertIntFromBytes(dataArray, loc);
            loc+=4;
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(dataArray, loc, dataLength);
            BinaryInputCapsule cap = new BinaryInputCapsule(importer, bco);
            capsuleTable.put(out, cap);
            contentTable.put(id, out);
            cap.setContent(baos.toByteArray());

            out.read(this);
                        
            return out;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
