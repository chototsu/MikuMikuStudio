package com.jme.scene.model.XMLparser;

import com.jme.scene.*;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.LightState;
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
import com.jme.bounding.BoundingBox;
import com.jme.animation.JointController;
import com.jme.animation.KeyframeController;
import com.jme.animation.SpatialTransformer;
import com.jme.scene.model.JointMesh2;
import com.jme.scene.model.EmptyTriMesh;
import com.jme.light.Light;
import com.jme.light.SpotLight;
import com.jme.light.PointLight;

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
     * Holds a list of objects that have encountered a being_tag but not an end_tag yet.
     */
    private Stack s=new Stack();

    /**
     * Holds already loaded objects that are to be shared at various locations in the file.
     */
    private Hashtable shares=new Hashtable();

    /**
     * Holds the attributes of a tag for processing.
     */
    private HashMap attributes=new HashMap();

    /**
     * Holds properties that modify how JmeBinaryReader loads a file.
     */
    private HashMap properties=new HashMap();

    /**
     * The scene that was last loaded.
     */
    private Node myScene;

    private Renderer renderer;
    private DataInputStream myIn;

    private final static boolean DEBUG=false;

    /**
     * Constructs a new JmeBinaryReader.  This must be called after a DisplaySystem
     * has been initialized.
     */
    public JmeBinaryReader(){
        renderer=DisplaySystem.getDisplaySystem().getRenderer();
    }


    /**
     * Reads the binaryJme InputStream and saves it to storeNode
     * @param storeNode Place to save the jME Scene
     * @param binaryJme InputStream with the jME Scene
     * @return The given storeNode
     * @throws IOException If anything wierd goes on while reading.
     */
    public Node loadBinaryFormat(Node storeNode, InputStream binaryJme) throws IOException {
        if (DEBUG) System.out.println("Begining read");
        myScene=null;
        s.clear();
        shares.clear();
        attributes.clear();
        myIn=new DataInputStream(binaryJme);
        readHeader();
        s.push(storeNode);  // This will be pop'd off when </scene> is encountered and saved into myScene
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
     * Reads the binaryJme InputStream to
     * convert jME's binary format to a Node.
     * @param binaryJme The binary format jME scene
     * @return A Node representing the binary file
     * @throws IOException If anything wierd goes on while reading
     */
    public Node loadBinaryFormat(InputStream binaryJme) throws IOException {
        return loadBinaryFormat(new Node("XML loaded scene"),binaryJme);
    }

    /**
     * Processes a BEGIN_TAG flag, which signals that a tag has begun.  Attributes for the
     * tag are read, and if needed an object is pushed on the stack
     * @throws IOException If anything wierd goes on in reading
     */
    private void readBegining() throws IOException {
        String tagName=myIn.readUTF();
        int debug=234;
        if (DEBUG) System.out.println("Reading tagName:" + tagName);
        readInObjects(attributes);
        if (tagName.equals("scene")){
//            s.push(new Node("XML Scene"));    Already on stack
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
        } else if (tagName.equals("sharedtrimesh")){
            s.push(new XMLSharedNode((String) attributes.get("ident")));
        } else if (tagName.equals("sharednode")){
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
        } else if (tagName.equals("lightstate")){
            s.push(buildLightState(attributes));
        } else if (tagName.equals("spotlight")){
            LightState parentLS=(LightState) s.pop();
            parentLS.attach(buildSpotLight(attributes));
            s.push(parentLS);
        } else if (tagName.equals("pointlight")){
            LightState parentLS=(LightState) s.pop();
            parentLS.attach(buildPointLight(attributes));
            s.push(parentLS);
        } else if (tagName.equals("jmefile")){
            if (attributes.get("file")!=null){
                LoaderNode i=new LoaderNode("file "+(String) attributes.get("file"));
                i.loadFromFilePath((String)attributes.get("type"),(String) attributes.get("file"),properties);
                s.push(i);
            } else if (attributes.get("classloader")!=null){
                LoaderNode i=new LoaderNode("classloader "+(String) attributes.get("classloader"));
                i.loadFromClassLoader((String)attributes.get("type"),(String) attributes.get("classloader"),properties);
                s.push(i);
            } else if (attributes.get("url")!=null){
                LoaderNode i=new LoaderNode("classloader "+(URL) attributes.get("url"));
                i.loadFromURLPath((String)attributes.get("type"),(URL) attributes.get("url"),properties);
                s.push(i);
            }
        } else if (tagName.equals("spatialtransformer")){
            SpatialTransformer st=new SpatialTransformer(((Integer)attributes.get("numobjects")).intValue());
            s.push(st);
        } else if (tagName.equals("stobj")){
            s.push(attributes.get("obnum"));
            s.push(attributes.get("parnum"));
            s.push(new XMLSharedNode(null));
        } else if (tagName.equals("spatialpointtime")){
            s.push(attributes.get("time"));
        } else if (tagName.equals("sptscale")){
            Float oldTime=(Float) s.pop();
            float time=oldTime.floatValue();
            int[] scaleIndexes=(int[]) attributes.get("index");
            Vector3f[] scalevalues=(Vector3f[]) attributes.get("scalevalues");
            SpatialTransformer st=(SpatialTransformer) s.pop();
            if (scalevalues!=null)
                for (int i=0;i<scaleIndexes.length;i++)
                    st.setScale(scaleIndexes[i],time,scalevalues[i]);
            s.push(st);
            s.push(oldTime);
        } else if (tagName.equals("sptrot")){
            Float oldTime=(Float) s.pop();
            float time=oldTime.floatValue();

            int[] rotIndexes=(int[]) attributes.get("index");
            Quaternion[] rotvalues=(Quaternion[]) attributes.get("rotvalues");
            SpatialTransformer st=(SpatialTransformer) s.pop();
            if (rotvalues!=null)
                for (int i=0;i<rotIndexes.length;i++)
                    st.setRotation(rotIndexes[i],time,rotvalues[i]);
            s.push(st);
            s.push(oldTime);
        } else if (tagName.equals("spttrans")){
            Float oldTime=(Float) s.pop();
            float time=oldTime.floatValue();
            int[] transIndexes=(int[]) attributes.get("index");
            Vector3f[] transvalues=(Vector3f[]) attributes.get("transvalues");
            SpatialTransformer st=(SpatialTransformer) s.pop();
            if (transvalues!=null)
                for (int i=0;i<transIndexes.length;i++)
                    st.setPosition(transIndexes[i],time,transvalues[i]);
            s.push(st);
            s.push(oldTime);
        } else{
            throw new JmeException("Illegale Qualified name: " + tagName);
        }
        return;

    }

    private PointLight buildPointLight(HashMap attributes) {
        PointLight toReturn=new PointLight();
        putLightInfo(toReturn,attributes);
        toReturn.setLocation((Vector3f)attributes.get("loc"));
        toReturn.setEnabled(true);
        return toReturn;
    }

    private SpotLight buildSpotLight(HashMap attributes) {
        SpotLight toReturn=new SpotLight();
        putLightInfo(toReturn,attributes);
        toReturn.setLocation((Vector3f)attributes.get("loc"));
        toReturn.setAngle(((Float)attributes.get("fangle")).floatValue());
        toReturn.setDirection((Vector3f)attributes.get("dir"));
        toReturn.setExponent(((Float)attributes.get("fexponent")).floatValue());
        toReturn.setEnabled(true);
        return toReturn;
    }

    private void putLightInfo(Light light, HashMap attributes) {
        light.setAmbient((ColorRGBA) attributes.get("ambient"));
        light.setConstant(((Float)attributes.get("fconstant")).floatValue());
        light.setDiffuse((ColorRGBA) attributes.get("diffuse"));
        light.setLinear(((Float)attributes.get("flinear")).floatValue());
        light.setQuadratic(((Float)attributes.get("fquadratic")).floatValue());
        light.setSpecular((ColorRGBA) attributes.get("specular"));
        light.setAttenuate(((Boolean)attributes.get("isattenuate")).booleanValue());
    }

    private LightState buildLightState(HashMap attributes) {
        LightState ls=renderer.getLightState();
        ls.setEnabled(true);
        return ls;
    }

    /**
     * Processes an END_TAG flag, which signals a tag has finished reading all children information.
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
                if ("box".equals(properties.get("bound")))
                    childMesh.setModelBound(new BoundingBox());
                else
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
        } else if (tagName.equals("pointlight") || tagName.equals("spotlight") || tagName.equals("sharedtypes") || tagName.equals("keyframe")){
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
        } else if (tagName.equals("sharedtrimesh")){
            XMLSharedNode XMLShare=(XMLSharedNode) s.pop();
            shares.put(XMLShare.myIdent,XMLShare.whatIReallyAm);
        } else if (tagName.equals("sharednode")){
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
        } else if (tagName.equals("lightstate")){
            LightState ls=(LightState) s.pop();
            parentSpatial=(Spatial) s.pop();
            parentSpatial.setRenderState(ls);
            s.push(parentSpatial);
        } else if (tagName.equals("keyframepointintime")){
            TriMesh parentMesh=(TriMesh) s.pop();
            float time=((Float) s.pop()).floatValue();
            KeyframeController kc=(KeyframeController)s.pop();
            kc.setKeyframe(time,parentMesh);
            s.push(kc);
        } else if (tagName.equals("jmefile")){
            LoaderNode childLoaderNode=(LoaderNode) s.pop();
            parentNode=(Node) s.pop();
            parentNode.attachChild(childLoaderNode);
            s.push(parentNode);
        } else if (tagName.equals("spatialtransformer")){
            SpatialTransformer st=(SpatialTransformer) s.pop();
            parentSpatial=(Spatial) s.pop();
            st.interpolateMissing();
            st.setActive(true);
            parentSpatial.addController(st);
            s.push(parentSpatial);
        } else if (tagName.equals("stobj")){
            XMLSharedNode xsn=(XMLSharedNode) s.pop();
            int parNum=((Integer)s.pop()).intValue();
            int obNum=((Integer)s.pop()).intValue();
            SpatialTransformer parentST=(SpatialTransformer) s.pop();
            parentST.setObject((Spatial) xsn.whatIReallyAm,obNum,parNum);
            s.push(parentST);
        } else if (tagName.equals("spatialpointtime")){
            s.pop();
        } else if (tagName.equals("sptscale") || tagName.equals("sptrot") || tagName.equals("spttrans")){ // nothing to do at these ends

        } else {
            throw new JmeException("Illegale Qualified name: " + tagName);
        }
    }

    /**
     * Builds a primitive given attributes.
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
     * Builds a texture with the given attributes.  Will use the "texurl" property if needed to
     * help build the texture
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
                } else if (properties.containsKey("texclasspath")){
                    context=JmeBinaryReader.class.getClassLoader().getResource(
                            (String)properties.get("texclasspath")+(String)atts.get("file")
                    );
                } else {
                    context=new File((String) atts.get("file")).toURI().toURL();
                }
                p=TextureManager.loadTexture(context,
                        Texture.MM_LINEAR,Texture.FM_LINEAR,true);
                if (p==null) {
                    t.setEnabled(false);
                    return t;
                } else{
                    p.setImageLocation("file:/"+atts.get("file"));
                }
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
     * Changes a Spatial's parameters acording to the attributes.
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
     * Builds a MaterialState with the given attributes.
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
                case BinaryFormatConstants.DATA_BOOLEAN:
                    atribMap.put(name,new Boolean(myIn.readBoolean()));
                    break;
                case BinaryFormatConstants.DATA_QUATARRAY:
                    atribMap.put(name,getQuatArray());
                    break;
                default:
                    throw new IOException("Unknown data type:" + type);
            }
        }
    }

    // Note, a quat that is all NaN for values is considered null
    private Quaternion[] getQuatArray() throws IOException {
        int length=myIn.readInt();
        if (length==0) return null;
        Quaternion[] array=new Quaternion[length];
        for (int i=0;i<length;i++){
            array[i]=new Quaternion(myIn.readFloat(),myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
            if (array[i].x==Float.NaN && array[i].y==Float.NaN && array[i].z==Float.NaN && array[i].w==Float.NaN) array[i]=null;
        }
        return array;
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
     * Adds a property .  Properties can tell this how to process the binary file.<br><br>
     * The only keys currently used are:<br>
     * key -> PropertyDataType<br>
     * "texurl" --> (URL) When loading a texture, will use this directory as the base texture directory <br>
     * "bound" --> "box","sphere"  "box" uses BoundingBoxes, "sphere" uses boundingspheres.  "sphere" is default
     *
     * @param key Key to add (For example "texdir")
     * @param property Property for that key to have (For example "c:\\blarg\\")
     */
    public void setProperty(String key, Object property) {
        properties.put(key,property);
    }

    /**
     * Removes a property.  This is equivalent to setProperty(key,null)
     * @param key The property to remove
     */
    public void clearProperty(String key){
        properties.remove(key);
    }
}
