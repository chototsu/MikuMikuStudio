package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.renderer.Camera;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * type ==  NAMED_OBJECT == 0x4000<br>
 * parent == 3d3d == EDIT_3DS<br>
 *
 * @author Jack Lindamood
 */
public class NamedObjectChunk extends ChunkerClass{
    String name;
//    ArrayList meshList;
//    ArrayList cameraList;
//    ArrayList lightList;
    Object whatIAm;
    public NamedObjectChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn, header);
    }

    protected void initializeVariables() throws IOException {
//        meshList=new ArrayList();
//        cameraList=new ArrayList();
//        lightList=new ArrayList();
        name=readcStrAndDecrHeader();
        if (DEBUG) System.out.println("Editable object name="+name);
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case OBJ_TRIMESH:
                if (whatIAm!=null)
                    throw new IOException("logic error whatIAm in Named Object isn't null");
                whatIAm=new TriMeshChunk(myIn,i);
                return true;
            case CAMERA_FLAG:
//                cameraList.add(createCamera());
                if (whatIAm!=null)
                    throw new IOException("logic error whatIAm in Named Object isn't null");
                whatIAm="new camera here";
                return false;
            case LIGHT_OBJ:
                if (whatIAm!=null)
                    throw new IOException("logic error whatIAm in Named Object isn't null");
                whatIAm=new LightChunk(myIn,i);
                return true;
            default:
                return false;
        }
    }
}
