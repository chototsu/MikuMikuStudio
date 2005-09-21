/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.model.XMLparser;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.logging.Level;

import com.jme.animation.SpatialTransformer;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.light.Light;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.lod.ClodMesh;
import com.jme.scene.lod.CollapseRecord;
import com.jme.scene.shape.Box;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jmex.model.JointMesh;
import com.jmex.model.animation.JointController;
import com.jmex.model.animation.KeyframeController;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.TerrainPage;


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

    private Hashtable repeatShare=new Hashtable();

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
        clearValues();
        myScene=storeNode;
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
        clearValues();
        return myScene;
    }

    private void clearValues() {
        repeatShare.clear();
        s.clear();
        shares.clear();
        attributes.clear();
        myIn=null;
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
        String tagName=myIn.readUTF().trim();
        if (DEBUG) System.out.println("Reading tagName:" + tagName);
        readInObjects(attributes);
        if (tagName.equals("scene")){
//            s.push(new Node("XML Scene"));    Already on stack
        } else if (tagName.equals("node")){
            s.push(processSpatial(new Node((String) attributes.get("name")),attributes));
        } else if (tagName.equals("terrainpage")){
            s.push(processTerrainPage(new TerrainPage((String) attributes.get("name")),attributes));
        } else if (tagName.equals("repeatobject")){
            s.push(repeatShare.get(attributes.get("ident")));
        } else if (tagName.equals("materialstate")){
            s.push(buildMaterial(attributes));
        } else if (tagName.equals("texturestate")){
            s.push(renderer.createTextureState());
        } else if (tagName.equals("texture")){
            Texture t=buildTexture(attributes);
            if (t!=null){
                TextureState ts=(TextureState) s.pop();
                Integer retrieveNumber=(Integer)attributes.get("texnum");
                int textureNum=(retrieveNumber==null ? 0 : retrieveNumber.intValue());
                ts.setTexture(t,textureNum);
                s.push(ts);
            }
        } else if (tagName.equals("clod")){
            s.push(processSpatial(new ClodMesh((String) attributes.get("name")),attributes));
        } else if (tagName.equals("obb")){
            s.push(processOBB(new OrientedBoundingBox(),attributes));
        } else if (tagName.equals("boundsphere")){
            s.push(processBSphere(new BoundingSphere(),attributes));
        } else if (tagName.equals("boundbox")){
            s.push(processBBox(new BoundingBox(),attributes));
        } else if (tagName.equals("terrainblock")){
            s.push(processTerrainBlock(new TerrainBlock((String) attributes.get("name")),attributes));
        } else if (tagName.equals("areaclod")){
            s.push(processAreaClod(new AreaClodMesh((String) attributes.get("name")),attributes));
        } else if (tagName.equals("clodrecords")){
            s.push(new CollapseRecord[((Integer)attributes.get("numrec")).intValue()]);
        } else if (tagName.equals("crecord")){
            writeCollapseRecord(attributes);
        } else if (tagName.equals("mesh")){
            s.push(processSpatial(new TriMesh((String) attributes.get("name")),attributes));
        } else if (tagName.equals("vertex")){
            Geometry geo=(Geometry) s.pop();
            if (attributes.get("q3vert")!=null)
                geo.setVertexBuffer(BufferUtils.createFloatBuffer(decodeShortCompress((short[])attributes.get("q3vert"))));
            else
                geo.setVertexBuffer(BufferUtils.createFloatBuffer((Vector3f[]) attributes.get("data")));
            s.push(geo);
        } else if (tagName.equals("normal")){
            Geometry geo=(Geometry) s.pop(); // FIXME: The reading/writing could skip the intermediate Vector3f[] array.
            if (attributes.get("q3norm")!=null)
                geo.setNormalBuffer(BufferUtils.createFloatBuffer(decodeLatLong((byte[])attributes.get("q3norm"))));
            else
                geo.setNormalBuffer(BufferUtils.createFloatBuffer((Vector3f[]) attributes.get("data")));
            s.push(geo);
        } else if (tagName.equals("texturecoords")){
            Geometry geo=(Geometry) s.pop();
            if (attributes.get("texindex")==null)
                geo.setTextureBuffer(BufferUtils.createFloatBuffer((Vector2f[]) attributes.get("data")));
            else
                geo.setTextureBuffer(BufferUtils.createFloatBuffer((Vector2f[]) attributes.get("data")),((Integer)attributes.get("texindex")).intValue());
            s.push(geo);
        } else if (tagName.equals("color")){
            Geometry geo=(Geometry) s.pop();
            geo.setColorBuffer((FloatBuffer) attributes.get("data"));
            s.push(geo);
        } else if (tagName.equals("defcolor")){
            Geometry geo=(Geometry) s.pop();
            geo.setDefaultColor((ColorRGBA) attributes.get("data"));
            s.push(geo);
        } else if (tagName.equals("index")){
            TriMesh m=(TriMesh) s.pop();
            m.setIndexBuffer(BufferUtils.createIntBuffer((int[]) attributes.get("data")));
            s.push(m);
        } else if (tagName.equals("origvertex")){
            JointMesh jm=(JointMesh) s.pop();
            jm.originalVertex=(Vector3f[]) attributes.get("data");
            s.push(jm);
        } else if (tagName.equals("orignormal")){
            JointMesh jm=(JointMesh) s.pop();
            jm.originalNormal=(Vector3f[]) attributes.get("data");
            s.push(jm);
        } else if (tagName.equals("jointindex")){
            JointMesh jm=(JointMesh) s.pop();
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
//            if (toAdd==null){
//                throw new JmeException("Unknown publicobject: " +shares.get(attributes.get("ident")));
//            }
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
            JointController jc=new JointController(((Integer)attributes.get("numJoints")).intValue());
            processController(jc,attributes);
            jc.FPS = ((Float)attributes.get("fps")).floatValue();
            s.push(jc);
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
//            jc.localRefMatrix[((Integer)attributes.get("index")).intValue()].set((Matrix3f) attributes.get("localrot"),(Vector3f) attributes.get("localvec"));
            jc.localRefMatrix[((Integer)attributes.get("index")).intValue()].setRotation((Matrix3f) attributes.get("localrot"));
            jc.localRefMatrix[((Integer)attributes.get("index")).intValue()].setTranslation((Vector3f) attributes.get("localvec"));
            s.push(jc);
            s.push(attributes.get("index"));
        } else if (tagName.equals("jointmesh")){
            s.push(processSpatial(new JointMesh((String) attributes.get("name")),attributes));
        } else if (tagName.equals("keyframecontroller")){
            KeyframeController kc=new KeyframeController();
            kc.setActive(true);
            TriMesh parentMesh=(TriMesh) s.pop();
            kc.setMorphingMesh(parentMesh);
            s.push(parentMesh);
            s.push(kc);
        } else if (tagName.equals("keyframepointintime")){
            s.push(attributes.get("time"));  // Store the current time on the stack
            s.push(new TriMesh());
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
        } else if (tagName.equals("cullstate")){
            s.push(buildCullState(attributes));
        } else if (tagName.equals("wirestate")){
            s.push(buildWireState(attributes));
        } else{
            throw new JmeException("Illegale Qualified name: '" + tagName + "'");
        }
        if (attributes.containsKey("sharedident")){
            Object temp=s.pop();
            repeatShare.put(attributes.get("sharedident"),temp);
            s.push(temp);
        }
        return;

    }

    private void processController(Controller jc, HashMap attributes) {
        if (attributes.containsKey("speed"))
            jc.setSpeed(((Float)attributes.get("speed")).floatValue());
        if (attributes.containsKey("rptype"))
            jc.setRepeatType(((Integer)attributes.get("rptype")).intValue());
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
        } else if (tagName.equals("node") || tagName.equals("terrainpage")){
            childNode=(Node) s.pop();
            parentNode=(Node) s.pop();
            parentNode.attachChild(childNode);
            s.push(parentNode);
        } else if (tagName.equals("repeatobject")){
            Object childObject=s.pop();
            if (childObject instanceof RenderState){
                parentSpatial=(Spatial) s.pop();
                parentSpatial.setRenderState((RenderState) childObject);
                s.push(parentSpatial);
            } else if (childObject instanceof Controller){
                parentSpatial=(Spatial) s.pop();
                parentSpatial.addController((Controller) childObject);
                s.push(parentSpatial);
            } else if (childObject instanceof Spatial){
                parentNode=(Node) s.pop();
                parentNode.attachChild((Spatial) childObject);
                s.push(parentNode);
            } else
                throw new IOException("Unknown child repeat object " + childObject.getClass());
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
        } else if (tagName.equals("texture")){
        } else if (tagName.equals("cullstate")){
            CullState childCull=(CullState) s.pop();
            parentSpatial=(Spatial)s.pop();
            parentSpatial.setRenderState(childCull);
            s.push(parentSpatial);
        }
        else if (tagName.equals("mesh") || tagName.equals("jointmesh")
                || tagName.equals("clod")|| tagName.equals("areaclod") ||tagName.equals("terrainblock")){
            Geometry childMesh=(Geometry) s.pop();
            if (childMesh.getModelBound()==null){
                if ("box".equals(properties.get("bound")))
                    childMesh.setModelBound(new BoundingBox());
                else if ("obb".equals(properties.get("bound")))
                    childMesh.setModelBound(new OrientedBoundingBox());
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
        } else if (tagName.equals("defcolor")){
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
            if (XMLShare.whatIReallyAm!=null) shares.put(XMLShare.myIdent,XMLShare.whatIReallyAm);
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
                if (parentNode.getChild(i) instanceof JointMesh)
                    jc.addJointMesh((JointMesh) parentNode.getChild(i));
            }
            jc.processController();
            if (jc.numJoints!=0) parentNode.addController(jc);
            s.push(parentNode);
        } else if (tagName.equals("joint")){
            s.pop();    // remove unneeded information tag
        } else if (tagName.equals("obb") || tagName.equals("boundsphere") || tagName.equals("boundbox")){
            BoundingVolume bv=(BoundingVolume) s.pop();
            Geometry parentGeo=(Geometry) s.pop();
            parentGeo.setModelBound(bv);
            s.push(parentGeo);
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
        } else if (tagName.equals("clodrecords")){
            CollapseRecord[] toPut=(CollapseRecord[]) s.pop();
            ClodMesh parentClod=(ClodMesh) s.pop();
            parentClod.create(toPut);
            s.push(parentClod);
        } else if (tagName.equals("wirestate")){
            WireframeState ws=(WireframeState) s.pop();
            parentSpatial=(Spatial) s.pop();
            parentSpatial.setRenderState(ws);
            s.push(parentSpatial);
        } else if (tagName.equals("crecord") || tagName.equals("sptscale") || tagName.equals("sptrot") || tagName.equals("spttrans")){ // nothing to do at these ends

        } else {
            throw new JmeException("Illegale Qualified name: " + tagName);
        }
    }

    private OrientedBoundingBox processOBB(OrientedBoundingBox obb, HashMap attributes) {
        obb.setCenter((Vector3f) attributes.get("center"));
        obb.setXAxis((Vector3f) attributes.get("xaxis"));
        obb.setYAxis((Vector3f) attributes.get("yaxis"));
        obb.setZAxis((Vector3f) attributes.get("zaxis"));
        obb.setExtent((Vector3f) attributes.get("extent"));
        return obb;
    }

    private BoundingSphere processBSphere(BoundingSphere v, HashMap attributes) {
        v.setCenter((Vector3f) attributes.get("center"));
        v.setRadius(((Float)attributes.get("radius")).floatValue());
        return v;
    }

    private BoundingBox processBBox(BoundingBox v, HashMap attributes) {
        v.setCenter((Vector3f) attributes.get("nowcent"));
        Vector3f ext=(Vector3f) attributes.get("nowext");
        v.xExtent=ext.x;
        v.yExtent=ext.y;
        v.zExtent=ext.z;
        return v;
    }

    private void writeCollapseRecord(HashMap attributes) {
        CollapseRecord temp=new CollapseRecord();
        temp.indices=(int[]) attributes.get("indexary");
        temp.numbIndices=((Integer)attributes.get("numi")).intValue();
        temp.numbTriangles=((Integer)attributes.get("numt")).intValue();
        temp.numbVerts=((Integer)attributes.get("numv")).intValue();
        temp.vertToKeep=((Integer)attributes.get("vkeep")).intValue();
        temp.vertToThrow=((Integer)attributes.get("vthrow")).intValue();
        CollapseRecord[] toPut=(CollapseRecord[]) s.pop();
        toPut[((Integer)attributes.get("index")).intValue()]=temp;
        s.push(toPut);
    }

    private TerrainPage processTerrainPage(TerrainPage terrainPage, HashMap attributes) {
        processSpatial(terrainPage,attributes);
        terrainPage.setOffset((Vector2f) attributes.get("offset"));
        terrainPage.setTotalSize(((Integer)attributes.get("totsize")).intValue());
        terrainPage.setSize(((Integer)attributes.get("size")).intValue());
        terrainPage.setStepScale((Vector3f) attributes.get("stepscale"));
        terrainPage.setOffsetAmount(((Integer)attributes.get("offamnt")).intValue());
        return terrainPage;
    }

    private TerrainBlock processTerrainBlock(TerrainBlock terrainBlock, HashMap attributes) {
        processAreaClod(terrainBlock,attributes);
        terrainBlock.setSize(((Integer)attributes.get("tbsize")).intValue());
        terrainBlock.setTotalSize(((Integer)attributes.get("totsize")).intValue());
        terrainBlock.setStepScale((Vector3f)attributes.get("step"));
        terrainBlock.setUseClod(((Boolean)attributes.get("isclod")).booleanValue());
        terrainBlock.setOffset((Vector2f) attributes.get("offset"));
        terrainBlock.setOffsetAmount(((Integer)attributes.get("offamnt")).intValue());
        terrainBlock.setHeightMap((int[]) attributes.get("hmap"));
        return terrainBlock;
    }

    private AreaClodMesh processAreaClod(AreaClodMesh areaClodMesh, HashMap attributes) {
        processSpatial(areaClodMesh,attributes);
        areaClodMesh.setDistanceTolerance(((Float)attributes.get("disttol")).floatValue());
        areaClodMesh.setTrisPerPixel(((Float)attributes.get("trisppix")).floatValue());
        return areaClodMesh;
    }

    private Vector3f[] decodeShortCompress(short[] shorts) throws IOException {
        if (shorts.length%3!=0)
            throw new IOException("Illeagle short[] length of " + shorts.length);
        Vector3f[] toReturn=new Vector3f[shorts.length/3];
        for (int i=0;i<toReturn.length;i++){
            toReturn[i]=new Vector3f();
            toReturn[i].x = shorts[i*3+0]*BinaryFormatConstants.XYZ_SCALE;
            toReturn[i].y = shorts[i*3+1]*BinaryFormatConstants.XYZ_SCALE;
            toReturn[i].z = shorts[i*3+2]*BinaryFormatConstants.XYZ_SCALE;
        }
        return toReturn;
    }

    private Vector3f[] decodeLatLong(byte[] bytes) throws IOException {
        if (bytes==null) return null;
        if (bytes.length%2!=0){
            throw new IOException("Illeagle bytes[] length of " + bytes.length);
        }
        Vector3f[] vecs=new Vector3f[bytes.length/2];
        for (int i=0;i<bytes.length;i+=2){
            vecs[i/2]=new Vector3f();
            byte lng=bytes[i];
            byte lat=bytes[i+1];
            float newlat=FastMath.DEG_TO_RAD*lat;
            float newlng=FastMath.DEG_TO_RAD*lng;
            vecs[i/2].x = FastMath.cos(newlat)*FastMath.sin(newlng);
            vecs[i/2].y = FastMath.sin(newlat)*FastMath.sin(newlng);
            vecs[i/2].z = FastMath.cos(newlng);
        }
        return vecs;
    }

    private Object buildCullState(HashMap attributes) {
        CullState cs=renderer.createCullState();
        cs.setEnabled(true);
        String state=(String) attributes.get("cull");
        if ("none".equals(state))
            cs.setCullMode(CullState.CS_NONE);
        else if ("back".equals(state))
            cs.setCullMode(CullState.CS_BACK);
        else if ("front".equals(state))
            cs.setCullMode(CullState.CS_FRONT);
        return cs;
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

    private WireframeState buildWireState(HashMap attributes) {
        WireframeState ws=renderer.createWireframeState();
        ws.setFace(((Integer)attributes.get("facetype")).intValue());
        ws.setLineWidth(((Float)attributes.get("width")).floatValue());
        ws.setEnabled(true);
        return ws;
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
        LightState ls=renderer.createLightState();
        ls.setEnabled(true);
        return ls;
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
    private Texture buildTexture(HashMap atts){
        Texture p=null;
        int mipMap = Texture.MM_LINEAR;
        int filter = Texture.FM_LINEAR;
        int imageType = TextureManager.COMPRESS_BY_DEFAULT ? Image.GUESS_FORMAT : Image.GUESS_FORMAT_NO_S3TC;
        float aniso = 1.0f;
        boolean flip = true;
        
        if (properties.containsKey("tex_mm"))
            mipMap = ((Integer)properties.get("tex_mm")).intValue();
        if (properties.containsKey("tex_fm"))
            filter = ((Integer)properties.get("tex_fm")).intValue();
        if (properties.containsKey("tex_type"))
            imageType = ((Integer)properties.get("tex_type")).intValue();
        if (properties.containsKey("tex_aniso"))
            aniso = ((Float)properties.get("tex_aniso")).floatValue();
        if (properties.containsKey("tex_flip"))
            flip = ((Boolean)properties.get("tex_flip")).booleanValue();
        
        try {
            if (atts.get("URL")!=null && !atts.get("URL").equals("null")){
                p=TextureManager.loadTexture(
                        (URL) atts.get("URL"),
                        mipMap,
                        filter,
                        imageType,
                        aniso,
                        flip);
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
                        mipMap,
                        filter,
                        imageType,
                        aniso,
                        flip);
                if (p==null) {
                    return p;
                } else{
                    p.setImageLocation("file:/"+atts.get("file"));
                }
            }
            if (p==null)
                LoggingSystem.getLogger().log(Level.INFO,"Unable to load file: " + atts.get("file"));
            else{
//                t.setTexture(p);
                if (atts.get("wrap")!=null)
                    p.setWrap(((Integer)atts.get("wrap")).intValue());
            }
        } catch (MalformedURLException e) {
            throw new JmeException("Bad file name: " + atts.get("file") + "*" + atts.get("URL"));
        }
//        t.setEnabled(true);
//        return t;
        return p;
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
        MaterialState m=renderer.createMaterialState();
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
                    atribMap.put(name,getColorBuffer());
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
                    if (DEBUG) System.out.println("readvec:"+atribMap.get(name));
                    break;
                case BinaryFormatConstants.DATA_V2F:
                    atribMap.put(name,getVec2f());
                    if (DEBUG) System.out.println("readvec2f:"+atribMap.get(name));
                    break;
                case BinaryFormatConstants.DATA_QUAT:
                    atribMap.put(name,getQuat());
                    if (DEBUG) System.out.println("readquat:"+atribMap.get(name));
                    break;
                case BinaryFormatConstants.DATA_FLOAT:
                    atribMap.put(name,new Float(myIn.readFloat()));
                    if (DEBUG) System.out.println("readfloat:"+atribMap.get(name));
                    break;
                case BinaryFormatConstants.DATA_COLOR:
                    atribMap.put(name,getColor());
                    if (DEBUG) System.out.println("readcolor:"+atribMap.get(name));
                    break;
                case BinaryFormatConstants.DATA_URL:
                    atribMap.put(name,new URL(myIn.readUTF()));
                    break;
                case BinaryFormatConstants.DATA_INT:
                    atribMap.put(name,new Integer(myIn.readInt()));
                    if (DEBUG) System.out.println("readint:"+atribMap.get(name));
                    break;
                case BinaryFormatConstants.DATA_BOOLEAN:
                    atribMap.put(name,new Boolean(myIn.readBoolean()));
                    break;
                case BinaryFormatConstants.DATA_QUATARRAY:
                    atribMap.put(name,getQuatArray());
                    break;
                case BinaryFormatConstants.DATA_BYTEARRAY:
                    atribMap.put(name,getByteArray());
                    break;
                case BinaryFormatConstants.DATA_SHORTARRAY:
                    atribMap.put(name,getShortArray());
                    break;
                case BinaryFormatConstants.DATA_MATRIX3:
                    atribMap.put(name,getMat3());
                    break;
                default:
                    throw new IOException("Unknown data type:" + type);
            }
        }
    }

    private Matrix3f getMat3() throws IOException {
        Matrix3f m=new Matrix3f();
        m.m00=myIn.readFloat();
        m.m01=myIn.readFloat();
        m.m02=myIn.readFloat();
        m.m10=myIn.readFloat();
        m.m11=myIn.readFloat();
        m.m12=myIn.readFloat();
        m.m20=myIn.readFloat();
        m.m21=myIn.readFloat();
        m.m22=myIn.readFloat();
        return m;
    }

    private short[] getShortArray() throws IOException {
        int length=myIn.readInt();
        if (length==0) return null;
        short[] array=new short[length];
        for (int i=0;i<length;i++)
            array[i]=myIn.readShort();
        return array;
    }

    private byte[] getByteArray() throws IOException {
        int length=myIn.readInt();
        if (length==0) return null;
        byte[] array=new byte[length];
        for (int i=0;i<length;i++)
            array[i]=myIn.readByte();
        return array;
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

    private Vector2f getVec2f() throws IOException{
        return new Vector2f(myIn.readFloat(),myIn.readFloat());
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
            if (array[i].x==Float.NaN && array[i].y==Float.NaN) array[i]=null;
        }
        return array;
    }

    private FloatBuffer getColorBuffer() throws IOException {
        int length=myIn.readInt();
        if (length==0) return null;
        FloatBuffer buff = BufferUtils.createColorBuffer(length);
        for (int i=0;i<length;i++)
            buff.put(myIn.readFloat()).put(myIn.readFloat()).put(myIn.readFloat()).put(myIn.readFloat());
        return buff;
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
     * "bound" --> "box","sphere","obb" ; Type of bounding Volume.  "sphere" is default
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
