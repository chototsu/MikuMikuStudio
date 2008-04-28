package com.jme.scene.state.lwjgl.records;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.ARBBufferObject;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;

import com.jme.renderer.ColorRGBA;
import com.jme.util.geom.BufferUtils;

public class RendererRecord extends StateRecord {
    private int matrixMode = -1;
    private int currentElementVboId = -1, currentVboId = -1;
    private boolean matrixValid;
    private boolean vboValid;
    private boolean elementVboValid;
    private transient ColorRGBA tempColor = new ColorRGBA();
    private ArrayList<Integer> vboCleanupCache = new ArrayList<Integer>();
    private IntBuffer idBuff = BufferUtils.createIntBuffer(16);

    public void switchMode(int mode) {
        if (!matrixValid || this.matrixMode != mode) {
            GL11.glMatrixMode(mode);
            this.matrixMode = mode;
            matrixValid = true;
        }
    }

    public void setCurrentColor(ColorRGBA setTo) {
//        if (!colorValid || !currentColor.equals(setTo)) {
            GL11.glColor4f(setTo.r, setTo.g, setTo.b, setTo.a);
//            currentColor.set(setTo);
//            colorValid = true;
//        }
    }

    public void setBoundVBO(int id) {
        if (!vboValid || currentVboId != id) {
            ARBBufferObject.glBindBufferARB(
                    ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
            currentVboId = id;
            vboValid = true;
        }
    }

    public void setBoundElementVBO(int id) {
        if (!elementVboValid || currentElementVboId != id) {
            ARBBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, id);
            currentElementVboId = id;
            elementVboValid = true;
        }
    }
    
    public void setCurrentColor(float red, float green, float blue, float alpha) {
        tempColor.set(red, green, blue, alpha);
        setCurrentColor(tempColor);
    }
    
    @Override
    public void invalidate() {
        invalidateMatrix();
        invalidateVBO();
    }
    
    @Override
    public void validate() {
        ; // ignore  - validate per item or locally
    }

    public void invalidateMatrix() {
        matrixValid = false;
        matrixMode = -1;
    }

    public void invalidateVBO() {
        vboValid = false;
        elementVboValid = false;
        currentElementVboId = currentVboId = -1;
    }

    public int makeVBOId() {
        idBuff.rewind();
        ARBBufferObject.glGenBuffersARB(idBuff);
        int vboID = idBuff.get(0);
        vboCleanupCache.add(vboID);
        return vboID;
    }

    public void deleteVBOId(int id) {
        idBuff.rewind();
        idBuff.put(id).flip();
        ARBBufferObject.glDeleteBuffersARB(idBuff);
        vboCleanupCache.remove(Integer.valueOf(id));
    }

    public void cleanupVBOs() {
        for (int x = vboCleanupCache.size(); --x >= 0; ) {
            deleteVBOId(vboCleanupCache.get(x));
        }
        vboCleanupCache.clear();
    }
}
