package com.jme.scene.model.XMLparser;

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.MaterialState;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;

import java.io.OutputStream;
import java.io.IOException;

/**
 * Started Date: Jun 5, 2004
 *
 * Class to write a node to an OutputStream in XML format
 *
 * @author Jack Lindamood
 */
public class XMLWriter {
    private OutputStream myStream;
    final static Vector3f defaultTranslation=new Vector3f(0,0,0);
    final static Quaternion defaultRotation=new Quaternion(0,0,0,1);
    final static float defaultScale=1;
    StringBuffer tabs=new StringBuffer();
    StringBuffer currentLine=new StringBuffer();
    public XMLWriter(OutputStream o){
        myStream=o;
    }

    public void setNewStream(OutputStream o){
        myStream=o;
    }

    public void writeScene(Node toWrite) throws IOException {
        writeHeader();
        increaseTabSize();
        writeNode(toWrite);
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

        writeRenderStates(toWrite);
        for (int i=0;i<toWrite.getQuantity();i++){
            Spatial s=toWrite.getChild(i);
            if (s instanceof Node)
                writeNode((Node) s);
            if (s instanceof TriMesh)
                writeMesh((TriMesh)s);
        }
        decreaseTabSize();
        currentLine.append("</node>");
        writeLine();
    }

    private void writeRenderStates(Spatial s) throws IOException {
        RenderState[] states=s.getRenderStateList();
        if (states[RenderState.RS_MATERIAL]!=null){
            writeMaterialState((MaterialState) states[RenderState.RS_MATERIAL]);
        }
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

        writeRenderStates(toWrite);

        currentLine.append("<vertex>");
        writeLine();
        Vector3f[] theVerts=toWrite.getVertices();
        if (theVerts!=null)
            writeVec3fArray(theVerts);
        writeLine();
        currentLine.append("</vertex>");
        writeLine();

        currentLine.append("<normal>");
        writeLine();
        Vector3f[] theNorms=toWrite.getNormals();
        if (theNorms!=null)
            writeVec3fArray(theNorms);
        writeLine();
        currentLine.append("</normal>");
        writeLine();

        currentLine.append("<color>");
        writeLine();
        ColorRGBA[] theColors=toWrite.getColors();
        if (theColors!=null)
            writeColorRGBAArray(theColors);
        writeLine();
        currentLine.append("</color>");
        writeLine();

        currentLine.append("<texturecoords>");
        writeLine();
        Vector2f[] theTexCoords=toWrite.getTextures();
        if (theTexCoords!=null)
            writeVec2fArray(theTexCoords);
        writeLine();
        currentLine.append("</texturecoords>");
        writeLine();

        currentLine.append("<index>");
        int[] indexes=toWrite.getIndices();
        if (indexes!=null)
            writeIntArray(indexes);
        writeLine();
        currentLine.append("</index>");
        writeLine();
        decreaseTabSize();
        currentLine.append("</mesh>");
        writeLine();
    }

    private void writeIntArray(int[] indexes) throws IOException {
        for (int i=0;i<indexes.length;i++){
            currentLine.append(indexes[i]).append(" ");
        }
    }

    private void writeVec2fArray(Vector2f[] theTexCoords) throws IOException {
        for (int i=0;i<theTexCoords.length;i++){
            if (theTexCoords[i]!=null)
                writeVector2f(theTexCoords[i]);
        }
    }

    private void writeVector2f(Vector2f theVec) throws IOException {
        currentLine.append(Float.toString(theVec.x)).append(" ").append(Float.toString(theVec.y)).append(" ");
    }

    private void writeColorRGBAArray(ColorRGBA[] theColors) throws IOException {
        for (int i=0;i<theColors.length;i++){
            if (theColors[i]!=null)
                appendColorRGBA(theColors[i]);
        }

    }

    private void appendColorRGBA(ColorRGBA theColor) throws IOException {
        currentLine.append(Float.toString(theColor.r)).append(" ").append(Float.toString(theColor.g)).append(" ").append(Float.toString(theColor.b)).append(" ").append(Float.toString(theColor.a)).append(" ");
    }

    private void writeVec3fArray(Vector3f[] vecs) throws IOException {
        for (int i=0;i<vecs.length;i++){
            if (vecs[i]!=null)
                writeVector3f(vecs[i]);
        }
    }

    private void writeVector3f(Vector3f vec) throws IOException {
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
        if (toWrite.getLocalScale()!=defaultScale){
            header.append("scale = \"").append(toWrite.getLocalScale()).append("\" ");
        }
        return header;
    }

    private void writeHeader() throws IOException {
        currentLine.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writeLine();
        currentLine.append("<scene xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"\">\n");
        writeLine();
    }


    private void writeLine() throws IOException{
        myStream.write(tabs.toString().getBytes());
        myStream.write(currentLine.toString().getBytes());
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