/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectkyoto.mmd.file.util2;

import com.jme3.export.InputCapsule;
import com.jme3.export.OutputCapsule;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kobayasi
 */
public class SavableUtil {
    
    public static void write(OutputCapsule c, Serializable obj, String name) {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.close();
            byte buf[] = os.toByteArray();
            c.write(buf, name, null);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static Object read(InputCapsule c, String name, Object defVal) {
        try {
            byte[] buf = c.readByteArray(name, null);
            if (buf != null) {
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(buf));
                return is.readObject();
            }        
            return defVal;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
