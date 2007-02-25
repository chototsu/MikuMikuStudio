/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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
package com.jmex.model.util;

import java.awt.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.prefs.*;

import javax.swing.*;

import com.jme.bounding.*;
import com.jme.image.*;
import com.jme.scene.*;
import com.jme.util.*;
import com.jme.util.export.binary.*;
import com.jmex.game.*;
import com.jmex.game.state.*;
import com.jmex.game.state.load.*;
import com.jmex.model.XMLparser.Converters.*;
import com.jmex.model.collada.*;

/**
 * This is a utility that will prompt for a model file and then load it into
 * a scene.
 * 
 * @author Matthew D. Hicks
 * @author Alexander J. Gilpin
 */
public class ModelLoader {
	public static void main(String[] args) {
		// Store the texture in the binary file
//		Texture.DEFAULT_STORE_TEXTURE = true;
		
		try {
			JFileChooser chooser = new JFileChooser();
			Preferences preferences = Preferences.userNodeForPackage(ModelLoader.class);
			File directory = new File(preferences.get("StartDirectory", "."));
			chooser.setCurrentDirectory(directory);
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (isValidModelFile(file)) {
					// Set it in preferences so we remember next time
					preferences.put("StartDirectory", file.getAbsolutePath());
					
					StandardGame game = new StandardGame("Model Loader");
					try {
						game.getSettings().clear();
					} catch(Exception exc) {
						exc.printStackTrace();
					}
					game.start();
					
					GameTaskQueueManager.getManager().update(new Callable<Object>() {
						public Object call() throws Exception {
							//MouseInput.get().setCursorVisible(true);
							return null;
						}
					});
					
					DebugGameState debug = new DebugGameState();
					GameStateManager.getInstance().attachChild(debug);
					debug.setActive(true);
					
					LoadingGameState loading = new LoadingGameState();
					GameStateManager.getInstance().attachChild(loading);
					loading.setActive(true);
					loading.setProgress(0.5f, "Loading Model: " + file.getName());
					long time = System.currentTimeMillis();
					final Node modelNode = loadModel(file);
					outputElapsed(time);
					if (modelNode != null) {
						//modelNode.setLocalScale(0.2f);
						modelNode.setModelBound(new BoundingBox());
						modelNode.updateModelBound();
						modelNode.updateRenderState();
						debug.getRootNode().attachChild(modelNode);
						debug.getRootNode().updateRenderState();
						if (file.getName().toLowerCase().endsWith(".jme")) {
							loading.setProgress(1.0f, "Loaded Successfully");
						} else {
							loading.setProgress(0.8f, "Loaded Successfully - Saving");
							try {
								BinaryExporter.getInstance().save(modelNode, createJMEFile(file.getAbsoluteFile()));
								loading.setProgress(1.0f, "Binary File Written Successfully");
							} catch(IOException exc) {
								exc.printStackTrace();
								loading.setProgress(0.9f, "Binary Save Failure");
								try {
									Thread.sleep(5000);
								} catch(InterruptedException exc2) {
									exc2.printStackTrace();
								}
								loading.setProgress(1.0f);
							}
						}
					} else {
						loading.setProgress(0.9f, "Model Not Loaded");
						try {
							Thread.sleep(5000);
						} catch(InterruptedException exc) {
							exc.printStackTrace();
						}
						loading.setProgress(1.0f);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Selected file's extension is unknown model type: " + file.getName());
				}
			}
		} catch(Throwable t) {
			StringWriter writer = new StringWriter();
			PrintWriter stream = new PrintWriter(writer);
			t.printStackTrace(stream);
			JFrame frame = new JFrame();
			frame.setTitle("ModelLoader - StackTrace");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JTextPane panel = new JTextPane();
			panel.setPreferredSize(new Dimension(400, 400));
			panel.setContentType("text/plain");
			panel.setText(writer.getBuffer().toString());
			frame.setContentPane(new JScrollPane(panel));
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
	}
	
	private static final File createJMEFile(File f) {
		String filename = f.getName();
		if (filename.indexOf('.') != -1) {
			filename = filename.substring(0, filename.lastIndexOf('.'));
		}
		filename = filename + ".jme";
		return new File(f.getParentFile(), filename);
	}
	
	private static final void outputElapsed(long startTime) {
		float elapsed = (System.currentTimeMillis() - startTime) / 1000.0f;
		System.out.println("Took " + elapsed + " seconds to load the model.");
	}
	
	public static final Node loadModel(final File file) throws Exception {
		String filename = file.getName().toUpperCase();
		Node model = null;
		try {
			if (filename.endsWith(".DAE")) {
				Future<Node> future = GameTaskQueueManager.getManager().update(new Callable<Node>() {
					public Node call() throws Exception {
						ColladaImporter.load(file.toURI().toURL().openStream(), file.getAbsoluteFile().getParentFile().toURI().toURL(), "Model");
						Node model = ColladaImporter.getModel();
						return model;
					}
				});
				model = future.get();
			} else if (filename.endsWith(".JME")) {
				model = (Node)BinaryImporter.getInstance().load(file);
			} else if (filename.endsWith(".OBJ")) {
//	            Note that .OBJ Ambient colors are multiplied. I would strongly suggest making them black.
	            Future<Node> future = GameTaskQueueManager.getManager().update(new Callable<Node>() {
	               public Node call() throws Exception {
	                  FormatConverter converter = new ObjToJme();
	                  converter.setProperty("mtllib", file.getParentFile().toURI().toURL());
	                  converter.setProperty("texdir", file.getParentFile().toURI().toURL());
	                  ByteArrayOutputStream bos = new ByteArrayOutputStream();
	                  converter.convert(file.toURI().toURL().openStream(), bos);
	                  if (BinaryImporter.getInstance().load(new ByteArrayInputStream(bos.toByteArray())).getClass() == TriMesh.class) {
	                     Node model = new Node("Imported Model " + file.getName());
	                     System.out.println("Trimesh! Attaching the Trimesh to the model Node!");
	                     model.attachChild((Spatial)BinaryImporter.getInstance().load(new ByteArrayInputStream(bos.toByteArray())));
	                     return model;
	                  } else {
	                     System.out.println("NOT a Trimesh! Directly converting to a Node!");
	                     Node model = (Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(bos.toByteArray()));
	                     return model;
	                  }
	               }
	            });
	            model = future.get();
	         } else if (filename.endsWith(".3DS")) {
//	            Note that some .3DS Animations from Blender may not work. I'd suggest using a version of 3DSMax.
	            Future<Node> future = GameTaskQueueManager.getManager().update(new Callable<Node>() {
	               public Node call() throws Exception {
	                  FormatConverter converter = new MaxToJme();
	                  ByteArrayOutputStream bos = new ByteArrayOutputStream();
	                  converter.convert(file.toURI().toURL().openStream(), bos);
	                  Node model = (Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(bos.toByteArray()));
	                  return model;
	               }
	            });
	            model = future.get();
			}
		} catch(IOException exc) {
			exc.printStackTrace();
		}
		return model;
	}
	
	public static final boolean isValidModelFile(File file) {
		String filename = file.getName().toUpperCase();
		if (filename.endsWith(".DAE")) return true;
		else if (filename.endsWith(".JME")) return true;
		else if (filename.endsWith(".OBJ")) return true;
		else if (filename.endsWith(".3DS")) return true;
		return false;
	}
}
