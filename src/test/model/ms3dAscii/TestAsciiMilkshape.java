/*
 * Copyright (c) 2003 najgl Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3) Neither the name of 'najgl' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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

package test.model.ms3dAscii;

import jme.geometry.model.Model;
import jme.geometry.model.ms.MilkshapeModel;

import org.lwjgl.Display;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCaps;
import org.lwjgl.opengl.GLU;
import org.lwjgl.opengl.Window;

/**
 * A basic lwjgl game skeleton.  Starts up opengl.  The game loop executes
 * forever, or until the escape key is pressed or the window is closed.  Can
 * be run in windowed or fullscreen mode.
 *
 * The demo will load a Milkshape 3D model, rotate it around the y-axis, and
 * play its animation.
 *
 * @author naj
 * @version 0.1
 */
public class TestAsciiMilkshape {

    private static final boolean WIREFRAME = false;
    private static final boolean ANIMATED = true;

    private static boolean fullscreen = false;
    private static String modelFilename = "data/run.txt";
//    private static String modelFilename = "data/diablo.txt";

    private static boolean finished;
    private static float yrot;
    private static int count;
    private static Model model;

    public static float dt = 0.2f;

    public static void main(String[] args) {
        if (args.length == 1) {
            modelFilename = args[0];
        } else if (args.length == 2) {
            modelFilename = args[0];
            fullscreen = (args[1] != null && args[1].equalsIgnoreCase("fullscreen")) ? true : false;
        } else {
            System.out.println("Usage: java -jar example1.jar <filename> [fullscreen]");
            System.out.println("      - filename must be a ms3d ascii text file");
            System.out.println("      - add the optional word 'fullscreen' for fullscreen mode");
            System.exit(1);
        }
        model = new MilkshapeModel(ANIMATED);
        try {
            init();
            while (!finished) {
                Keyboard.poll();
                mainLoop();
                render();
                Window.update();
                Window.paint();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            cleanup();
        }
    }

    /**
     * Start up and initialize OpenGL.
     */
    private final static void init() throws Exception {
        if (fullscreen) {
            Window.create("Milkshape Model Animation (Fullscreen)", 16, 0, 8, 0);
        } else {
            Window.create("Milkshape Model Animation", 50, 50, 640, 480, 16, 0, 8, 0);
        }
        Keyboard.create();

        model.load(modelFilename);

        if (WIREFRAME) {
            GL.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        } else {
            GL.glEnable(GL.GL_TEXTURE_2D);
            GL.glShadeModel(GL.GL_SMOOTH);
        }
        GL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL.glClearDepth(1.0);
        GL.glEnable(GL.GL_DEPTH_TEST);
        GL.glDepthFunc(GL.GL_LEQUAL);

        GL.glMatrixMode(GL.GL_PROJECTION);
        GL.glLoadIdentity();

        GLU.gluPerspective(45.0f, (float) Display.getWidth() / (float) Display.getHeight(), 100.0f, 2000.0f);
        GL.glMatrixMode(GL.GL_MODELVIEW);

        GL.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        GLCaps.determineAvailableExtensions();
        if (GLCaps.WGL_EXT_swap_control) {
            GL.wglSwapIntervalEXT(1);
        }
    }

    /**
     *  Rendering method.
     */
    private final static void render() {
        if (!fullscreen && count++ > 20) {
            Window.setTitle("Milkshape Model Animation");
            count = 0;
        }

        GL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        GL.glLoadIdentity();
        GLU.gluLookAt(50, 0, 300, 0, 0, 0, 0, 1, 0);
        GL.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
        yrot += 0.3f;
        model.render();
    }

    /**
     * Main loop.
     */
    private final static void mainLoop() {
        processKeyboard();
        processWindow();
    }

    /**
     * Process keyboard events.
     */
    private final static void processKeyboard() {
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            finished = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            dt += 0.01;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            dt -= 0.01; dt = Math.max(0.05f, dt);
        }
    }

    /**
     * Process window events.
     */
    private final static void processWindow() {
        if (Window.isCloseRequested()) {
            finished = true;
        }
    }

    /**
     * Cleanup.
     */
    private final static void cleanup() {
        Keyboard.destroy();
        Window.destroy();
    }

}