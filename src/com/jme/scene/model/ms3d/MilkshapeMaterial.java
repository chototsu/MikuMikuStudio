package com.jme.scene.model.ms3d;

import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.TriMesh;
import com.jme.image.Texture;
import com.jme.util.TextureManager;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;
import java.io.File;
import java.io.IOException;


/**
 * Helper class for MilkLoader to hold materials
 *
 * @author Jack Lindamood
 */
class MilkshapeMaterial {
    String name;
    ColorRGBA ambColor;
    ColorRGBA difColor;
    ColorRGBA specColor;
    ColorRGBA emisColor;
    float shininess;
    float transparency;
    String texture;
    String alphaMap;
    MaterialState myMat;
    TextureState myTex;

    private MaterialState generateMaterial() {
        if (myMat!=null) return myMat;
        myMat=DisplaySystem.getDisplaySystem().getRenderer().getMaterialState();
        myMat.setAmbient(ambColor);
        myMat.setEmissive(emisColor);
        myMat.setDiffuse(difColor);
        myMat.setSpecular(specColor);
        myMat.setShininess(shininess);
        myMat.setAlpha(transparency);
        myMat.setEnabled(true);
        return myMat;
    }

    private TextureState generateTexture(URL baseURL){
        if (myTex!=null || texture.length()==0) return myTex;
        myTex=DisplaySystem.getDisplaySystem().getRenderer().getTextureState();
        //TODO: Clean this up
        URL toLoad=null;
        try {
            toLoad=new URL(baseURL,texture);
            new File(baseURL.getPath(),texture).toURL().openStream();
            toLoad=new File(baseURL.getPath(),texture).toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
        myTex.setTexture(TextureManager.loadTexture(toLoad,
            Texture.MM_LINEAR_LINEAR,
            Texture.FM_LINEAR,
            true));
        if (myTex==null) throw new JmeException("Problem loading file " + texture);
        myTex.setEnabled(true);
        return myTex;
        //TODO: Support Alpha Maps
    }

    public void setApperance(TriMesh setMesh, URL baseURL){

        setMesh.setRenderState(generateMaterial());
        if (generateTexture(baseURL)!=null)
            setMesh.setRenderState(myTex);
    }


    public String toString() {
        return "MilkshapeMaterial{" +
                ", name='" + name + '\'' +
                ", MaterialState='" + myMat+ '\'' +
                '}';
    }
}
