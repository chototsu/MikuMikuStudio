package com.jme.scene.model;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.scene.Node;

import java.util.logging.Level;
import java.io.File;
import java.io.InputStream;

/**
 * XML file format parser for jME
 *
 * @author Jack Lindamood
 */
public class SAXReader extends DefaultHandler{
    public final static File XSD=new File("data/XML docs/LoaderFormat.xsd");
    static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";
    static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";


    private StringBuffer currentData=new StringBuffer();
    long time;
    private SAXStackProcessor computer;

    public SAXReader(){
        super();
        try{
            computer=new SAXStackProcessor();
        } catch (NullPointerException np){
            throw new JmeException("Try setting the display system first");
        }
    }

    public Node loadXML(InputStream SAXFile){
        time=System.currentTimeMillis();
        SAXParserFactory factory=SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);

        try {
            SAXParser parser=factory.newSAXParser();    // Use .xsd validating parser?
//            parser.setProperty(JAXP_SCHEMA_LANGUAGE,W3C_XML_SCHEMA);
//            parser.setProperty(JAXP_SCHEMA_SOURCE,XSD);

            parser.parse(SAXFile,this);

        } catch (Throwable t) {
            throw new JmeException("Parser exception caught:" + t.getClass() + " * " + t.getCause() + "*" + t.getMessage());
        }
        System.out.println("Total load time: " + (System.currentTimeMillis()-time));
        return computer.fetchOriginal();
    }

    public Node fetchCopy(){
        return computer.fetchCopy();
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
        System.out.print("qName: " + qName);
        computer.increaseStack(qName,atts);
        System.out.println("*");
    }

    public void endElement(String uri,String localName, String qName) throws SAXException{
        System.out.print("End: " + qName);
        computer.decreaseStack(qName,currentData);
        System.out.println("*");
    }

    public void characters(char[] ch, int start,int length) throws SAXException{
        currentData.append(ch,start,length);
    }
    public InputSource resolveEntity(String publicID,String systemID) throws SAXException{
        return null;
    }
}