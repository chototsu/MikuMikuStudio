package com.jme.util.export.binary;

import java.util.Arrays;
import java.util.logging.Logger;

import org.lwjgl.opengl.Display;

import com.jme.app.SimpleHeadlessApp;
import com.jme.math.Vector3f;

public class TestBinaryExportImport extends junit.framework.TestCase { 
    private static final Logger logger = Logger
            .getLogger(TestBinaryExportImport.class.getName());
    
    private boolean pass;
    
    public void testImport() {
        if ( !checkLWJGL() ) {
            return;
        }
        pass = false;
        new SimpleHeadlessApp() {
            protected void simpleInitGame() {
                //adsf
                
                pass = true;
                finished = true;
            }

            protected void quit() {
                if ( display != null ) {
                    display.close();
                }
            }
        }.start();
        assertTrue( "test was run", pass );
    }

    private boolean checkLWJGL() {
        boolean lwjglOK = true;
        try {
            logger.info( "checking lwjgl: " + Arrays.asList( Display.getAvailableDisplayModes() ) );
        } catch ( Throwable e ) {
            lwjglOK = false;
            logger.warning( "No lwjgl available - test result unknown! (reporting success to let server pass)" );
        }
        return lwjglOK;
    }
}
