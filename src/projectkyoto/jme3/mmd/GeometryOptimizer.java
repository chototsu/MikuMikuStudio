/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectkyoto.jme3.mmd;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import projectkyoto.mmd.file.util2.BufferUtil;

/**
 *
 * @author kobayasi
 */
public class GeometryOptimizer {
    public static GeometryOptimizer createNewInstance() {
        return new GeometryOptimizer();
    }
    protected GeometryOptimizer() {
        
    }
    Set<Mesh> meshSet = new HashSet<Mesh>();
    int interleavedSize;
    ByteBuffer interleavedBuffer;
    public void add(Spatial sp) {
        sp.depthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                add(geom);
            }
        });
    }
    public void add(Geometry geom) {
        Mesh mesh = geom.getMesh();
        if (mesh != null) {
            meshSet.add(mesh);
        }
    }
    public void add(Mesh mesh) {
        meshSet.add(mesh);
    }
    public void optimize2() {
        for(Mesh mesh : meshSet) {
            mesh.setInterleaved();
            for(VertexBuffer vb : mesh.getBufferList()) {
                System.out.println(
                        "type = "+vb.getBufferType()
                        + "stride = "+vb.getStride()
                        + "offset "+vb.getOffset()
                        );
                
            }
            System.out.println("done");
        }
    }
    String createVBKey(VertexBuffer vb) {
        StringBuilder sb = new StringBuilder();
           sb.append(vb.getNumComponents())
                   .append(",")
                   .append(vb.getFormat())
                   .append(",")
                   .append(vb.isNormalized())
                   .append(",")
                   .append(vb.getStride())
                   .append(",")
                   .append(vb.getOffset())
                   .append(",")
                   .append(vb.getBufferType());
           return sb.toString();
    }
    public void optimize3() {
        HashMap<String, VertexBuffer>vbMap = new HashMap<String, VertexBuffer>();
        ArrayList<VertexBuffer> vbList = new ArrayList<VertexBuffer>();
        for(Mesh mesh : meshSet) {
            vbList.clear();
            for(VertexBuffer vb : mesh.getBufferList()) {
                if (vb.getBufferType().equals(VertexBuffer.Type.Index)
                        || vb.getBufferType().equals(VertexBuffer.Type.InterleavedData)){
                    continue;
                }
                if (vb.getStride() > 0) {
                    String key = createVBKey(vb);
                    System.out.append("key = "+key);
                    VertexBuffer vb2 = vbMap.get(key);
                    if (vb2 != null) {
                        vbList.add(vb2);
                    } else {
                        vbMap.put(key, vb);
                    }
                }
            }
            for(VertexBuffer vb : vbList) {
//                mesh.setBuffer(vb);
            }
        }
    }
    public void optimize() {
        calcInterleavedSize();
        interleavedBuffer = BufferUtil.createByteBuffer(interleavedSize);
//        interleavedBuffer = ByteBuffer.allocateDirect(interleavedSize);
//        interleavedBuffer.order(ByteOrder.nativeOrder());
        VertexBuffer allData = new VertexBuffer(VertexBuffer.Type.InterleavedData);
//        ByteBuffer dataBuf = BufferUtils.createByteBuffer(stride * getVertexCount());
        allData.setupData(VertexBuffer.Usage.Static, 1, VertexBuffer.Format.UnsignedByte, interleavedBuffer);
        int offset = 0;
        for(Mesh mesh : meshSet) {
            int stride = 0;
            for(VertexBuffer vb : mesh.getBufferList()) {
                if (vb.getBufferType().equals(VertexBuffer.Type.Index)
                        || vb.getBufferType().equals(VertexBuffer.Type.InterleavedData)){
                    continue;
                }
                vb.getData().clear();
                vb.setOffset(offset + stride);
                stride += vb.getComponentsLength();
            }            
            for(VertexBuffer vb : mesh.getBufferList()) {
                if (vb.getBufferType().equals(VertexBuffer.Type.Index)
                        || vb.getBufferType().equals(VertexBuffer.Type.InterleavedData)){
                    continue;
                }
                vb.setStride(stride);
            }
            for(int i=0;i<mesh.getVertexCount();i++)
            for(VertexBuffer vb : mesh.getBufferList()) {
                if (vb.getBufferType().equals(VertexBuffer.Type.Index)
                        || vb.getBufferType().equals(VertexBuffer.Type.InterleavedData)){
                    continue;
                }
                switch (vb.getFormat()){
                    case Float:
                        FloatBuffer fb = (FloatBuffer) vb.getData();
                        for (int comp = 0; comp < vb.getNumComponents(); comp++){
                            interleavedBuffer.putFloat(fb.get());
                        }
                        break;
                    case Byte:
                    case UnsignedByte:
                        ByteBuffer bb = (ByteBuffer) vb.getData();
                        for (int comp = 0; comp < vb.getNumComponents(); comp++){
                            interleavedBuffer.put(bb.get());
                        }
                        break;
                    case Half:
                    case Short:
                    case UnsignedShort:
                        ShortBuffer sb = (ShortBuffer) vb.getData();
                        for (int comp = 0; comp < vb.getNumComponents(); comp++){
                            interleavedBuffer.putShort(sb.get());
                        }
                        break;
                    case Int:
                    case UnsignedInt:
                        IntBuffer ib = (IntBuffer) vb.getData();
                        for (int comp = 0; comp < vb.getNumComponents(); comp++){
                            interleavedBuffer.putInt(ib.get());
                        }
                        break;
                    case Double:
                        DoubleBuffer db = (DoubleBuffer) vb.getData();
                        for (int comp = 0; comp < vb.getNumComponents(); comp++){
                            interleavedBuffer.putDouble(db.get());
                        }
                        break;
                }
            }
//            mesh.setBuffer(allData);
            offset = interleavedBuffer.position();
            for(VertexBuffer vb : mesh.getBufferList()) {
                if (vb.getBufferType().equals(VertexBuffer.Type.Index)
                        || vb.getBufferType().equals(VertexBuffer.Type.InterleavedData)){
                    continue;
                }
//                System.out.println(
//                        "type = "+vb.getBufferType()
//                        + "stride = "+vb.getStride()
//                        + "offset "+vb.getOffset()
//                        );
                vb.updateData(null);
            }
//            System.out.println("done");
//            optimize2();
//            VertexBuffer allData2 = mesh.getBuffer(VertexBuffer.Type.InterleavedData);
//            ByteBuffer bb = (ByteBuffer)allData2.getData();
//            for(int i=0;i<allData.getData().capacity();i++) {
//                    System.out.printf("%d %x %x ", i, bb.get(i), interleavedBuffer.get(i));
//                    System.out.println();
//            }
            mesh.setBuffer(allData);
        }
        
    }
    int calcInterleavedSize() {
        interleavedSize = 0;
        for(Mesh mesh : meshSet) {
            for(VertexBuffer vb : mesh.getBufferList()) {
                if (vb.getBufferType().equals(VertexBuffer.Type.Index)
                        || vb.getBufferType().equals(VertexBuffer.Type.InterleavedData)){
                    continue;
                }
                int limit = vb.getData().capacity();
//                interleavedSize += vb.getComponentsLength() * vb.get
                interleavedSize += limit * vb.getFormat().getComponentSize();
            }
        }
        return interleavedSize;
    }
}
