package com.jmex.font3d;

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.batch.SharedBatch;
import com.jme.scene.batch.TriangleBatch;


public class Glyph3DSharedBatch extends SharedBatch
{
	private static final long serialVersionUID = -8833674612016107162L;
	Vector3f glyphTranslation = new Vector3f();

	public Glyph3DSharedBatch(TriangleBatch batch)
	{
		super(batch);
	}

	/**
	 * draw renders the target mesh, at the translation, rotation and scale of
	 * this shared mesh.
	 * 
	 * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
	 */
	public void draw(Renderer r) {
		if (!r.isProcessingQueue()) {
			if (r.checkAndAdd(this))
				return;
		}
		
		// Notice that the translation depends on the glyph
		getTarget().getParentGeom().getWorldRotation().set(parentGeom.getWorldRotation());
		getTarget().getParentGeom().getWorldScale().set(parentGeom.getWorldScale());
		getTarget().getParentGeom().getWorldTranslation().set(glyphTranslation);
		// And set default colour
		getTarget().setDefaultColor(getDefaultColor());
		
		// We just get the display-ID of our target (if it has one)
		/*
    	setDisplayListID(getTarget().getDisplayListID());
        if(getDisplayListID() != -1)
        {
        	Geometry oldgeom = parentGeom;
        	this.parentGeom = getTarget().getParentGeom();
        	r.draw(this);
        	this.parentGeom = oldgeom;
        }
        else
        {
    		RenderState oldstates[] =  getTarget().states;
    		getTarget().states = this.states;
            //for(int i = 0; i < states.length; i++) {
        	//	getTarget().states[i] = this.states[i];
            //}
    		//logger.info(((TextureState)this.states[RenderState.RS_TEXTURE]).getTexture());
    		//logger.info(((LightState)this.states[RenderState.RS_LIGHT]));
            
        	r.draw(getTarget());
    		
        	getTarget().states = oldstates;
        }
        */
		r.draw(getTarget());
	}


    /**
     * this one should work as if the "target" was between us and our parent.
    @Override
	protected void applyRenderState(Stack[] states1) {
    	logger.info("applyRenderState(Stack[] states)");
    	super.applyRenderState(states1);
        // Then our selves.
        for (int x = 0; x < states1.length; x++) {
            if (states1[x].size() > 0) {
                this.states[x] = ((RenderState) states1[x].peek()).extract(
                        states1[x], this);
            } else {
                this.states[x] = Renderer.defaultStateList[x];
            }
        }
    }
     */

    /**
     * Works as if our target is just above us.
     * 
     * @param states
     *            The Stack[] to push states onto.
    @SuppressWarnings("unchecked")
    public void propagateStatesFromRoot(Stack[] states) {
    	logger.info(this+".propagateStatesFromRoot(Stack[] states)");
        // traverse to root to allow downward state propagation
        if (parentGeom != null)
            parentGeom.propagateStatesFromRoot(states);

        // Push our targets parent-geom states on (with no recursion)
        //getTarget().propagateStatesFromRoot(states);
        for (int x = 0; x < RenderState.RS_MAX_STATE; x++)
            if (getTarget().getParentGeom().getRenderState(x) != null)
                states[x].push(getTarget().getParentGeom().getRenderState(x));
        
        // Push our targets states on
        for (int x = 0; x < RenderState.RS_MAX_STATE; x++)
            if (getTarget().getRenderState(x) != null)
                states[x].push(getTarget().getRenderState(x));
        
        // push our states onto current render state stack
        for (int x = 0; x < RenderState.RS_MAX_STATE; x++)
            if (getRenderState(x) != null)
                states[x].push(getRenderState(x));
    }
     */
    
    /**
     * Called internally. Updates the render states of this SceneElement. The stack
     * contains parent render states.
     *
     * @param parentStates
     *            The list of parent renderstates.
    @SuppressWarnings("unchecked")
    protected void updateRenderState(Stack[] parentStates) {
    	logger.info(this+".updateRenderState(Stack[] parentStates)");
        boolean initiator = (parentStates == null);

        // first we need to get all the states from parent to us.
        if (initiator) {
            // grab all states from root to here.
            parentStates = new Stack[RenderState.RS_MAX_STATE];
            for (int x = 0; x < parentStates.length; x++)
                parentStates[x] = new Stack<RenderState>();
            propagateStatesFromRoot(parentStates);
        } else {
            for (int x = 0; x < RenderState.RS_MAX_STATE; x++) {
            	// We just need to get our target geom with
                if (getTarget().getParentGeom().getRenderState(x) != null)
                    parentStates[x].push(getTarget().getParentGeom().getRenderState(x));
                // We just need to get our target with us.
                if (getTarget().getRenderState(x) != null)
                    parentStates[x].push(getTarget().getRenderState(x));
                // Then we add our own
                if (getRenderState(x) != null)
                    parentStates[x].push(getRenderState(x));
            }
        }

        applyRenderState(parentStates);

        // restore previous if we are not the initiator
        if (!initiator) {
            for (int x = 0; x < RenderState.RS_MAX_STATE; x++) {
            	// Remove our "targets" parent geoms render-states.
                if (getTarget().getParentGeom().getRenderState(x) != null)
                    parentStates[x].pop();
            	// Remove our "targets" render-states.
                if (getTarget().getRenderState(x) != null)
                    parentStates[x].pop();
                // Remove our own render-states
                if (getRenderState(x) != null)
                    parentStates[x].pop();
            }
        }
    }
     */

}
