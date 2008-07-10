package com.jme.util.export.xml;

import com.jme.util.export.JMEExporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLExporter implements JMEExporter{
	public static final String ELEMENT_MAPENTRY = "MapEntry";	
	public static final String ELEMENT_KEY = "Key";	
	public static final String ELEMENT_VALUE = "Value";
	public static final String ELEMENT_FLOATBUFFER = "FloatBuffer";
	public static final String ATTRIBUTE_SIZE = "size";		
	
	private DOMOutputCapsule domOut;
    
    
    public XMLExporter() {
       
    }

    public boolean save(Savable object, OutputStream f) throws IOException {
        try {
        	//Initialize Document when saving so we don't retain state of previous exports
        	this.domOut = new DOMOutputCapsule(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument(), this);
            domOut.write(object, object.getClass().getName(), null);
            DOM_PrettyPrint.serialize(domOut.getDoc(), f);
            f.flush();
            return true;
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public boolean save(Savable object, File f) throws IOException {
        return save(object, new FileOutputStream(f));
    }

    public OutputCapsule getCapsule(Savable object) {
        return domOut;
    }

	public static XMLExporter getInstance() {
		return new XMLExporter();
	}
    
}
