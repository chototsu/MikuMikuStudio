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
    public XMLWriter(OutputStream o){
        myStream=o;
    }

    public void setNewStream(OutputStream o){
        myStream=o;
    }

    public void writeScene(Node toWrite) throws IOException {
        writeHeader();
        writeNode(toWrite);
        writeClosing();
        myStream.close();
    }

    private void writeClosing() throws IOException {
        myStream.write("</scene>".getBytes());
    }

    private void writeNode(Node toWrite) throws IOException {
        StringBuffer header=new StringBuffer();
        header.append("<node ").append(getSpatialHeader(toWrite)).append(">\n");
        writeBuffer(header);
        writeRenderStates(toWrite);
        for (int i=0;i<toWrite.getQuantity();i++){
            Spatial s=toWrite.getChild(i);
            if (s instanceof Node)
                writeNode((Node) s);
            if (s instanceof TriMesh)
                writeMesh((TriMesh)s);
        }
        myStream.write("</node>\n".getBytes());
    }

    private void writeRenderStates(Spatial s) throws IOException {
        RenderState[] states=s.getRenderStateList();
        if (states[RenderState.RS_MATERIAL]!=null){
            writeMaterialState((MaterialState) states[RenderState.RS_MATERIAL]);
        }
    }

    private void writeMaterialState(MaterialState state) throws IOException {
        StringBuffer header=new StringBuffer();
        myStream.write("<materialstate ".getBytes());

        myStream.write("emissive=\"".getBytes());
        writeColorRGBA(state.getEmissive());
        myStream.write("\" ".getBytes());

        myStream.write("ambient=\"".getBytes());
        writeColorRGBA(state.getAmbient());
        myStream.write("\" ".getBytes());

        myStream.write("diffuse=\"".getBytes());
        writeColorRGBA(state.getDiffuse());
        myStream.write("\" ".getBytes());

        myStream.write("specular=\"".getBytes());
        writeColorRGBA(state.getSpecular());
        myStream.write("\" ".getBytes());

        myStream.write("alpha=\"".getBytes());
        myStream.write(Float.toString(state.getAlpha()).getBytes());
        myStream.write("\" ".getBytes());

        myStream.write("shiny=\"".getBytes());
        myStream.write((Float.toString(state.getShininess())).getBytes());
        myStream.write("\" ".getBytes());

        myStream.write("/>\n".getBytes());
    }

    private void writeMesh(TriMesh toWrite) throws IOException {
        StringBuffer header=new StringBuffer();
        header.append("<mesh ").append(getSpatialHeader(toWrite)).append(">\n");
        writeBuffer(header);

        writeRenderStates(toWrite);

        myStream.write("<vertex>\n".getBytes());
        Vector3f[] theVerts=toWrite.getVertices();
        if (theVerts!=null)
            writeVec3fArray(theVerts);
        myStream.write("\n</vertex>\n".getBytes());

        myStream.write("<normal>\n".getBytes());
        Vector3f[] theNorms=toWrite.getNormals();
        if (theNorms!=null)
            writeVec3fArray(theNorms);
        myStream.write("\n</normal>\n".getBytes());

        myStream.write("<color>\n".getBytes());
        ColorRGBA[] theColors=toWrite.getColors();
        if (theColors!=null)
            writeColorRGBAArray(theColors);
        myStream.write("\n</color>\n".getBytes());

        myStream.write("<texturecoords>\n".getBytes());
        Vector2f[] theTexCoords=toWrite.getTextures();
        if (theTexCoords!=null)
            writeVec2fArray(theTexCoords);
        myStream.write("\n</texturecoords>\n".getBytes());

        myStream.write("<index>\n".getBytes());
        int[] indexes=toWrite.getIndices();
        if (indexes!=null)
            writeIntArray(indexes);
        myStream.write("\n</index>\n".getBytes());
        myStream.write("</mesh>\n".getBytes());

    }

    private void writeIntArray(int[] indexes) throws IOException {
        StringBuffer nums=new StringBuffer();
        for (int i=0;i<indexes.length;i++){
            nums.append(indexes[i]).append(" ");
        }
        writeBuffer(nums);
    }

    private void writeVec2fArray(Vector2f[] theTexCoords) throws IOException {
        for (int i=0;i<theTexCoords.length;i++){
            if (theTexCoords[i]!=null)
                writeVector2f(theTexCoords[i]);
        }
    }

    private void writeVector2f(Vector2f theVec) throws IOException {
        StringBuffer toWrite=new StringBuffer();
        toWrite.append(Float.toString(theVec.x)).append(" ").append(Float.toString(theVec.y)).append(" ");
        writeBuffer(toWrite);
    }

    private void writeColorRGBAArray(ColorRGBA[] theColors) throws IOException {
        for (int i=0;i<theColors.length;i++){
            if (theColors[i]!=null)
                writeColorRGBA(theColors[i]);
        }

    }

    private void writeColorRGBA(ColorRGBA theColor) throws IOException {
        StringBuffer toWrite=new StringBuffer();
        toWrite.append(Float.toString(theColor.r)).append(" ").append(Float.toString(theColor.g)).append(" ").append(Float.toString(theColor.b)).append(" ").append(Float.toString(theColor.a)).append(" ");
        writeBuffer(toWrite);
    }

    private void writeVec3fArray(Vector3f[] vecs) throws IOException {
        for (int i=0;i<vecs.length;i++){
            if (vecs[i]!=null)
                writeVector3f(vecs[i]);
        }
    }

    private void writeVector3f(Vector3f vec) throws IOException {
        StringBuffer toWrite=new StringBuffer();
        toWrite.append(Float.toString(vec.x)).append(" ").append(Float.toString(vec.y)).append(" ").append(Float.toString(vec.z)).append(" " );
        writeBuffer(toWrite);
    }

    private void writeBuffer(StringBuffer header) throws IOException {
        myStream.write(header.toString().getBytes());
        myStream.flush();
        header.setLength(0);
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
        myStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
        myStream.write("<scene xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"\">\n".getBytes());
        myStream.flush();
    }
}