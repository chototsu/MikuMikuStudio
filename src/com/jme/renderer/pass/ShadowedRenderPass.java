/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jme.renderer.pass;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import com.jme.light.Light;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shadow.MeshShadows;
import com.jme.scene.shadow.ShadowVolume;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.ColorMaskState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.StencilState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;

/**
 * <code>ShadowedRenderPass</code> is a render pass that renders the added
 * spatials along with shadows cast by givens occluders and lights flagged as
 * casting shadows.
 *
 * @author Mike Talbot (some code for MODULATIVE method written Jan 2005)
 * @author Joshua Slack
 * @version $Id: ShadowedRenderPass.java,v 1.5 2005-12-05 19:28:44 renanse Exp $
 */
public class ShadowedRenderPass extends Pass {

   private static final long serialVersionUID = 1L;

   /**
    * value for lightingMethod indicating that a scene should be rendered first
    * with ambient lighting and then multiple passes per light done to
    * illuminate unshadowed areas (resulting in shadows.) More costly but more
    * accurate than MODULATIVE.
    */
   public final static int ADDITIVE = 0;

   /**
    * value for lightingMethod indicating that a scene should be rendered first
    * with full lighting and then multiple screens applied per light to darken
    * shadowed areas. More prone to artifacts than ADDITIVE, but faster.
    */
   public final static int MODULATIVE = 1;

   /** list of occluders registered with this pass. */
   protected ArrayList occluders = new ArrayList();

   /** node used to gather and hold shadow volumes for rendering. */
   protected Node volumeNode = new Node("Volumes");

   /** whether or not the renderstates for this pass have been init'd yet. */
   protected boolean initialised = false;

   /**
    * A quad to use with MODULATIVE lightMethod for full screen darkening
    * against the shadow stencil.
    */
   protected Quad shadowQuad = null;

   /**
    * Used with MODULATIVE lightMethod. Defines the base color of the shadow -
    * the alpha value is replaced with 1 - the alpha of the light's alpha.
    */
   protected ColorRGBA shadowColor = new ColorRGBA(.2f,.2f,.2f,.1f);

   /** Whether shadow volumes are visible */
   protected boolean renderVolume = false;

   /** Whether to render shadows (true) or act like a normal RenderPass (false) */
   protected boolean renderShadows = true;

   /** Sets the type of pass to do to show shadows - ADDITIVE or MODULATIVE */
   protected int lightingMethod = ADDITIVE;

   /** collection of TriMesh to MeshShadows mappings */
   protected HashMap meshes = new HashMap();

   /**
    * list of occluders that will be casting shadows in this pass. If no
    * occluders set, pass acts like normal RenderPass.
    */
   protected ArrayList occluderMeshes = new ArrayList();

   /**
    * list of lights that will be used to calculate shadows in this pass.
    * Constructed dynamically by searching through the scene for lights with
    * shadowCaster set to true.
    */
   protected ArrayList shadowLights = new ArrayList();

   /**
    * a place to internally save previous enforced states setup before
    * rendering this pass
    */
   protected RenderState[] preStates = new RenderState[RenderState.RS_MAX_STATE];

   public static boolean rTexture = true;


   /**
    * <code>addOccluder</code> adds an occluder to this pass.
    *
    * @param toAdd
    *            Occluder Spatial to add to this pass.
    */
   public void addOccluder(Spatial toAdd) {
       occluders.add(toAdd);
   }

   /**
    * <code>clearOccluders</code> removes all occluders from this pass.
    */
   public void clearOccluders() {
       occluders.clear();
   }

   /**
    * <code>containsOccluder</code>
    *
    * @param s
    * @return
    */
   public boolean containsOccluder(Spatial s) {
       return occluders.contains(s);
   }

   /**
    * <code>removeOccluder</code>
    *
    * @param toRemove the Occluder Spatial to remove from this pass.
    * @return true if the Spatial was found and removed.
    */
   public boolean removeOccluder(Spatial toRemove) {
       return occluders.remove(toRemove);
   }

   /**
    * @return the number of occluders registered with this pass
    */
   public int occludersSize() {
       return occluders.size();
   }

   /**
    * @return Returns whether shadow volumes will be rendered to the display.
    */
   public boolean getRenderVolume() {
       return renderVolume;
   }

   /**
    * @param renderVolume
    *            sets whether shadow volumes will be rendered to the display
    */
   public void setRenderVolume(boolean renderVolume) {
       this.renderVolume = renderVolume;
   }

   /**
    * @return whether shadow volumes will be rendered to the display.
    */
   public boolean getRenderShadows() {
       return renderShadows;
   }

   /**
    * @param renderShadows
    *            whether shadows will be rendered by this pass.
    */
   public void setRenderShadows(boolean renderShadows) {
       this.renderShadows = renderShadows;
   }


   /**
    * @return the shadowColor used by MODULATIVE lightMethod.
    */
   public ColorRGBA getShadowColor() {
       return shadowColor;
   }


   /**
    * @param shadowColor
    *            the shadowColor used by MODULATIVE lightMethod.
    */
   public void setShadowColor(ColorRGBA shadowColor) {
       if (shadowColor == null)
           throw new IllegalArgumentException("shadowColor must not be null!");
       this.shadowColor = shadowColor;
   }


   /**
    * @return the lightingMethod currently in use.
    */
   public int getLightingMethod() {
       return lightingMethod;
   }


   /**
    * Sets which method to use with the shadow volume stencils in order to
    * generate shadows in the scene. See javadoc descriptions of ADDITIVE and
    * MODULATIVE for more info.
    *
    * @param lightingMethod
    *            method to use - ADDITIVE or MODULATIVE
    */
   public void setLightingMethod(int lightingMethod) {
       this.lightingMethod = lightingMethod;
   }


   /**
    * <code>doRender</code> renders this pass to the framebuffer
    *
    * @param r
    *            Renderer to use for drawing.
    * @see com.jme.renderer.pass.Pass#doRender(com.jme.renderer.Renderer)
    */
   public void doRender(Renderer r) {

       if (!renderShadows) {
           renderScene(r);
           return;
       }

       // init states
       init();

       // grab the shadowcasting lights
       getShadowLights();

       // grab the occluders
       getOccluderMeshes();

       // if no occluders or no shadow casting lights, just render the scene normally and return.
       if (occluderMeshes.size() == 0 || shadowLights.size() == 0) {
           //render normal
           renderScene(r);
           cleanup();
           return;
       } else {
           // otherwise render an ambient pass by masking the diffuse and specular of shadowcasting lights.
           if (lightingMethod == ADDITIVE) {
               maskShadowLights(LightState.MASK_DIFFUSE | LightState.MASK_SPECULAR);
               saveEnforcedStates();
               Spatial.enforceState(noTexture);
               renderScene(r);
               replaceEnforcedStates();
               unmaskShadowLights();
               GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
               GL11.glPolygonOffset(0.0f,-5.0f);
           } else {
               renderScene(r);
           }
       }

       generateVolumes();

       for (int l = shadowLights.size(); --l >= 0; ) {
           Light light = (Light)shadowLights.get(l);
           light.setEnabled(false);
       }
       for (int l = shadowLights.size(); --l >= 0; ) {
           Light light = (Light)shadowLights.get(l);
           // Clear out the stencil buffer
           r.clearStencilBuffer();
           light.setEnabled(true);

           saveEnforcedStates();
           Spatial.enforceState(noTexture);
           Spatial.enforceState(forTesting);
           Spatial.enforceState(colorDisabled);
           Spatial.enforceState(stencilFrontFaces);
           Spatial.enforceState(cullBackFace);

           volumeNode.getChildren().clear();
           addShadowVolumes(volumeNode, light);
           volumeNode.updateGeometricState(0, false);
           volumeNode.onDraw(r);

           Spatial.enforceState(stencilBackFaces);
           Spatial.enforceState(cullFrontFace);
           volumeNode.onDraw(r);

           Spatial.enforceState(colorEnabled);
           Spatial.enforceState(forColorPassTesting);
           Spatial.enforceState(cullBackFace);
           if (lightingMethod == ADDITIVE) {
               Spatial.enforceState(lights);
               Spatial.enforceState(blended);
               lights.detachAll();
               lights.attach(light);
               Spatial.enforceState(stencilDrawWhenNotSet);
               renderScene(r);
           } else {
               Spatial.enforceState(modblended);
               Spatial.enforceState(zbufferAlways);
               Spatial.enforceState(cullBackFace);
               Spatial.enforceState(noLights);
               Spatial.enforceState(stencilDrawOnlyWhenSet);

               shadowColor.a = 1 - light.getAmbient().a;
               shadowQuad.setDefaultColor(shadowColor);
               r.setOrtho();
               if (rTexture) shadowQuad.draw(r);
               r.unsetOrtho();
           }
           light.setEnabled(false);
           replaceEnforcedStates();
       }

       for (int l = shadowLights.size(); --l >= 0; ) {
           Light light = (Light)shadowLights.get(l);
           light.setEnabled(true);
       }

       if (lightingMethod == ADDITIVE && rTexture ) {
           saveEnforcedStates();
           Spatial.enforceState(noStencil);
           Spatial.enforceState(colorEnabled);
           Spatial.enforceState(cullBackFace);
           Spatial.enforceState(blendTex);
           renderScene(r);
           GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
           replaceEnforcedStates();
       }

       if (renderVolume) {
           drawVolumes(r);
       }

       cleanup();
   }

   protected void cleanup() {
       occluderMeshes.clear();
       shadowLights.clear();
   }


   protected void maskShadowLights(int mask) {
       for (int x = shadowLights.size(); --x >= 0; ) {
           Light l = (Light)shadowLights.get(x);
           l.pushLightMask();
           l.setLightMask(mask);
       }
   }

   protected void unmaskShadowLights() {
       for (int x = shadowLights.size(); --x >= 0; ) {
           Light l = (Light)shadowLights.get(x);
           l.popLightMask();
       }
   }


   protected void renderScene(Renderer r) {
       for (int i = 0, sSize = spatials.size(); i < sSize; i++) {
           Spatial s = (Spatial)spatials.get(i);
           s.onDraw(r);
       }
       r.renderQueue();
   }


   protected void getShadowLights() {
       if (shadowLights == null) shadowLights = new ArrayList();
       for (int x = occluders.size(); --x >= 0; )
           getShadowLights((Spatial)occluders.get(x));
   }

   protected void getShadowLights(Spatial s) {
       if ((s.getType() & Spatial.GEOMETRY) != 0) {
           Geometry g = (Geometry)s;
           LightState ls = (LightState)g.states[RenderState.RS_LIGHT];
           if (ls != null)
               for (int x = ls.getQuantity(); --x >= 0; ) {
                   Light l = ls.get(x);
                   if (l.isShadowCaster()
                           && (l.getType() == Light.LT_DIRECTIONAL ||
                                   l.getType() == Light.LT_POINT)
                           && !shadowLights.contains(l)) {
                       shadowLights.add(l);
                   }
               }
       }
       if ((s.getType() & Spatial.NODE) != 0) {
           Node n = (Node)s;
           ArrayList children = n.getChildren();
           for (int i = children.size(); --i >= 0; ) {
               Spatial child = (Spatial)children.get(i);
               getShadowLights(child);
           }
       }

   }

   protected void getOccluderMeshes() {
       if (occluderMeshes == null) occluderMeshes = new ArrayList();
       for (int x = occluders.size(); --x >= 0; )
           getOccluderMeshes((Spatial)occluders.get(x));
   }

   protected void getOccluderMeshes(Spatial spat) {
       if ((spat.getType() & Spatial.TRIMESH) != 0)
           occluderMeshes.add(spat);
       else if ((spat.getType() & Spatial.NODE) != 0) {
           Node node = (Node)spat;
           for (int c = 0, nQ = node.getQuantity(); c < nQ; c++) {
               Spatial child = (Spatial) node.getChild(c);
               getOccluderMeshes(child);
           }
       }

   }


   /**
    * saves any states enforced by the user for replacement at the end of the
    * pass.
    */
   protected void saveEnforcedStates() {
       for (int x = RenderState.RS_MAX_STATE; --x >= 0; ) {
           preStates[x] = Spatial.enforcedStateList[x];
       }
   }

   /**
    * replaces any states enforced by the user at the end of the pass.
    */
   protected void replaceEnforcedStates() {
       for (int x = RenderState.RS_MAX_STATE; --x >= 0; ) {
           Spatial.enforcedStateList[x] = preStates[x];
       }
   }

   protected void generateVolumes() {
       for (int c = 0; c < occluderMeshes.size(); c++) {
           TriMesh t = (TriMesh) occluderMeshes.get(c);
           if (!meshes.containsKey(t)) {
               meshes.put(t, new MeshShadows(t));
           }

           MeshShadows sv = (MeshShadows) meshes.get(t);

           //Create the geometry for the shadow volume
           sv.createGeometry((LightState)t.states[RenderState.RS_LIGHT]);
       }
   }

   /**
    * <code>addShadowVolumes</code> adds the shadow volumes for a specific
    * light to the given node
    *
    * @param shadowBaseNode
    *            the node to add shadow volumes to
    * @param light
    *            the light whose volumes should be added
    */
   protected void addShadowVolumes(Node shadowBaseNode, Light light) {
       if (enabled) {
           for (int i = occluderMeshes.size(); --i >= 0; ) {
               Object key = occluderMeshes.get(i);
               MeshShadows ms = (MeshShadows)meshes.get(key);
               ShadowVolume lv = ms.getShadowVolume(light);
               if (lv != null)
                   shadowBaseNode.getChildren().add(lv);
           }
       }

   }


   /**
    * <code>drawVolumes</code> is a debug method used to draw the shadow
    * volumes currently in use in the pass.
    *
    * @param r
    *            Renderer to draw with.
    */
   protected void drawVolumes(Renderer r) {

       Node renderNode = new Node("renderVolume");
       renderNode.setRenderState(cullBackFace);
       renderNode.setRenderState(forTesting);
       renderNode.setRenderState(colorEnabled);
       renderNode.setRenderState(noStencil);
       renderNode.setRenderState(r.createWireframeState());

       for (int i = occluderMeshes.size(); --i >= 0; ) {
           Object key = occluderMeshes.get(i);
           MeshShadows ms = (MeshShadows)meshes.get(key);
           ArrayList volumes = ms.getVolumes();
           for (int v = 0, vSize = volumes.size(); v < vSize; v++) {
               ShadowVolume vol = (ShadowVolume) volumes.get(v);
               renderNode.attachChild(vol);
               vol.setDefaultColor(ColorRGBA.green);
           }
       }

       renderNode.updateRenderState();
       renderNode.updateGeometricState(0, true);
       renderNode.onDraw(r);
   }

   protected static ZBufferState zbufferWriteLE;
   protected static ZBufferState zbufferAlways;
   protected static ZBufferState forTesting;
   protected static ZBufferState forColorPassTesting;

   protected static StencilState noStencil;
   protected static StencilState stencilFrontFaces;
   protected static StencilState stencilBackFaces;
   protected static StencilState stencilDrawOnlyWhenSet;
   protected static StencilState stencilDrawWhenNotSet;

   protected static CullState cullFrontFace;
   protected static CullState cullBackFace;
   protected static CullState noCull;

   protected static TextureState noTexture;

   protected static LightState lights;
   protected static LightState noLights;

   public static AlphaState blended;
   public static AlphaState modblended;
   public static AlphaState blendTex;

   protected static ColorMaskState colorEnabled;
   protected static ColorMaskState colorDisabled;

   protected void init() {
       if (initialised)
           return;

       initialised = true;

       Renderer r = DisplaySystem.getDisplaySystem().getRenderer();

       zbufferWriteLE = r.createZBufferState();
       zbufferWriteLE.setWritable(true);
       zbufferWriteLE.setFunction(ZBufferState.CF_LEQUAL);
       zbufferWriteLE.setEnabled(true);

       zbufferAlways = r.createZBufferState();
       zbufferAlways.setEnabled(false);
       zbufferAlways.setWritable(false);

       forTesting = r.createZBufferState();
       forTesting.setWritable(false);
       forTesting.setFunction(ZBufferState.CF_LESS);
       forTesting.setEnabled(true);

       forColorPassTesting = r.createZBufferState();
       forColorPassTesting.setWritable(false);
       forColorPassTesting.setFunction(ZBufferState.CF_LEQUAL);
       forColorPassTesting.setEnabled(true);

       noStencil = r.createStencilState();
       noStencil.setEnabled(false);

       stencilFrontFaces = r.createStencilState();
       stencilFrontFaces.setEnabled(true);
       stencilFrontFaces.setStencilMask(~0);
       stencilFrontFaces.setStencilFunc(StencilState.SF_ALWAYS);
       stencilFrontFaces.setStencilOpFail(StencilState.SO_KEEP);
       stencilFrontFaces.setStencilOpZFail(StencilState.SO_KEEP);
       stencilFrontFaces.setStencilOpZPass(StencilState.SO_INCR);

       stencilBackFaces = r.createStencilState();
       stencilBackFaces.setEnabled(true);
       stencilBackFaces.setStencilMask(~0);
       stencilBackFaces.setStencilFunc(StencilState.SF_ALWAYS);
       stencilBackFaces.setStencilOpFail(StencilState.SO_KEEP);
       stencilBackFaces.setStencilOpZFail(StencilState.SO_KEEP);
       stencilBackFaces.setStencilOpZPass(StencilState.SO_DECR);

       stencilDrawOnlyWhenSet = r.createStencilState();
       stencilDrawOnlyWhenSet.setEnabled(true);
       stencilDrawOnlyWhenSet.setStencilMask(~0);
       stencilDrawOnlyWhenSet.setStencilFunc(StencilState.SF_NOTEQUAL);
       stencilDrawOnlyWhenSet.setStencilOpFail(StencilState.SO_KEEP);
       stencilDrawOnlyWhenSet.setStencilOpZFail(StencilState.SO_KEEP);
       stencilDrawOnlyWhenSet.setStencilOpZPass(StencilState.SO_KEEP);
       stencilDrawOnlyWhenSet.setStencilRef(0);

       stencilDrawWhenNotSet = r.createStencilState();
       stencilDrawWhenNotSet.setEnabled(true);
       stencilDrawWhenNotSet.setStencilMask(~0);
       stencilDrawWhenNotSet.setStencilFunc(StencilState.SF_EQUAL);
       stencilDrawWhenNotSet.setStencilOpFail(StencilState.SO_KEEP);
       stencilDrawWhenNotSet.setStencilOpZFail(StencilState.SO_KEEP);
       stencilDrawWhenNotSet.setStencilOpZPass(StencilState.SO_KEEP);
       stencilDrawWhenNotSet.setStencilRef(0);

       cullFrontFace = r.createCullState();
       cullFrontFace.setEnabled(true);
       cullFrontFace.setCullMode(CullState.CS_FRONT);

       noCull = r.createCullState();
       noCull.setEnabled(false);

       noLights = r.createLightState();
       noLights.setEnabled(false);

       cullBackFace = r.createCullState();
       cullBackFace.setEnabled(true);
       cullBackFace.setCullMode(CullState.CS_BACK);

       blended = r.createAlphaState();
       blended.setEnabled(true);
       blended.setBlendEnabled(true);
       blended.setDstFunction(AlphaState.DB_ONE);
       blended.setSrcFunction(AlphaState.SB_DST_COLOR);

       modblended = r.createAlphaState();
       modblended.setEnabled(true);
       modblended.setBlendEnabled(true);
       modblended.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
       modblended.setSrcFunction(AlphaState.SB_DST_COLOR);

       blendTex = r.createAlphaState();
       blendTex.setEnabled(true);
       blendTex.setBlendEnabled(true);
       blendTex.setDstFunction(AlphaState.DB_ZERO);
       blendTex.setSrcFunction(AlphaState.SB_DST_COLOR);

       colorEnabled = r.createColorMaskState();
       colorEnabled.setAll(true);

       colorDisabled = r.createColorMaskState();
       colorDisabled.setAll(false);

       volumeNode.setRenderQueueMode(Renderer.QUEUE_SKIP);
       volumeNode.updateRenderState();

       noTexture = r.createTextureState();
       noTexture.setEnabled(false);

       shadowQuad = new Quad("RenderForeground", DisplaySystem
               .getDisplaySystem().getRenderer().getWidth(), DisplaySystem
               .getDisplaySystem().getRenderer().getHeight());
       shadowQuad.setLocalTranslation(new Vector3f(DisplaySystem
               .getDisplaySystem().getRenderer().getWidth() / 2, DisplaySystem
               .getDisplaySystem().getRenderer().getHeight() / 2, 0));
       shadowQuad.setRenderQueueMode(Renderer.QUEUE_SKIP);
       shadowQuad.updateGeometricState(0, true);
       shadowQuad.updateRenderState();

       lights = r.createLightState();
       lights.setEnabled(true);
       lights.setLightMask(LightState.MASK_AMBIENT | LightState.MASK_GLOBALAMBINET);
   }

}