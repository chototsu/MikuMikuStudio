package com.jme.scene.model.XMLparser.Converters;

import com.jme.util.LittleEndien;
import com.jme.util.BinaryFileReader;
import com.jme.math.Vector3f;
import com.jme.math.Matrix3f;
import com.jme.math.Vector2f;
import com.jme.math.FastMath;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.model.XMLparser.JmeBinaryWriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Started Date: Jul 15, 2004<br><br>
 *
 * Converts from MD3 files to jme binary.
 * 
 * @author Jack Lindamood
 */
public class Md3ToJme extends FormatConverter{
    BinaryFileReader file;
    MD3Header head;
    MD3Frame[] frames;
    MD3Tag[] tags;
    MD3Surface[] surfaces;

    public void convert(InputStream format, OutputStream jMEFormat) throws IOException {
        file=new BinaryFileReader(format);
        readHeader();
        readFrames();
        readTags();
        readSurfaces();
        JmeBinaryWriter jbw=new JmeBinaryWriter();
        jbw.writeScene(constructMesh(),jMEFormat);
    }

    private Node constructMesh() {
        Node toReturn=new Node("MD3 File");
        for (int i=0;i<head.numSurface;i++){
            MD3Surface thisSurface=surfaces[i];
//            for (int j=0;j<thisSurface.numTriangles;j++){
                TriMesh object=new TriMesh(thisSurface.name);
                object.setIndices(thisSurface.triIndexes);
                object.setVertices(thisSurface.verts[0]);
                object.setNormals(thisSurface.norms[0]);
                object.setTextures(thisSurface.texCoords);
//                object.setLocalScale(frames[0].scale);
                toReturn.attachChild(object);
//            }
        }
        return toReturn;
    }

    private void readSurfaces() throws IOException {
        file.setOffset(head.surfaceOffset);
        surfaces=new MD3Surface[head.numSurface];
        for (int i=0;i<head.numSurface;i++){
            surfaces[i]=new MD3Surface();
            surfaces[i].readMe();
        }
    }

    private void readTags() {
        file.setOffset(head.tagOffset);
        tags=new MD3Tag[head.numTags];
        for (int i=0;i<head.numTags;i++){
            tags[i]=new MD3Tag();
            tags[i].readMe();
        }

    }

    private void readFrames() {
        file.setOffset(head.frameOffset);
        frames=new MD3Frame[head.numFrames];
        for (int i=0;i<head.numFrames;i++){
            frames[i]=new MD3Frame();
            frames[i].readMe();
        }
    }

    private void readHeader() throws IOException {
        head=new MD3Header();
        head.readMe();
    }

    private class MD3Header{
        int version;
        String name;
        int flags;
        int numFrames;
        int numTags;
        int numSurface;
        int numSkins;
        int frameOffset;
        int tagOffset;
        int surfaceOffset;
        int fileOffset;
        void readMe() throws IOException {
            int ident=file.readInt();
            if (ident!=0x33504449)
                throw new IOException("Unknown file format:"+ident);
            version=file.readInt();
            if (version!=15)
                throw new IOException("Unsupported version " + version + ", only know ver 15");
            name=file.readString(64);
            System.out.println("Name:"+ new String(name));
            flags=file.readInt();
            numFrames=file.readInt();
            numTags=file.readInt();
            numSurface=file.readInt();
            numSkins=file.readInt();
            frameOffset=file.readInt();
            tagOffset=file.readInt();
            surfaceOffset=file.readInt();
            fileOffset=file.readInt();
        }
    }
    private class MD3Frame{
        Vector3f minBounds=new Vector3f();
        Vector3f maxBounds=new Vector3f();
        Vector3f localOrigin=new Vector3f();
        float scale;
        String name;
        void readMe(){
            readVecFloat(minBounds);
            readVecFloat(maxBounds);
            readVecFloat(localOrigin);
            scale=file.readFloat();
            name=file.readString(16);
        }
    }

    private class MD3Tag{
        String path;
        Vector3f origin=new Vector3f();
        Matrix3f axis;
        void readMe(){
            path=file.readString(64);
            readVecFloat(origin);
            float[] axisFs=new float[9];
            for (int i=0;i<9;i++)
                axisFs[i]=file.readFloat();
            axis=new Matrix3f();
            axis.set(axisFs);
        }
    }

    private class MD3Surface{
        String name;
        int flags;
        int numFrames;
        int numShaders;
        int numVerts;
        int numTriangles;
        int offTriangles;
        int offShaders;
        int offTexCoord;
        int offXyzNor;
        int offEnd;
        int[] triIndexes;
        Vector2f[] texCoords;
        Vector3f[][] verts;
        Vector3f[][] norms;
        private final static float XYZ_SCALE=1.0f/64;

        public void readMe() throws IOException {
            file.markPos();
            int ident=file.readInt();
            if (ident!=0x33504449)
                throw new IOException("Unknown file format:"+ident);
            name=file.readString(64);
            flags=file.readInt();
            numFrames=file.readInt();
            numShaders=file.readInt();
            numVerts=file.readInt();
            numTriangles=file.readInt();
            offTriangles=file.readInt();
            offShaders=file.readInt();
            offTexCoord=file.readInt();
            offXyzNor=file.readInt();
            offEnd=file.readInt();
            readShader();            // Skip shader info TODO REMOVE
            readTriangles();
            readTexCoord();
            readVerts();
        }

        private void readVerts() {
            file.seekMarkOffset(offXyzNor);
            verts=new Vector3f[head.numFrames][];
            norms=new Vector3f[head.numFrames][];
            for (int i=0;i<head.numFrames;i++){
                verts[i]=new Vector3f[numVerts];
                norms[i]=new Vector3f[numVerts];
                for (int j=0;j<numVerts;j++){
                    verts[i][j]=new Vector3f();
                    norms[i][j]=new Vector3f();
                    readVecShort(verts[i][j]);
                    readNormal(norms[i][j]);
                }
            }
        }

        private void readVecShort(Vector3f vector3f) {
            vector3f.z = file.readSignedShort()*XYZ_SCALE;
            vector3f.x = file.readSignedShort()*XYZ_SCALE;
            vector3f.y = file.readSignedShort()*XYZ_SCALE;


        }

        private void readNormal(Vector3f norm) {
            int lng=file.readByte();
            int lat=file.readByte();
            float newlat=(lat*2*FastMath.PI)/255;
            float newlng=(lng*2*FastMath.PI)/255;
            norm.x = FastMath.cos(newlat)*FastMath.sin(newlng);
            norm.y = FastMath.sin(newlat)*FastMath.sin(newlng);
            norm.z = FastMath.cos(newlng);
        }

        private void readTexCoord() {
            file.seekMarkOffset(offTexCoord);
            texCoords=new Vector2f[numVerts];
            for (int i=0;i<texCoords.length;i++){
                texCoords[i]=new Vector2f();
                texCoords[i].x=file.readFloat();
                texCoords[i].y=file.readFloat();
            }

        }

        private void readTriangles() {
            file.seekMarkOffset(offTriangles);
            triIndexes=new int[numTriangles*3];
            for (int i=0;i<triIndexes.length;i++)
                triIndexes[i]=file.readInt();
        }

        private void readShader() {
            file.seekMarkOffset(offShaders);
            for (int i=0;i<numShaders;i++){
                String pathName=file.readString(64);
                int shaderIndex=file.readInt();
                System.out.println("path:"+pathName+" Index:"+shaderIndex);
            }
        }

    }

    void readVecFloat(Vector3f in){
        in.z=file.readFloat();
        in.x=file.readFloat();
        in.y=file.readFloat();

    }
}