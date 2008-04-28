/**
 * allType.java
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
import com.jmex.xml.types.SchemaIDRef;

public class allType extends com.jmex.xml.xml.Node {

	/**
     * 
     */
    private static final long serialVersionUID = -1844591078350011832L;

    public allType(allType node) {
		super(node);
	}

	public allType(org.w3c.dom.Node node) {
		super(node);
	}

	public allType(org.w3c.dom.Document doc) {
		super(doc);
	}

	public allType(com.jmex.xml.xml.Document doc, String namespaceURI, String prefix, String name) {
		super(doc, namespaceURI, prefix, name);
	}
	
	public void adjustPrefix() {
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Attribute, null, "ref" );
				tmpNode != null;
				tmpNode = getDomNextChild( Attribute, null, "ref", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, false);
		}
	}
	public void setXsiType() {
 		org.w3c.dom.Element el = (org.w3c.dom.Element) domNode;
		el.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "all");
	}

	public static int getrefMinCount() {
		return 1;
	}

	public static int getrefMaxCount() {
		return 1;
	}

	public int getrefCount() {
		return getDomChildCount(Attribute, null, "ref");
	}

	public boolean hasref() {
		return hasDomChild(Attribute, null, "ref");
	}

	public SchemaIDRef newref() {
		return new SchemaIDRef();
	}

	public SchemaIDRef getrefAt(int index) throws Exception {
		return new SchemaIDRef(getDomNodeValue(getDomChildAt(Attribute, null, "ref", index)));
	}

	public org.w3c.dom.Node getStartingrefCursor() throws Exception {
		return getDomFirstChild(Attribute, null, "ref" );
	}

	public org.w3c.dom.Node getAdvancedrefCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Attribute, null, "ref", curNode );
	}

	public SchemaIDRef getrefValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new SchemaIDRef(getDomNodeValue(curNode));
	}

	public SchemaIDRef getref() throws Exception 
 {
		return getrefAt(0);
	}

	public void removerefAt(int index) {
		removeDomChildAt(Attribute, null, "ref", index);
	}

	public void removeref() {
		removerefAt(0);
	}

	public org.w3c.dom.Node addref(SchemaIDRef value) {
		if( value.isNull() )
			return null;

		return  appendDomChild(Attribute, null, "ref", value.toString());
	}

	public org.w3c.dom.Node addref(String value) throws Exception {
		return addref(new SchemaIDRef(value));
	}

	public void insertrefAt(SchemaIDRef value, int index) {
		insertDomChildAt(Attribute, null, "ref", index, value.toString());
	}

	public void insertrefAt(String value, int index) throws Exception {
		insertrefAt(new SchemaIDRef(value), index);
	}

	public void replacerefAt(SchemaIDRef value, int index) {
		replaceDomChildAt(Attribute, null, "ref", index, value.toString());
	}

	public void replacerefAt(String value, int index) throws Exception {
		replacerefAt(new SchemaIDRef(value), index);
	}

}
