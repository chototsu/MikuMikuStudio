package com.jme.scene.model.XMLparser.Converters;

import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.model.XMLparser.JmeBinaryWriter;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;
import com.jme.renderer.ColorRGBA;
import com.jme.image.Texture;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URL;


/**
 * Started Date: Jul 17, 2004<br><br>
 *
 * Converts .obj files into .jme binary format.  In order for ObjToJme to find the .mtl library, you must specify the
 * "mtllib" tag to the baseURL where the mtl libraries are to be found.  Somewhat similar to this.setProperty("mtllib",objFile);
 * 
 * @author Jack Lindamood
 */
public class ObjToJme extends FormatConverter{
    private BufferedReader inFile;
    /** Every vertex in the file*/
    private ArrayList vertexList=new ArrayList();
    /** Every texture coordinate in the file*/
    private ArrayList textureList=new ArrayList();
    /** Every normal in the file*/
    private ArrayList normalList=new ArrayList();
    /** Last 'material' flag in the file*/
    private MaterialGrouping curGroup;
    /** Default material group for groups without a material*/
    private final MaterialGrouping DEFAULT_GROUP=new MaterialGrouping();
    /** Maps material names to the actual material object **/
    private HashMap materialNames=new HashMap();
    /** Maps Materials to their vertex usage **/
    private HashMap materialSets=new HashMap();

    /**
     * Converts an Obj file to jME format.  The syntax is: "ObjToJme file.obj outfile.jme".
     * @param args The array of parameters
     */
    public static void main(String[] args){
        new DummyDisplaySystem();
        new ObjToJme().attemptFileConvert(args);
    }

    /**
     * Converts an .obj file to .jme format.  If you wish to use a .mtl to load the obj's material information please specify
     * the base url where the .mtl is located with setProperty("mtllib",new URL(baseURL))
     * @param format The .obj file's stream.
     * @param jMEFormat The .jme file's stream.
     * @throws IOException If anything bad happens.
     */
    public void convert(InputStream format, OutputStream jMEFormat) throws IOException {
        vertexList.clear();
        textureList.clear();
        normalList.clear();
        materialSets.clear();
        materialNames.clear();
        inFile=new BufferedReader(new InputStreamReader(format));
        String in;
        curGroup=DEFAULT_GROUP;
        materialSets.put(DEFAULT_GROUP,new ArraySet());
        while ((in=inFile.readLine())!=null){
            processLine(in);
        }
        new JmeBinaryWriter().writeScene(buildStructure(),jMEFormat);
        nullAll();
    }

    /**
     * Nulls all to let the gc do its job.
     * @throws IOException
     */
    private void nullAll() throws IOException {
        vertexList.clear();
        textureList.clear();
        normalList.clear();
        curGroup=null;
        materialSets.clear();
        materialNames.clear();
        inFile.close();
        inFile=null;
    }

    /**
     * Converts the structures of the .obj file to a scene to write
     * @return The TriMesh or Node that represents the .obj file.
     */
    private Spatial buildStructure() {
        Node toReturn=new Node("obj file");
        Object[] o=materialSets.keySet().toArray();
        for (int i=0;i<o.length;i++){
            MaterialGrouping thisGroup=(MaterialGrouping) o[i];
            ArraySet thisSet=(ArraySet) materialSets.get(thisGroup);
            if (thisSet.indexes.size()<3) continue;
            TriMesh thisMesh=new TriMesh("temp"+i);
            Vector3f[] vert=new Vector3f[thisSet.vertexes.size()];
            Vector3f[] norm=new Vector3f[thisSet.vertexes.size()];
            Vector2f[] text=new Vector2f[thisSet.vertexes.size()];

            for (int j=0;j<thisSet.vertexes.size();j++){
                vert[j]=(Vector3f) thisSet.vertexes.get(j);
                norm[j]=(Vector3f) thisSet.normals.get(j);
                text[j]=(Vector2f) thisSet.textures.get(j);
            }
            int[] indexes=new int[thisSet.indexes.size()];
            for (int j=0;j<thisSet.indexes.size();j++)
                indexes[j]=((Integer)thisSet.indexes.get(j)).intValue();
            thisMesh.reconstruct(vert, norm,null,text, indexes);
            if (properties.get("sillycolors")!=null)
                thisMesh.setRandomColors();
            if (thisGroup.ts.isEnabled()) thisMesh.setRenderState(thisGroup.ts);
            thisMesh.setRenderState(thisGroup.m);
            toReturn.attachChild(thisMesh);
        }
        if (toReturn.getQuantity()==1)
            return toReturn.getChild(0);
        else
            return toReturn;
    }

    /**
     * Processes a line of text in the .obj file.
     * @param s The line of text in the file.
     * @throws IOException
     */
    private void processLine(String s) throws IOException {
        if (s==null) return ;
        if (s.length()==0) return;
        String[] parts=s.split(" ");
        parts=removeEmpty(parts);
        if ("#".equals(parts[0])) return;
        if ("v".equals(parts[0])){
            addVertextoList(parts);
            return;
        }else if ("vt".equals(parts[0])){
            addTextoList(parts);
            return;
        } else if ("vn".equals(parts[0])){
            addNormalToList(parts);
            return;
        } else if ("g".equals(parts[0])){
            if (materialNames.get(parts[1])!=null && materialNames.get(parts[1])!=null)
                curGroup=(MaterialGrouping) materialNames.get(parts[1]);
            else
                setDefaultGroup();
            return;
        } else if ("f".equals(parts[0])){
            addFaces(parts);
            return;
        } else if ("mtllib".equals(parts[0])){
            loadMaterials(parts);
            return;
        } else if ("newmtl".equals(parts[0])){
            addMaterial(parts);
            return;
        } else if ("usemtl".equals(parts[0])){
            if (materialNames.get(parts[1])!=null)
                curGroup=(MaterialGrouping) materialNames.get(parts[1]);
            else
                setDefaultGroup();
            return;
        } else if ("Ka".equals(parts[0])){
            curGroup.m.setAmbient(new ColorRGBA(Float.parseFloat(parts[1]),Float.parseFloat(parts[2]),Float.parseFloat(parts[3]),1));
            return;
        } else if ("Kd".equals(parts[0])){
            curGroup.m.setDiffuse(new ColorRGBA(Float.parseFloat(parts[1]),Float.parseFloat(parts[2]),Float.parseFloat(parts[3]),1));
            return;
        } else if ("Ks".equals(parts[0])){
            curGroup.m.setSpecular(new ColorRGBA(Float.parseFloat(parts[1]),Float.parseFloat(parts[2]),Float.parseFloat(parts[3]),1));
            return;
        } else if ("Ks".equals(parts[0])){
            curGroup.m.setShininess(Float.parseFloat(parts[1]));
            return;
        } else if ("d".equals(parts[0])){
            curGroup.m.setAlpha(Float.parseFloat(parts[1]));
            return;
        } else if ("map_Kd".equals(parts[0]) || "map_Ka".equals(parts[0])){
            Texture t=new Texture();
            t.setImageLocation("file:/"+s.substring(6).trim());
            curGroup.ts.setTexture(t);
            curGroup.ts.setEnabled(true);
            return;
        }
    }

    private String[] removeEmpty(String[] parts) {
        int cnt=0;
        for (int i=0;i<parts.length;i++){
            if (!parts[i].equals(""))
                cnt++;
        }
        String[] toReturn=new String[cnt];
        int index=0;
        for (int i=0;i<parts.length;i++){
            if (!parts[i].equals("")){
                toReturn[index++]=parts[i];
            }
        }
        return toReturn;
    }

    private void addMaterial(String[] parts) {
        MaterialGrouping newMat=new MaterialGrouping();
        materialNames.put(parts[1],newMat);
        materialSets.put(newMat,new ArraySet());
        curGroup=newMat;
    }

    private void loadMaterials(String[] fileNames) throws IOException {
        URL matURL=(URL) properties.get("mtllib");
        if (matURL==null) return;
        for (int i=1;i<fileNames.length;i++){
            processMaterialFile(new URL(matURL,fileNames[i]).openStream());
        }
    }

    private void processMaterialFile(InputStream inputStream) throws IOException {
        BufferedReader matFile=new BufferedReader(new InputStreamReader(inputStream));
        String in;
        while ((in=matFile.readLine())!=null){
            processLine(in);
        }
    }

    private void addFaces(String[] parts) {
        ArraySet thisMat=(ArraySet) materialSets.get(curGroup);
        IndexSet first=new IndexSet(parts[1]);
        int firstIndex=thisMat.findSet(first);
        IndexSet second=new IndexSet(parts[2]);
        int secondIndex=thisMat.findSet(second);
        IndexSet third=new IndexSet();
        for (int i=3;i<parts.length;i++){
            third.parseStringArray(parts[i]);
            thisMat.indexes.add(new Integer(firstIndex));
            thisMat.indexes.add(new Integer(secondIndex));
            int thirdIndex=thisMat.findSet(third);
            thisMat.indexes.add(new Integer(thirdIndex));
        }
    }


    private void setDefaultGroup() {
        curGroup=DEFAULT_GROUP;
    }

    private void addNormalToList(String[] parts) {
        normalList.add(new Vector3f(Float.parseFloat(parts[1]),Float.parseFloat(parts[2]),Float.parseFloat(parts[3])));

    }

    private void addTextoList(String[] parts) {
        if (parts.length==2)
            textureList.add(new Vector2f(Float.parseFloat(parts[1]),0));
        else
            textureList.add(new Vector2f(Float.parseFloat(parts[1]),Float.parseFloat(parts[2])));
    }

    private void addVertextoList(String[] parts) {
        vertexList.add(new Vector3f(Float.parseFloat(parts[1]),Float.parseFloat(parts[2]),Float.parseFloat(parts[3])));
    }

    private class MaterialGrouping{
        public MaterialGrouping(){
            m=DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
            m.setAmbient(new ColorRGBA(.2f,.2f,.2f,1));
            m.setDiffuse(new ColorRGBA(.8f,.8f,.8f,1));
            m.setSpecular(ColorRGBA.white);
            m.setEnabled(true);
            ts=DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        }
        MaterialState m;
        TextureState ts;
    }

    /**
     * Stores a complete set of vertex/texture/normal triplet set that is to be indexed by the TriMesh.
     */
    private class IndexSet{
        public IndexSet(){}
        public IndexSet(String parts){
            parseStringArray(parts);
        }
        public void parseStringArray(String parts){
            int vIndex,nIndex,tIndex;
            String[] triplet=parts.split("/");
            vIndex=Integer.parseInt(triplet[0]);
            if (vIndex<0){
                vertex=(Vector3f) vertexList.get(vertexList.size()+vIndex);
            } else{
                vertex=(Vector3f) vertexList.get(vIndex-1); // obj is 1 indexed
            }
            if (triplet[1]==null || triplet[1].equals("")){
                texture=null;
            } else{
                tIndex=Integer.parseInt(triplet[1]);
                if (tIndex<0){
                    texture=(Vector2f) textureList.get(textureList.size()+tIndex);
                } else{
                    texture=(Vector2f) textureList.get(tIndex-1); // obj is 1 indexed
                }
            }
            if (triplet[2]==null || triplet[2].equals("")){
                normal=null;
            } else{
                nIndex=Integer.parseInt(triplet[2]);
                if (nIndex<0){
                    normal=(Vector3f) normalList.get(normalList.size()+nIndex);
                } else{
                    normal=(Vector3f) normalList.get(nIndex-1); // obj is 1 indexed
                }

            }

        }
        Vector3f vertex;
        Vector2f texture;
        Vector3f normal;
    }

    /**
     * An array of information that will become a renderable trimesh.  Each material has it's own trimesh.
     */
    private class ArraySet{
        private ArrayList vertexes=new ArrayList();
        private ArrayList normals=new ArrayList();
        private ArrayList textures=new ArrayList();
        private ArrayList indexes=new ArrayList();

        public int findSet(IndexSet v) {
            int i=0;
            for (i=0;i<normals.size();i++){
                if (compareObjects(v.normal,normals.get(i)) &&
                        compareObjects(v.texture,textures.get(i)) &&
                        compareObjects(v.vertex,vertexes.get(i)))
                    return i;
            }
            normals.add(v.normal);
            textures.add(v.texture);
            vertexes.add(v.vertex);
            return i;
        }

        private boolean compareObjects(Object o1, Object o2) {
            if (o1==null) return (o2==null);
            if (o2==null) return false;
            return o1.equals(o2);
        }
    }
}
