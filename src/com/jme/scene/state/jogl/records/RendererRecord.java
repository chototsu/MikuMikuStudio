package com.jme.scene.state.jogl.records;

import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.StateRecord;
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
        final GL gl = GLU.getCurrentGL();

        if (!matrixValid || this.matrixMode != mode) {
            gl.glMatrixMode(mode);
            this.matrixMode = mode;
            matrixValid = true;
        }
    }

    public void setCurrentColor(ColorRGBA setTo) {
final GL gl = GLU.getCurrentGL();

//        if (!colorValid || !currentColor.equals(setTo)) {
            gl.glColor4f(setTo.r, setTo.g, setTo.b, setTo.a);
//            currentColor.set(setTo);
//            colorValid = true;
//        }
    }

    public void setBoundVBO(int id) {
        final GL gl = GLU.getCurrentGL();

        if (!vboValid || currentVboId != id) {
            gl.glBindBufferARB(
                    GL.GL_ARRAY_BUFFER_ARB, id);
            currentVboId = id;
            vboValid = true;
        }
    }

    public void setBoundElementVBO(int id) {
        final GL gl = GLU.getCurrentGL();

        if (!elementVboValid || currentElementVboId != id) {
            gl.glBindBufferARB(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, id);
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
        final GL gl = GLU.getCurrentGL();

        idBuff.rewind();
        gl.glGenBuffersARB(idBuff.limit(),idBuff); // TODO Check <size>
        int vboID = idBuff.get(0);
        vboCleanupCache.add(vboID);
        return vboID;
    }

    public void deleteVBOId(int id) {
        final GL gl = GLU.getCurrentGL();

        idBuff.rewind();
        idBuff.put(id).flip();
        gl.glDeleteBuffersARB(idBuff.limit(),idBuff); // TODO Check <size>
        vboCleanupCache.remove(Integer.valueOf(id));
    }

    public void cleanupVBOs() {
        for (int x = vboCleanupCache.size(); --x >= 0; ) {
            deleteVBOId(vboCleanupCache.get(x));
        }
        vboCleanupCache.clear();
    }
}
