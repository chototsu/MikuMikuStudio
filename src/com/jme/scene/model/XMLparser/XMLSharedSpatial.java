package com.jme.scene.model.XMLparser;

import com.jme.scene.Spatial;
import com.jme.scene.Node;
import com.jme.scene.Controller;
import com.jme.scene.state.RenderState;

/**
 * Started Date: Jun 5, 2004
 * Dummy class only for use with SAXStackProcessor
 * 
 * @author Jack Lindamood
 */
class XMLSharedNode extends Node {
    String myIdent;
    Object whatIReallyAm;
    XMLSharedNode(String ident){
        super();
        myIdent=ident;
    }
    public int attachChild(Spatial c){
        whatIReallyAm=c;
        return 0;
    }

    public RenderState setRenderState(RenderState r){
        whatIReallyAm=r;
        return null;
    }

    public void addController(Controller c){
        whatIReallyAm=c;
    }
}