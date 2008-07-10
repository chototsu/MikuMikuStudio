package com.jme.util.export.xml;

import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class DOM_PrettyPrint {
    public static void serialize(Document doc, OutputStream out) throws Exception {
    	DOMSerializer serializer = new DOMSerializer();
        serializer.setIndent(2);
        serializer.serialize(doc, out);
    }
}

