package com.jme.scene.model.XMLparser;

import com.jme.scene.*;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.RenderState;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.math.Quaternion;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.image.Texture;
import com.jme.util.TextureManager;
import com.jme.util.LoggingSystem;
import com.jme.bounding.BoundingSphere;
import com.jme.animation.JointController;
import com.jme.animation.KeyframeController;
import com.jme.scene.model.JointMesh2;
import com.jme.scene.model.EmptyTriMesh;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.File;
import java.util.Stack;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.logging.Level;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * Started Date: Jun 23, 2004<br><br>
 *
 * This class converts jME's binary format to a scenegraph.  Even
 * though this class's name ends with Reader, it does not extend Reader
 * @author Jack Lindamood
 */
public class JmeBinaryReader {

    /**
     * Holds a list of objects that have encountered a being_tag but not an end_tag yet
     */
    private Stack s=new Stack();

    /**
     * Holds already loaded objects that are to be shared at various locations in the file
     */
    private Hashtable shares=new Hashtable();

    /**
     * Holds the attributes of a tag for processing
     */
    private HashMap attributes=new HashMap();

    /**
     * Holds properties that modify how JmeBinaryReader loads a file
     */
    private HashMap properties=new HashMap();

    /**
     * The scene that was last loaded
     */
    private Node myScene;

    private Renderer renderer;
    private DataInputStream myIn;

    private final static boolean DEBUG=false;

    /**
     * Constructs a new JmeBinaryReader.  This must be called after a DisplaySystem
     * has been initialized
     */
    public JmeBinaryReader(){
        renderer=DisplaySystem.getDisplaySystem().getRenderer();
    }

    /**
     * This will read the binaryJme InputStream to
     * convert jME's binary format to a Node.
     * @param binaryJme The binary format jME scene
     * @return A Node representing the binary file
     * @throws IOException If anything wierd goes on while reading
     */
    public Node loadBinaryFormat(InputStream binaryJme) throws IOException {
        if (DEBUG) System.out.println("Begining read");
        myScene=null;
        s.clear();
        shares.clear();
        attributes.clear();
        myIn=new DataInputStream(binaryJme);
        readHeader();
        byte flag=myIn.readByte();
        while (flag!=BinaryFormatConstants.END_FILE){
            if (flag==BinaryFormatConstants.BEGIN_TAG)
                readBegining();
            else if (flag==BinaryFormatConstants.END_TAG)
                readEnd();
            else{
                throw new IOException("Unknown flag:" + flag);
            }
            flag=myIn.readByte();
        }
        if (DEBUG) System.out.println("Done reading");
        return myScene;
    }

    /**
     * processes an END_TAG flag, which signals a tag has finished reading all children information
     * @throws IOException If anything bad happens in reading the binary file
     */
    private void readEnd() throws IOException {
        String tagName=myIn.readUTF();
        if (DEBUG) System.out.println("reading endtag:" + tagName);
        Node childNode,parentNode;
        Spatial parentSpatial,childSpatial;
        if (tagName.equals("scene")){
            myScene=(Node) s.pop();
        } else if (tagName.equals("node")){
            childNode=(Node) s.pop();
            parentNode=(Node) s.pop();
            parentNode.attachChild(childNode);
            s.push(parentNode);
        } else if (tagName.equals("materialstate")){
            MaterialState childMaterial=(MaterialState) s.pop();
            parentSpatial=(Spatial) s.pop();
            parentSpatial.setRenderState(childMaterial);
            s.push(parentSpatial);
        } else if (tagName.equals("texturestate")){
            TextureState childMaterial=(TextureState) s.pop();
            parentSpatial=(Spatial) s.pop();
            parentSpatial.setRenderState(childMaterial);
            s.push(parentSpatial);
        } else if (tagName.equals("mesh") || tagName.equals("jointmesh")){
            Geometry childMesh=(Geometry) s.pop();
            if (childMesh.getModelBound()==null){
                childMesh.setModelBound(new BoundingSphere());
                childMesh.updateModelBound();
            }
            parentNode=(Node) s.pop();
            parentNode.attachChild(childMesh);
            s.push(parentNode);
        } else if (tagName.equals("vertex")){
        } else if (tagName.equals("normal")){
        } else if (tagName.equals("color")){
        } else if (tagName.equals("texturecoords")){
        } else if (tagName.equals("index")){
        } else if (tagName.equals("primitive")){
            childSpatial=(Spatial) s.pop();
            parentNode=(Node) s.pop();
            parentNode.attachChild(childSpatial);
            s.push(parentNode);
        } else if (tagName.equals("sharedtypes") || tagName.equals("keyframe")){
            // Nothing to do, these only identify XML areas
        } else if (tagName.equals("xmlloadable")){
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
        } else if (tagName.equals("sharedrenderstate")){
            XMLSharedNode XMLShare=(XMLSharedNode) s.pop();
            shares.put(XMLShare.myIdent,XMLShare.whatIReallyAm);
        } else if (tagName.equals("publicobject")){
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
        } else if (tagName.equals("jointcontroller")){
            JointController jc=(JointController) s.pop();
            parentNode=(Node) s.pop();
            for (int i=0;i<parentNode.getQuantity();i++){
                if (parentNode.getChild(i) instanceof JointMesh2)
                    jc.addJointMesh((JointMesh2) parentNode.getChild(i));
            }
            jc.processController();
            parentNode.addController(jc);
            s.push(parentNode);
        } else if (tagName.equals("joint")){
            s.pop();    // remove unneeded information tag
        } else if (tagName.equals("jointindex")){
        } else if (tagName.equals("origvertex")){
        } else if (tagName.equals("orignormal")){
        } else if (tagName.equals("keyframecontroller")){
            KeyframeController kc=(KeyframeController) s.pop();
            TriMesh parentMesh=(TriMesh) s.pop();
            parentMesh.addController(kc);
            s.push(parentMesh);
        } else if (tagName.equals("keyframepointintime")){
            TriMesh parentMesh=(TriMesh) s.pop();
            float time=((Float) s.pop()).floatValue();
            KeyframeController kc=(KeyframeController)s.pop();
            kc.setKeyframe(time,parentMesh);
            s.push(kc);
        } else {
            throw new JmeException("Illegale Qualified name: " + tagName);
        }
    }


    /**
     * Processes a BEGIN_TAG flag, which signals that a tag has begun.  Attributes for the
     * tag are read, and if needed an object is pushed on the stack
     * @throws IOException If anything wierd goes on in reading
     */
    private void readBegining() throws IOException {
        String tagName=myIn.readUTF();
        if (DEBUG) System.out.println("Reading tagName:" + tagName);
        readInObjects(attributes);
        if (tagName.equalsIgnoreCase("Scene")){
            s.push(new Node("XML Scene"));
        } else if (tagName.equals("node")){
            s.push(processSpatial(new Node((String) attributes.get("name")),attributes));
        } else if (tagName.equals("materialstate")){
            s.push(buildMaterial(attributes));
        } else if (tagName.equals("texturestate")){
            s.push(buildTexture(attributes));
        } else if (tagName.equals("mesh")){
            s.push(processSpatial(new TriMesh((String) attributes.get("name")),attributes));
        } else if (tagName.equals("vertex")){
            Geometry geo=(Geometry) s.pop();
            geo.setVertices((Vector3f[]) attributes.get("data"));
            s.push(geo);
        } else if (tagName.equals("normal")){
            Geometry geo=(Geometry) s.pop();
            geo.setNormals((Vector3f[]) attributes.get("data"));
            s.push(geo);
        } else if (tagName.equals("texturecoords")){
            Geometry geo=(Geometry) s.pop();
            geo.setTextures((Vector2f[]) attributes.get("data"));
            s.push(geo);
        } else if (tagName.equals("color")){
            Geometry geo=(Geometry) s.pop();
            geo.setColors((ColorRGBA[]) attributes.get("data"));
            s.push(geo);
        } else if (tagName.equals("index")){
            TriMesh m=(TriMesh) s.pop();
            m.setIndices((int[]) attributes.get("data"));
            s.push(m);
        } else if (tagName.equals("origvertex")){
            JointMesh2 jm=(JointMesh2) s.pop();
            jm.originalVertex=(Vector3f[]) attributes.get("data");
            s.push(jm);
        } else if (tagName.equals("orignormal")){
            JointMesh2 jm=(JointMesh2) s.pop();
            jm.originalNormal=(Vector3f[]) attributes.get("data");
            s.push(jm);
        } else if (tagName.equals("jointindex")){
            JointMesh2 jm=(JointMesh2) s.pop();
            jm.jointIndex=(int[]) attributes.get("data");
            s.push(jm);
        } else if (tagName.equals("sharedtypes")){
            // Do nothing, these have no attributes
        } else if (tagName.equals("primitive")){
            s.push(processPrimitive(attributes));
        } else if (tagName.equals("sharedrenderstate")){
            s.push(new XMLSharedNode((String) attributes.get("ident")));
        } else if (tagName.equals("publicobject")){
            Object toAdd=shares.get(attributes.get("ident"));
            if (toAdd==null){
                throw new JmeException("Unknown publicobject: " +shares.get(attributes.get("ident")));
            }
            s.push(toAdd);
        } else if (tagName.equals("xmlloadable")){
            try {
                Class c=Class.forName((String) attributes.get("class"));
                if (!XMLloadable.class.isAssignableFrom(c)){
                    throw new JmeException("Given XML class must implement XMLloadable");
                }
                XMLloadable x=(XMLloadable) c.newInstance();
                Object o=x.loadFromXML((String) attributes.get("args"));
                if (o instanceof Spatial){
                    processSpatial((Spatial) o,attributes);
                }
                s.push(o);
            } catch (ClassNotFoundException e) {
                throw new JmeException("Unknown class type:" + attributes.get("class"));
            } catch (IllegalAccessException e) {
                throw new JmeException("XMLloadable classes must have a default() constructor: " + attributes.get("class"));
            } catch (InstantiationException e) {
                throw new JmeException("XMLloadable classes cannot be abstract: " + attributes.get("class"));
            }
        } else if (tagName.equals("jointcontroller")){
            s.push(new JointController(((Integer)attributes.get("numJoints")).intValue()));
        } else if (tagName.equals("keyframe")){
            Integer jointIndex=(Integer) s.pop();
            JointController jc=(JointController) s.pop();

            if (attributes.get("rot")!=null)
                jc.setRotation(jointIndex.intValue(),((Float)attributes.get("time")).floatValue(),(Quaternion) attributes.get("rot"));

            if (attributes.get("trans")!=null)
                jc.setTranslation(jointIndex.intValue(),((Float)attributes.get("time")).floatValue(),(Vector3f) attributes.get("trans"));

            s.push(jc);
            s.push(jointIndex);
        } else if (tagName.equals("joint")){
            JointController jc=(JointController) s.pop();
            jc.parentIndex[((Integer)attributes.get("index")).intValue()]=((Integer)attributes.get("parentindex")).intValue();
            jc.localRefMatrix[((Integer)attributes.get("index")).intValue()].set((Quaternion) attributes.get("localrot"),(Vector3f) attributes.get("localvec"));
            s.push(jc);
            s.push(attributes.get("index"));
        } else if (tagName.equals("jointmesh")){
            s.push(processSpatial(new JointMesh2((String) attributes.get("name")),attributes));
        } else if (tagName.equals("keyframecontroller")){
            KeyframeController kc=new KeyframeController();
            kc.setActive(true);
            TriMesh parentMesh=(TriMesh) s.pop();
            kc.setMorphingMesh(parentMesh);
            s.push(parentMesh);
            s.push(kc);
        } else if (tagName.equals("keyframepointintime")){
            s.push(attributes.get("time"));  // Store the current time on the stack
            s.push(new EmptyTriMesh());
        } else{
            throw new JmeException("Illegale Qualified name: " + tagName);
        }
        return;

    }

    /**
     * Builds a primitive given attributes
     * @param atts Attributes to build with
     * @return The loaded primitive
     */
    private Spatial processPrimitive(HashMap atts){
        String parameters=(String) atts.get("params");
        String type=(String) atts.get("type");
        if (parameters==null) throw new JmeException("Must specify parameters");
        Spatial toReturn;
        String[] parts=parameters.trim().split(" ");
        if (type.equalsIgnoreCase("box")){
            if (parts.length!=7) throw new JmeException("Box must have 7 parameters");
            Box box=new Box(parts[0],new Vector3f(
                    Float.parseFloat(parts[1]),
                    Float.parseFloat(parts[2]),
                    Float.parseFloat(parts[3])),
                    new Vector3f(Float.parseFloat(parts[4]),
                    Float.parseFloat(parts[5]),
                    Float.parseFloat(parts[6])));
            box.setModelBound(new BoundingSphere());
            box.updateModelBound();
            toReturn=box;
        }else{
            throw new JmeException("Unknown primitive type: " + type);
        }
        return processSpatial(toReturn,atts);
    }

    /**
     * Builds a texture with the given attributes
     * @param atts The attributes of the Texture
     * @return The new texture
     */
    private TextureState buildTexture(HashMap atts){
        TextureState t=renderer.getTextureState();
        try {
            Texture p=null;
            if (atts.get("URL")!=null && !atts.get("URL").equals("null")){
                p=TextureManager.loadTexture((URL) atts.get("URL"),
                        Texture.MM_LINEAR,Texture.FM_LINEAR,true);
            } else if (atts.get("file")!=null && !atts.get("file").equals("null")){
                URL context;
                if (properties.containsKey("texurl")){
                    context=new URL((URL) properties.get("texurl"),(String) atts.get("file"));
                } else{
                    context=new File((String) atts.get("file")).toURI().toURL();
                }
                p=TextureManager.loadTexture(context,
                        Texture.MM_LINEAR,Texture.FM_LINEAR,true);
                p.setImageLocation("file:/"+atts.get("file"));
            }
            if (p==null)
                LoggingSystem.getLogger().log(Level.INFO,"Unable to load file: " + atts.get("file"));
            else
                t.setTexture(p);
        } catch (MalformedURLException e) {
            throw new JmeException("Bad file name: " + atts.get("file") + "*" + atts.get("URL"));
        }
        t.setEnabled(true);
        return t;
    }

    /**
     * Changes a Spatial's parameters acording to the attributes
     * @param toAdd The spatial to change
     * @param atts The attributes
     * @return The given (<code>toAdd</code>) Spatial
     */
    private Spatial processSpatial(Spatial toAdd, HashMap atts) {
        if (atts.get("name")!=null)
            toAdd.setName((String) atts.get("name"));
        if (atts.get("translation")!=null)
            toAdd.setLocalTranslation((Vector3f) atts.get("translation"));
        if (atts.get("rotation")!=null)
            toAdd.setLocalRotation((Quaternion)atts.get("rotation"));
        if (atts.get("scale")!=null)
            toAdd.setLocalScale((Vector3f) atts.get("scale"));
        return toAdd;
    }

    /**
     * Builds a MaterialState with the given attributes
     * @param atts The attributes
     * @return A new material state
     */
    private MaterialState buildMaterial(HashMap atts) {
        MaterialState m=renderer.getMaterialState();
        m.setAlpha(((Float)atts.get("alpha")).floatValue());
        m.setAmbient((ColorRGBA) atts.get("ambient"));
        m.setDiffuse((ColorRGBA) atts.get("diffuse"));
        m.setEmissive((ColorRGBA) atts.get("emissive"));
        m.setShininess(((Float)atts.get("shiny")).floatValue());
        m.setSpecular((ColorRGBA) atts.get("specular"));
        m.setEnabled(true);
        return m;
    }

    /**
     * Reads byte information from the binary file to put the needed attributes into a hashmap.  For
     * example, the hashmap may contain {"translation":new Vector3f(1,1,1),"name":new String("guy")}
     * @param atribMap The hashmap to hold the attributes
     * @throws IOException If reading goes wrong
     */
    private void readInObjects(HashMap atribMap) throws IOException {
        atribMap.clear();
        byte numFlags=myIn.readByte();
        for (int i=0;i<numFlags;i++){
            String name=myIn.readUTF();
            byte type=myIn.readByte();
            if (DEBUG) System.out.println("Reading attribute*" + name + "* with type " + type);
            switch (type){
                case BinaryFormatConstants.DATA_COLORARRAY:
                    atribMap.put(name,getColorArray());
                    break;
                case BinaryFormatConstants.DATA_INTARRAY:
                    atribMap.put(name,getIntArray());
                    break;
                case BinaryFormatConstants.DATA_STRING:
                    atribMap.put(name,myIn.readUTF());
                    break;
                case BinaryFormatConstants.DATA_V2FARRAY:
                    atribMap.put(name,getVec2fArray());
                    break;
                case BinaryFormatConstants.DATA_V3FARRAY:
                    atribMap.put(name,getVec3fArray());
                    break;
                case BinaryFormatConstants.DATA_V3F:
                    atribMap.put(name,getVec3f());
                    break;
                case BinaryFormatConstants.DATA_QUAT:
                    atribMap.put(name,getQuat());
                    break;
                case BinaryFormatConstants.DATA_FLOAT:
                    atribMap.put(name,new Float(myIn.readFloat()));
                    break;
                case BinaryFormatConstants.DATA_COLOR:
                    atribMap.put(name,getColor());
                    break;
                case BinaryFormatConstants.DATA_URL:
                    atribMap.put(name,new URL(myIn.readUTF()));
                    break;
                case BinaryFormatConstants.DATA_INT:
                    atribMap.put(name,new Integer(myIn.readInt()));
                    break;
                default:
                    throw new IOException("Unknown data type:" + type);
            }
        }
    }

    private Quaternion getQuat() throws IOException{
        return new Quaternion(myIn.readFloat(),myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
    }

    private ColorRGBA getColor() throws IOException{
        return new ColorRGBA(myIn.readFloat(),myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
    }

    private Vector3f getVec3f() throws IOException{
        return new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
    }

    private int[] getIntArray() throws IOException {
        int length=myIn.readInt();
        if (length==0) return null;
        int[] array=new int[length];
        for (int i=0;i<length;i++){
            array[i]=myIn.readInt();
        }
        return array;
    }

    private Vector2f[] getVec2fArray() throws IOException {
        int length=myIn.readInt();
        if (length==0) return null;
        Vector2f[] array=new Vector2f[length];
        for (int i=0;i<length;i++){
            array[i]=new Vector2f(myIn.readFloat(),myIn.readFloat());
        }
        return array;
    }

    private ColorRGBA[] getColorArray() throws IOException {
        int length=myIn.readInt();
        if (length==0) return null;
        ColorRGBA[] array=new ColorRGBA[length];
        for (int i=0;i<length;i++)
            array[i]=new ColorRGBA(myIn.readFloat(),myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        return array;
    }

    // Note, a vector3f that is all NaN is considered null
    private Vector3f[] getVec3fArray() throws IOException {
        int length=myIn.readInt();
        if (length==0) return null;
        Vector3f[] array=new Vector3f[length];
        for (int i=0;i<length;i++){
            array[i]=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
            if (array[i].x==Float.NaN && array[i].y==Float.NaN && array[i].z==Float.NaN) array[i]=null;
        }
        return array;
    }

    private void readHeader() throws IOException {
        if (BinaryFormatConstants.BEGIN_FILE!=myIn.readLong()){
            throw new IOException("Binary Header doesn't match.  Maybe wrong file?");
        }
    }

    /**
     * Adds a property .  Properties can tell this how to process the binary file
     * @param key Key to add (For example "texdir")
     * @param property Property for that key to have (For example "c:\\blarg\\")
     */
    public void setProperty(String key, Object property) {
        properties.put(key,property);
    }

    /**
     * Removes a property
     * @param key The property to remove
     */
    public void clearProperty(String key){
        properties.remove(key);
    }
}
