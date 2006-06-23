package com.jmex.effects.particles;

import java.util.ArrayList;

import com.jme.intersection.CollisionResults;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.PointBatch;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.geom.BufferUtils;

/**
 * ParticlePoints is a particle system that uses PointBatch as its underlying
 * geometric data.
 * 
 * @author Joshua Slack
 * @version $Id: ParticlePoints.java,v 1.1 2006-06-23 22:31:54 nca Exp $
 */
public class ParticlePoints extends ParticleGeometry {

    private static final long serialVersionUID = 2L;

    public ParticlePoints() {}
    
    public ParticlePoints(String name, int numParticles) {
        super(name, numParticles);
    }

    protected void initializeParticles(int numParticles) {
        Vector2f sharedTextureData[];

        // setup texture coords
        sharedTextureData = new Vector2f[] {new Vector2f(0.0f, 0.0f)};
        
        int verts = getVertsForParticleType(getParticleType());

        geometryCoordinates = BufferUtils
                .createVector3Buffer(numParticles * verts);

        // setup indices for PT_POINT
        int[] indices = new int[numParticles];
        for (int j = 0; j < numParticles; j++) {
            indices[j] = j;
        }

        appearanceColors = BufferUtils.createColorBuffer(numParticles * verts);
        particles = new Particle[numParticles];

        setVertexBuffer(0, geometryCoordinates);
        setColorBuffer(0, appearanceColors);
        setTextureBuffer(0, BufferUtils.createVector2Buffer(numParticles * verts));
        getBatch(0).setIndexBuffer(BufferUtils.createIntBuffer(indices));
        setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        setLightCombineMode(LightState.OFF);
        setTextureCombineMode(TextureState.REPLACE);

        invScale = new Vector3f();

        for (int k = 0; k < numParticles; k++) {
            particles[k] = new Particle(this);
            particles[k].init();
            particles[k].setStartIndex(k*verts);
            for (int a = verts-1; a >= 0; a--) {
                int ind = (k * verts) + a;
                BufferUtils.setInBuffer(sharedTextureData[a],
                        getTextureBuffer(0,0), ind);
                BufferUtils.setInBuffer(particles[k].getCurrentColor(),
                        appearanceColors, (ind));
            }

        }
    }

    @Override
    public int getParticleType() {
        return ParticleGeometry.PT_POINT;
    }

    public void draw(Renderer r) {
        Camera camera = r.getCamera();
        for (int i = 0; i < particles.length; i++) {
            Particle particle = particles[i];
            if (particle.getStatus() == Particle.ALIVE) {
                particle.updateVerts(camera);
            }
        }

        PointBatch batch;
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
        PointBatch batch = new PointBatch();
        batch.setParentGeom(this);
        batchList.add(batch);
    }

    public PointBatch getBatch(int index) {
        return (PointBatch) batchList.get(index);
    }
    
    /**
     * @return true if points are to be drawn antialiased
     */
    public boolean isAntialiased() {
        return getBatch(0).isAntialiased();
    }
    
    /**
     * Sets whether the points should be antialiased. May decrease performance. If
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
     * @return the pixel size of each point.
     */
    public float getPointSize() {
        return getBatch(0).getPointSize();
    }

    /**
     * Sets the pixel width of the points when drawn. Non anti-aliased point
     * sizes are rounded to the nearest whole number by opengl.
     * 
     * @param size
     *            The size to set.
     */
    public void setPointSize(float size) {
        getBatch(0).setPointSize(size);
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
