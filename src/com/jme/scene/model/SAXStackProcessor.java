package com.jme.scene.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Stack;

import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.math.Quaternion;
import com.jme.bounding.BoundingBox;
import com.jme.system.DisplaySystem;

/**
 * Started Date: May 31, 2004
 * SAX Stack processor.  Helper class for SAXReader
 * 
 * @author Jack Lindamood
 */
class SAXStackProcessor {

    Node myScene=new Node("XML node");
    Stack s=new Stack();
    Renderer renderer;
    SAXStackProcessor(){
        renderer=DisplaySystem.getDisplaySystem().getRenderer();
        s.push(myScene);
    }

    void increaseStack(String qName, Attributes atts) throws SAXException{
        if (qName.equalsIgnoreCase("Scene")){
//            s.push(myScene);
        } else if (qName.equalsIgnoreCase("Model")){
            Spatial toAdd=new Node(atts.getValue("name"));
            processSpatial(toAdd,atts);
            s.push(toAdd);
        } else if (qName.equalsIgnoreCase("Mesh")){
            Spatial toAdd=new TriMesh(atts.getValue("name"));
            processSpatial(toAdd,atts);
            s.push(toAdd);

        } else if (qName.equalsIgnoreCase("Color")){

        } else if (qName.equalsIgnoreCase("Index")){

        } else if (qName.equalsIgnoreCase("Normal")){

        } else if (qName.equalsIgnoreCase("TextureCoords")){

        } else if (qName.equalsIgnoreCase("Vertex")){

        } else if (qName.equalsIgnoreCase("DirectionalLight")){

        } else if (qName.equalsIgnoreCase("KeyframeAnimation")){

        } else if (qName.equalsIgnoreCase("primative")){
            String type=atts.getValue("type");
            if (type==null) throw new SAXException("Must supply primative type");
            Spatial i=processPrimative(type,atts.getValue("params"));
            processSpatial(i,atts);
            s.push(i);
        } else if (qName.equalsIgnoreCase("MaterialState")){
            MaterialState m=renderer.getMaterialState();
            m.setAlpha(Float.parseFloat(atts.getValue("alpha")));
            m.setAmbient(createSingleColor(atts.getValue("ambient")));
            m.setDiffuse(createSingleColor(atts.getValue("diffuse")));
            m.setEmissive(createSingleColor(atts.getValue("emissive")));
            m.setShininess(Float.parseFloat(atts.getValue("shiny")));
            m.setSpecular(createSingleColor(atts.getValue("specular")));
            m.setEnabled(true);
            s.push(m);
        } else{
            throw new SAXException("Illegale Qualified name: " + qName);
        }
        return;
    }

    private void processSpatial(Spatial toAdd, Attributes atts) {
        if (atts.getValue("name")!=null){
            toAdd.setName(atts.getValue("name"));
        }
        if (atts.getValue("translation")!=null){
            String [] split=atts.getValue("translation").trim().split(" ");
            toAdd.setLocalTranslation(new Vector3f(
                    Float.parseFloat(split[0]),
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2])
            ));
        }
        if (atts.getValue("rotation")!=null){
            String [] split=atts.getValue("rotation").trim().split(" ");
            toAdd.setLocalRotation(new Quaternion(
                    Float.parseFloat(split[0]),
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]),
                    Float.parseFloat(split[3])
            ));
        }
        if (atts.getValue("scale")!=null){
            toAdd.setLocalScale(Float.parseFloat(atts.getValue("scale")));
        }
    }

    void decreaseStack(String qName,StringBuffer data) throws SAXException {
        Object child,parent;
        Node childNode,parentNode;
        Geometry tempGeoCurrent;
        Spatial parentSpatial,childSpatial;
        if (qName.equalsIgnoreCase("Scene")){
//            s.pop();
        } else if (qName.equalsIgnoreCase("Model")){
            childNode=(Node) s.pop();
            parentNode=(Node) s.pop();
            parentNode.attachChild(childNode);
            s.push(parentNode);
        } else if (qName.equalsIgnoreCase("Mesh")){
            tempGeoCurrent=(Geometry) s.pop();
            if (tempGeoCurrent.getModelBound()==null){
                tempGeoCurrent.setModelBound(new BoundingBox());
                tempGeoCurrent.updateModelBound();
            }
            parentNode=(Node) s.pop();
            ((Node)parentNode).attachChild(tempGeoCurrent);
            s.push(parentNode);
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

        } else if (qName.equalsIgnoreCase("primative")){
            childSpatial= (Spatial) s.pop();
            parentNode =(Node) s.pop();
            parentNode.attachChild(childSpatial);
            s.push(parentNode);
        } else if (qName.equalsIgnoreCase("KeyframeAnimation")){

        } else if (qName.equalsIgnoreCase("materialstate")){
            child=s.pop();
            parentSpatial=(Spatial) s.pop();
            parentSpatial.setRenderState((RenderState) child);
            s.push(parentSpatial);
        } else {
            throw new SAXException("Illegale Qualified name: " + qName);
        }
    }

    private Spatial processPrimative(String type, String parameters) throws SAXException {
        if (parameters==null) throw new SAXException("Must specify parameters");
        Spatial toReturn=null;
        String[] parts=parameters.trim().split(" ");
        if (type.equalsIgnoreCase("box")){
            if (parts.length!=7) throw new SAXException("Box must have 7 parameters");
            toReturn=new Box(parts[0],new Vector3f(
                    Float.parseFloat(parts[1]),
                    Float.parseFloat(parts[2]),
                    Float.parseFloat(parts[3])),
                    new Vector3f(Float.parseFloat(parts[4]),
                    Float.parseFloat(parts[5]),
                    Float.parseFloat(parts[6])));

        }
        return toReturn;
    }


    private static ColorRGBA createSingleColor(String data) throws SAXException {
        if (data==null || data.length()==0) return null;
        String[] information=data.toString().trim().split(" ");
        if (information.length!=4){
            throw new SAXException("Colors must be of length 4:" + data);
        }
        return new ColorRGBA(
                Float.parseFloat(information[0]),
                Float.parseFloat(information[1]),
                Float.parseFloat(information[2]),
                Float.parseFloat(information[3])
        );
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

    private static int[] createIntArray(StringBuffer data) {
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

    public Node fetchCopy() {
        return myScene; // cloning would go on here.
    }

    public Node fetchOriginal() {
        return myScene;
    }
}