/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
package com.jme.effects.transients;

import com.jme.image.Texture;
import com.jme.scene.Controller;

/**
 * <code>FadeInOutController</code>
 * 
 * @author Ahmed
 * @version $Id: FadeInOutController.java,v 1.1 2004-04-04 16:48:10 darkprophet Exp $
 */
public class FadeInOutController extends Controller {

	private FadeInOut fio;
	private float time;
	private float counter;
	private int currentStage;
	private boolean isFirst;
	private Texture texOut, texIn, texEffect;

	public FadeInOutController(FadeInOut f) {
		fio = f;
		currentStage = 0;
		isFirst = true;
	}

	public void update(float timeF) {
		fio.getTextureRenderer().render(fio.getRemoveNode(), texOut);
		fio.getTextureRenderer().render(fio.getInsertNode(), texIn);
		time = timeF * fio.getSpeed();
		counter++;
		counter *= time;

		if (fio.getCurrentStage() == 0) {
			fio.getColour().a += 0.01f;
		}else if (fio.getCurrentStage() == 1) {
			fio.getColour().a -= 0.01f;
		}
	}

}
