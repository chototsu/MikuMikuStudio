package com.jme.scene.model.ms3d;

import com.jme.scene.model.Loader;
import com.jme.scene.Node;
import com.jme.bounding.BoundingBox;
import com.jme.system.JmeException;
import com.jme.util.LittleEndien;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class to load .ms3d files for jME.  Base directory for textures
 * is by default the same directory as the first loaded file, unless changed
 * otherwise set by <code>Loader.setBase</code>
 *
 * @author Jack Lindamood
 */
public class MilkLoader extends Loader{
    private MilkFile lastLoad;
    /**
     * Private helper method to create a MS3D file from a MilkFile
     *
     * @param myFile MilkFile to create a scene from
     * @return Scene created from the file
     * @see Node
     */
    private Node createScene(MilkFile myFile){
        Node myScene=new Node("Loaded MilkShape3D scene");

        for (int i=0;i<myFile.nNumGroups;i++)
            myScene.attachChild(myFile.myGroups[i]);

        if ((loadFlags&LOAD_CONTROLLERS)!=0){
            MilkAnimation ma=new MilkAnimation(myFile,0,myFile.iTotalFrames-1,myFile.speed);
            if ((loadFlags&PRECOMPUTE_BOUNDS)!=0){
                for (int i=0;i<myFile.nNumGroups;i++){
                    if (dirty || myFile.myGroups[i].getModelBound()==null)
                        myFile.myGroups[i].setModelBound(ma.findBiggestFit(i));
                }
            }
            myScene.addController(ma);
        } else{
            for (int i=0;i<myFile.nNumGroups;i++){
                if (dirty || myFile.myGroups[i].getModelBound()==null){
                    myFile.myGroups[i].setModelBound(new BoundingBox());
                    myFile.myGroups[i].updateModelBound();
                }
            }
        }
        dirty=false;
        return myScene;
    }

    /**
     * Produces a copy of the last file loaded.  More efficient than reloaded a
     * file because initial processing is completed and no file needs to be opened
     *
     * @return a new MS3D file by spawning the last loaded file, or null no preivous file was loaded
     * @see Node
     */
    public Node fetchCopy(){
        if (lastLoad==null)
            return null;
        else
            return createScene(lastLoad.spawnCopy());
    }


    /**
     * Loads a MilkShape file from the path in the URL.  All texture/alpha
     * maps associated with the URL are by default in the same directory as the
     * .ms3d file specified.  Texture directory can be changed by a call to
     * setBase(URL), allowing the programmer to seperate storage of model
     * files and pictures.
     *
     * @param url Location of .ms3d file
     * @see Node
     */
    public Node load(URL url){
        if (url==null){
            throw new JmeException("Can't load null models");
        }
        if (baseUrl==null){
            try {
                baseUrl=new URL(url.getProtocol(),url.getHost(),url.getPort(),new File(url.getPath()).getParent()+'\\');
            } catch (MalformedURLException e) {
                throw new JmeException("Try setting a baseURL: " +e.getMessage());
            }
        }
        try {
            LittleEndien file=new LittleEndien(url.openStream());
            lastLoad=new MilkFile(baseUrl,file);
            file.close();
        } catch (IOException e) {
            throw new JmeException("Something wierd in load(URL) (DataInputStream) " + e.getMessage());
        }
        return createScene(lastLoad);
    }
}