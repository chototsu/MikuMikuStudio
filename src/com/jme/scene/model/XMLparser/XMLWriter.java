package com.jme.scene.model.XMLparser;

import com.jme.scene.*;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.animation.VertexKeyframeController;

import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Started Date: Jun 5, 2004
 *
 * Class to write a node to a Writer in XML format
 *
 * @author Jack Lindamood
 */
public class XMLWriter {
    private Writer myStream;
    private final static Vector3f defaultTranslation=new Vector3f(0,0,0);
    private final static Quaternion defaultRotation=new Quaternion(0,0,0,1);
    private final static Vector3f defaultScale=new Vector3f(1,1,1);
    private StringBuffer tabs=new StringBuffer();
    private StringBuffer currentLine=new StringBuffer();

    /**
     * Creates a new XMLWriter that will write a node's contents to the given Writer
     * @param o Writer to write the XML to.
     */
    public XMLWriter(Writer o){
        myStream=o;
    }

    /**
     * Sets a new Writer to write a given node's contents to.
     * @param o New Writer
     */
    public void setNewStream(Writer o){
        myStream=o;
    }

    /**
     * Writes a node to the current Writer in XML format.  A .close() is called on the
     * stream after the Node is written.
     * @param toWrite The node to write
     * @throws IOException If an exception happens during Node writting
     */
    public void writeScene(Node toWrite) throws IOException {
        writeHeader();
        increaseTabSize();
        writeNode(toWrite);
        decreaseTabSize();
        writeClosing();
        myStream.close();
    }

    /**
     * Writes a Geometry to the current Writer in XML format.  A .close() is called on the
     * stream after the Geometry is written.
     * @param toWrite The node to write
     * @throws IOException If an exception happens during Node writting
     */
    public void writeScene(Geometry toWrite) throws IOException {
        writeHeader();
        increaseTabSize();
        writeSpatial(toWrite);
        decreaseTabSize();
        writeClosing();
        myStream.close();
    }

    private void writeClosing() throws IOException {
        currentLine.append("</scene>");
        writeLine();
    }

    private void writeNode(Node toWrite) throws IOException {
        currentLine.append("<node ").append(getSpatialHeader(toWrite)).append(">");
        writeLine();
        increaseTabSize();

        writeChildren(toWrite);
        processSpatial(toWrite);
        decreaseTabSize();
        currentLine.append("</node>");
        writeLine();
    }


    private void processSpatial(Spatial s) throws IOException {
        writeRenderStates(s);
        writeControllers(s);
    }

    private void writeControllers(Spatial s) throws IOException {
        ArrayList conts=s.getControllers();
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

    private void writeKeyframeController(KeyframeController keyframeController) throws IOException {
        // Assume that morphMesh is keyframeController's parent
        currentLine.append("<keyframecontroller>");
        writeLine();
        increaseTabSize();
        ArrayList keyframes=keyframeController.keyframes;
        for (int i=0;i<keyframes.size();i++){
            writeKeyFramePointInTime((KeyframeController.PointInTime)keyframes.get(i));
        }
        decreaseTabSize();
        currentLine.append("</keyframecontroller>");
        writeLine();
    }

    private void writeKeyFramePointInTime(KeyframeController.PointInTime pointInTime) throws IOException {
        currentLine.append("<keyframepointintime time=\"").append(pointInTime.time).append("\" >");
        writeLine();
        increaseTabSize();
        writeTriMeshTags(pointInTime.newShape);
        decreaseTabSize();
        currentLine.append("</keyframepointintime>");
        writeLine();
    }

    private void writeJointController(JointController jc) throws IOException {
        currentLine.append("<jointcontroller numJoints=\"").append(jc.numJoints).append("\" >");
        writeLine();
        increaseTabSize();
        Object[] o=jc.movementInfo.toArray();
        Vector3f tempV=new Vector3f();
        Quaternion tempQ=new Quaternion();
        for (int j=0;j<jc.numJoints;j++){
            currentLine.append("<joint index=\"").append(j).append("\" parentindex=\"").append(jc.parentIndex[j]).append("\" ");
            jc.localRefMatrix[j].getRotation(tempQ);
            jc.localRefMatrix[j].getTranslation(tempV);
            currentLine.append("localrot=\"");
            currentLine.append(tempQ.x).append(' ').append(tempQ.y).append(' ').append(tempQ.z).append(' ');
            currentLine.append(tempQ.w).append("\" ");
            currentLine.append("localvec=\"");
            currentLine.append(tempV.x).append(' ').append(tempV.y).append(' ').append(tempV.z).append("\">");
            writeLine();
            increaseTabSize();
            for (int i=0;i<o.length;i++){
                JointController.PointInTime jp=(JointController.PointInTime) o[i];
                if (jp.usedTrans.get(j) || jp.usedRot.get(j)){
                    currentLine.append("<keyframe time=\"").append(jp.time).append("\"");
                    if (jp.usedTrans.get(j)){
                        currentLine.append(" trans=\"");
                        tempV.set(jp.jointTranslation[j]);
                        currentLine.append(tempV.x).append(' ').append(tempV.y).append(' ').append(tempV.z).append("\"");
                    }
                    if (jp.usedRot.get(j)){
                        currentLine.append(" rot=\"");
                        tempQ.set(jp.jointRotation[j]);
                        currentLine.append(tempQ.x).append(' ').append(tempQ.y).append(' ').append(tempQ.z).append(' ');
                        currentLine.append(tempQ.w).append("\"");
                    }
                    currentLine.append("/>");
                    writeLine();
                }
            }
            decreaseTabSize();
            currentLine.append("</joint>");
            writeLine();
        }
        decreaseTabSize();
        currentLine.append("</jointcontroller>");
        writeLine();

    }

    private void writeXMLloadable(XMLloadable xmlloadable) throws IOException {
        currentLine.append("<xmlloadable ");
        currentLine.append("class=\"").append(xmlloadable.getClass().getName()).append("\" ");
        if (xmlloadable instanceof Spatial){
            currentLine.append(getSpatialHeader((Spatial) xmlloadable));
        }
        currentLine.append("args=\"").append(xmlloadable.writeToXML()).append("\">");
        writeLine();
        increaseTabSize();
        if (xmlloadable instanceof Spatial)
            writeRenderStates((Spatial) xmlloadable);
        decreaseTabSize();
        increaseTabSize();
        if (xmlloadable instanceof Node)
            writeChildren((Node)xmlloadable);
        decreaseTabSize();
        currentLine.append("</xmlloadable>");
        writeLine();
    }

    private void writeChildren(Node node) throws IOException {
        for (int i=0;i<node.getQuantity();i++){
            writeSpatial(node.getChild(i));
        }
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

    private void writeJointMesh(JointMesh jointMesh) throws IOException {
        currentLine.append("<jointmesh ").append(getSpatialHeader(jointMesh)).append(">");
        writeLine();
        increaseTabSize();
        writeJointMeshTags(jointMesh);
        processSpatial(jointMesh);
        decreaseTabSize();
        currentLine.append("</jointmesh>");
        writeLine();
    }

    private void writeJointMeshTags(JointMesh jointMesh) throws IOException {
        currentLine.append("<jointindex data=\"");
//        writeLine();
//        increaseTabSize();
        int[] jointInfo=jointMesh.jointIndex;
        writeIntArray(jointInfo);
        if (currentLine.length()!=0) writeLine();
//        decreaseTabSize();
        currentLine.append("\" />");
        writeLine();

        currentLine.append("<origvertex data=\"");
//        writeLine();
//        increaseTabSize();
        writeVec3fArray(jointMesh.originalVertex);
        if (currentLine.length()!=0) writeLine();
//        decreaseTabSize();
        currentLine.append("\" />");
        writeLine();

        currentLine.append("<orignormal data=\"");
//        writeLine();
//        increaseTabSize();
        writeVec3fArray(jointMesh.originalNormal);
        if (currentLine.length()!=0) writeLine();
//        decreaseTabSize();
        currentLine.append("\" />");
        writeLine();
        writeTriMeshTags(jointMesh);
    }

    private void writeTriMeshTags(TriMesh toWrite) throws IOException {
        currentLine.append("<vertex data=\"");
//        writeLine();
        Vector3f[] theVerts=toWrite.getVertices();
//        increaseTabSize();
        if (theVerts!=null)
            writeVec3fArray(theVerts);
        if (currentLine.length()!=0) writeLine();
//        decreaseTabSize();
        currentLine.append("\" />");
        writeLine();

        currentLine.append("<normal data=\"");
//        writeLine();
        Vector3f[] theNorms=toWrite.getNormals();
//        increaseTabSize();
        if (theNorms!=null)
            writeVec3fArray(theNorms);
        if (currentLine.length()!=0) writeLine();
//        decreaseTabSize();
        currentLine.append("\" />");
        writeLine();

        currentLine.append("<color data=\"");
//        writeLine();
        ColorRGBA[] theColors=toWrite.getColors();
//        increaseTabSize();
        if (theColors!=null)
            writeColorRGBAArray(theColors);
        if (currentLine.length()!=0) writeLine();
//        decreaseTabSize();
        currentLine.append("\" />");
        writeLine();

        currentLine.append("<texturecoords data=\"");
//        writeLine();
        Vector2f[] theTexCoords=toWrite.getTextures();
//        increaseTabSize();
        if (theTexCoords!=null)
            writeVec2fArray(theTexCoords);
        if (currentLine.length()!=0) writeLine();
//        decreaseTabSize();
        currentLine.append("\" />");
        writeLine();

        currentLine.append("<index data=\"");
//        writeLine();
        int[] indexes=toWrite.getIndices();
//        increaseTabSize();
        if (indexes!=null)
            writeIntArray(indexes);
        if (currentLine.length()!=0) writeLine();
//        decreaseTabSize();
        currentLine.append("\" />");
        writeLine();

    }

    private void writeRenderStates(Spatial s) throws IOException {
        RenderState[] states=s.getRenderStateList();
        if (states==null) return;
        if (states[RenderState.RS_MATERIAL]!=null){
            writeMaterialState((MaterialState) states[RenderState.RS_MATERIAL]);
        }
        if (states[RenderState.RS_TEXTURE]!=null){
            writeTextureState((TextureState)states[RenderState.RS_TEXTURE]);
        }
    }

    private void writeTextureState(TextureState textureState) throws IOException {
        if (textureState.getTexture()==null) return;
        String s=textureState.getTexture().getImageLocation();
        currentLine.append("<texturestate ");
        if ("file:/".equals(s.substring(0,6)))
            currentLine.append("file=\"").append(replaceSpecialsForFile(new StringBuffer(s.substring(6)))).append("\" ");
        else
            currentLine.append("URL=\"").append(s).append("\" ");
        currentLine.append("/>");
        writeLine();
    }

    private StringBuffer replaceSpecialsForFile(StringBuffer s) {
        int i=s.indexOf("%20");
        if (i==-1) return s; else return replaceSpecialsForFile(s.replace(i,i+3," "));
    }

    private void writeMaterialState(MaterialState state) throws IOException {
        currentLine.append("<materialstate ");
        currentLine.append("emissive=\"");
        appendColorRGBA(state.getEmissive());
        currentLine.append("\" ");

        currentLine.append("ambient=\"");
        appendColorRGBA(state.getAmbient());
        currentLine.append("\" ");

        currentLine.append("diffuse=\"");
        appendColorRGBA(state.getDiffuse());
        currentLine.append("\" ");

        currentLine.append("specular=\"");
        appendColorRGBA(state.getSpecular());
        currentLine.append("\" ");

        currentLine.append("alpha=\"");
        currentLine.append(state.getAlpha());
        currentLine.append("\" ");

        currentLine.append("shiny=\"");
        currentLine.append(state.getShininess());
        currentLine.append("\" ");

        currentLine.append("/>");
        writeLine();
    }

    private void writeMesh(TriMesh toWrite) throws IOException {
        currentLine.append("<mesh ").append(getSpatialHeader(toWrite)).append(">");
        writeLine();
        increaseTabSize();
        writeTriMeshTags(toWrite);
        processSpatial(toWrite);
        decreaseTabSize();
        currentLine.append("</mesh>");
        writeLine();
    }

    private void writeIntArray(int[] indexes) throws IOException {
        for (int i=0;i<indexes.length;i++){
            currentLine.append(indexes[i]).append(" ");
            if ((i+1)%18==0) writeLine();
        }
    }

    private void writeVec2fArray(Vector2f[] theTexCoords) throws IOException {
        int counter=0;
        for (int i=0;i<theTexCoords.length;i++){
            if (theTexCoords[i]!=null){
                appendVector2f(theTexCoords[i]);
                if (++counter==6){
                    writeLine();
                    counter=0;
                }
            }
        }
    }

    private void appendVector2f(Vector2f theVec){
        currentLine.append(Float.toString(theVec.x)).append(" ").append(Float.toString(theVec.y)).append(" ");
    }

    private void writeColorRGBAArray(ColorRGBA[] theColors) throws IOException {
        int counter=0;
        for (int i=0;i<theColors.length;i++){
            if (theColors[i]!=null){
                appendColorRGBA(theColors[i]);
                if (++counter==6){
                    counter=0;
                    writeLine();
                }
            }
        }

    }

    private void appendColorRGBA(ColorRGBA theColor){
        currentLine.append(Float.toString(theColor.r)).append(" ").append(Float.toString(theColor.g)).append(" ").append(Float.toString(theColor.b)).append(" ").append(Float.toString(theColor.a)).append(' ');
    }

    private void writeVec3fArray(Vector3f[] vecs) throws IOException {
        int counter=0;
        for (int i=0;i<vecs.length;i++){
            if (vecs[i]!=null){
                appendVector3f(vecs[i]);
                if (++counter==6){
                    counter=0;
                    writeLine();
                }
            }
        }
    }

    private void appendVector3f(Vector3f vec){
        currentLine.append(Float.toString(vec.x)).append(" ").append(Float.toString(vec.y)).append(" ").append(Float.toString(vec.z)).append(" " );
    }

    private StringBuffer getSpatialHeader(Spatial toWrite){
        StringBuffer header=new StringBuffer();
        header.append("name=\"").append(toWrite.getName()).append("\" ");
        Vector3f trans=toWrite.getLocalTranslation();
        if (!trans.equals(defaultTranslation)){
            header.append("translation=\"");
            header.append(trans.x).append(" ").append(trans.y).append(" ").append(trans.z).append("\" ");
        }
        Quaternion quat=toWrite.getLocalRotation();
        if (!quat.equals(defaultRotation)){
            header.append("rotation=\"");
            header.append(quat.x).append(" ").append(quat.y).append(" ").append(quat.z).append(" ").append(quat.w).append("\" ");
        }
        if (!toWrite.getLocalScale().equals(defaultScale)){
            header.append("scale = \"").append(toWrite.getLocalScale()).append("\" ");
        }
        return header;
    }

    private void writeHeader() throws IOException {
        currentLine.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writeLine();
        currentLine.append("<scene xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"\">");
        writeLine();
    }


    private void writeLine() throws IOException{
        myStream.write(tabs.toString());
        myStream.write(currentLine.toString());
        myStream.write('\n');
        myStream.flush();
        currentLine.setLength(0);
    }
    private void increaseTabSize(){
        tabs.append('\t');
    }
    private void decreaseTabSize(){
        tabs.deleteCharAt(0);
    }
}
