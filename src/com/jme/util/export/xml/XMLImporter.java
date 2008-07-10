package com.jme.util.export.xml;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class XMLImporter implements JMEImporter{
    private DOMInputCapsule domIn;
    
    public XMLImporter(){
    }
    
    public Savable load(InputStream f) throws IOException {
        try {
            domIn = new DOMInputCapsule(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f), this);
            return domIn.readSavable(null, null);
        } catch (IOException ex) {
            throw new IOException(ex.getMessage());
        } catch (SAXException e) {
        	throw new IOException(e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new IOException(e.getMessage());
		}
    }

    public Savable load(URL f) throws IOException {
        return load(f.openStream());
    }

    public Savable load(File f) throws IOException {
        return load(new FileInputStream(f));
    }

    public InputCapsule getCapsule(Savable id) {
        return domIn;
    }

	public static XMLImporter getInstance() {
		return new XMLImporter();
	}


}
