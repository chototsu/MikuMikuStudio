/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jme.scene.state.lwjgl;

import org.lwjgl.opengl.GL11;

import com.jme.scene.state.AttributeState;

/**
 * <code>LWJGLAttributeState</code>
 * @author Mark Powell
 * @version $Id: LWJGLAttributeState.java,v 1.3 2004-04-22 22:26:57 renanse Exp $
 */
public class LWJGLAttributeState extends AttributeState {
  int[] glAttributeState = {
      GL11.GL_ALL_ATTRIB_BITS,
      GL11.GL_ACCUM_BUFFER_BIT,
      GL11.GL_COLOR_BUFFER_BIT,
      GL11.GL_CURRENT_BIT,
      GL11.GL_DEPTH_BUFFER_BIT,
      GL11.GL_ENABLE_BIT,
      GL11.GL_EVAL_BIT,
      GL11.GL_FOG_BIT,
      GL11.GL_HINT_BIT,
      GL11.GL_LIGHTING_BIT,
      GL11.GL_LINE_BIT,
      GL11.GL_LIST_BIT,
      GL11.GL_PIXEL_MODE_BIT,
      GL11.GL_POINT_BIT,
      GL11.GL_POLYGON_BIT,
      GL11.GL_POLYGON_STIPPLE_BIT,
      GL11.GL_SCISSOR_BIT,
      GL11.GL_STENCIL_BUFFER_BIT,
      GL11.GL_TEXTURE_BIT,
      GL11.GL_TRANSFORM_BIT,
      GL11.GL_VIEWPORT_BIT
  };

  /** <code>set</code>
   *
   * @see com.jme.scene.state.RenderState#set()
   */
  public void apply() {
    if (isEnabled()) {
      GL11.glPushAttrib(glAttributeState[getMask()]);
      level++;
    } else if (level > 0) {
      GL11.glPopAttrib();
      level--;
    }
  }

}
