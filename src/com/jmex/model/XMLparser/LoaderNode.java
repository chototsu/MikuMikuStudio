/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jmex.model.XMLparser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import com.jme.scene.Node;

/**
 * Started Date: Jul 3, 2004<br><br>
 *
 * This node is created to store xml or jme files signaled inside a file.  It stores
 * how the file is loaded/created so that it can be easily saved and recreated the same way.
 *
 * @author Jack Lindamood
 */
public class LoaderNode extends Node{
    private static final long serialVersionUID = 1L;
	String filePath;
    String classLoaderPath;
    URL urlPath;
    String type;

    public LoaderNode(String name) {
        super(name);
    }

    public void loadFromFilePath(String type,String filePath,HashMap parentAttributes) throws IOException {
        this.type=type;
        this.filePath=filePath;
        JmeBinaryReader jbr=new JmeBinaryReader();
        setProperties(jbr,parentAttributes);
        InputStream loaderInput=new File(filePath).toURI().toURL().openStream();
        if (type.equals("xml")){
            XMLtoBinary xtb=new XMLtoBinary();
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            xtb.sendXMLtoBinary(loaderInput,BO);
            loaderInput=new ByteArrayInputStream(BO.toByteArray());
        } else if (!type.equals("binary"))
            throw new IOException("Unknown LoaderNode flag: " + type);
        jbr.loadBinaryFormat(this,loaderInput);
    }
    public void loadFromURLPath(String type,URL urlPath,HashMap parentAttributes) throws IOException {
        this.urlPath=urlPath;
        this.type=type;
        JmeBinaryReader jbr=new JmeBinaryReader();
        setProperties(jbr,parentAttributes);
        InputStream loaderInput=urlPath.openStream();
        if (type.equals("xml")){
            XMLtoBinary xtb=new XMLtoBinary();
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            xtb.sendXMLtoBinary(loaderInput,BO);
            loaderInput=new ByteArrayInputStream(BO.toByteArray());
        } else if (!type.equals("binary"))
            throw new IOException("Unknown LoaderNode flag: " + type);
        jbr.loadBinaryFormat(this,loaderInput);
    }
    public void loadFromClassLoader(String type,String classLoaderPath,HashMap parentAttributes) throws IOException {
        this.classLoaderPath=classLoaderPath;
        this.type=type;
        JmeBinaryReader jbr=new JmeBinaryReader();
        setProperties(jbr,parentAttributes);
        URL location=LoaderNode.class.getClassLoader().getResource(classLoaderPath);
        InputStream loaderInput=location.openStream();
        if (type.equals("xml")){
            XMLtoBinary xtb=new XMLtoBinary();
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            xtb.sendXMLtoBinary(loaderInput,BO);
            loaderInput=new ByteArrayInputStream(BO.toByteArray());
        } else if (!type.equals("binary"))
            throw new IOException("Unknown LoaderNode flag: " + type);
        jbr.loadBinaryFormat(this,loaderInput);
    }
    public void setProperties(JmeBinaryReader jbr, HashMap parentAttributes) {
        final Object[] keys=parentAttributes.keySet().toArray();
        for (int i=0;i<keys.length;i++)
            jbr.setProperty((String) keys[i],parentAttributes.get(keys[i]));
    }
}