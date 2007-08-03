package jmetest.util;

import java.io.IOException;
import java.util.logging.Logger;

import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;

public class Bar implements Savable {
    private static final Logger logger = Logger.getLogger(Bar.class.getName());

    float f = 0;
    float g = 0;
    
    public Bar() {
        
    }
    
    public void write(JMEExporter e) {
        try {
            e.getCapsule(this).write(f, "f", 0);
            e.getCapsule(this).write(g, "g", 0);
        } catch (IOException e1) {
            logger.throwing(this.getClass().toString(), "write(JMEExporter e)", e1);
        }
    }

    public void read(JMEImporter e) {
        try {
            f = e.getCapsule(this).readFloat("f", 0);
            g = e.getCapsule(this).readFloat("g", 0);
        } catch (IOException e1) {
            logger.throwing(this.getClass().toString(), "read(JMEImporter e)", e1);
        }
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}
