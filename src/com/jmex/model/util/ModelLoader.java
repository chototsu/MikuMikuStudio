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

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.export.Savable;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.load.LoadingGameState;
import com.jmex.model.collada.ColladaImporter;
import com.jmex.model.converters.AseToJme;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;
import com.jmex.model.converters.Md2ToJme;
import com.jmex.model.converters.Md3ToJme;
import com.jmex.model.converters.MilkToJme;
import com.jmex.model.converters.ObjToJme;

/**
 * This is a utility that will prompt for a model file and then load it into
 * a scene.
 * 
 * @author Matthew D. Hicks
 * @author Alexander J. Gilpin
 */
public class ModelLoader {
    private static final Logger logger = Logger.getLogger(ModelLoader.class
            .getName());
    
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
						logger.logp(Level.SEVERE, ModelLoader.class.toString(),
                                "main(args)", "Exception", exc);
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
						modelNode.updateRenderState();
						if (file.getName().toLowerCase().endsWith(".jme")) {
							loading.setProgress(1.0f, "Loaded Successfully");
						} else {
							loading.setProgress(0.8f, "Loaded Successfully - Saving");
							try {
								BinaryExporter.getInstance().save(modelNode, createJMEFile(file.getAbsoluteFile()));
								loading.setProgress(1.0f, "Binary File Written Successfully");
							} catch(IOException exc) {
								logger.logp(Level.SEVERE, ModelLoader.class.toString(),
                                        "main(args)", "Exception", exc);
								loading.setProgress(0.9f, "Binary Save Failure");
								try {
									Thread.sleep(5000);
								} catch(InterruptedException exc2) {
                                    logger.logp(Level.SEVERE, ModelLoader.class.toString(),
                                            "main(args)", "Exception", exc2);
								}
								loading.setProgress(1.0f);
							}
						}
                        debug.getRootNode().attachChild( scale( modelNode ) );
                        debug.getRootNode().updateRenderState();
                    } else {
						loading.setProgress(0.9f, "Model Not Loaded");
						try {
							Thread.sleep(5000);
						} catch(InterruptedException exc) {
							logger.logp(Level.SEVERE, ModelLoader.class.toString(),
                                    "main(args)", "Exception", exc);
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
	
	private static File createJMEFile(File f) {
		String filename = f.getName();
		if (filename.indexOf('.') != -1) {
			filename = filename.substring(0, filename.lastIndexOf('.'));
		}
		filename = filename + ".jme";
		return new File(f.getParentFile(), filename);
	}
	
	private static void outputElapsed(long startTime) {
		float elapsed = (System.currentTimeMillis() - startTime) / 1000.0f;
		logger.info("Took " + elapsed + " seconds to load the model.");
	}

    public static Node loadModel( final File file ) throws Exception {
    	// Add to resource locator
    	SimpleResourceLocator locator = new SimpleResourceLocator(file.getParentFile().toURI());
        ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
    	
        String extension = extensionOf( file );

        ModelLoaderCallable callable = loaders.get( extension );
        if ( callable == null ) {
            throw new UnsupportedOperationException( "Unknown file type: " + file.getName() );
        }
        callable.setFile( file );
        Future<Node> future = GameTaskQueueManager.getManager().update( callable );
        return future.get();
    }

    private static String extensionOf( File file ) {
        String fileName = file.getName().toUpperCase();
        int lastDot = fileName.lastIndexOf( '.' );
        String extension;
        if ( lastDot >= 0 ) {
            extension = fileName.substring( lastDot + 1 );
        } else {
            extension = "";
        }
        return extension;
    }

    private static Node scale( Node model ) {
        if ( model != null ) {
            // scale model to maximum extent of 5.0
            model.updateGeometricState( 0, true );
            BoundingVolume worldBound = model.getWorldBound();
            if ( worldBound == null ) {
                model.setModelBound( new BoundingBox() );
                model.updateModelBound();
                model.updateGeometricState( 0, true );
                worldBound = model.getWorldBound();
            }
            if ( worldBound != null ) // check not still null (no geoms)
            {
                Vector3f center = worldBound.getCenter();
                BoundingBox boundingBox = new BoundingBox( center, 0, 0, 0 );
                boundingBox.mergeLocal( worldBound );
                Vector3f extent = boundingBox.getExtent( null );
                float maxExtent = Math.max( Math.max( extent.x, extent.y ), extent.z );
                if ( maxExtent != 0 ) {
                    Node scaledModel = new Node( "scaled model" );
                    scaledModel.attachChild( model );
                    scaledModel.setLocalScale( 5.0f / maxExtent );
                    model = scaledModel;
                }
            }
        }
        return model;
    }

    public static boolean isValidModelFile( File file ) {
        return loaders.containsKey( extensionOf( file ) );
    }

    private static Map<String, ModelLoaderCallable> loaders = new HashMap<String, ModelLoaderCallable>();

    static {
        loaders.put( "DAE", new DAECallable() );
        loaders.put( "JME", new JMECallable() );
//	            Note that .OBJ Ambient colors are multiplied. I would strongly suggest making them black.
        loaders.put( "OBJ", new OBJCallable() );
//	            Note that some .3DS Animations from Blender may not work. I'd suggest using a version of 3DSMax.
        loaders.put( "3DS", new TDSCallable() );
        loaders.put( "ASE", new ASECallable() );
        loaders.put( "MD2", new MD2Callable() );
        loaders.put( "MD3", new MD3Callable() );
        loaders.put( "MS3D", new MilkCallable() );
    }

    public interface ModelLoaderCallable extends Callable<Node> {
        public void setFile( File file );
    }

    private static class JMECallable implements ModelLoaderCallable {
        private File file;

        public void setFile( File file ) {
            this.file = file;
        }

        public Node call() throws Exception {
            return (Node) BinaryImporter.getInstance().load( file );
        }
    }

    private static class DAECallable implements ModelLoaderCallable {
        private File file;

        public void setFile( File file ) {
            this.file = file;
        }

        public Node call() throws Exception {
            ColladaImporter.load( file.toURI().toURL().openStream(), "Model" );
            Node model = ColladaImporter.getModel();
            return model;
        }
    }

    private static class OBJCallable implements ModelLoaderCallable {
        private File file;

        public void setFile( File file ) {
            this.file = file;
        }

        public Node call() throws Exception {
            FormatConverter converter = new ObjToJme();
            URL url = file.toURI().toURL();
            converter.setProperty( "mtllib", url );
            converter.setProperty( "texdir", url );
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            converter.convert( url.openStream(), bos );
            Savable savable = BinaryImporter.getInstance().load( new ByteArrayInputStream( bos.toByteArray() ) );
            if ( savable instanceof Node ) {
                return (Node) savable;
            } else {
                Node model = new Node( "Imported Model " + file.getName() );
                model.attachChild( (Spatial) savable );
                return model;
            }
        }
    }

    private static class TDSCallable implements ModelLoaderCallable {
        private File file;

        public void setFile( File file ) {
            this.file = file;
        }

        public Node call() throws Exception {
            FormatConverter converter = new MaxToJme();
            URL url = file.toURI().toURL();
            converter.setProperty( "texurl", url );
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            converter.convert( url.openStream(), bos );
            Node model = (Node) BinaryImporter.getInstance().load( new ByteArrayInputStream( bos.toByteArray() ) );
            return model;
        }
    }

    private static class ASECallable implements ModelLoaderCallable {
        private File file;

        public void setFile( File file ) {
            this.file = file;
        }

        public Node call() throws Exception {
            FormatConverter converter = new AseToJme();
            URL url = file.toURI().toURL();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            converter.convert( url.openStream(), bos );
            Node model = (Node) BinaryImporter.getInstance().load( new ByteArrayInputStream( bos.toByteArray() ) );
            return model;
        }
    }

    private static class MD2Callable implements ModelLoaderCallable {
        private File file;

        public void setFile( File file ) {
            this.file = file;
        }

        public Node call() throws Exception {
            FormatConverter converter = new Md2ToJme();
            URL url = file.toURI().toURL();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            converter.convert( url.openStream(), bos );
            Node model = (Node) BinaryImporter.getInstance().load( new ByteArrayInputStream( bos.toByteArray() ) );
            return model;
        }
    }

    private static class MD3Callable implements ModelLoaderCallable {
        private File file;

        public void setFile( File file ) {
            this.file = file;
        }

        public Node call() throws Exception {
            FormatConverter converter = new Md3ToJme();
            URL url = file.toURI().toURL();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            converter.convert( url.openStream(), bos );
            Node model = (Node) BinaryImporter.getInstance().load( new ByteArrayInputStream( bos.toByteArray() ) );
            return model;
        }
    }

    private static class MilkCallable implements ModelLoaderCallable {
        private File file;

        public void setFile( File file ) {
            this.file = file;
        }

        public Node call() throws Exception {
            FormatConverter converter = new MilkToJme();
            URL url = file.toURI().toURL();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            converter.convert( url.openStream(), bos );
            Node model = (Node) BinaryImporter.getInstance().load( new ByteArrayInputStream( bos.toByteArray() ) );
            return model;
        }
    }
}
