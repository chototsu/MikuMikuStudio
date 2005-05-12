package com.jmex.model;

import com.jme.scene.Node;
import com.jme.system.JmeException;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;

/**
 * Generic abstract Loader class for FileFormat Loaders to
 * inherit from to standardize file loading.  Future file loaders should
 * extend this class.
 * @author Jack Lindamood
 */
public abstract class Loader {
    public long loadFlags=LOAD_ALL;
    public final static long LOAD_CONTROLLERS=1;
    public final static long LOAD_ALL=2*2*2*2*2*2*2*2-1;
    public final static long PRECOMPUTE_BOUNDS=2;
    protected boolean dirty=false;
    protected URL baseUrl;


    /**
     * Default Constructor.  Flags are LOAD_ALL by default.
     */
    public Loader(){
    }

    /**
     * Constructs file loader with given flags.
     * @param flags The flags for this file loader
     */
    public Loader(int flags){
        loadFlags=flags;
    }

    /**
     * Sets the base path to load textures from.
     * @param path New texture path
     */
    public void setBase(URL path){
        baseUrl=path;
    }
    /**
     * Returns base path for textures
     * @return URL to base path
     */
    public URL getBase(){
        return baseUrl;
    }

    /**
     * Returns a copy of the previously loaded file.  The two should
     * be able to operate separately acording to the needs of the loader,
     * but are allowed and encouraged to share as much as posible.
     * @return A node to the new copy
     */
    public abstract Node fetchCopy();
    /**
     * Loads a MilkShape file from the path in the string s.  All texture/alpha
     * maps associated with the file are by default in the same directory as the
     * .ms3d file specified.  Texture directory can be changed by a call to
     * setBasePath(String), allowing the programmer to seperate storage of model
     * files and pictures.
     *
     * @param s Filename
     * @throws JmeException Either .ms3d file or texture files don't exist
     * @return Node to the loaded file.
     * @see Node
     */
    public Node load(String s){
        try {
            return load(new File(s).toURI().toURL());
        } catch (MalformedURLException e) {
            throw new JmeException("Couldn't find file in load(String): " + e.getMessage());
        }
    }

    /**
     * Loads a URL, similar to <code>load(String s)</code>
     * @param url URL to load
     * @return Node to the loaded file
     */
    public abstract Node load(URL url);

    /**
     * Sets the give flag to true
     * @param flag New flag to set
     */
    public void setLoadFlag(long flag) {
        loadFlags|=flag;
        dirty=true;
    }
    /**
     * Removes the given flag, setting it to false
     * @param flag The flag to remove
     */
    public void removeLoadFlag(long flag){
        loadFlags&=~flag;
        dirty=true;
    }
    /**
     * Returns the current flag state, as a long
     * @return long to represent current flag state
     */
    public long getLoadFlags(){
        return loadFlags;
    }

}
