package jmetest.util;

import java.io.IOException;
import java.util.logging.Logger;

import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;

public class Snafu extends Bar implements Savable {
    private static final Logger logger = Logger
            .getLogger(Snafu.class.getName());
    
    int test = 0;
    
    public Snafu() {}
    
    public void write(JMEExporter e) {
        super.write(e);
        try {
            e.getCapsule(this).write(test, "test", 0);
        } catch (IOException e1) {
            logger.throwing(this.getClass().toString(), "write(JMEExporter e)", e1);
        }
        
    }
    
    public void read(JMEImporter e) {
        super.read(e);
        try {
            test = e.getCapsule(this).readInt("test", 0);
        } catch (IOException e1) {
            logger.throwing(this.getClass().toString(), "read(JMEImporter e)", e1);
        }
    }
}
