package com.jme.scene.model.XMLparser;

import com.jme.scene.*;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.animation.VertexKeyframeController;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.util.*;
import java.net.URL;


/**
 * Started Date: Jun 25, 2004<br><br>
 *
 * This class converts a scenegraph to jME binary format
 *
 * @author Jack Lindamood
 */
public class JmeBinaryWriter {
    DataOutputStream myOut;
    private static final Quaternion DEFAULT_ROTATION=new Quaternion();
    private static final Vector3f DEFAULT_TRANSLATION = new Vector3f();
    private final static boolean DEBUG=false;
    private static final Vector3f DEFAULT_SCALE = new Vector3f(1,1,1);

    public JmeBinaryWriter(){

    }

    /**
     * Converts a given node to jME's binary format
     * @param scene The node to save
     * @param bin The OutputStream that will store the binary format
     * @throws IOException If anything wierd happens.
     */
    public void writeScene(Node scene,OutputStream bin) throws IOException {
        myOut=new DataOutputStream(bin);
        writeHeader();
        writeNode(scene);
        writeClosing();
        myOut.close();
    }

    /**
     * Converts a given Geometry to jME's binary format
     * @param geo The Geometry to save
     * @param bin The OutputStream that will store the binary format
     * @throws IOException If anything wierd happens.
     */
    public void writeScene(Geometry geo,OutputStream bin) throws IOException {
        myOut=new DataOutputStream(bin);
        writeHeader();
        writeSpatial(geo);
        writeClosing();
        myOut.close();
    }


    private void writeNode(Node node) throws IOException {
        HashMap atts=new HashMap();
        atts.clear();
        putSpatialAtts(node,atts);
        writeTag("node",atts);
        writeChildren(node);
        writeSpatialChildren(node);
        writeEndTag("node");
    }

    private void writeChildren(Node node) throws IOException {
        for (int i=0;i<node.getQuantity();i++)
            writeSpatial(node.getChild(i));
    }

    private void writeSpatial(Spatial s) throws IOException {
        if (s instanceof XMLloadable)
            writeXMLloadable((XMLloadable)s);
        else if (s instanceof JointMesh)
            writeJointMesh((JointMesh)s);
        else if (s instanceof Node)
            writeNode((Node) s);
        else if (s instanceof TriMesh)
            writeMesh((TriMesh)s);
    }

    private void writeMesh(TriMesh triMesh) throws IOException {
        HashMap atts=new HashMap();
        atts.clear();
        putSpatialAtts(triMesh,atts);
        writeTag("mesh",atts);
        writeTriMeshTags(triMesh);
        writeSpatialChildren(triMesh);
        writeEndTag("mesh");
    }

    private void writeJointMesh(JointMesh jointMesh) throws IOException {
        HashMap atts=new HashMap();
        atts.clear();
        putSpatialAtts(jointMesh,atts);
        writeTag("jointmesh",atts);
        writeJointMeshTags(jointMesh);
        writeSpatialChildren(jointMesh);
        writeEndTag("jointmesh");
    }

    private void writeJointMeshTags(JointMesh jointMesh) throws IOException {
        HashMap atts=new HashMap();
        atts.clear();
        atts.put("data",jointMesh.jointIndex);
        writeTag("jointindex",atts);
        writeEndTag("jointindex");

        atts.clear();
        atts.put("data",jointMesh.originalVertex);
        writeTag("origvertex",atts);
        writeEndTag("origvertex");

        atts.clear();
        atts.put("data",jointMesh.originalNormal);
        writeTag("orignormal",atts);
        writeEndTag("orignormal");
        writeTriMeshTags(jointMesh);
    }

    private void writeXMLloadable(XMLloadable xmlloadable) throws IOException {
        HashMap atts=new HashMap();
        atts.put("class",xmlloadable.getClass().getName());
        if (xmlloadable instanceof Spatial)
            putSpatialAtts((Spatial) xmlloadable,atts);
        atts.put("args",xmlloadable.writeToXML());
        writeTag("xmlloadable",atts);
        if (xmlloadable instanceof Spatial)
            writeSpatialChildren((Spatial) xmlloadable);
        if (xmlloadable instanceof Node)
            writeChildren((Node) xmlloadable);
        writeEndTag("xmlloadable");
    }

    private void writeSpatialChildren(Spatial spatial) throws IOException {
        writeRenderStates(spatial);
        writeControllers(spatial);
    }

    private void writeControllers(Spatial spatial) throws IOException {
        ArrayList conts=spatial.getControllers();
        if (conts==null) return;
        for (int i=0;i<conts.size();i++){
            Controller r=(Controller) conts.get(i);
            if (r instanceof JointController){
                writeJointController((JointController)r);
            } else if (r instanceof VertexKeyframeController){

            } else if (r instanceof KeyframeController){
                writeKeyframeController((KeyframeController)r);
            }
        }
    }

    private void writeKeyframeController(KeyframeController kc) throws IOException {
        // Assume that morphMesh is keyframeController's parent
        writeTag("keyframecontroller",null);
        ArrayList keyframes=kc.keyframes;
        for (int i=0;i<keyframes.size();i++)
            writeKeyFramePointInTime((KeyframeController.PointInTime)keyframes.get(i));

        writeEndTag("keyframecontroller");
    }

    private void writeKeyFramePointInTime(KeyframeController.PointInTime pointInTime) throws IOException {
        HashMap atts=new HashMap();
        atts.clear();
        atts.put("time",new Float(pointInTime.time));
        writeTag("keyframepointintime",atts);
        writeTriMeshTags(pointInTime.newShape);
        writeEndTag("keyframepointintime");
    }

    private void writeTriMeshTags(TriMesh triMesh) throws IOException {
        if (triMesh==null) return;
        HashMap atts=new HashMap();
        atts.clear();
        if (triMesh.getVertices()!=null)
            atts.put("data",triMesh.getVertices());
        writeTag("vertex",atts);
        writeEndTag("vertex");

        atts.clear();
        if (triMesh.getNormals()!=null)
            atts.put("data",triMesh.getNormals());
        writeTag("normal",atts);
        writeEndTag("normal");

        atts.clear();
        if (triMesh.getColors()!=null)
            atts.put("data",triMesh.getColors());
        writeTag("color",atts);
        writeEndTag("color");

        atts.clear();
        if (triMesh.getTextures()!=null)
            atts.put("data",triMesh.getTextures());
        writeTag("texturecoords",atts);
        writeEndTag("texturecoords");

        atts.clear();
        if (triMesh.getIndices()!=null)
            atts.put("data",triMesh.getIndices());
        writeTag("index",atts);
        writeEndTag("index");
    }

    private void writeJointController(JointController jc) throws IOException{
        HashMap atts=new HashMap();
        atts.clear();
        atts.put("numJoints",new Integer(jc.numJoints));
        writeTag("jointcontroller",atts);
        Object[] o=jc.movementInfo.toArray();
        Vector3f tempV=new Vector3f();
        Quaternion tempQ=new Quaternion();
        for (int j=0;j<jc.numJoints;j++){
            atts.clear();
            atts.put("index",new Integer(j));
            atts.put("parentindex",new Integer(jc.parentIndex[j]));
            jc.localRefMatrix[j].getRotation(tempQ);
            jc.localRefMatrix[j].getTranslation(tempV);
            atts.put("localrot",tempQ);
            atts.put("localvec",tempV);

            writeTag("joint",atts);
            for (int i=0;i<o.length;i++){
                JointController.PointInTime jp=(JointController.PointInTime) o[i];
                if (jp.usedTrans.get(j) || jp.usedRot.get(j)){
                    atts.clear();
                    atts.put("time",new Float(jp.time));
                    if (jp.usedTrans.get(j))
                        atts.put("trans",jp.jointTranslation[j]);

                    if (jp.usedRot.get(j))
                        atts.put("rot",jp.jointRotation[j]);
                    writeTag("keyframe",atts);
                    writeEndTag("keyframe");
                }
            }
            writeEndTag("joint");
        }
        writeEndTag("jointcontroller");
    }

    private void writeRenderStates(Spatial spatial) throws IOException {
        RenderState[] states=spatial.getRenderStateList();
        if (states==null) return;
        if (states[RenderState.RS_MATERIAL]!=null){
            writeMaterialState((MaterialState) states[RenderState.RS_MATERIAL]);
        }
        if (states[RenderState.RS_TEXTURE]!=null){
            writeTextureState((TextureState)states[RenderState.RS_TEXTURE]);
        }
    }

    private void writeTextureState(TextureState state) throws IOException{
        if (state.getTexture()==null) return;
        String s=state.getTexture().getImageLocation();
        HashMap atts=new HashMap();
        atts.clear();
        if ("file:/".equals(s.substring(0,6)))
            atts.put("file",replaceSpecialsForFile(new StringBuffer(s.substring(6))).toString());
        else
            atts.put("URL",new URL(s));
        writeTag("texturestate",atts);
        writeEndTag("texturestate");
    }

    private static StringBuffer replaceSpecialsForFile(StringBuffer s) {
        int i=s.indexOf("%20");
        if (i==-1) return s; else return replaceSpecialsForFile(s.replace(i,i+3," "));
    }


    private void writeMaterialState(MaterialState state) throws IOException {
        if (state==null) return;
        HashMap atts=new HashMap();
        atts.clear();
        atts.put("emissive",state.getEmissive());
        atts.put("ambient",state.getAmbient());
        atts.put("diffuse",state.getDiffuse());
        atts.put("specular",state.getSpecular());
        atts.put("alpha",new Float(state.getAlpha()));
        atts.put("shiny",new Float(state.getShininess()));
        writeTag("materialstate",atts);
        writeEndTag("materialstate");
    }

    private void writeEndTag(String name) throws IOException{
        if (DEBUG) System.out.println("Writting end tag for *" + name + "*");
        myOut.writeByte(BinaryFormatConstants.END_TAG);
        myOut.writeUTF(name);
    }

    private void writeTag(String name, HashMap atts) throws IOException {
        if (DEBUG) System.out.println("Writting begining tag for *" + name + "*");
        myOut.writeByte(BinaryFormatConstants.BEGIN_TAG);
        myOut.writeUTF(name);
        if (atts==null){    // no attributes
            myOut.writeByte(0);
            return;
        }
        myOut.writeByte(atts.size());

        Iterator i=atts.keySet().iterator();
        while (i.hasNext()){
            String attName=(String) i.next();
            Object attrib=atts.get(attName);
            myOut.writeUTF(attName);
            if (attrib instanceof Vector3f[])
                writeVec3fArray((Vector3f[]) attrib);
            else if (attrib instanceof Vector2f[])
                writeVec2fArray((Vector2f[]) attrib);
            else if (attrib instanceof ColorRGBA[])
                writeColorArray((ColorRGBA[]) attrib);
            else if (attrib instanceof String)
                writeString((String) attrib);
            else if (attrib instanceof int[])
                writeIntArray((int[]) attrib);
            else if (attrib instanceof Vector3f)
                writeVec3f((Vector3f) attrib);
            else if (attrib instanceof Quaternion)
                writeQuat((Quaternion) attrib);
            else if (attrib instanceof Float)
                writeFloat((Float) attrib);
            else if (attrib instanceof ColorRGBA)
                writeColor((ColorRGBA) attrib);
            else if (attrib instanceof URL)
                writeURL((URL) attrib);
            else if (attrib instanceof Integer)
                writeInt((Integer) attrib);
            else{
                throw new IOException("unknown class type for " + attrib + " of " + attrib.getClass());
            }
            i.remove();
        }
    }

    private void writeInt(Integer i) throws IOException {
        myOut.writeByte(BinaryFormatConstants.DATA_INT);
        myOut.writeInt(i.intValue());
    }

    private void writeURL(URL url) throws IOException {
        myOut.writeByte(BinaryFormatConstants.DATA_URL);
        myOut.writeUTF(url.toString());
    }

    private void writeColor(ColorRGBA c) throws IOException {
        myOut.writeByte(BinaryFormatConstants.DATA_COLOR);
        myOut.writeFloat(c.r);
        myOut.writeFloat(c.g);
        myOut.writeFloat(c.b);
        myOut.writeFloat(c.a);
    }

    private void writeFloat(Float f) throws IOException {
        myOut.writeByte(BinaryFormatConstants.DATA_FLOAT);
        myOut.writeFloat(f.floatValue());
    }

    private void writeQuat(Quaternion q) throws IOException {
        myOut.writeByte(BinaryFormatConstants.DATA_QUAT);
        myOut.writeFloat(q.x);
        myOut.writeFloat(q.y);
        myOut.writeFloat(q.z);
        myOut.writeFloat(q.w);
    }

    private void writeVec3f(Vector3f v) throws IOException {
        myOut.writeByte(BinaryFormatConstants.DATA_V3F);
        myOut.writeFloat(v.x);
        myOut.writeFloat(v.y);
        myOut.writeFloat(v.z);
    }

    private void writeIntArray(int[] array) throws IOException {
        myOut.writeByte(BinaryFormatConstants.DATA_INTARRAY);
        myOut.writeShort(array.length);
        for (int i=0;i<array.length;i++)
            myOut.writeInt(array[i]);
    }

    private void writeString(String s) throws IOException {
        myOut.writeByte(BinaryFormatConstants.DATA_STRING);
        myOut.writeUTF(s);
    }

    private void writeColorArray(ColorRGBA[] array) throws IOException {
        myOut.writeByte(BinaryFormatConstants.DATA_COLORARRAY);
        myOut.writeShort(array.length);
        for (int i=0;i<array.length;i++){
            myOut.writeFloat(array[i].r);
            myOut.writeFloat(array[i].g);
            myOut.writeFloat(array[i].b);
            myOut.writeFloat(array[i].a);
        }
    }

    private void writeVec2fArray(Vector2f[] array) throws IOException {
        myOut.writeByte(BinaryFormatConstants.DATA_V2FARRAY);
        myOut.writeShort(array.length);
        for (int i=0;i<array.length;i++){
            myOut.writeFloat(array[i].x);
            myOut.writeFloat(array[i].y);
        }
    }

    private void writeVec3fArray(Vector3f[] array) throws IOException {
        myOut.writeByte(BinaryFormatConstants.DATA_V3FARRAY);
        myOut.writeShort(array.length);
        for (int i=0;i<array.length;i++){
            myOut.writeFloat(array[i].x);
            myOut.writeFloat(array[i].y);
            myOut.writeFloat(array[i].z);
        }
    }

    private void putSpatialAtts(Spatial spatial, HashMap atts) {
        atts.put("name",spatial.getName());
        if (!spatial.getLocalScale().equals(DEFAULT_SCALE))
            atts.put("scale",spatial.getLocalScale());
        if (!spatial.getLocalRotation().equals(DEFAULT_ROTATION))
            atts.put("rotation",spatial.getLocalRotation());
        if (!spatial.getLocalTranslation().equals(DEFAULT_TRANSLATION))
            atts.put("translation",spatial.getLocalTranslation());
    }

    private void writeClosing() throws IOException {
        writeEndTag("scene");
        if (DEBUG) System.out.println("Writting file close");
        myOut.writeByte(BinaryFormatConstants.END_FILE);
    }

    private void writeHeader() throws IOException {
        if (DEBUG) System.out.println("Writting file begin");
        myOut.writeLong(BinaryFormatConstants.BEGIN_FILE);
        writeTag("scene",null);
    }
}