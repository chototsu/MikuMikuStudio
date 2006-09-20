package com.jme.util;

import java.util.ArrayList;

/**
 * A set of configuration describing how fields will be treated during the cloning process
 * including ignoring and shallow copying fields by name.
 *
 * @author kevin
 * @version $Id: CloneConfiguration.java,v 1.1 2006-09-20 19:22:58 llama Exp $
 */
public class CloneConfiguration {
        /** A configuration that specifies that all geometry buffers should be shared between copies */
        public static final CloneConfiguration SHARED_GEOM_BUFFER_CLONE = 
                                                                                        new CloneConfiguration(new String[] {},
                                                                                                                                   new String[] {"vertBuf","colorBuf", "texBuf","normBuf"});
        /** A configuration that specifies that color and texture buffers should be shared between copies */
        public static final CloneConfiguration SHARED_COLOR_AND_TEXTURE_BUFFER_CLONE = 
                                                                                        new CloneConfiguration(new String[] {},
                                                                                                                                   new String[] {"colorBuf", "texBuf"});
        
        /** The list of ignored fields */
        private ArrayList<String> ignored = new ArrayList<String>();
        /** THe list of fields that should only be shallow copied */
        private ArrayList<String> shallow = new ArrayList<String>();
        
        /**
         * Create a new empty clone configuration
         */
        public CloneConfiguration() {
        }
        
        /**
         * Create a configuration 
         * 
         * @param ignore The list of fields to ignore 
         * @param shal The list of fields to shallow copy
         */
        public CloneConfiguration(String[] ignore, String[] shal) {
                for (int i=0;i<ignore.length;i++) {
                        ignored.add(ignore[i]);
                }
                for (int i=0;i<shal.length;i++) {
                        shallow.add(shal[i]);
                }
        }
        
        /**
         * Add an ignored field
         * 
         * @param name The name of the field to ignore during the cloning process
         */
        public void addIgnoredField(String name) {
                ignored.add(name);
        }

        /**
         * Add a fied to be shallow copied
         * 
         * @param name The name of the field to ignore during the cloning process
         */
        public void addShallowCopyField(String name) {
                shallow.add(name);
        }
        
        /**
         * Get the list of fields to be ignored
         * 
         * @return The list of fields to be ignored
         */
        public ArrayList<String> getIgnored() {
                return ignored;
        }

        /**
         * Get the list of fields to be shallow copied
         * 
         * @return The list of fields to be shallow copied
         */
        public ArrayList<String> getShallow() {
                return shallow;
        }
}


