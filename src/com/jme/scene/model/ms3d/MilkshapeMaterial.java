package com.jme.scene.model.ms3d;

import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.TriMesh;
import com.jme.image.Texture;
import com.jme.util.TextureManager;
import com.jme.util.LoggingSystem;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.util.logging.Level;


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
        try {
            URL textureLocation;
            if (new File(texture).isAbsolute())
                textureLocation=new File(texture).toURL();
            else
                textureLocation=new URL(baseURL,texture);
            myTex.setTexture(TextureManager.loadTexture(new URL(baseURL,texture),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        } catch (MalformedURLException e) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Couldn't load texture: BaseURL=" + baseURL.toString() + ":" +
                    "Texture name:" + texture);
            myTex=null;
            texture="";
            return null;
        }
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