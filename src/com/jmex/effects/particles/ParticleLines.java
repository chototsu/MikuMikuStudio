package com.jmex.effects.particles;

import java.util.ArrayList;

import com.jme.intersection.CollisionResults;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.LineBatch;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.geom.BufferUtils;

/**
 * ParticleLines is a particle system that uses LineBatch as its underlying
 * geometric data.
 * 
 * @author Joshua Slack
 * @version $Id: ParticleLines.java,v 1.1 2006-06-23 22:31:54 nca Exp $
 */
public class ParticleLines extends ParticleGeometry {

    private static final long serialVersionUID = 2L;

    public ParticleLines() {}

    public ParticleLines(String name, int numParticles) {
        super(name, numParticles);
    }

    protected void initializeParticles(int numParticles) {

        // setup texture coords
        Vector2f[] sharedTextureData = new Vector2f[] {
                new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f) };

        int verts = getVertsForParticleType(getParticleType());

        geometryCoordinates = BufferUtils.createVector3Buffer(numParticles
                * verts);

        // setup indices for PT_LINES
        int[] indices = new int[numParticles * 2];
        for (int j = 0; j < numParticles; j++) {
            indices[0 + j * 2] = j * 2 + 0;
            indices[1 + j * 2] = j * 2 + 1;
        }

        appearanceColors = BufferUtils.createColorBuffer(numParticles * verts);
        particles = new Particle[numParticles];

        setVertexBuffer(0, geometryCoordinates);
        setColorBuffer(0, appearanceColors);
        setTextureBuffer(0, BufferUtils.createVector2Buffer(numParticles
                * verts));
        getBatch(0).setIndexBuffer(BufferUtils.createIntBuffer(indices));
        setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        setLightCombineMode(LightState.OFF);
        setTextureCombineMode(TextureState.REPLACE);

        invScale = new Vector3f();

        for (int k = 0; k < numParticles; k++) {
            particles[k] = new Particle(this);
            particles[k].init();
            particles[k].setStartIndex(k * verts);
            for (int a = verts - 1; a >= 0; a--) {
                int ind = (k * verts) + a;
                BufferUtils.setInBuffer(sharedTextureData[a], getTextureBuffer(
                        0, 0), ind);
                BufferUtils.setInBuffer(particles[k].getCurrentColor(),
                        appearanceColors, (ind));
            }

        }
    }

    @Override
    public int getParticleType() {
        return ParticleGeometry.PT_LINE;
    }

    public void draw(Renderer r) {
        Camera camera = r.getCamera();
        for (int i = 0; i < particles.length; i++) {
            Particle particle = particles[i];
            if (particle.getStatus() == Particle.ALIVE) {
                particle.updateVerts(camera);
            }
        }

        LineBatch batch;
        if (getBatchCount() == 1) {
            batch = getBatch(0);
            if (batch != null && batch.isEnabled()) {
                batch.setLastFrustumIntersection(frustrumIntersects);
                batch.draw(r);
                return;
            }
        }

        for (int i = 0, cSize = getBatchCount(); i < cSize; i++) {
            batch = getBatch(i);
            if (batch != null && batch.isEnabled())
                batch.onDraw(r);
        }

    }

    protected void setupBatchList() {
        batchList = new ArrayList<GeomBatch>(1);
        LineBatch batch = new LineBatch();
        batch.setParentGeom(this);
        batchList.add(batch);
    }

    public LineBatch getBatch(int index) {
        return (LineBatch) batchList.get(index);
    }

    /**
     * @return true if lines are to be antialiased
     */
    public boolean isAntialiased() {
        return getBatch(0).isAntialiased();
    }

    /**
     * Sets whether the line should be antialiased. May decrease performance. If
     * you want to enabled antialiasing, you should also use an alphastate with
     * a source of SB_SRC_ALPHA and a destination of DB_ONE_MINUS_SRC_ALPHA or
     * DB_ONE.
     * 
     * @param antiAliased
     *            true if the line should be antialiased.
     */
    public void setAntialiased(boolean antialiased) {
        getBatch(0).setAntialiased(antialiased);
    }

    /**
     * @return either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public int getMode() {
        return getBatch(0).getMode();
    }

    /**
     * @param mode
     *            either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public void setMode(int mode) {
        getBatch(0).setMode(mode);
    }

    /**
     * @return the width of this line.
     */
    public float getLineWidth() {
        return getBatch(0).getLineWidth();
    }

    /**
     * Sets the width of each line when drawn. Non anti-aliased line widths are
     * rounded to the nearest whole number by opengl.
     * 
     * @param lineWidth
     *            The lineWidth to set.
     */
    public void setLineWidth(float lineWidth) {
        getBatch(0).setLineWidth(lineWidth);
    }

    /**
     * @return the set stipplePattern. 0xFFFF means no stipple.
     */
    public short getStipplePattern() {
        return getBatch(0).getStipplePattern();
    }

    /**
     * The stipple or pattern to use when drawing the particle lines. 0xFFFF is
     * a solid line.
     * 
     * @param stipplePattern
     *            a 16bit short whose bits describe the pattern to use when
     *            drawing this line
     */
    public void setStipplePattern(short stipplePattern) {
        getBatch(0).setStipplePattern(stipplePattern);
    }

    /**
     * @return the set stippleFactor.
     */
    public int getStippleFactor() {
        return getBatch(0).getStippleFactor();
    }

    /**
     * @param stippleFactor
     *            magnification factor to apply to the stipple pattern.
     */
    public void setStippleFactor(int stippleFactor) {
        getBatch(0).setStippleFactor(stippleFactor);
    }

    @Override
    public void findCollisions(Spatial scene, CollisionResults results) {
        ; // ignore
    }

    @Override
    public boolean hasCollision(Spatial scene, boolean checkTriangles) {
        return false;
    }

}
