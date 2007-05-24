/*
 * Copyright (c) 2003-2007 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.scene;

import java.io.IOException;
import java.io.Serializable;
import java.util.Stack;

import com.jme.bounding.BoundingVolume;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>SceneElement</code> defines the base class for all elements of a
 * scene graph.
 * 
 * @author Joshua Slack
 * @author Mark Powell
 * @version $Id: SceneElement.java,v 1.9 2007-05-24 20:55:25 nca Exp $
 */
public abstract class SceneElement implements Serializable, Savable {

    public static final int NODE = 1;
    public static final int GEOMETRY = 2;
    public static final int TRIMESH = 4;
    public static final int SHARED_MESH = 8;
    public static final int SKY_BOX = 16;
    public static final int TERRAIN_BLOCK = 32;
    public static final int TERRAIN_PAGE = 64;
    public static final int COMPOSITE_MESH = 128;
    public static final int GEOMBATCH = 256;
    public static final int TRIANGLEBATCH = 512;
    public static final int SHAREDBATCH = 1024;
    public static final int QUADBATCH = 2048;

    public static final int CULL_INHERIT = 0;
    public static final int CULL_DYNAMIC = 1;
    public static final int CULL_ALWAYS = 2;
    public static final int CULL_NEVER = 3;

    public static final int LOCKED_NONE = 0;
    public static final int LOCKED_BOUNDS = 1;
    public static final int LOCKED_MESH_DATA = 2;
    public static final int LOCKED_TRANSFORMS = 4;
    public static final int LOCKED_SHADOWS = 8;
    public static final int LOCKED_BRANCH = 16;

    public static final int NM_INHERIT = 0;
    public static final int NM_USE_PROVIDED = 1;
    public static final int NM_GL_NORMALIZE_PROVIDED = 2;
    public static final int NM_GL_NORMALIZE_IF_SCALED = 3;
    public static final int NM_OFF = 4;

    /**
     * A flag indicating how normals should be treated by the renderer.
     */
    protected int normalsMode = NM_INHERIT;

    /**
     * A flag indicating if scene culling should be done on this object by
     * inheritance, dynamically, never, or always.
     */
    protected int cullMode = CULL_INHERIT;

    /** Spatial's bounding volume relative to the world. */
    protected BoundingVolume worldBound;

    /** The render states of this spatial. */
    protected RenderState[] renderStateList;

    protected int renderQueueMode = Renderer.QUEUE_INHERIT;

    /** Used to determine draw order for ortho mode rendering. */
    protected int zOrder = 0;

    /**
     * Used to indicate this spatial (and any below it in the case of Node) is
     * locked against certain changes.
     */
    protected int lockedMode = LOCKED_NONE;

    /**
     * Flag signaling how lights are combined for this node. By default set to
     * INHERIT.
     */
    protected int lightCombineMode = LightState.INHERIT;

    /**
     * Flag signaling how textures are combined for this node. By default set to
     * INHERIT.
     */
    protected int textureCombineMode = TextureState.INHERIT;

    /** This spatial's name. */
    protected String name;

    // scale values
    protected int frustrumIntersects = Camera.INTERSECTS_FRUSTUM;

    /**
     * Defines if this spatial will be used in intersection operations or not.
     * Default is true
     */
    protected boolean isCollidable = true;

    public transient float queueDistance = Float.NEGATIVE_INFINITY;

    private static final long serialVersionUID = 1;

    /**
     * Sets the name of this spatial.
     * 
     * @param name
     *            The spatial's new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this spatial.
     * 
     * @return This spatial's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets if this Spatial is to be used in intersection (collision and
     * picking) calculations. By default this is true.
     * 
     * @param isCollidable
     *            true if this Spatial is to be used in intersection
     *            calculations, false otherwise.
     */
    public void setIsCollidable(boolean isCollidable) {
        this.isCollidable = isCollidable;
    }

    /**
     * Defines if this Spatial is to be used in intersection (collision and
     * picking) calculations. By default this is true.
     * 
     * @return true if this Spatial is to be used in intersection calculations,
     *         false otherwise.
     */
    public boolean isCollidable() {
        return this.isCollidable;
    }

    /**
     * <code>getWorldBound</code> retrieves the world bound at this node
     * level.
     * 
     * @return the world bound at this level.
     */
    public BoundingVolume getWorldBound() {
        return worldBound;
    }

    /**
     * <code>getType</code> returns an int representing the class type of this
     * SceneElement. This allows avoidance of instanceof. Comparisons are to be
     * done via bitwise & allowing checking of superclass instance.
     */
    public abstract int getType();

    /**
     * <code>draw</code> abstract method that handles drawing data to the
     * renderer if it is geometry and passing the call to it's children if it is
     * a node.
     * 
     * @param r
     *            the renderer used for display.
     */
    public abstract void draw(Renderer r);

    /**
     * <code>setCullMode</code> sets how scene culling should work on this
     * spatial during drawing. CULL_DYNAMIC: Determine via the defined Camera
     * planes whether or not this Spatial should be culled. CULL_ALWAYS: Always
     * throw away this object and any children during draw commands. CULL_NEVER:
     * Never throw away this object (always draw it) CULL_INHERIT: Look for a
     * non-inherit parent and use its cull mode. NOTE: You must set this AFTER
     * attaching to a parent or it will be reset with the parent's cullMode
     * value.
     * 
     * @param mode
     *            one of CULL_DYNAMIC, CULL_ALWAYS, CULL_INHERIT or CULL_NEVER
     */
    public void setCullMode(int mode) {
        cullMode = mode;
    }

    /**
     * @return the cullmode set on this Spatial
     */
    public int getLocalCullMode() {
        return cullMode;
    }

    /**
     * 
     * @return
     */
    public abstract int getCullMode();

    /**
     * 
     * @param time
     * @param initiator
     */
    public abstract void updateGeometricState(float time, boolean initiator);

    /**
     * Calling this method tells the scenegraph that it is not necessary to
     * update bounds from this point in the scenegraph on down to the leaves.
     * This is useful for performance gains where you have scene items that do
     * not move (at all) or change shape and thus do not need constant
     * re-calculation of boundaries. When you call lock, the bounds are first
     * updated to ensure current bounds are accurate.
     * 
     * @see #unlockBounds()
     */
    public void lockBounds() {
        updateGeometricState(0, true);
        lockedMode |= LOCKED_BOUNDS;
    }

    /**
     * Calling this method tells the scenegraph that it is not necessary to
     * update Shadow volumes that may be associated with this SceneElement. This
     * is useful for skipping various checks for spatial transformation when
     * deciding whether or not to recalc a shadow volume for a SceneElement.
     * 
     * @see #unlockShadows()
     */
    public void lockShadows() {
        lockedMode |= LOCKED_SHADOWS;
    }

    /**
     * Calling this method tells the scenegraph that it is not necessary to
     * traverse this SceneElement or any below it during the update phase. This
     * should be called *after* any other lock call to ensure they are able to
     * update any bounds or vectors they might need to update.
     * 
     * @see #unlockBranch()
     */
    public void lockBranch() {
        lockedMode |= LOCKED_BRANCH;
    }

    /**
     * Flags this spatial and those below it in the scenegraph to not
     * recalculate world transforms such as translation, rotation and scale on
     * every update. This is useful for efficiency when you have scene items
     * that stay in one place all the time as it avoids needless recalculation
     * of transforms.
     * 
     * @see #unlockTransforms()
     */
    public void lockTransforms() {
        lockedMode |= LOCKED_TRANSFORMS;
    }

    /**
     * Flags this spatial and those below it that any meshes in the specified
     * scenegraph location or lower will not have changes in vertex, texcoord,
     * normal or color data. This allows optimizations by the engine such as
     * creating display lists from the data. Calling this method does not
     * provide a guarentee that data changes will not be allowed or will/won't
     * show up in the scene. It is merely a hint to the engine.
     * 
     * @param r
     *            A renderer to lock against.
     * @see #unlockMeshes(Renderer)
     */
    public void lockMeshes(Renderer r) {
        updateRenderState();
        lockedMode |= LOCKED_MESH_DATA;
    }

    /**
     * Flags this spatial and those below it that any meshes in the specified
     * scenegraph location or lower will not have changes in vertex, texcoord,
     * normal or color data. This allows optimizations by the engine such as
     * creating display lists from the data. Calling this method does not
     * provide a guarentee that data changes will not be allowed or will/won't
     * show up in the scene. It is merely a hint to the engine. Calls
     * lockMeshes(Renderer) with the current display system's renderer.
     * 
     * @see #lockMeshes(Renderer)
     */
    public void lockMeshes() {
        lockMeshes(DisplaySystem.getDisplaySystem().getRenderer());
    }

    /**
     * Convienence function for locking all aspects of a SceneElement.
     * 
     * @see #lockBounds()
     * @see #lockTransforms()
     * @see #lockMeshes(Renderer)
     * @see #lockShadows()
     */
    public void lock(Renderer r) {
        lockBounds();
        lockTransforms();
        lockMeshes(r);
        lockShadows();
    }

    /**
     * Convienence function for locking all aspects of a SceneElement. For
     * lockMeshes it calls:
     * <code>lockMeshes(DisplaySystem.getDisplaySystem().getRenderer());</code>
     * 
     * @see #lockBounds()
     * @see #lockTransforms()
     * @see #lockMeshes()
     * @see #lockShadows()
     */
    public void lock() {
        lockBounds();
        lockTransforms();
        lockMeshes();
        lockShadows();
    }

    /**
     * Flags this spatial and those below it to allow for bounds updating (the
     * default).
     * 
     * @see #lockBounds()
     */
    public void unlockBounds() {
        lockedMode &= ~LOCKED_BOUNDS;
    }

    /**
     * Flags this spatial and those below it to allow for shadow volume updates
     * (the default).
     * 
     * @see #lockShadows()
     */
    public void unlockShadows() {
        lockedMode &= ~LOCKED_SHADOWS;
    }

    /**
     * Flags this SceneElement and any below it as being traversable during the
     * update phase.
     * 
     * @see #lockBranch()
     */
    public void unlockBranch() {
        lockedMode &= ~LOCKED_BRANCH;
    }

    /**
     * Flags this spatial and those below it to allow for transform updating
     * (the default).
     * 
     * @see #lockTransforms()
     */
    public void unlockTransforms() {
        lockedMode &= ~LOCKED_TRANSFORMS;
    }

    /**
     * Flags this spatial and those below it to allow for mesh updating (the
     * default). Generally this means that any display lists setup will be
     * erased and released. Calls unlockMeshes(Renderer) with the current
     * display system's renderer.
     * 
     * @see #unlockMeshes(Renderer)
     */
    public void unlockMeshes() {
        unlockMeshes(DisplaySystem.getDisplaySystem().getRenderer());
    }

    /**
     * Flags this spatial and those below it to allow for mesh updating (the
     * default). Generally this means that any display lists setup will be
     * erased and released.
     * 
     * @param r
     *            The renderer used to lock against.
     * @see #lockMeshes(Renderer)
     */
    public void unlockMeshes(Renderer r) {
        lockedMode &= ~LOCKED_MESH_DATA;
    }

    /**
     * Convienence function for unlocking all aspects of a SceneElement.
     * 
     * @see #unlockBounds()
     * @see #unlockTransforms()
     * @see #unlockMeshes(Renderer)
     * @see #unlockShadows()
     * @see #unlockBranch()
     */
    public void unlock(Renderer r) {
        unlockBounds();
        unlockTransforms();
        unlockMeshes(r);
        unlockShadows();
        unlockBranch();
    }

    /**
     * Convienence function for unlocking all aspects of a SceneElement. For
     * unlockMeshes it calls:
     * <code>unlockMeshes(DisplaySystem.getDisplaySystem().getRenderer());</code>
     * 
     * @see #unlockBounds()
     * @see #unlockTransforms()
     * @see #unlockMeshes()
     * @see #unlockShadows()
     * @see #unlockBranch()
     */
    public void unlock() {
        unlockBounds();
        unlockTransforms();
        unlockMeshes();
        unlockShadows();
        unlockBranch();
    }

    /**
     * @return a bitwise combination of the current locks established on this
     *         SceneElement.
     */
    public int getLocks() {
        return lockedMode;
    }

    /**
     * Note: Uses the currently set Renderer to generate a display list if
     * LOCKED_MESH_DATA is set.
     * 
     * @param locks
     *            a bitwise combination of the locks to establish on this
     *            SceneElement.
     */
    public void setLocks(int locks) {
        if ((lockedMode & SceneElement.LOCKED_BOUNDS) != 0)
            lockBounds();
        if ((lockedMode & SceneElement.LOCKED_MESH_DATA) != 0)
            lockMeshes();
        if ((lockedMode & SceneElement.LOCKED_SHADOWS) != 0)
            lockShadows();
        if ((lockedMode & SceneElement.LOCKED_TRANSFORMS) != 0)
            lockTransforms();
    }

    /**
     * @param locks
     *            a bitwise combination of the locks to establish on this
     *            SceneElement.
     * @param r
     *            the renderer to create display lists with if LOCKED_MESH_DATA
     *            is set.
     */
    public void setLocks(int locks, Renderer r) {
        if ((lockedMode & SceneElement.LOCKED_BOUNDS) != 0)
            lockBounds();
        if ((lockedMode & SceneElement.LOCKED_MESH_DATA) != 0)
            lockMeshes(r);
        if ((lockedMode & SceneElement.LOCKED_SHADOWS) != 0)
            lockShadows();
        if ((lockedMode & SceneElement.LOCKED_TRANSFORMS) != 0)
            lockTransforms();
    }

    /**
     * <code>updateWorldBound</code> updates the bounding volume of the world.
     * Abstract, geometry transforms the bound while node merges the children's
     * bound. In most cases, users will want to call updateModelBound() and let
     * this function be called automatically during updateGeometricState().
     */
    public abstract void updateWorldBound();

    /**
     * Updates the render state values of this Spatial and and children it has.
     * Should be called whenever render states change.
     */
    public void updateRenderState() {
        updateRenderState(null);
    }

    /**
     * Called internally. Updates the render states of this SceneElement. The
     * stack contains parent render states.
     * 
     * @param parentStates
     *            The list of parent renderstates.
     */
    @SuppressWarnings("unchecked")
    protected void updateRenderState(Stack[] parentStates) {
        boolean initiator = (parentStates == null);

        // first we need to get all the states from parent to us.
        if (initiator) {
            // grab all states from root to here.
            parentStates = new Stack[RenderState.RS_MAX_STATE];
            for (int x = 0; x < parentStates.length; x++)
                parentStates[x] = new Stack<RenderState>();
            propagateStatesFromRoot(parentStates);
        } else {
            for (int x = 0; x < RenderState.RS_MAX_STATE; x++) {
                if (getRenderState(x) != null)
                    parentStates[x].push(getRenderState(x));
            }
        }

        applyRenderState(parentStates);

        // restore previous if we are not the initiator
        if (!initiator) {
            for (int x = 0; x < RenderState.RS_MAX_STATE; x++)
                if (getRenderState(x) != null)
                    parentStates[x].pop();
        }

    }

    /**
     * Called during updateRenderState(Stack[]), this function determines how
     * the render states are actually applied to the spatial and any children it
     * may have. By default, this function does nothing.
     * 
     * @param states
     *            An array of stacks for each state.
     */
    protected void applyRenderState(Stack[] states) {
    }

    /**
     * Called during updateRenderState(Stack[]), this function goes up the scene
     * graph tree until the parent is null and pushes RenderStates onto the
     * states Stack array.
     * 
     * @param states
     *            The Stack[] to push states onto.
     */
    public void propagateStatesFromRoot(Stack[] states) {
    }

    /**
     * <code>propagateBoundToRoot</code> passes the new world bound up the
     * tree to the root.
     */
    public void propagateBoundToRoot() {
    }

    /**
     * <code>setRenderState</code> sets a render state for this node. Note,
     * there can only be one render state per type per node. That is, there can
     * only be a single AlphaState a single TextureState, etc. If there is
     * already a render state for a type set the old render state will be
     * returned. Otherwise, null is returned.
     * 
     * @param rs
     *            the render state to add.
     * @return the old render state.
     */
    public RenderState setRenderState(RenderState rs) {
        if (rs == null) {
            return null;
        }

        if (renderStateList == null) {
            renderStateList = new RenderState[RenderState.RS_MAX_STATE];
        }

        RenderState oldState = renderStateList[rs.getType()];
        renderStateList[rs.getType()] = rs;
        return oldState;
    }

    /**
     * Returns the requested RenderState that this Spatial currently has set or
     * null if none is set.
     * 
     * @param type
     *            the renderstate type to retrieve
     * @return a renderstate at the given position or null
     */
    public RenderState getRenderState(int type) {
        return renderStateList != null ? renderStateList[type] : null;
    }

    /**
     * Clears a given render state index by setting it to null.
     * 
     * @param renderStateType
     *            The index of a RenderState to clear
     * @see com.jme.scene.state.RenderState#getType()
     */
    public void clearRenderState(int renderStateType) {
        if (renderStateList != null) {
            renderStateList[renderStateType] = null;
        }
    }

    /**
     * <code>setRenderQueueMode</code> determines at what phase of the
     * rendering proces this Spatial will rendered. There are 4 different
     * phases: QUEUE_SKIP - The spatial will be drawn as soon as possible,
     * before the other phases of rendering. QUEUE_OPAQUE - The renderer will
     * try to find the optimal order for rendering all objects using this mode.
     * You should use this mode for most normal objects, except transparant
     * ones, as it could give a nice performance boost to your application.
     * QUEUE_TRANSPARENT - This is the mode you should use for object with
     * transparancy in them. It will ensure the objects furthest away are
     * rendered first. That ensures when another transparent object is drawn on
     * top of previously drawn objects, you can see those (and the object drawn
     * using SKIP and OPAQUE) through the tranparant parts of the newly drawn
     * object. QUEUE_ORTHO - This is a special mode, for drawing 2D object
     * without prespective (such as GUI or HUD parts) Lastly, there is a special
     * mode, QUEUE_INHERIT, that will ensure that this spatial uses the same
     * mode as the parent Node does.
     * 
     * @param renderQueueMode
     *            The mode to use for this SceneElement.
     */
    public void setRenderQueueMode(int renderQueueMode) {
        this.renderQueueMode = renderQueueMode;
    }

    /**
     * 
     * @return
     */
    public int getLocalRenderQueueMode() {
        return renderQueueMode;
    }

    /**
     * 
     * @return
     */
    public abstract int getRenderQueueMode();

    /**
     * 
     * @param zOrder
     */
    public void setZOrder(int zOrder) {
        this.zOrder = zOrder;
    }

    /**
     * 
     * @return
     */
    public int getZOrder() {
        return zOrder;
    }

    /**
     * 
     * @return
     */
    public abstract int getNormalsMode();

    /**
     * 
     * @return
     */
    public int getLocalNormalsMode() {
        return normalsMode;
    }

    /**
     * 
     * @param mode
     */
    public void setNormalsMode(int mode) {
        this.normalsMode = mode;
    }

    /**
     * Sets how lights from parents should be combined for this spatial.
     * 
     * @param lightCombineMode
     *            The light combine mode for this spatial
     * @see com.jme.scene.state.LightState#COMBINE_CLOSEST
     * @see com.jme.scene.state.LightState#COMBINE_FIRST
     * @see com.jme.scene.state.LightState#COMBINE_RECENT_ENABLED
     * @see com.jme.scene.state.LightState#INHERIT
     * @see com.jme.scene.state.LightState#OFF
     * @see com.jme.scene.state.LightState#REPLACE
     */
    public void setLightCombineMode(int lightCombineMode) {
        this.lightCombineMode = lightCombineMode;
    }

    /**
     * @return the lightCombineMode set on this Spatial
     */
    public int getLocalLightCombineMode() {
        return lightCombineMode;
    }

    /**
     * 
     * @return
     */
    public abstract int getLightCombineMode();

    /**
     * Sets how textures from parents should be combined for this SceneElement.
     * 
     * @param textureCombineMode
     *            The new texture combine mode for this spatial.
     * @see com.jme.scene.state.TextureState#COMBINE_CLOSEST
     * @see com.jme.scene.state.TextureState#COMBINE_FIRST
     * @see com.jme.scene.state.TextureState#COMBINE_RECENT_ENABLED
     * @see com.jme.scene.state.TextureState#INHERIT
     * @see com.jme.scene.state.TextureState#OFF
     * @see com.jme.scene.state.TextureState#REPLACE
     */
    public void setTextureCombineMode(int textureCombineMode) {
        this.textureCombineMode = textureCombineMode;
    }

    /**
     * @return the textureCombineMode set on this Spatial
     */
    public int getLocalTextureCombineMode() {
        return textureCombineMode;
    }

    /**
     * 
     * @return
     */
    public abstract int getTextureCombineMode();

    /**
     * Returns this spatial's last frustum intersection result. This int is set
     * when a check is made to determine if the bounds of the object fall inside
     * a camera's frustum. If a parent is found to fall outside the frustum, the
     * value for this spatial will not be updated. Possible values include:
     * Camera.OUTSIDE_FRUSTUM, Camera.INTERSECTS_FRUSTUM, and
     * Camera.INSIDE_FRUSTUM
     * 
     * @return The spatial's last frustum intersection result.
     */
    public int getLastFrustumIntersection() {
        return frustrumIntersects;
    }

    /**
     * Overrides the last intersection result. This is useful for operations
     * that want to start rendering at the middle of a scene tree and don't want
     * the parent of that node to influence culling. (See texture renderer code
     * for example.) Possible values include: Camera.OUTSIDE_FRUSTUM,
     * Camera.INTERSECTS_FRUSTUM, and Camera.INSIDE_FRUSTUM
     * 
     * @param intersects
     *            the new value, one of those given above.
     */
    public void setLastFrustumIntersection(int intersects) {
        frustrumIntersects = intersects;
    }

    /**
     * Returns the Spatial's name followed by the class of the spatial <br>
     * Example: "MyNode (com.jme.scene.Spatial)
     * 
     * @return Spatial's name followed by the class of the Spatial
     */
    public String toString() {
        return name + " (" + this.getClass().getName() + ')';
    }

    public void write(JMEExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(name, "name", null);
        capsule.write(isCollidable, "isCollidable", true);
        capsule.write(cullMode, "cullMode", CULL_INHERIT);

        capsule.write(renderQueueMode, "renderQueueMode",
                Renderer.QUEUE_INHERIT);
        capsule.write(zOrder, "zOrder", 0);
        capsule.write(lightCombineMode, "lightCombineMode", LightState.INHERIT);
        capsule.write(textureCombineMode, "textureCombineMode",
                TextureState.INHERIT);
        capsule.write(normalsMode, "normalsMode", NM_INHERIT);
        capsule.write(renderStateList, "renderStateList", null);
    }

    public void read(JMEImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        name = capsule.readString("name", null);
        isCollidable = capsule.readBoolean("isCollidable", true);
        cullMode = capsule.readInt("cullMode", CULL_INHERIT);

        renderQueueMode = capsule.readInt("renderQueueMode",
                Renderer.QUEUE_INHERIT);
        zOrder = capsule.readInt("zOrder", 0);
        lightCombineMode = capsule.readInt("lightCombineMode",
                LightState.INHERIT);
        textureCombineMode = capsule.readInt("textureCombineMode",
                TextureState.INHERIT);
        normalsMode = capsule.readInt("normalsMode", NM_INHERIT);

        Savable[] savs = capsule.readSavableArray("renderStateList", null);
        if (savs == null)
            renderStateList = null;
        else {
            renderStateList = new RenderState[savs.length];
            for (int x = 0; x < savs.length; x++) {
                renderStateList[x] = (RenderState) savs[x];
            }
        }
    }

    public Class getClassTag() {
        return this.getClass();
    }
}
