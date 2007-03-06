package com.jmex.model.collada;

import java.util.WeakHashMap;

import com.jmex.model.collada.schema.extraType;
import com.jmex.model.collada.schema.techniqueType5;

public class ExtraPluginManager {

	private static WeakHashMap<String, ExtraPlugin> plugins = new WeakHashMap<String, ExtraPlugin>();
	
	public static void registerExtraPlugin(String key, ExtraPlugin plugin) {
		plugins.put(key, plugin);
	}
	
	public static Object processExtra(Object target, extraType extra) throws Exception {
		if(extra.hastechnique()) {
			techniqueType5 tt = extra.gettechnique();
			
			if(tt.hasprofile()) {
				String key = tt.getprofile().toString();
				ExtraPlugin ep = plugins.get(key);
				if(ep != null) {
					return ep.processExtra(key, target, extra);
				}
			}
		}
		
		return null;
	}
}
