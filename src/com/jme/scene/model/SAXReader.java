package com.jme.scene.model;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.scene.Node;

import java.util.logging.Level;
import java.util.Stack;
import java.io.File;
import java.io.InputStream;

/**
 * XML file format parser for jME
 *
 * @author Jack Lindamood
 */
public class SAXReader extends DefaultHandler{
    public final static File XSD=new File("data/XML docs/LoaderFormat.xsd");
    private StringBuffer currentData=new StringBuffer();
    private Stack currentlyProcessing=new Stack();
    private Node lastScene;
    long time;
    public SAXReader(){
        super();
    }

    public void loadXML(InputStream SAXFile){
        lastScene=new Node("File loaded Scene");
        time=System.currentTimeMillis();
        SAXParserFactory factory=SAXParserFactory.newInstance();
        factory.setValidating(true);
        try {
            factory.setFeature("http://xml.org/sax/features/validation",true);
            SAXParser parser=factory.newSAXParser();
            parser.parse(SAXFile,this);
        } catch (Throwable t) {
            throw new JmeException("Parser exception caught:" + t.getClass() + " * " + t.getCause() + "*" + t.getMessage());
        }
        System.out.println("Total load time: " + (System.currentTimeMillis()-time));
    }

    public Node fetchCopy(){
        return lastScene;
    }

    public void setDocumentLocator(Locator l){

    }
    public void startDocument() throws SAXException{
        LoggingSystem.getLogger().log(
            Level.INFO,
            "XML document processing begun");
    }

    public void endDocument() throws SAXException{
        LoggingSystem.getLogger().log(
            Level.INFO,
            "XML document processing finished");
    }

    public void startElement(String uri,String localName,String qName, Attributes atts) throws SAXException{
        currentData.setLength(0);
        SAXStackProcessor.increaseStack(qName,atts,currentlyProcessing);
    }

    public void endElement(String uri,String localName, String qName) throws SAXException{
        Node r=SAXStackProcessor.processStack(qName,currentData,currentlyProcessing);
        if (r!=null) lastScene.attachChild(r);
    }

    public void characters(char[] ch, int start,int length) throws SAXException{
        currentData.append(ch,start,length);
    }
    public InputSource resolveEntity(String publicID,String systemID) throws SAXException{
        return null;
    }
}