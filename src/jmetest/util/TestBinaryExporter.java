package jmetest.util;

import java.io.File;
import java.io.IOException;

import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;

public class TestBinaryExporter {
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
            e.printStackTrace();
        }
        
        try {
            Foo j = (Foo)BinaryImporter.getInstance().load(new File("C:/testFoo.fate"));
            System.out.println("x: Should be 8: " + j.x);
            System.out.println("y: We have bar... " + j.y + "  f: " + j.y.f + "  g: " + j.y.g);
            System.out.println("z: We have bar... " + j.z + "  f: " + j.z.f + "  g: " + j.z.g );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}
