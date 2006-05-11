package jmetest.util;

import java.io.IOException;

import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;

public class Bar implements Savable {

    float f = 0;
    float g = 0;
    
    public Bar() {
        
    }
    
    public void write(JMEExporter e) {
        try {
            e.getCapsule(this).write(f, "f", 0);
            e.getCapsule(this).write(g, "g", 0);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void read(JMEImporter e) {
        try {
            f = e.getCapsule(this).readFloat("f", 0);
            g = e.getCapsule(this).readFloat("g", 0);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
