package com.jmex.effects.particles;

import java.io.IOException;
import java.util.ArrayList;

import com.jme.intersection.CollisionResults;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * ParticleMesh is a particle system that uses TriangleBatch as its underlying
 * geometric data.
 * 
 * @author Joshua Slack
 * @version $Id: ParticleMesh.java,v 1.9 2006-09-07 14:57:51 nca Exp $
 */
public class ParticleMesh extends ParticleGeometry {

    private static final long serialVersionUID = 2L;
    
    private boolean useBatchTexCoords = true;
    private boolean useTriangleNormalEmit = true;

    public ParticleMesh() {}

    public ParticleMesh(String name, int numParticles) {
        super(name, numParticles);
    }

    public ParticleMesh(String name, int numParticles, int type) {
        super(name, numParticles, type);
    }

    public ParticleMesh(String name, TriangleBatch batch) {
        super(name, 0, ParticleGeometry.PT_GEOMBATCH);
        numParticles = batch.getTriangleCount();
        psBatch = batch;
        initializeParticles(batch.getTriangleCount());
    }

    protected void initializeParticles(int numParticles) {
        TriangleBatch batch = getBatch(0);
        particles = new Particle[numParticles];
        if (numParticles == 0) return;
        Vector2f sharedTextureData[];

        // setup texture coords
        switch (getParticleType()) {
            case PT_GEOMBATCH:
            case PT_TRIANGLE:
                sharedTextureData = new Vector2f[] {
                        new Vector2f(0.0f, 0.0f), 
                        new Vector2f(0.0f, 2.0f),
                        new Vector2f(2.0f, 0.0f)
                        };
                break;
            case PT_QUAD:
                sharedTextureData = new Vector2f[] {
                        new Vector2f(0.0f, 0.0f), 
                        new Vector2f(0.0f, 1.0f),
                        new Vector2f(1.0f, 0.0f),
                        new Vector2f(1.0f, 1.0f)
                        };
                break;
            default:
                throw new IllegalStateException("Particle Mesh may only have particle type of PT_QUAD or PT_TRIANGLE");
        }
        
        int verts = getVertsForParticleType(getParticleType());

        geometryCoordinates = BufferUtils
                .createVector3Buffer(numParticles * verts);

        // setup indices
        int[] indices;
        switch (getParticleType()) {
            case PT_TRIANGLE:
            case PT_GEOMBATCH:
                indices = new int[numParticles * 3];
                for (int j = 0; j < numParticles; j++) {
                    indices[0 + j * 3] = j * 3 + 2;
                    indices[1 + j * 3] = j * 3 + 1;
                    indices[2 + j * 3] = j * 3 + 0;
                }
                break;
            case PT_QUAD:
                indices = new int[numParticles * 6];
                for (int j = 0; j < numParticles; j++) {
                    indices[0 + j * 6] = j * 4 + 2;
                    indices[1 + j * 6] = j * 4 + 1;
                    indices[2 + j * 6] = j * 4 + 0;
                    
                    indices[3 + j * 6] = j * 4 + 2;
                    indices[4 + j * 6] = j * 4 + 3;
                    indices[5 + j * 6] = j * 4 + 1;
                }
                break;
            default:
                throw new IllegalStateException("Particle Mesh may only have particle type of PT_QUAD or PT_TRIANGLE");
        }

        appearanceColors = BufferUtils.createColorBuffer(numParticles * verts);

        batch.setVertexBuffer(geometryCoordinates);
        batch.setColorBuffer(appearanceColors);
        batch.setTextureBuffer(BufferUtils.createVector2Buffer(numParticles * verts), 0);
        batch.setIndexBuffer(BufferUtils.createIntBuffer(indices));
        setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        setLightCombineMode(LightState.OFF);
        setTextureCombineMode(TextureState.REPLACE);

        invScale = new Vector3f();

        for (int k = 0; k < numParticles; k++) {
            particles[k] = new Particle(this);
            particles[k].init();
            particles[k].setStartIndex(k*verts);
            for (int a = verts-1; a >= 0; a--) {
                int ind = (k * verts) + a;
                if (particleType == ParticleGeometry.PT_GEOMBATCH && useBatchTexCoords) {
                    int index = ((TriangleBatch)psBatch).getIndexBuffer().get(ind);
                    BufferUtils.populateFromBuffer(workVect2, psBatch.getTextureBuffer(0), index);
                    BufferUtils.setInBuffer(workVect2, batch.getTextureBuffer(0), ind);
                } else
                    BufferUtils.setInBuffer(sharedTextureData[a],
                            batch.getTextureBuffer(0), ind);
                BufferUtils.setInBuffer(particles[k].getCurrentColor(),
                        appearanceColors, (ind));
            }

        }
    }

    public void draw(Renderer r) {
        Camera camera = r.getCamera();
        for (int i = 0; i < particles.length; i++) {
            Particle particle = particles[i];
            if (particle.getStatus() == Particle.ALIVE) {
                particle.updateVerts(camera);
            }
        }

        TriangleBatch batch;
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


    public void resetParticleVelocity(int i) {
        if (particleType == ParticleGeometry.PT_GEOMBATCH && useTriangleNormalEmit) {
            particles[i].getVelocity().set(particles[i].getTriangleModel().getNormal());
            particles[i].getVelocity().multLocal(emissionDirection);
            particles[i].getVelocity().multLocal(getInitialVelocity());
        } else {
            super.resetParticleVelocity(i);
        }
    }

    public boolean isUseBatchTexCoords() {
        return useBatchTexCoords;
    }

    public void setUseBatchTexCoords(boolean useBatchTexCoords) {
        this.useBatchTexCoords = useBatchTexCoords;
    }

    public boolean isUseTriangleNormalEmit() {
        return useTriangleNormalEmit;
    }

    public void setUseTriangleNormalEmit(boolean useTriangleNormalEmit) {
        this.useTriangleNormalEmit = useTriangleNormalEmit;
    }

    // TRIMESH TYPE METHODS
    
    protected void setupBatchList() {
        batchList = new ArrayList<GeomBatch>(1);
        TriangleBatch batch = new TriangleBatch();
        batch.setParentGeom(this);
        batchList.add(batch);
    }

    public TriangleBatch getBatch(int index) {
        return (TriangleBatch) batchList.get(index);
    }

    /**
     * determines if a collision between this trimesh and a given spatial occurs
     * if it has true is returned, otherwise false is returned.
     */
    public boolean hasCollision(Spatial scene, boolean checkTriangles) {
        if (this == scene || !isCollidable || !scene.isCollidable()) {
            return false;
        }
        if (getWorldBound().intersects(scene.getWorldBound())) {
            if ((scene.getType() & SceneElement.NODE) != 0) {
                Node parent = (Node) scene;
                for (int i = 0; i < parent.getQuantity(); i++) {
                    if (hasCollision(parent.getChild(i), checkTriangles)) {
                        return true;
                    }
                }

                return false;
            } 
                
            if (!checkTriangles) {
                return true;
            } 
            
            return hasTriangleCollision((TriMesh) scene);
        } 
            
        return false;        
    }

    /**
     * determines if this TriMesh has made contact with the give scene. The
     * scene is recursively transversed until a trimesh is found, at which time
     * the two trimesh OBBTrees are then compared to find the triangles that
     * hit.
     */
    public void findCollisions(Spatial scene, CollisionResults results) {
        if (this == scene || !isCollidable || !scene.isCollidable()) {
            return;
        }

        if (getWorldBound().intersects(scene.getWorldBound())) {
            if ((scene.getType() & SceneElement.NODE) != 0) {
                Node parent = (Node) scene;
                for (int i = 0; i < parent.getQuantity(); i++) {
                    findCollisions(parent.getChild(i), results);
                }
            } else {
                results.addCollision(this, (Geometry) scene);
            }
        }
    }

    /**
     * This function checks for intersection between this trimesh and the given
     * one. On the first intersection, true is returned.
     * 
     * @param toCheck
     *            The intersection testing mesh.
     * @return True if they intersect.
     */
    public boolean hasTriangleCollision(TriMesh toCheck) {
        TriangleBatch a,b;
        for (int x = 0; x < getBatchCount(); x++) {
            a = getBatch(x);
            if (a == null || !a.isEnabled()) continue;
            for (int y = 0; y < toCheck.getBatchCount(); y++) {
                b = toCheck.getBatch(y);
                if (b == null || !b.isEnabled()) continue;
                if (hasTriangleCollision(toCheck, x, y))
                    return true;
            }
        }
        return false;
    }

    /**
     * This function checks for intersection between this trimesh and the given
     * one. On the first intersection, true is returned.
     * 
     * @param toCheck
     *            The intersection testing mesh.
     * @return True if they intersect.
     */
    public boolean hasTriangleCollision(TriMesh toCheck, int thisBatch, int checkBatch) {
        if (getBatch(thisBatch).getCollisionTree() == null
                || toCheck.getBatch(checkBatch).getCollisionTree() == null
                || !isCollidable || !toCheck.isCollidable())
            return false;
        
        getBatch(thisBatch).getCollisionTree().bounds.transform(
                worldRotation, worldTranslation, worldScale,
                getBatch(thisBatch).getCollisionTree().worldBounds);
        return getBatch(thisBatch).getCollisionTree().intersect(
                toCheck.getBatch(checkBatch).getCollisionTree());        
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(useBatchTexCoords, "useBatchTexCoords", true);
        capsule.write(useTriangleNormalEmit, "useTriangleNormalEmit", true);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        useBatchTexCoords = capsule.readBoolean("useBatchTexCoords", true);
        useTriangleNormalEmit = capsule.readBoolean("useTriangleNormalEmit", true);
    }
}
