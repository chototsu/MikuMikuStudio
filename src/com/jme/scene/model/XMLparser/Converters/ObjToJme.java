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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Started Date: Jul 17, 2004<br><br>
 *
 * Converts .obj files into .jme binary format.
 * 
 * @author Jack Lindamood
 */
public class ObjToJme extends FormatConverter{
    private BufferedReader inFile;
    private ArrayList vertexList=new ArrayList();
    private ArrayList textureList=new ArrayList();
    private ArrayList normalList=new ArrayList();
    private MaterialGrouping curGroup;
    private final MaterialGrouping DEFAULT_GROUP=new MaterialGrouping();
    private HashMap materialSets=new HashMap(); // Maps MaterialGroup to ArraySet

    public void convert(InputStream format, OutputStream jMEFormat) throws IOException {
        vertexList.clear();
        textureList.clear();
        normalList.clear();
        materialSets.clear();
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

    private void nullAll() throws IOException {
        vertexList.clear();
        textureList.clear();
        normalList.clear();
        curGroup=null;
        materialSets.clear();
        inFile.close();
        inFile=null;
    }

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
            thisMesh.setRenderState(thisGroup.ts);
            thisMesh.setRenderState(thisGroup.m);
            toReturn.attachChild(thisMesh);
        }
        if (toReturn.getQuantity()==1)
            return toReturn.getChild(0);
        else
            return toReturn;
    }

    private void processLine(String s) {
        if (s==null) return ;
        if (s.length()==0) return;
        String[] parts=s.split(" ");
        if ("#".equals(parts[0])) return;
        if ("v".equals(parts[0])){
            addVertextoList(parts);
            return;
        }
        if ("vt".equals(parts[0])){
            addTextoList(parts);
            return;
        }
        if ("vn".equals(parts[0])){
            addNormalToList(parts);
            return;
        }
        if ("g".equals(parts[0])){
            setDefaultGroup();
            return;
        }
        if ("f".equals(parts[0])){
            addFaces(parts);
            return;
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
            firstIndex = secondIndex;
            secondIndex = thirdIndex;
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
            m=DisplaySystem.getDisplaySystem().getRenderer().getMaterialState();
            m.setAmbient(ColorRGBA.gray);
            m.setDiffuse(ColorRGBA.white);
            m.setSpecular(ColorRGBA.white);
            m.setEnabled(true);
            ts=DisplaySystem.getDisplaySystem().getRenderer().getTextureState();
        }
        MaterialState m;
        TextureState ts;
    }

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

    private class ArraySet{
        private ArrayList vertexes=new ArrayList();
        private ArrayList normals=new ArrayList();
        private ArrayList textures=new ArrayList();
        private ArrayList indexes=new ArrayList();

        public int findSet(IndexSet v) {
            int i=0;
            for (i=0;i<normals.size();i++){
                if (normals.get(i).equals(v.normal) &&
                        textures.get(i).equals(v.texture) &&
                        vertexes.get(i).equals(v.vertex))
                    return i;
            }
            normals.add(v.normal);
            textures.add(v.texture);
            vertexes.add(v.vertex);
            return i;
        }
    }
}
