/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectkyoto.jme3.mmd;

import com.jme3.asset.AssetManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import projectkyoto.jme3.mmd.PMDLoaderGLSLSkinning2;
import projectkyoto.jme3.mmd.PMDNode;
import projectkyoto.mmd.file.PMDModel;
import projectkyoto.mmd.file.util2.MeshConverter;

/**
 *
 * @author kobayasi
 */
public class PmdUtil {
    public static void output(MeshConverter mc) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(
                new BufferedOutputStream(
                new FileOutputStream("/tmp/out.serial")));
        os.writeObject(mc);
        os.close();
    }

    public static MeshConverter input() throws IOException {
        ObjectInputStream is = new ObjectInputStream(
                new BufferedInputStream(
                new FileInputStream("/tmp/out.serial")));
        try {
            return (MeshConverter) is.readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException("class not found.", ex);
        } finally {
            is.close();
        }
    }
    public static PMDNode readNode(AssetManager assetManager, String folderName, ObjectInputStream is) {
        try {
            MeshConverter mc = (MeshConverter) is.readObject();
            PMDLoaderGLSLSkinning2 loader = new PMDLoaderGLSLSkinning2(assetManager, mc);
            loader.setFolderName(folderName);
            return loader.createNode("model");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}
