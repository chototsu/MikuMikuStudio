package com.jme.scene.model.XMLparser;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;

import com.jme.system.JmeException;
import com.jme.scene.Node;

import java.io.InputStream;
import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XML file format parser for jME
 *
 * @author Jack Lindamood
 */
public class SAXReader{
    private SAXStackProcessor computer;
    private JMESAXHandler myHandler;
    long time;
    private static boolean DEBUG=true;


    /**
     * Constructs a new SAXReader. <code>loadXML</code> should be called afterwards
     * to load an XML InputStream and return a Node
     */
    public SAXReader(){
        super();
        try{
            computer=new SAXStackProcessor();
            myHandler=new JMESAXHandler(computer);
        } catch (NullPointerException np){
            throw new JmeException("Try setting the display system first");
        }
    }

    /**
     * Loads a Node from an InputStream
     * @param SAXFile The InputStream containing the XML
     * @return A Node that represents the XML file
     */
    public Node loadXML(InputStream SAXFile){
        if (DEBUG) time=System.currentTimeMillis();
        computer.reInitialize();
        SAXParserFactory factory=SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);

        try {
            SAXParser parser = factory.newSAXParser();
            //TODO: Use .xsd validating parser?

            parser.parse(SAXFile, myHandler);

        } catch (IOException e) {
            throw new JmeException("Unable to do IO correctly:" + e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new JmeException("Serious parser configuration error:" + e.getMessage());
        } catch (SAXParseException e) {
            throw new JmeException(e.toString() +'\n' + "Line: " +e.getLineNumber() + '\n' + "Column: " + e.getColumnNumber());
        }catch (SAXException e) {
            throw new JmeException("Unknown sax error: " + e.getMessage());
        }
        if (DEBUG)System.out.println("Total load time: " + (System.currentTimeMillis()-time));
        return computer.fetchOriginal();
    }

    /**
     * Returns a copy of the node last loaded
     * @return
     */
    public Node fetchCopy(){
        return computer.fetchCopy();
    }

    /**
     * Adds a property to this SAXReader.  Properties can tell the SAXReader how to process the XML file
     * @param key Key to add (For example "texdir")
     * @param property Property for that key to have (For example "c:\\blarg\\")
     */
    public void setProperty(String key, Object property) {
        computer.properties.put(key,property);
    }

    /**
     * Removes a property from the SAXReader's properties HashMap
     * @param key
     */
    public void clearProperty(String key){
        computer.properties.remove(key);
    }

}