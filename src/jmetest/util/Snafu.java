package jmetest.util;

import java.io.IOException;

import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;

public class Snafu extends Bar implements Savable {
    int test = 0;
    
    public Snafu() {}
    
    public void write(JMEExporter e) {
        super.write(e);
        try {
            e.getCapsule(this).write(test, "test", 0);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
    }
    
    public void read(JMEImporter e) {
        super.read(e);
        try {
            test = e.getCapsule(this).readInt("test", 0);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
