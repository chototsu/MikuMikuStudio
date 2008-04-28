/**
 * textureType.java
 *
 * This file was generated by XMLSpy 2007sp2 Enterprise Edition.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the XMLSpy Documentation for further details.
 * http://www.altova.com/xmlspy
 */


package com.jmex.model.collada.schema;

import com.jmex.xml.types.SchemaNCName;

public class textureType extends com.jmex.xml.xml.Node {

	public textureType(textureType node) {
		super(node);
	}

	public textureType(org.w3c.dom.Node node) {
		super(node);
	}

	public textureType(org.w3c.dom.Document doc) {
		super(doc);
	}

	public textureType(com.jmex.xml.xml.Document doc, String namespaceURI, String prefix, String name) {
		super(doc, namespaceURI, prefix, name);
	}
	
	public void adjustPrefix() {
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Attribute, null, "texture" );
				tmpNode != null;
				tmpNode = getDomNextChild( Attribute, null, "texture", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, false);
		}
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Attribute, null, "texcoord" );
				tmpNode != null;
				tmpNode = getDomNextChild( Attribute, null, "texcoord", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, false);
		}
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "extra" );
				tmpNode != null;
				tmpNode = getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "extra", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, true);
			new extraType(tmpNode).adjustPrefix();
		}
	}
	public void setXsiType() {
 		org.w3c.dom.Element el = (org.w3c.dom.Element) domNode;
		el.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "texture");
	}

	public static int gettextureMinCount() {
		return 1;
	}

	public static int gettextureMaxCount() {
		return 1;
	}

	public int gettextureCount() {
		return getDomChildCount(Attribute, null, "texture");
	}

	public boolean hastexture() {
		return hasDomChild(Attribute, null, "texture");
	}

	public SchemaNCName newtexture() {
		return new SchemaNCName();
	}

	public SchemaNCName gettextureAt(int index) throws Exception {
		return new SchemaNCName(getDomNodeValue(getDomChildAt(Attribute, null, "texture", index)));
	}

	public org.w3c.dom.Node getStartingtextureCursor() throws Exception {
		return getDomFirstChild(Attribute, null, "texture" );
	}

	public org.w3c.dom.Node getAdvancedtextureCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Attribute, null, "texture", curNode );
	}

	public SchemaNCName gettextureValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new SchemaNCName(getDomNodeValue(curNode));
	}

	public SchemaNCName gettexture() throws Exception 
 {
		return gettextureAt(0);
	}

	public void removetextureAt(int index) {
		removeDomChildAt(Attribute, null, "texture", index);
	}

	public void removetexture() {
		removetextureAt(0);
	}

	public org.w3c.dom.Node addtexture(SchemaNCName value) {
		if( value.isNull() )
			return null;

		return  appendDomChild(Attribute, null, "texture", value.toString());
	}

	public org.w3c.dom.Node addtexture(String value) throws Exception {
		return addtexture(new SchemaNCName(value));
	}

	public void inserttextureAt(SchemaNCName value, int index) {
		insertDomChildAt(Attribute, null, "texture", index, value.toString());
	}

	public void inserttextureAt(String value, int index) throws Exception {
		inserttextureAt(new SchemaNCName(value), index);
	}

	public void replacetextureAt(SchemaNCName value, int index) {
		replaceDomChildAt(Attribute, null, "texture", index, value.toString());
	}

	public void replacetextureAt(String value, int index) throws Exception {
		replacetextureAt(new SchemaNCName(value), index);
	}

	public static int gettexcoordMinCount() {
		return 1;
	}

	public static int gettexcoordMaxCount() {
		return 1;
	}

	public int gettexcoordCount() {
		return getDomChildCount(Attribute, null, "texcoord");
	}

	public boolean hastexcoord() {
		return hasDomChild(Attribute, null, "texcoord");
	}

	public SchemaNCName newtexcoord() {
		return new SchemaNCName();
	}

	public SchemaNCName gettexcoordAt(int index) throws Exception {
		return new SchemaNCName(getDomNodeValue(getDomChildAt(Attribute, null, "texcoord", index)));
	}

	public org.w3c.dom.Node getStartingtexcoordCursor() throws Exception {
		return getDomFirstChild(Attribute, null, "texcoord" );
	}

	public org.w3c.dom.Node getAdvancedtexcoordCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Attribute, null, "texcoord", curNode );
	}

	public SchemaNCName gettexcoordValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new SchemaNCName(getDomNodeValue(curNode));
	}

	public SchemaNCName gettexcoord() throws Exception 
 {
		return gettexcoordAt(0);
	}

	public void removetexcoordAt(int index) {
		removeDomChildAt(Attribute, null, "texcoord", index);
	}

	public void removetexcoord() {
		removetexcoordAt(0);
	}

	public org.w3c.dom.Node addtexcoord(SchemaNCName value) {
		if( value.isNull() )
			return null;

		return  appendDomChild(Attribute, null, "texcoord", value.toString());
	}

	public org.w3c.dom.Node addtexcoord(String value) throws Exception {
		return addtexcoord(new SchemaNCName(value));
	}

	public void inserttexcoordAt(SchemaNCName value, int index) {
		insertDomChildAt(Attribute, null, "texcoord", index, value.toString());
	}

	public void inserttexcoordAt(String value, int index) throws Exception {
		inserttexcoordAt(new SchemaNCName(value), index);
	}

	public void replacetexcoordAt(SchemaNCName value, int index) {
		replaceDomChildAt(Attribute, null, "texcoord", index, value.toString());
	}

	public void replacetexcoordAt(String value, int index) throws Exception {
		replacetexcoordAt(new SchemaNCName(value), index);
	}

	public static int getextraMinCount() {
		return 0;
	}

	public static int getextraMaxCount() {
		return 1;
	}

	public int getextraCount() {
		return getDomChildCount(Element, "http://www.collada.org/2005/11/COLLADASchema", "extra");
	}

	public boolean hasextra() {
		return hasDomChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "extra");
	}

	public extraType newextra() {
		return new extraType(domNode.getOwnerDocument().createElementNS("http://www.collada.org/2005/11/COLLADASchema", "extra"));
	}

	public extraType getextraAt(int index) throws Exception {
		return new extraType(getDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "extra", index));
	}

	public org.w3c.dom.Node getStartingextraCursor() throws Exception {
		return getDomFirstChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "extra" );
	}

	public org.w3c.dom.Node getAdvancedextraCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "extra", curNode );
	}

	public extraType getextraValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new extraType(curNode);
	}

	public extraType getextra() throws Exception 
 {
		return getextraAt(0);
	}

	public void removeextraAt(int index) {
		removeDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "extra", index);
	}

	public void removeextra() {
		removeextraAt(0);
	}

	public org.w3c.dom.Node addextra(extraType value) {
		return appendDomElement("http://www.collada.org/2005/11/COLLADASchema", "extra", value);
	}

	public void insertextraAt(extraType value, int index) {
		insertDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "extra", index, value);
	}

	public void replaceextraAt(extraType value, int index) {
		replaceDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "extra", index, value);
	}

}
