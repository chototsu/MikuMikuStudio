package com.jme.scene.model.XMLparser;

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
public class SAXReader{
    private SAXStackProcessor computer;
    private JMESAXHandler myHandler;
    long time;


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
        time=System.currentTimeMillis();
        computer.reInitialize();
        SAXParserFactory factory=SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);

        try {
            SAXParser parser=factory.newSAXParser();
            //TODO: Use .xsd validating parser?

            parser.parse(SAXFile,myHandler);

        } catch (Throwable t) {
            throw new JmeException("Parser exception caught:" + t.getClass() + " * " + t.getMessage());
        }
        System.out.println("Total load time: " + (System.currentTimeMillis()-time));
        return computer.fetchOriginal();
    }

    /**
     * Returns a copy of the node last loaded
     * @return
     */
    public Node fetchCopy(){
        return computer.fetchCopy();
    }
}