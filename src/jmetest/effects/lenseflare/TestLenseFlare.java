/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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
package jmetest.effects.lenseflare;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.glu.GLU;
import com.jme.app.VariableTimestepGame;
import com.jme.math.Vector2f;
import com.jme.scene.Text;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.Timer;
/**
 * <code>TestLenseFlare</code>
 * 
 * @author Ahmed Al-Hindawi
 * @version $Id: TestLenseFlare.java,v 1.4 2004-04-27 14:55:33 darkprophet Exp $
 */
public class TestLenseFlare extends VariableTimestepGame {
	private Timer timer;
	private Text fps;
	private Vector2f bigGlowPos;
	private ArrayList lenseFlarePos;
	private int numOfLF = 4;
	public static void main(String[] args) {
		LoggingSystem.getLogger().setLevel(java.util.logging.Level.OFF);
		TestLenseFlare app = new TestLenseFlare();
		app.setDialogBehaviour(FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
		app.start();
	}
	protected void update(float interpolation) {
		timer.update();
		//recalculateLenseFlare();
	}
	private void recalculateLenseFlare() {
		float diffX = bigGlowPos.x;
		float diffY = bigGlowPos.y;
		for (int i = numOfLF; i > 0; i--) {
			float temp = i / ((float) numOfLF);
			float x = diffX * temp;
			float y = diffY * temp;
			Vector2f v = new Vector2f(x, y);
			lenseFlarePos.add(v);
		}
		for (int i = 0; i < numOfLF; i++) {
			float temp = i / ((float) numOfLF * -1);
			float x = diffX * temp;
			float y = diffY * temp;
			Vector2f v = new Vector2f(x, y);
			lenseFlarePos.add(i + numOfLF, v);
		}
	}
	protected void render(float interpolation) {
		display.getRenderer().clearBuffers();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(-(properties.getWidth() / 2), properties.getWidth() / 2,
				-(properties.getHeight() / 2), properties.getHeight() / 2);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		// draw crap
		GL11.glColor4f(0, 0, 1, 1);
		GL11.glBegin(GL11.GL_QUADS);
		{
			Vector2f pos;
			for (int i = 0; i < lenseFlarePos.size(); i++) {
				pos = (Vector2f) lenseFlarePos.get(i);
				GL11.glVertex2f(pos.x - (i * 3), pos.y - (i * 3));
				GL11.glVertex2f(pos.x - (i * 3), pos.y + (i * 3));
				GL11.glVertex2f(pos.x + (i * 3), pos.y + (i * 3));
				GL11.glVertex2f(pos.x + (i * 3), pos.y - (i * 3));
				System.out.println("I: " + i + ", Size: " + (i * 2));
			}
		}
		GL11.glEnd();
		GL11.glColor4f(1, 0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2f(-2, -2);
			GL11.glVertex2f(-2, 2);
			GL11.glVertex2f(2, 2);
			GL11.glVertex2f(2, -2);
		}
		GL11.glEnd();
		// pop crap
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
	}
	protected void initSystem() {
		try {
			display = DisplaySystem.getDisplaySystem(properties.getRenderer());
			display.createWindow(properties.getWidth(), properties.getHeight(),
					properties.getDepth(), properties.getFreq(), properties
							.getFullscreen());
		} catch (JmeException e) {
			e.printStackTrace();
			System.exit(1);
		}
		display.setTitle("Test Lense Flare");
		timer = Timer.getTimer(properties.getRenderer());
	}
	protected void initGame() {
		lenseFlarePos = new ArrayList();
		bigGlowPos = new Vector2f(200, 200);
		lenseFlarePos.add(bigGlowPos);
		float diffX = bigGlowPos.x;
		float diffY = bigGlowPos.y;
		for (int i = numOfLF; i > 0; i--) {
			float temp = i / ((float) numOfLF);
			float x = diffX * temp;
			float y = diffY * temp;
			System.out.println("X" + i + ": " + x + "\tY" + i + ": " + y);
			Vector2f v = new Vector2f(x, y);
			lenseFlarePos.add(v);
		}
		for (int i = 0; i < numOfLF; i++) {
			float temp = i / ((float) numOfLF * -1);
			float x = diffX * temp;
			float y = diffY * temp;
			System.out.println("X" + i + ": " + x + "\tY" + i + ": " + y);
			Vector2f v = new Vector2f(x, y);
			lenseFlarePos.add(v);
		}
	}
	protected void reinit() {
	}
	protected void cleanup() {
	}
}