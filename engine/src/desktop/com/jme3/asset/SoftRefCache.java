/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.asset;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 *
 * @author kobayasi
 */
public class SoftRefCache {
    private final HashMap<AssetKey, SoftReference> cache = new HashMap<AssetKey, SoftReference>();
    public void addToCache(AssetKey key, Object obj) {
        cache.put(key, new SoftReference(obj));
    }

    public void deleteAllAssets() {
        cache.clear();
    }

    public boolean deleteFromCache(AssetKey key) {
        return cache.remove(key) != null;
    }

    public Object getFromCache(AssetKey key) {
        SoftReference ref = cache.get(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

//    public AssetCache.SmartAssetInfo getFromSmartCache(AssetKey key) {
//        SoftReference ref = cache.get(key);
//        if (ref != null) {
//            return ref.get();
//        }
//        return null;
//    }
    
}
