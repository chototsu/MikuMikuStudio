package com.jme.scene.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Stack;

import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.renderer.ColorRGBA;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.bounding.BoundingBox;

/**
 * Started Date: May 31, 2004
 * SAX Stack processor.  Helper class for SAXReader
 * 
 * @author Jack Lindamood
 */
class SAXStackProcessor {
    private SAXStackProcessor(){
        // I don't WANT to be made!!!
    }
    static Node processStack(String qName,StringBuffer data,Stack s) throws SAXException {
        Object current,parent;
        Node tempNodeCurrent,tempNodeParent;
        Geometry tempGeoCurrent;
        Spatial childSpatial;
        if (qName.equalsIgnoreCase("Scene")){
            return (Node) s.pop();
        } else if (qName.equalsIgnoreCase("Model")){
            childSpatial=(Spatial) s.pop();
            if (childSpatial.getWorldBound()==null){
                childSpatial.setWorldBound(new BoundingBox());
                childSpatial.updateWorldBound();
            }
            tempNodeParent=(Node) s.pop();
            tempNodeParent.attachChild(childSpatial);
            s.push(tempNodeParent);
        } else if (qName.equalsIgnoreCase("Mesh")){
            tempGeoCurrent=(Geometry) s.pop();
            if (tempGeoCurrent.getModelBound()==null){
                tempGeoCurrent.setModelBound(new BoundingBox());
                tempGeoCurrent.updateModelBound();
            }
            tempNodeParent=(Node) s.pop();
            ((Node)tempNodeParent).attachChild(tempGeoCurrent);
            s.push(tempNodeParent);
        } else if (qName.equalsIgnoreCase("Color")){
//            current=s.pop();
            parent=s.pop();
            ((Geometry)parent).setColors(createColors(data));
            s.push(parent);
        } else if (qName.equalsIgnoreCase("Index")){
//            current=s.pop();
            parent=s.pop();
            ((TriMesh)parent).setIndices(createIntArray(data));
            s.push(parent);
        } else if (qName.equalsIgnoreCase("Normal")){
//            current=s.pop();
            parent=s.pop();
            ((Geometry)parent).setNormals(createVector3f(data));
            s.push(parent);
        } else if (qName.equalsIgnoreCase("TextureCoords")){
//            current=s.pop();
            parent=s.pop();
            ((Geometry)parent).setTextures(createVector2f(data));
            s.push(parent);
        } else if (qName.equalsIgnoreCase("Vertex")){
//            current=s.pop();
            parent=s.pop();
            ((Geometry)parent).setVertices(createVector3f(data));
            s.push(parent);
        } else if (qName.equalsIgnoreCase("DirectionalLight")){

        } else if (qName.equalsIgnoreCase("KeyframeAnimation")){

        } else {
            throw new SAXException("Illegale Qualified name: " + qName);
        }

        return null;
    }

    private static Vector2f[] createVector2f(StringBuffer data) throws SAXException {
        if (data.length()==0) return null;
        String [] information=data.toString().trim().split(" ");
        if (information.length%2!=0){
            throw new SAXException("Vector2f length not modulus of 2: " + information.length);
        }
        Vector2f[] vecs=new Vector2f[information.length/2];
        for (int i=0;i<vecs.length;i++){
            vecs[i]=new Vector2f(Float.parseFloat(information[i*2+0]),
                    Float.parseFloat(information[i*2+0]));
        }
        return vecs;
    }

    private static Vector3f[] createVector3f(StringBuffer data) throws SAXException {
        if (data.length()==0) return null;
        String [] information=data.toString().trim().split(" ");
        if (information.length%3!=0){
            throw new SAXException("Vector3f length not modulus of 3: " + information.length);
        }
        Vector3f[] vecs=new Vector3f[information.length/3];
        for (int i=0;i<vecs.length;i++){
            vecs[i]=new Vector3f(Float.parseFloat(information[i*3+0]),
                    Float.parseFloat(information[i*3+1]),
                    Float.parseFloat(information[i*3+2]));
        }
        return vecs;
    }

    private static int[] createIntArray(StringBuffer data) throws SAXException {
        if (data.length()==0) return null;
        String [] information=data.toString().trim().split(" ");
        int[] indexes=new int[information.length];
        for (int i=0;i<indexes.length;i++){
            indexes[i]=Integer.parseInt(information[i]);
        }
        return indexes;
    }

    private static ColorRGBA[] createColors(StringBuffer data) throws SAXException {
        if (data.length()==0) return null;
        String [] information=data.toString().split(" ");
        if (information.length%4!=0){
            throw new SAXException("Color length not modulus of 4: " + information.length);
        }
        ColorRGBA[] colors=new ColorRGBA[information.length/4];
        for (int i=0;i<colors.length;i++){
            colors[i]=new ColorRGBA(Float.parseFloat(information[i*4+0]),
                    Float.parseFloat(information[i*4+1]),
                    Float.parseFloat(information[i*4+2]),
                    Float.parseFloat(information[i*4+3]));
        }
        return colors;
    }

    public static void increaseStack(String qName, Attributes atts,Stack s) throws SAXException{
        if (qName.equalsIgnoreCase("Scene")){
            s.push(new Node(atts.getValue("name")));
        } else if (qName.equalsIgnoreCase("Model")){
            s.push(new Node(atts.getValue("name")));
        } else if (qName.equalsIgnoreCase("Mesh")){

            s.push(new TriMesh(atts.getValue("name")));

        } else if (qName.equalsIgnoreCase("Color")){

        } else if (qName.equalsIgnoreCase("Index")){

        } else if (qName.equalsIgnoreCase("Normal")){

        } else if (qName.equalsIgnoreCase("TextureCoords")){

        } else if (qName.equalsIgnoreCase("Vertex")){

        } else if (qName.equalsIgnoreCase("DirectionalLight")){

        } else if (qName.equalsIgnoreCase("KeyframeAnimation")){

        } else{
            throw new SAXException("Illegale Qualified name: " + qName);
        }
        return;
    }
}
