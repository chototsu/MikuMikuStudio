package com.jmex.effects.particles;

import java.io.IOException;
import java.nio.FloatBuffer;

import com.jme.math.FastMath;
import com.jme.math.Line;
import com.jme.math.Matrix3f;
import com.jme.math.Rectangle;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

public class ParticleMesh extends TriMesh {

    private static final float DEFAULT_END_SIZE = 4f;
    private static final float DEFAULT_START_SIZE = 20f;
    private static final float DEFAULT_MAX_ANGLE = 0.7853982f;
    private static final float DEFAULT_MAX_LIFE = 3000f;
    private static final float DEFAULT_MIN_LIFE = 2000f;
    public static final int ET_POINT = 0;
    public static final int ET_LINE = 1;
    public static final int ET_RECTANGLE = 2;
    public static final int ET_GEOMBATCH = 3;

    private static final long serialVersionUID = 1L;

    private int emitType = ET_POINT;
    private Line psLine;
    private Rectangle psRect;
    private GeomBatch psBatch;

    private float startSize, endSize;
    private ColorRGBA startColor;
    private ColorRGBA endColor;
    private float initialVelocity;
    private float minimumLifeTime, maximumLifeTime;
    private float minimumAngle, maximumAngle;
    private float particleSpinSpeed;
    private Vector3f emissionDirection;
    private Vector3f worldEmit = new Vector3f();
    private int numParticles;
    private Vector3f gravityForce;
    private float randomMod;
    private boolean rotateWithScene = false;

    private FloatBuffer geometryCoordinates;
    private FloatBuffer appearanceColors;
    private final static Vector2f sharedTextureData[] = {
            new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 0.0f),
            new Vector2f(1.0f, 1.0f), new Vector2f(0.0f, 1.0f) };

    // vectors to prevent repeated object creation:
    private Vector3f upXemit, absUpVector, abUpMinUp;
    private Vector3f upVector;
    private Matrix3f rotMatrix;
    private Vector3f invScale;

    public Particle particles[];

    // private Vector3f particleSpeed;
    private int releaseRate; // particles per second
    private Vector3f originOffset;
    private Vector3f originCenter;

    private ParticleController controller;

    public ParticleMesh() {}
    
    public ParticleMesh(String name, int numParticles) {
        super(name);
        this.numParticles = numParticles;
        emissionDirection = new Vector3f(0.0f, 1.0f, 0.0f);
        minimumLifeTime = DEFAULT_MIN_LIFE;
        maximumLifeTime = DEFAULT_MAX_LIFE;
        maximumAngle = DEFAULT_MAX_ANGLE;
        startSize = DEFAULT_START_SIZE;
        endSize = DEFAULT_END_SIZE;
        startColor = new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f);
        endColor = new ColorRGBA(1.0f, 1.0f, 0.0f, 0.0f);
        gravityForce = new Vector3f(0.0f, 0.0f, 0.0f);
        upVector = new Vector3f(0.0f, 1.0f, 0.0f);
        originCenter = new Vector3f();
        originOffset = new Vector3f();
        particleSpinSpeed = 0;
        releaseRate = numParticles;
        // init working vectors.. used to prevent additional object creation.
        upXemit = new Vector3f();
        absUpVector = new Vector3f();
        abUpMinUp = new Vector3f();
        initialVelocity = 1.0f;

        randomMod = 1.0f;
        rotMatrix = new Matrix3f();
        initializeParticles(numParticles);
        getBatch(0).setCastsShadows(false);
    }

    private void initializeParticles(int numParticles) {
        geometryCoordinates = BufferUtils
                .createVector3Buffer(numParticles << 2);
        int[] indices = new int[numParticles * 6];
        for (int j = 0; j < numParticles; j++) {
            indices[0 + j * 6] = j * 4 + 2;
            indices[1 + j * 6] = j * 4 + 1;
            indices[2 + j * 6] = j * 4 + 0;
            indices[3 + j * 6] = j * 4 + 3;
            indices[4 + j * 6] = j * 4 + 2;
            indices[5 + j * 6] = j * 4 + 0;
        }

        appearanceColors = BufferUtils.createColorBuffer(numParticles << 2);
        particles = new Particle[numParticles];

        setVertexBuffer(0, geometryCoordinates);
        setColorBuffer(0, appearanceColors);
        setTextureBuffer(0, BufferUtils.createVector2Buffer(numParticles << 2));
        setIndexBuffer(0, BufferUtils.createIntBuffer(indices));
        setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        setLightCombineMode(LightState.OFF);
        setTextureCombineMode(TextureState.REPLACE);

        invScale = new Vector3f();

        for (int k = 0; k < numParticles; k++) {
            particles[k] = new Particle(this);
            particles[k].init();
            for (int a = 3; a >= 0; a--) {
                int ind = (k << 2) + a;
                BufferUtils.setInBuffer(sharedTextureData[a],
                        getTextureBuffer(0,0), ind);
                particles[k].verts[a] = ind;
                BufferUtils.setInBuffer(particles[k].currColor,
                        appearanceColors, (ind));
            }

        }
    }

    public void updateGeometricState(float time, boolean initiator) {
        super.updateGeometricState(time, initiator);
        if (isRotateWithScene()) {
            if (emitType == ET_GEOMBATCH && getGeomBatch() != null) {
                getGeomBatch().getParentGeom().getWorldRotation().mult(emissionDirection, worldEmit);
            } else {
                if (parent != null)
                    getWorldRotation().mult(emissionDirection, worldEmit);
                else
                    worldEmit.set(emissionDirection);
            }
        } else worldEmit.set(emissionDirection);

        originCenter.set(worldTranslation).addLocal(originOffset);

        getWorldTranslation().set(0,0,0);
        getWorldRotation().set(0, 0, 0, 1);
    }

    public void draw(Renderer r) {
        Camera camera = r.getCamera();
        for (int i = 0; i < particles.length; i++) {
            Particle particle = particles[i];
            if (particle.status == Particle.ALIVE) {
                particle.updateVerts(camera);
            }
        }

        super.draw(r);
    }

    public void forceRespawn() {
        for (int i = particles.length; --i >= 0;) {
            particles[i].status = Particle.AVAILABLE;
        }

        if (controller != null) {
            controller.setActive(true);
        }
    }

    /**
     * Setup the rotation matrix used to determine initial particle velocity
     * based on emission angle and emission direction. called automatically by
     * the set* methods for those parameters.
     */
    private Vector3f oldEmit = new Vector3f(Float.NaN, Float.NaN, Float.NaN);
    private float matData[][] = new float[3][3];

    public void updateRotationMatrix() {

        if (oldEmit.equals(worldEmit))
            return;

        float upDotEmit = upVector.dot(worldEmit);
        if (FastMath.abs(upDotEmit) > 1.0d - FastMath.DBL_EPSILON) {
            absUpVector.x = upVector.x <= 0.0f ? -upVector.x : upVector.x;
            absUpVector.y = upVector.y <= 0.0f ? -upVector.y : upVector.y;
            absUpVector.z = upVector.z <= 0.0f ? -upVector.z : upVector.z;
            if (absUpVector.x < absUpVector.y) {
                if (absUpVector.x < absUpVector.z) {
                    absUpVector.x = 1.0f;
                    absUpVector.y = absUpVector.z = 0.0f;
                } else {
                    absUpVector.z = 1.0f;
                    absUpVector.x = absUpVector.y = 0.0f;
                }
            } else if (absUpVector.y < absUpVector.z) {
                absUpVector.y = 1.0f;
                absUpVector.x = absUpVector.z = 0.0f;
            } else {
                absUpVector.z = 1.0f;
                absUpVector.x = absUpVector.y = 0.0f;
            }
            absUpVector.subtract(upVector, abUpMinUp);
            absUpVector.subtract(worldEmit, upXemit);
            float f4 = 2.0f / abUpMinUp.dot(abUpMinUp);
            float f6 = 2.0f / upXemit.dot(upXemit);
            float f8 = f4 * f6 * abUpMinUp.dot(upXemit);
            float af1[] = { abUpMinUp.x, abUpMinUp.y, abUpMinUp.z };
            float af2[] = { upXemit.x, upXemit.y, upXemit.z };
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    matData[i][j] = (-f4 * af1[i] * af1[j] - f6 * af2[i]
                            * af2[j])
                            + f8 * af2[i] * af1[j];

                }
                matData[i][i]++;
            }

        } else {
            upVector.cross(worldEmit, upXemit);
            float f2 = 1.0f / (1.0f + upDotEmit);
            float f5 = f2 * upXemit.x;
            float f7 = f2 * upXemit.z;
            float f9 = f5 * upXemit.y;
            float f10 = f5 * upXemit.z;
            float f11 = f7 * upXemit.y;
            matData[0][0] = upDotEmit + f5 * upXemit.x;
            matData[0][1] = f9 - upXemit.z;
            matData[0][2] = f10 + upXemit.y;
            matData[1][0] = f9 + upXemit.z;
            matData[1][1] = upDotEmit + f2 * upXemit.y * upXemit.y;
            matData[1][2] = f11 - upXemit.x;
            matData[2][0] = f10 - upXemit.y;
            matData[2][1] = f11 + upXemit.x;
            matData[2][2] = upDotEmit + f7 * upXemit.z;
        }
        rotMatrix.set(matData);
        oldEmit.set(worldEmit);
    }

    public ParticleController getParticleController() {
        return controller;
    }

    public void addController(Controller c) {
        super.addController(c);
        if (c instanceof ParticleController) {
            this.controller = (ParticleController) c;
        }
    }

    public Vector3f getEmissionDirection() {
        return emissionDirection;
    }

    public void setEmissionDirection(Vector3f emissionDirection) {
        this.emissionDirection = emissionDirection;
    }

    public float getEndSize() {
        return endSize;
    }

    public void setEndSize(float size) {
        endSize = size >= 0.0f ? size : 0.0f;
    }

    public float getStartSize() {
        return startSize;
    }

    public void setStartSize(float size) {
        startSize = size >= 0.0f ? size : 0.0f;
    }

    /**
     * Set the start color for particles. This is the base color of the quad.
     * 
     * @param color
     *            The start color.
     */
    public void setStartColor(ColorRGBA color) {
        this.startColor = color;
    }

    /**
     * <code>getStartColor</code> returns the starting color.
     * 
     * @return ColorRGBA The begining color.
     */
    public ColorRGBA getStartColor() {
        return startColor;
    }

    /**
     * Set the end color for particles. The base color of the quad will linearly
     * approach this color from the start color over the lifetime of the
     * particle.
     * 
     * @param color
     *            ColorRGBA The ending color.
     */
    public void setEndColor(ColorRGBA color) {
        this.endColor = color;
    }

    /**
     * getEndColor returns the ending color.
     * 
     * @return The ending color
     */
    public ColorRGBA getEndColor() {
        return endColor;
    }

    /**
     * Set a vector describing the force of gravity on a particle. Generally,
     * the values should be less than .01f
     * 
     * @param force
     *            Vector3f
     */
    public void setGravityForce(Vector3f force) {
        gravityForce.set(force);
    }

    /**
     * getGravityForce returns the gravity force.
     * 
     * @return The gravity force vector.
     */
    public Vector3f getGravityForce() {
        return gravityForce;
    }

    /**
     * Set the spinSpeed of new particles managed by this manager. Setting it to
     * 0 means no spin.
     * 
     * @param speed
     *            float
     */
    public void setParticleSpinSpeed(float speed) {
        particleSpinSpeed = speed;
    }

    /**
     * getParticleSpinSpeed returns the current spin speed of particles.
     * 
     * @return current spin speed of particles.
     */
    public float getParticleSpinSpeed() {
        return particleSpinSpeed;
    }

    /**
     * Set the "randomness" modifier. 0 = not random
     * 
     * @param mod
     *            The new randomness of particle information.
     */
    public void setRandomMod(float mod) {
        randomMod = mod;
    }

    /**
     * getRandomFactor returns the current randomness of particles.
     * 
     * @return float The current randomness.
     */
    public float getRandomMod() {
        return randomMod;
    }

    public Vector3f getInvScale() {
        return invScale;
    }

    public void setInvScale(Vector3f invScale) {
        this.invScale = invScale;
    }

    public void updateInvScale() {
        invScale.set(localScale);
        invScale.set(1f / invScale.x, 1f / invScale.y, 1f / invScale.z);
    }

    /**
     * Set the minimum angle (in radians) that particles can be emitted away
     * from the emission direction. Any angle less than 0 is trimmed to 0.
     * 
     * @param f
     *            The new emission minimum angle.
     */
    public void setMinimumAngle(float f) {
        minimumAngle = f >= 0.0f ? f : 0.0f;
    }

    /**
     * getEmissionMinimumAngle returns the minimum emission angle.
     * 
     * @return The minimum emission angle.
     */
    public float getMinimumAngle() {
        return minimumAngle;
    }

    /**
     * Set the maximum angle (in radians) that particles can be emitted away
     * from the emission direction. Any angle less than 0 is trimmed to 0.
     * 
     * @param f
     *            The new emission maximum angle.
     */
    public void setMaximumAngle(float f) {
        maximumAngle = f >= 0.0f ? f : 0.0f;
    }

    /**
     * getEmissionMaximumAngle returns the maximum emission angle.
     * 
     * @return The maximum emission angle.
     */
    public float getMaximumAngle() {
        return maximumAngle;
    }

    /**
     * Set the minimum lifespan of new particles (or recreated) managed by this
     * manager. if a value less than zero is given, 1.0f is used.
     * 
     * @param lifeSpan
     *            in ms
     */
    public void setMinimumLifeTime(float lifeSpan) {
        minimumLifeTime = lifeSpan >= 0.0f ? lifeSpan : 1.0f;
    }

    /**
     * getParticlesMinimumLifeTime returns the minimum life time of a particle.
     * 
     * @return The current minimum life time in ms.
     */
    public float getMinimumLifeTime() {
        return minimumLifeTime;
    }

    /**
     * Set the maximum lifespan of new particles (or recreated) managed by this
     * manager. if a value less than zero is given, 1.0f is used.
     * 
     * @param lifeSpan
     *            in ms
     */
    public void setMaximumLifeTime(float lifeSpan) {
        maximumLifeTime = lifeSpan >= 0.0f ? lifeSpan : 1.0f;
    }

    /**
     * getParticlesMaximumLifeTime returns the maximum life time of a particle.
     * 
     * @return The current maximum life time in ms.
     */
    public float getMaximumLifeTime() {
        return maximumLifeTime;
    }

    public Matrix3f getRotMatrix() {
        return rotMatrix;
    }

    public void setRotMatrix(Matrix3f rotMatrix) {
        this.rotMatrix = rotMatrix;
    }

    /**
     * Set the acceleration for any new particles created (or recreated) by this
     * manager.
     * 
     * @param velocity
     *            particle v0
     */
    public void setInitialVelocity(float velocity) {
        this.initialVelocity = velocity;
    }

    /**
     * Get the acceleration set in this manager.
     * 
     * @return The initialVelocity
     */
    public float getInitialVelocity() {
        return initialVelocity;
    }

    /**
     * Set the offset for any new particles created (or recreated) by this
     * manager. This is applicable only to managers generating from a point (not
     * a line, rectangle, etc..)
     * 
     * @param origin
     *            new origin position
     */
    public void setOriginOffset(Vector3f offset) {
        originOffset.set(offset);
    }

    /**
     * Get the offset point set in this manager.
     * 
     * @return origin
     */
    public Vector3f getOriginOffset() {
        return originOffset;
    }

    public Vector3f getWorldEmit() {
        return worldEmit;
    }

    public void setWorldEmit(Vector3f worldEmit) {
        this.worldEmit = worldEmit;
    }

    /**
     * Get the number of particles the manager should release per second.
     * 
     * @return The number of particles that should be released per second.
     */
    public int getReleaseRate() {
        return releaseRate;
    }

    /**
     * Set the number of particles the manager should release per second.
     * 
     * @param particlesPerSecond
     *            number of particles per second
     */
    public void setReleaseRate(int particlesPerSecond) {
        this.releaseRate = particlesPerSecond;
    }

    /**
     * Get which emittype method is being used by the underlying system. 0 =
     * point 1 = line 2 = rectangle 3 = trimesh
     * 
     * @return An int representing hte current geometry method being used.
     */
    public int getEmitType() {
        return emitType;
    }

    /**
     * Set which emittype method is being used by the underlying system. This is
     * already done by setGeometry(Line) and setGeometry(Rectangle) You should
     * not need to use this method unless you are switching between geometry
     * already set by those methods.
     * 
     * @param type
     *            Geometry type to use
     */
    public void setEmitType(int type) {
        emitType = type;
    }

    /**
     * Set a line segment to be used as the "emittor".
     * 
     * @param line
     *            New emittor line segment.
     */
    public void setGeometry(Line line) {
        psLine = line;
        emitType = ET_LINE;
    }

    /**
     * Set a rectangular patch to be used as the "emittor".
     * 
     * @param rect
     *            New rectangular patch.
     */
    public void setGeometry(Rectangle rect) {
        psRect = rect;
        emitType = ET_RECTANGLE;
    }

    /**
     * Set a GeomBatch's verts to be the random emission points
     * 
     * @param mesh
     *            The new geometry random verts.
     */
    public void setGeomBatch(GeomBatch batch) {
        psBatch = batch;
        emitType = ET_GEOMBATCH;
    }
    
    /**
     * Set a GeomBatch's verts to be the random emission points
     * 
     * @param mesh
     *            The new geometry random verts.
     */
    public void setGeometry(Geometry batch) {
        psBatch = batch.getBatch(0);
        emitType = ET_GEOMBATCH;
    }
    
    /**
     * Set a GeomBatch's verts to be the random emission points
     * 
     * @param mesh
     *            The new geometry random verts.
     */
    public void setGeometry(Geometry batch, int batchIndex) {
        psBatch = batch.getBatch(batchIndex);
        emitType = ET_GEOMBATCH;
    }

    /**
     * getLine returns the currently set line segment.
     * 
     * @return Current line segment.
     */
    public Line getLine() {
        return psLine;
    }

    /**
     * getRectangle returns the currently set rectangle segment.
     * 
     * @return Current rectangle segment.
     */
    public Rectangle getRectangle() {
        return psRect;
    }

    /**
     * getGeomBatch returns the currently set GeomBatch.
     * 
     * @return Current GeomBatch.
     */
    public GeomBatch getGeomBatch() {
        return psBatch;
    }

    public void updateLocation(int i) {
        switch (getEmitType()) {
            case ET_LINE:
                particles[i].location.set(getLine().random());
                break;
            case ET_RECTANGLE:
                particles[i].location.set(getRectangle().random());
                break;
            case ET_GEOMBATCH:
                if (getGeomBatch() != null && getGeomBatch() instanceof TriangleBatch)
                    ((TriangleBatch)getGeomBatch()).randomPointOnTriangles(particles[i].location, new Vector3f());
                else if (getGeomBatch() != null)
                    getGeomBatch().randomVertex(particles[i].location);
                break;
            case ET_POINT:
            default:
                particles[i].location.set(originCenter);
                break;
        }
        particles[i].location.multLocal(getInvScale());
    }

    public void recreateParticle(int i) {
        particles[i].getRandomSpeed(particles[i].getSpeed());
        particles[i].recreateParticle(particles[i].getSpeed(), particles[i]
                .getRandomLifeSpan());
        particles[i].status = Particle.ALIVE;
    }

    public void warmUp(int iterations) {
        if (controller != null) {
            controller.warmUp(iterations);
        }
    }

    public int getNumParticles() {
        return numParticles;
    }

    public void setNumParticles(int numParticles) {
        this.numParticles = numParticles;
    }

    public float getReleaseVariance() {
        if (controller != null) {
            return controller.getReleaseVariance();
        }
        return 0;
    }

    public void setReleaseVariance(float var) {
        if (controller != null) {
            controller.setReleaseVariance(var);
        }
    }

    /**
     * Changes the number of particles in this particle mesh.
     * 
     * @param count
     *            the desired number of particles to change to.
     */
    public void recreate(int count) {
        numParticles = count;
        initializeParticles(numParticles);
    }

    public boolean isRotateWithScene() {
        return rotateWithScene;
    }

    public void setRotateWithScene(boolean rotate) {
        this.rotateWithScene = rotate;
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(emitType, "emitType", ET_POINT);
        capsule.write(psLine, "psLine", null);
        capsule.write(psRect, "psRect", null);
        capsule.write(psBatch, "psBatch", null);
        capsule.write(startSize, "startSize", DEFAULT_START_SIZE);
        capsule.write(endSize, "endSize", DEFAULT_END_SIZE);
        capsule.write(startColor, "startColor", ColorRGBA.black);
        capsule.write(endColor, "endColor", ColorRGBA.black);
        capsule.write(initialVelocity, "initialVelocity", 1);
        capsule.write(minimumLifeTime, "minimumLifeTime", DEFAULT_MIN_LIFE);
        capsule.write(maximumLifeTime, "maximumLifeTime", DEFAULT_MAX_LIFE);
        capsule.write(minimumAngle, "minimumAngle", 0);
        capsule.write(maximumAngle, "maximumAngle", DEFAULT_MAX_ANGLE);
        capsule.write(particleSpinSpeed, "particleSpinSpeed", 0);
        capsule.write(emissionDirection, "emissionDirection", Vector3f.UNIT_Y);
        capsule.write(worldEmit, "worldEmit", Vector3f.ZERO);
        capsule.write(upVector, "upVector", Vector3f.UNIT_Y);
        capsule.write(numParticles, "numParticles", 0);
        capsule.write(gravityForce, "gravityForce", Vector3f.ZERO);
        capsule.write(randomMod, "randomMod", 1);
        capsule.write(rotateWithScene, "rotateWithScene", false);
        capsule.write(geometryCoordinates, "geometryCoordinates", null);
        capsule.write(appearanceColors, "appearanceColors", null);
        capsule.write(particles, "particles", null);
        capsule.write(releaseRate, "releaseRate", numParticles);
        capsule.write(originCenter, "originCenter", Vector3f.ZERO);
        capsule.write(originOffset, "originOffset", Vector3f.ZERO);
        capsule.write(controller, "controller", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        emitType = capsule.readInt("emitType",ET_POINT);
        psLine = (Line)capsule.readSavable("psLine", null);
        psRect = (Rectangle)capsule.readSavable("psRect", null);
        psBatch = (GeomBatch)capsule.readSavable("psBatch", null);
        startSize = capsule.readFloat("startSize", DEFAULT_START_SIZE);
        endSize = capsule.readFloat("endSize", DEFAULT_END_SIZE);
        startColor = (ColorRGBA)capsule.readSavable("startColor", new ColorRGBA(ColorRGBA.black));
        endColor = (ColorRGBA)capsule.readSavable("endColor", new ColorRGBA(ColorRGBA.black));
        initialVelocity = capsule.readFloat("initialVelocity", 1);
        minimumLifeTime = capsule.readFloat("minimumLifeTime", DEFAULT_MIN_LIFE);
        maximumLifeTime = capsule.readFloat("maximumLifeTime", DEFAULT_MAX_LIFE);
        minimumAngle = capsule.readFloat("minimumAngle", 0);
        maximumAngle = capsule.readFloat("maximumAngle", DEFAULT_MAX_ANGLE);
        particleSpinSpeed = capsule.readFloat("particleSpinSpeed", 0);
        emissionDirection = (Vector3f)capsule.readSavable("emissionDirection", new Vector3f(Vector3f.UNIT_Y));
        worldEmit = (Vector3f)capsule.readSavable("worldEmit", new Vector3f(Vector3f.ZERO));
        upVector = (Vector3f)capsule.readSavable("upVector", new Vector3f(Vector3f.UNIT_Y));
        numParticles = capsule.readInt("numParticles", 0);
        gravityForce = (Vector3f)capsule.readSavable("gravityForce", new Vector3f(Vector3f.ZERO));
        randomMod = capsule.readFloat("randomMod", 1);
        rotateWithScene = capsule.readBoolean("rotateWithScene", false);
        geometryCoordinates = capsule.readFloatBuffer("geometryCoordinates", null);
        appearanceColors = capsule.readFloatBuffer("appearanceColors", null);
        
        Savable[] savs = capsule.readSavableArray("particles", null);
        if (savs == null)
            particles = null;
        else {
            particles = new Particle[savs.length];
            for (int x = 0; x < savs.length; x++) {
                particles[x] = (Particle)savs[x];
            }
        }
        
        releaseRate = capsule.readInt("releaseRate", numParticles);
        originCenter = (Vector3f)capsule.readSavable("originCenter", new Vector3f());
        originOffset = (Vector3f)capsule.readSavable("originOffset", new Vector3f());
        controller = (ParticleController)capsule.readSavable("controller", null);
        
        invScale = new Vector3f();
        upXemit = new Vector3f();
        absUpVector = new Vector3f();
        abUpMinUp = new Vector3f();
        rotMatrix = new Matrix3f();
    }

    public boolean isActive() {
        return controller.isActive();
    }

    public void setSpeed(float f) {
        controller.setSpeed(f);
    }

    public void setRepeatType(int type) {
        controller.setRepeatType(type);
    }

    public void setControlFlow(boolean b) {
        controller.setControlFlow(b);
    }

    public Vector3f getOriginCenter() {
        return originCenter;
    }

    public Vector3f getUpVector() {
        return upVector;
    }

    public void setUpVector(Vector3f upVector) {
        this.upVector = upVector;
    }
    
    @Override
    public void updateWorldBound() {
        ; // ignore this since we want it to happen only when we say it can
            // happen due to world vectors not being used
    }
    public void updateWorldBoundManually() {
        super.updateWorldBound();
    }
}
