package com.jme.scene;

import java.io.IOException;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.jme.scene.TriMesh;
import com.jme.animation.SkinNode;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.state.RenderState;
import com.jme.renderer.Renderer;
import com.jme.util.export.StringFloatMap;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;
import com.jme.util.export.ListenableStringFloatMap;

/**
 * A MorphingGeometry implementation for TriMesh component morph Geometries.
 *
 * TODO:  Optimize the instantiation procedure.  It's pretty complicated to get
 * to reconstitute or instantiate local and delegated influences properly
 * without invoking expensive but unnecessary forceMorphs.
 *
 * @see #MorphingGeometry
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 */
public class MorphingTriMesh extends TriMesh implements MorphingGeometry {
    private static final Logger logger =
            Logger.getLogger(MorphingTriMesh.class.getName());

    protected List<TriMesh> morphs = new ArrayList<TriMesh>();
    protected List<String> morphKeys = new ArrayList<String>();
    // Not using an ordered Map of some type for these paired lists, only
    // because of the atrocious J2SE support for Maps which "preserve" order
    // but do not "apply" an order (I.e. to just preserve the order in which
    // the items are added).
    protected TriMesh primaryMorph;
    volatile private boolean dirty = true;

    /** This one may be a reference to a remotely managed map */
    protected ListenableStringFloatMap morphInfluencesMap;

    /** This one is persisted with this MorphingTriMesh instance */
    protected ListenableStringFloatMap localMorphInfluencesMap;

    /**
     * @see #MorphingGeometry#getMorphInfluencesMap()
     */
    public ListenableStringFloatMap getMorphInfluencesMap() {
        return morphInfluencesMap;
    }

    /**
     * @see #MorphingGeometry#setMorphInfluencesMap(ListenableStringFloatMap)
     */
    public void setMorphInfluencesMap(ListenableStringFloatMap m) {
        if (localMorphInfluencesMap == m) return;
        if (localMorphInfluencesMap != null)
            localMorphInfluencesMap.removeListener(this);
        localMorphInfluencesMap = m;
        localMorphInfluencesMap.addListener(this, morphKeys);
        morphInfluencesMap = localMorphInfluencesMap;
    }

    /**
     * @see #MorphingGeometry#setSingleMorphInfluence(String, float)
     */
    public void setSingleMorphInfluence(String morphKey, float influence) {
        if (morphInfluencesMap == null)
            throw new IllegalStateException("No morphInfluences set");
        morphInfluencesMap.put(morphKey, influence);
    }

    /**
     * @see #MorphingGeometry#setMorphInfluences(
     *           Map<? extends String, ? extends Float>)
     */
    public void setMorphInfluences(Map<? extends String, ? extends Float> m) {
        if (morphInfluencesMap == null)
            throw new IllegalStateException("No morphInfluences set");
        morphInfluencesMap.putAll(m);
    }

    /**
     * @see #MorphingGeometry#delegateInfluences()
     */
    public boolean delegateInfluences() {
        ListenableStringFloatMap newInfluencesMap = null;
        Node ancestor = null;
        for (ancestor = getParent(); ancestor != null;
                ancestor = ancestor.getParent()) {
            logger.log(Level.FINEST, "Trying anc '{0}'...", ancestor.getName());
            if (!(ancestor instanceof MorphInfluencesMapProvider)) continue;
            newInfluencesMap =
                ((MorphInfluencesMapProvider) ancestor).getMorphInfluencesMap();
            if (newInfluencesMap != null) break;
        }
        if (newInfluencesMap == null) {
            logger.info("Failed to find a parent node to delegate to");
            return false;
        }
        if (morphInfluencesMap == newInfluencesMap) {
            logger.fine("The first MorphInfluencesMapProvider found has the "
                    + "same MorphInfluencesMap that we are already using");
            return false;
        }
        morphInfluencesMap = newInfluencesMap;
        morphInfluencesMap.addListener(this, morphKeys);
        logger.log(Level.INFO, "Delegating influences to '{1}'", ancestor);
        return true;
    }

    /**
     * Constructor for internal use only.
     */
    public MorphingTriMesh() { }

    /**
     * Normal constructor.
     *
     * For local influence control, you should run <PRE><CODE>
     *     morphingTriMesh.addMorph(...);... // anytime before morph()ing
     *     morphingTriMesh.setMorphInfluencesMap(
     *             new ListenableStringFloatMap());
     *     morphingTriMesh.setMorphInfluences(...);
     *     morphingTriMesh.morph();
     * </CODE></PRE>
     * before attaching to a live scene.
     * For remote influence control, if you know there is an available
     * ancestor Node MorphInfluencesMapProvider, run
     * <PRE><CODE>
     *     morphingTriMesh.addMorph(...);... // anytime before morph()ing
     *     morphingTriMesh.delegateInfluences();
     *     morphingTriMesh.morph();
     * </CODE></PRE>
     * between attaching to the scene and rendering (like in a single update()
     * run).
     * <P>
     * If you want to delegate but aren't certain a capable ancestor is
     * at-hand, then run <PRE><CODE>
     *     morphingTriMesh.addMorph(...);... // anytime before morph()ing
     *     morphingTriMesh.setMorphInfluencesMap(
     *             new ListenableStringFloatMap());
     *     morphingTriMesh.setMorphInfluences(...);
     *     morphingTriMesh.delegateInfluences();
     *     morphingTriMesh.morph();
     * </CODE></PRE>
     * In this case, the local morphingInfluencesMap that you set up will be
     * overridden if the following delegateInfluences() call finds a provider.
     * </P>
     */
    public MorphingTriMesh(TriMesh primaryMorph) {
        super(primaryMorph.getName());
        this.primaryMorph = primaryMorph;
        initBase();
        logger.log(Level.FINE,
                "Primary morph set for MorphingTriMesh '{0}'", getName());
    }

    /**
     * @param morphGeo  Must be a TriMesh instance.
     * @see #MorphingGeometry#addMorph(String, Geometry)
     */
    public void addMorph(String morphKey, Geometry morphGeo) {
        if (primaryMorph == null)
            throw new IllegalStateException(
                    "Primary morph must be set before adding any others");
        // Validate that compatible with the primary morph.
        if (!(morphGeo instanceof TriMesh))
            throw new IllegalArgumentException(
                    "This class can only handle TriMeshes as morphs");
        TriMesh morph = (TriMesh) morphGeo;
        if (morph.getVertexCount() != primaryMorph.getVertexCount())
            throw new IllegalArgumentException(
                    "Trimesh " + morph.getName()
                    + " incompatible with primary Trimesh "
                    + primaryMorph.getName() + ".  Vertex counts "
                    + morph.getVertexCount() + " vs. "
                    + primaryMorph.getVertexCount());
        if (morph.getMaxIndex() != primaryMorph.getMaxIndex())
            throw new IllegalArgumentException(
                    "Trimesh " + morph.getName()
                    + " incompatible with primary Trimesh "
                    + primaryMorph.getName() + ".  Max indexes "
                    + morph.getMaxIndex() + " vs. "
                    + primaryMorph.getMaxIndex());
        if ((morph.getNormalBuffer() == null
                && primaryMorph.getNormalBuffer() != null)
                || (morph.getNormalBuffer() != null
                && primaryMorph.getNormalBuffer() == null))
            throw new IllegalArgumentException(
                "Normal buffer conflicts with Primary morph");

        enforceEquality("fog coords",
                primaryMorph.getFogBuffer(), morph.getFogBuffer());
        enforceEquality("tangent",
                primaryMorph.getTangentBuffer(), morph.getTangentBuffer());
        enforceEquality("binormal",
                primaryMorph.getBinormalBuffer(), morph.getBinormalBuffer());

        dirty = true;
        morphs.add(morph);
        morphKeys.add(morphKey);
        if (morphInfluencesMap != null)
            morphInfluencesMap.addListener(this, Arrays.asList(morphKey));
        logger.log(Level.FINE, "Added morph {0} to MorphingTriMesh '{1}':  {2}",
                new Object[] {
                morphs.size(), getName(), morph.getName()});
    }

    protected void enforceEquality(
            String label, FloatBuffer fb1, FloatBuffer fb2) {
        if (fb1 == fb2) return;
        if (fb1 == null || fb2 == null)
            throw new IllegalArgumentException(
                    "Incompatible " + label + " values (one is null)");
        logger.fine("fb1 pre.  Pos/Rem = " + fb1.position() + " / " + fb1.remaining());
        // TODO:  Remove log statement once verify .equals() does not modify
        // position or remaining.
        if (!fb1.equals(fb2))
            throw new IllegalArgumentException(
                    "Incompatible " + label + " values");
        logger.fine("fb1 post.  Pos/Rem = " + fb1.position() + " / " + fb1.remaining());
        // TODO:  Remove log statement once verify .equals() does not modify
        // position or remaining.
    }

    /**
     * Replaces data other than merge data, by copying from the virgin
     * primary morph TriMesh.
     */
    public void initBase() {
        if (primaryMorph == null)
            throw new IllegalStateException(
                    "Can't initBase when no Geometry has been assigned");
        logger.fine("Initializing base...");
        dirty = true;
        TriMesh primary = primaryMorph; // Just for brevity below
        RenderState renderState;

        // Set scalars according to primary morph
        setName(primary.getName());
        setMode(primary.getMode());
        setDefaultColor(primary.getDefaultColor());
        setLightState(primary.getLightState());
        setCastsShadows(primary.isCastsShadows());
        for (Controller c : primary.getControllers()) addController(c);
        setLocalTranslation(new Vector3f(primary.getLocalTranslation()));
        setLocalScale(new Vector3f(primary.getLocalScale()));
        setLocalRotation(new Quaternion(primary.getLocalRotation()));
        setZOrder(primary.getZOrder(), false);
        setCullHint(primary.getLocalCullHint());
        setTextureCombineMode(primary.getLocalTextureCombineMode());
        setLightCombineMode(primary.getLocalLightCombineMode());
        setRenderQueueMode(primary.getRenderQueueMode());
        setNormalsMode(primary.getLocalNormalsMode());
        setIsCollidable(primary.isCollidable());
        setRenderQueueMode(primary.getLocalRenderQueueMode());
        for (RenderState.StateType rsType : RenderState.StateType.values()) {
            clearRenderState(rsType);
            renderState = primary.getRenderState(rsType);
            if (renderState != null) setRenderState(renderState);
        }
        setIndexBuffer(primaryMorph.getIndexBuffer());
        setTextureCoords(primaryMorph.getTextureCoords());
        setColorBuffer(primaryMorph.getColorBuffer());
        setVBOInfo(primaryMorph.getVBOInfo());

        setLocks(primary.getLocks());
        logger.info("Base initialized successfully");
    }

    /**
     * @see #MorphingGeometry#morph()
     */
    public void morph() {
        if (dirty) forceMorph();
    }

    /**
     * @see #MorphingGeometry#forceMorph()
     */
    public void forceMorph() {
        try {
        if (morphs.size() != morphKeys.size())
            throw new AssertionError(
                    "Morph dimensions != morph keys:  "
                    + morphs.size() + " vs. " + morphKeys.size());
        List<FloatBuffer> vertBuffers = new ArrayList<FloatBuffer>();
        List<FloatBuffer> normBuffers = new ArrayList<FloatBuffer>();
        float[] infs = new float[morphKeys.size()];
        Float tmpF;
        if (morphInfluencesMap == null)
            throw new IllegalStateException(
                    "morphInfluencesMap must be non-null");
        for (int i = 0; i < morphKeys.size(); i++) {
            tmpF = morphInfluencesMap.get(morphKeys.get(i));
            if (tmpF == null)
                throw new IllegalStateException(
                        "Morph influence not set for required key: "
                        + morphKeys.get(i));
            infs[i] = tmpF.floatValue();
        }
        for (TriMesh morph : morphs) {
            vertBuffers.add(morph.getVertexBuffer());
            normBuffers.add(morph.getNormalBuffer());
            // If more buffers need to be merged, add them here
        }
        logger.log(Level.INFO, "Morphing ''{0}'' with influences:  {1}",
                new String[] {getName(), Arrays.toString(infs)});

        setVertexBuffer(mergeBuffers(
                primaryMorph.getVertexBuffer(), vertBuffers, infs));
        setNormalBuffer(mergeBuffers(
                primaryMorph.getNormalBuffer(), normBuffers, infs));

        //setModelBound(primary.getModelBound()); // Regenereate a BV
        setHasDirtyVertices(true);  // necessary?
        // Need to scaleTextureCoordinates()?
        dirty = false;
        } catch (RuntimeException re) {
            if (autoMorph) {
                autoMorph = false;
                logger.warning("autoMorphing disabled.  "
                        + "Turn it back on after you fix your problem");
                throw re;
            }
        }
        SkinNode skinNode = getSkinNode();
        if (getSkinNode() != null) skinNode.regenInfluenceOffsets(this);
    }

    /**
     * @see MorphingGeometry#getSkinNode()
     */
    public SkinNode getSkinNode() {
        return (getParent() != null && getParent().getParent() != null
                && (getParent().getParent() instanceof SkinNode)
                && ((SkinNode) getParent().getParent()).hasSkinGeometry(
                        getName(), null))
                ? ((SkinNode) getParent().getParent()) : null;
    }

    /**
     * Assumes that influence.length == morphBuffers.size() - 1
     *
     * @return null if all the input buffers are null.
     */
    protected FloatBuffer mergeBuffers(FloatBuffer primaryBuffer,
            List<FloatBuffer> morphBuffers, float[] influences) {
        for (int i = 0; i < morphBuffers.size(); i++) {
            if (primaryBuffer == null && morphBuffers.get(i) != null)
                throw new IllegalStateException("Buffer mismatch (A)");
            if (primaryBuffer != null && morphBuffers.get(i) == null)
                throw new IllegalStateException("Buffer mismatch (B)");
        }
        if (primaryBuffer == null) return null;
        FloatBuffer outBuffer =
                BufferUtils.createFloatBuffer(primaryBuffer.capacity());
        float f0, f;
        while (outBuffer.hasRemaining()) {
            f = f0 = primaryBuffer.get();
            for (int i = 0; i < morphBuffers.size(); i++)
                f += influences[i] * (morphBuffers.get(i).get() - f0);
            outBuffer.put(f);
        }
        primaryBuffer.flip();
        for (FloatBuffer fb : morphBuffers) fb.flip();
        outBuffer.flip();
        return outBuffer;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.writeSavableArrayList(
                new ArrayList(morphs), "morphs", null);
        capsule.write(morphKeys.toArray(new String[0]), "morphKeys", null);
        capsule.write(primaryMorph, "primaryMorph", null);
        capsule.write(localMorphInfluencesMap, "morphInfluences", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        morphs = capsule.readSavableArrayList("morphs", null);
        String[] morphKeysArray = capsule.readStringArray("morphKeys", null);
        if (morphKeysArray != null) morphKeys = Arrays.asList(morphKeysArray);
        primaryMorph = (TriMesh) capsule.readSavable("primaryMorph", null);
        if (getVertexBuffer() == null) initBase();
            // A good clue that the MorphingTriMesh needs to be initialized.
        setMorphInfluencesMap((ListenableStringFloatMap)
                capsule.readSavable("morphInfluences", null));
        if (morphInfluencesMap != null) forceMorph();
        // If want to override the local map with delegation, will have to
        // manually change the map then re-morph.
        autoMorph = true;
    }

    public void floatChanged(StringFloatMap sfm) {
        dirty = true;
    }

    public void draw(Renderer r) {
        //if (dirty) System.err.print('d'); // TODO: REMOVE BEFORE PRODUCTION
        if (!dirty) super.draw(r);
    }

    /**
     * @see #MorphingGeometry#setAutoMorph(boolean)
     */
    public void setAutoMorph(boolean autoMorph) {
        this.autoMorph = autoMorph;
    }

    protected boolean autoMorph;

    public void updateGeometricState(float time, boolean initiator) {
        if (autoMorph && dirty) forceMorph();
        super.updateGeometricState(time, initiator);
    }

    /**
     * This behavior is pretty invasive.
     * <P>
     * Trying to settle on a behavior which is very automated in normal
     * circumstances, but allows developers to override everything in a
     * consistent and reliable way.
     * </P>
     *
     * <b>Behavior here is subject to change</b>.
    public void setParent(Node parent) {
        super.setParent(parent);
        // Make an attempt to set up an influences map if we lack one.
        if (morphInfluencesMap != null) return;
        delegateInfluences();
        if (morphInfluencesMap != null) autoMorph = true;
    }
     */
}
