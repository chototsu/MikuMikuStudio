package com.jme.scene.model.XMLparser;

import com.jme.scene.Node;

import java.net.URL;
import java.util.HashMap;
import java.io.*;

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