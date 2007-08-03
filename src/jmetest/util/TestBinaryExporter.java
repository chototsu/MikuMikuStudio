package jmetest.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;

public class TestBinaryExporter {
    private static final Logger logger = Logger
            .getLogger(TestBinaryExporter.class.getName());
    
    public static void main(String[] args) {
        Foo f = new Foo();
        try {
            f.x = 8;
            Bar y = new Bar();
            y.f = 7.5f;
            y.g = 9.32423f;
            
            Bar z = y;
            
            f.y = y;
            f.z = z;
            BinaryExporter.getInstance().save(f, new File("C:/testFoo.fate"));
        } catch (IOException e) {
            logger.throwing(TestBinaryExporter.class.toString(), "main(args)",
                    e);
        }
        
        try {
            Foo j = (Foo)BinaryImporter.getInstance().load(new File("C:/testFoo.fate"));
            logger.info("x: Should be 8: " + j.x);
            logger.info("y: We have bar... " + j.y + "  f: " + j.y.f + "  g: " + j.y.g);
            logger.info("z: We have bar... " + j.z + "  f: " + j.z.f + "  g: " + j.z.g );
        } catch (IOException e) {
            logger.throwing(TestBinaryExporter.class.toString(), "main(args)",
                    e);
        }
    }
    
    
}
