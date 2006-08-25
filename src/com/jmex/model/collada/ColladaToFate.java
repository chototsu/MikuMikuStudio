package com.jmex.model.collada;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.jme.app.SimpleHeadlessApp;
import com.jme.scene.Spatial;
import com.jme.util.export.binary.BinaryExporter;

public class ColladaToFate extends SimpleHeadlessApp {
   Spatial collada;
   static String in;
   static String texDir;
   static String outDir;
    public static void main(String[] args) {
        System.out.println(args.length);
        if (args.length < 3 || args.length > 3) {
            System.out
                    .println("USAGE: ColladaToFate <COLLADA File> <Texture Directory> <Fate File>");
            System.exit(1);
        }
        in = args[0];
        texDir = args[1];
        outDir = args[2];
        
        //make sure outDir exists:
        File out = new File(outDir);
        
        if(!out.exists()) {
            out.mkdir();
        }
        
        ColladaToFate ctf = new ColladaToFate();
        ctf.start();

    }

    protected void simpleInitGame() {
        long start = System.nanoTime();
        writeFile(in, texDir);

        this.finished = true;
        long end = System.nanoTime();
        
        System.out.println("Conversion took: " + ((end-start)/1000000000) + " seconds.");
    }
    
    protected void writeFile(String inputFile, String texdir) {
        File inFile = new File(inputFile);
        if(inFile.isDirectory()) {
            if(!inputFile.endsWith("/")) {
                inputFile += "/";
            }
            System.out.println(inputFile + " is a Directory, getting subfiles: ");
            String[] files = inFile.list();
            for(int i = 0; i < files.length; i++) {
                System.out.println("Sending: " + (inputFile+files[i]));
                writeFile(inputFile+files[i], texdir);
            }
            
            return;
        }
        
        if(inFile.getName().toUpperCase().endsWith(".DAE")) {
            collada = null;
            System.gc();
            
            System.out.println("This is a Collada file, converting.");
            String out = outDir + inFile.getName().toLowerCase().replace(".dae", ".fate");
            System.out.println("Storing as: " + out);
            URL url = null;
            String modelName = inFile.getName().substring(0,
                    inFile.getName().indexOf("."));
            try {
                url = new File(texDir).toURL();
            } catch (MalformedURLException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            FileInputStream input = null;
            try {
                input = new FileInputStream(inFile);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            if (input == null) {
                System.out
                        .println("Unable to find file");
                System.exit(0);
            }
            
            
            try {
                ColladaImporter.load(input, new File(texDir).toURL(),
                        modelName);
                collada = ColladaImporter.getModel();
                ColladaImporter.cleanUp();
            } catch (Exception e) {
                
            }
            
            collada.updateGeometricState(0, true);
            collada.updateRenderState();
            
            try {
                File f = new File(out);
                if(f.exists()) {
                    f.delete();
                }
                BinaryExporter.getInstance().save(collada, f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
