package com.jme.scene.model.XMLparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Stack;
import java.util.Hashtable;

import com.jme.scene.*;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.math.Quaternion;
import com.jme.bounding.BoundingBox;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.image.Texture;

/**
 * Started Date: May 31, 2004
 * SAX Stack processor.  Helper class for SAXReader
 * 
 * @author Jack Lindamood
 */
class SAXStackProcessor {

    Node myScene;
    Stack s=new Stack();
    Hashtable shares=new Hashtable();
    Renderer renderer;
    SAXStackProcessor(){
        renderer=DisplaySystem.getDisplaySystem().getRenderer();
    }

    void increaseStack(String qName, Attributes atts) throws SAXException{
        new TriMesh();
        if (qName.equalsIgnoreCase("Scene")){
            s.push(new Node("XML Scene"));
        } else if (qName.equals("node")){
            s.push(processSpatial(new Node(atts.getValue("name")),atts));
        } else if (qName.equals("materialstate")){
            s.push(buildMaterial(atts));
        } else if (qName.equals("mesh")){
            s.push(processSpatial(new TriMesh(atts.getValue("name")),atts));
        } else if (qName.equals("vertex") || qName.equals("normal") ||
                qName.equals("texturecoords") || qName.equals("index") || qName.equals("color") || qName.equals("sharedtypes")){
            // Do nothing, these have no attributes
        } else if (qName.equals("primitive")){
            s.push(processPrimitive(atts));
        } else if (qName.equals("sharednodeitem")){
            s.push(new XMLSharedNode(atts.getValue("ident")));
        } else if (qName.equals("publicobject")){
            Object toAdd=shares.get(atts.getValue("ident"));
            if (toAdd==null){
                throw new SAXException("Unknown publicobject: " +shares.get(atts.getValue("ident")));
            }
            s.push(toAdd);
        } else{
            throw new SAXException("Illegale Qualified name: " + qName);
        }
        return;
    }


    void decreaseStack(String qName,StringBuffer data) throws SAXException {
        Object child,parent;
        Node childNode,parentNode;
        Geometry tempGeoCurrent;
        Spatial parentSpatial,childSpatial;
        if (qName.equalsIgnoreCase("Scene")){
            myScene=(Node) s.pop();
        } else if (qName.equals("node")){
            childNode=(Node) s.pop();
            parentNode=(Node) s.pop();
            parentNode.attachChild(childNode);
            s.push(parentNode);
        } else if (qName.equals("materialstate")){
            MaterialState childMaterial=(MaterialState) s.pop();
            parentSpatial=(Spatial) s.pop();
            parentSpatial.setRenderState(childMaterial);
            s.push(parentSpatial);
        } else if (qName.equals("mesh")){
            TriMesh childMesh=(TriMesh) s.pop();
            if (childMesh.getModelBound()==null){
                childMesh.setModelBound(new BoundingBox());
                childMesh.updateModelBound();
            }
            parentNode=(Node) s.pop();
            parentNode.attachChild(childMesh);
            s.push(parentNode);
        } else if (qName.equals("vertex")){
            Geometry childGeometry=(Geometry) s.pop();
            childGeometry.setVertices(createVector3f(data));
            s.push(childGeometry);
        } else if (qName.equals("normal")){
            Geometry childGeometry=(Geometry) s.pop();
            childGeometry.setNormals(createVector3f(data));
            s.push(childGeometry);
        } else if (qName.equals("color")){
            Geometry childGeometry=(Geometry) s.pop();
            childGeometry.setColors(createColors(data));
            s.push(childGeometry);
        } else if (qName.equals("texturecoords")){
            Geometry childGeometry=(Geometry) s.pop();
            childGeometry.setTextures(createVector2f(data));
            s.push(childGeometry);
        } else if (qName.equals("index")){
            TriMesh childGeometry=(TriMesh) s.pop();
            childGeometry.setIndices(createIntArray(data));
            s.push(childGeometry);
        } else if (qName.equals("primitive")){
            childSpatial=(Spatial) s.pop();
            parentNode=(Node) s.pop();
            parentNode.attachChild(childSpatial);
            s.push(parentNode);
        } else if (qName.equals("sharedtypes")){
            // Nothing to do, these only identify XML areas
        } else if (qName.equals("sharednodeitem")){
            XMLSharedNode XMLShare=(XMLSharedNode) s.pop();
            shares.put(XMLShare.myIdent,XMLShare.whatIReallyAm);
        } else if (qName.equals("publicobject")){
            Object o=s.pop();
            if (o instanceof RenderState){
                parentSpatial=(Spatial) s.pop();
                parentSpatial.setRenderState((RenderState) o);
                s.push(parentSpatial);
            } else if (o instanceof Controller){
                parentSpatial=(Spatial) s.pop();
                parentSpatial.addController((Controller) o);
                s.push(parentSpatial);
            } else if (o instanceof Spatial){
                parentNode=(Node) s.pop();
                parentNode.attachChild((Spatial) o);
                s.push(parentNode);
            }
        } else {
            throw new SAXException("Illegale Qualified name: " + qName);
        }
    }

    private void lookThruSpatial(Spatial lookThru) {
        if (lookThru.getControllers().size()!=0){

        }
    }

    private TextureState buildTexture(Attributes atts) throws SAXException {
        TextureState t=renderer.getTextureState();
            t.setTexture(TextureManager.loadTexture(atts.getValue("filename"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        t.setEnabled(true);
        return t;
    }

    private MaterialState buildMaterial(Attributes atts) throws SAXException {
        MaterialState m=renderer.getMaterialState();
        m.setAlpha(Float.parseFloat(atts.getValue("alpha")));
        m.setAmbient(createSingleColor(atts.getValue("ambient")));
        m.setDiffuse(createSingleColor(atts.getValue("diffuse")));
        m.setEmissive(createSingleColor(atts.getValue("emissive")));
        m.setShininess(Float.parseFloat(atts.getValue("shiny")));
        m.setSpecular(createSingleColor(atts.getValue("specular")));
        m.setEnabled(true);
        return m;
    }

    private Spatial processSpatial(Spatial toAdd, Attributes atts) {
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
        return toAdd;
    }


    private Spatial processPrimitive(Attributes atts) throws SAXException {
        String parameters=atts.getValue("params");
        String type=atts.getValue("type");
        if (parameters==null) throw new SAXException("Must specify parameters");
        Spatial toReturn=null;
        String[] parts=parameters.trim().split(" ");
        if (type.equalsIgnoreCase("box")){
            if (parts.length!=7) throw new SAXException("Box must have 7 parameters");
            Geometry box=new Box(parts[0],new Vector3f(
                    Float.parseFloat(parts[1]),
                    Float.parseFloat(parts[2]),
                    Float.parseFloat(parts[3])),
                    new Vector3f(Float.parseFloat(parts[4]),
                    Float.parseFloat(parts[5]),
                    Float.parseFloat(parts[6])));
            box.setModelBound(new BoundingBox());
            box.updateModelBound();
            toReturn=box;
        }else{
            throw new SAXException("Unknown primitive type: " + type);
        }
        return processSpatial(toReturn,atts);
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
        if (information.length==1 && information[0].equals("")) return null;
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
        if (information.length==1 && information[0].equals("")) return null;
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
        if (information.length==1 && information[0].equals("")) return null;
        int[] indexes=new int[information.length];
        for (int i=0;i<indexes.length;i++){
            indexes[i]=Integer.parseInt(information[i]);
        }
        return indexes;
    }

    private static ColorRGBA[] createColors(StringBuffer data) throws SAXException {
        if (data.length()==0) return null;
        String [] information=data.toString().trim().split(" ");
        if (information.length==1 && information[0].equals("")) return null;
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