/*
 * Copyright (c) 2003-2009 jMonkeyEngine
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
 *
 */
  

package com.jme.util.export.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

// DOM imports
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The DOMSerializer was based primarily off the DOMSerializer.java class from the 
 * "Java and XML" 3rd Edition book by Brett McLaughlin, and Justin Edelson. Some 
 * modifications were made to support formatting of elements and attributes.
 * 
 * @author Brett McLaughlin, Justin Edelson - Original creation for "Java and XML" book.
 * @author Doug Daniels (dougnukem) - adjustments for XML formatting
 *
 */
public class DOMSerializer {

    /** Indentation to use (default is no indentation) */
    private String indent = "";

    /** Line separator to use (default is for Windows) */
    private String lineSeparator = "\n";

    /** Encoding for output (default is UTF-8) */
    private String encoding = "UTF8";

    /** Attributes will be displayed on seperate lines   */
	private boolean displayAttributesOnSeperateLine = true;

    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setIndent(int numSpaces) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < numSpaces; i++)
            buffer.append("\t");
        this.indent = buffer.toString();
    }

    public void serialize(Document doc, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, encoding);
        serialize(doc, writer);
    }

    public void serialize(Document doc, File file) throws IOException {
        Writer writer = new FileWriter(file);
        serialize(doc, writer);
    }

    public void serialize(Document doc, Writer writer) throws IOException {
        // Start serialization recursion with no indenting
        serializeNode(doc, writer, "");
        writer.flush();
    }

    private void serializeNode(Node node, Writer writer, String indentLevel)
            throws IOException {
        // Determine action based on node type
        switch (node.getNodeType()) {
        case Node.DOCUMENT_NODE:
            Document doc = (Document) node;
            /**
             * DOM Level 2 code writer.write("<?xml version=\"1.0\"
             * encoding=\"UTF-8\"?>");
             */
            writer.write("<?xml version=\"");
            writer.write(doc.getXmlVersion());
            writer.write("\" encoding=\"UTF-8\" standalone=\"");
            if (doc.getXmlStandalone())
                writer.write("yes");
            else
                writer.write("no");
            writer.write("\"");
            writer.write("?>");
            writer.write(lineSeparator);

            // recurse on each top-level node
            NodeList nodes = node.getChildNodes();
            if (nodes != null)
                for (int i = 0; i < nodes.getLength(); i++)
                    serializeNode(nodes.item(i), writer, "");
            break;
        case Node.ELEMENT_NODE:
            String name = node.getNodeName();
            //writer.write(indentLevel + "<" + name);
            writer.write("<" + name);
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node current = attributes.item(i);
                String attributeSeperator = " ";
                if(displayAttributesOnSeperateLine && i!=0) {
                	attributeSeperator = lineSeparator + indentLevel + indent; 
                }
                //Double indentLevel to match parent element and then one indention to format below parent
                String attributeStr = attributeSeperator + current.getNodeName() + "=\"";
                writer.write(attributeStr);
                print(writer, current.getNodeValue());
                writer.write("\"");
            }
            writer.write(">");

            // recurse on each child
            NodeList children = node.getChildNodes();
            if (children != null) {
                if ((children.item(0) != null) && (children.item(0).getNodeType() == Node.ELEMENT_NODE)) {
                    //writer.write(lineSeparator);
                }

                for (int i = 0; i < children.getLength(); i++)
                    serializeNode(children.item(i), writer, indentLevel + indent);

                if ((children.item(0) != null) && (children.item(children.getLength() - 1).getNodeType() == Node.ELEMENT_NODE))
                    ;//writer.write(indentLevel);
            }

            writer.write("</" + name + ">");
            //writer.write(lineSeparator);
            break;
        case Node.TEXT_NODE:
            print(writer, node.getNodeValue());
            break;
        case Node.CDATA_SECTION_NODE:
            writer.write("<![CDATA[");
            print(writer, node.getNodeValue());
            writer.write("]]>");
            break;
        case Node.COMMENT_NODE:
            writer.write(indentLevel + "<!-- " + node.getNodeValue() + " -->");
            writer.write(lineSeparator);
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            writer.write("<?" + node.getNodeName() + " " + node.getNodeValue()
                    + "?>");
            writer.write(lineSeparator);
            break;
        case Node.ENTITY_REFERENCE_NODE:
            writer.write("&" + node.getNodeName( ) + ";");
            break;
        case Node.DOCUMENT_TYPE_NODE:
            DocumentType docType = (DocumentType) node;
            String publicId = docType.getPublicId();
            String systemId = docType.getSystemId();
            String internalSubset = docType.getInternalSubset();
            writer.write("<!DOCTYPE " + docType.getName());
            if (publicId != null)
                writer.write(" PUBLIC \"" + publicId + "\" ");
            else
                writer.write(" SYSTEM ");
            writer.write("\"" + systemId + "\"");
            if (internalSubset != null)
                writer.write(" [" + internalSubset + "]");
            writer.write(">");
            writer.write(lineSeparator);
            break;
        }
    }

    private void print(Writer writer, String s) throws IOException {

        if (s == null)
            return;
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            switch (c) {
            case '<':
                writer.write("&lt;");
                break;
            case '>':
                writer.write("&gt;");
                break;
            case '&':
                writer.write("&amp;");
                break;
            case '\r':
                writer.write("&#xD;");
                break;
            default:
                writer.write(c);
            }
        }
    }

}
