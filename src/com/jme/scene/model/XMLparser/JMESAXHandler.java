package com.jme.scene.model.XMLparser;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import com.jme.util.LoggingSystem;

import java.util.logging.Level;

/**
 * Started Date: Jun 6, 2004
 *
 * This class does the actual SAX Processing for SAXReader
 * 
 * @author Jack Lindamood
 */
class JMESAXHandler extends DefaultHandler{
    private StringBuffer currentData;
    SAXStackProcessor myComputer;

    public JMESAXHandler(SAXStackProcessor s){
        super();
        currentData=new StringBuffer();
        myComputer=s;
    }

    public void startDocument() throws SAXException{
        currentData.setLength(0);
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
        currentData.setLength(16);
        System.out.print("Start: " + qName);
        myComputer.increaseStack(qName,atts);
        System.out.println("*done-increaseStack");
    }

    public void endElement(String uri,String localName, String qName) throws SAXException{
        System.out.print("End: " + qName);
        myComputer.decreaseStack(qName,currentData);
        System.out.println("*done-decreaseStack*");
    }

    public void characters(char[] ch, int start,int length) throws SAXException{
        currentData.append(ch,start,length);
    }

    public InputSource resolveEntity(String publicID,String systemID) throws SAXException{
        return null;
    }
}