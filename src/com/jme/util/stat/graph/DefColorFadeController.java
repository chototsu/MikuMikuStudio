package com.jme.util.stat.graph;

import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial.CullHint;

public class DefColorFadeController extends Controller {

    private static final long serialVersionUID = 1L;
    
    private Geometry target;
    private float targetAlpha;
    private float rate;
    private boolean dir;

    public DefColorFadeController(Geometry target, float targetAlpha, float rate) {
        this.target = target;
        this.targetAlpha = targetAlpha;
        this.rate = rate;
        this.dir = target.getDefaultColor().a > targetAlpha;
    }

    @Override
    public void update(float time) {
        float alpha = target.getDefaultColor().a; 

        alpha += rate * time;
        if (dir && alpha <= targetAlpha) {
            alpha = targetAlpha;
        } else if (!dir && alpha >= targetAlpha) {
            alpha = targetAlpha;
        }

        if (alpha != 0) {
            target.setCullHint(CullHint.Inherit);
        } else {
            target.setCullHint(CullHint.Always);
        }
        
        target.getDefaultColor().a = alpha;
        
        if (alpha == targetAlpha) {
            target.removeController(this);
        }
    }

}
