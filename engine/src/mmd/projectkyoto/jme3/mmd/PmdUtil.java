/*
 * Copyright (c) 2010-2014, Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
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
