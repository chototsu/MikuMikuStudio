/*
 * Created on Jun 1, 2004
 */
package com.jme.effects;

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;

/**
 * @author Ahmed
 */
public class Shake extends Controller {

    public static final int RETURN_TO_ORIGIN = 0;

    public static final int RANDOMISE = 1;

    private int currentType;

    private float amount;

    private float random;

    private boolean goingBackToActualLock;

    private Spatial child;

    public Shake(Spatial spat, float amount) {
        this.amount = amount;
        this.setActive(false);
        goingBackToActualLock = true;
        this.child = spat;
    }

    public void setType(int type) {
        currentType = type;
    }

    public void update(float time) {
        if (isActive()) {

            Vector3f loc = child.getLocalTranslation();
            if (currentType == RETURN_TO_ORIGIN) {
                if (goingBackToActualLock) {
                    loc.x -= ((random * amount) - (amount / 2));
                    loc.y -= ((random * amount) - (amount / 2));
                    loc.z -= ((random * amount) - (amount / 2));
                    goingBackToActualLock = false;
                } else {
                    random = (float) Math.random();
                    loc.x += ((random * amount) - (amount / 2));
                    loc.y += ((random * amount) - (amount / 2));
                    loc.z += ((random * amount) - (amount / 2));

                    child.setLocalTranslation(loc);
                    goingBackToActualLock = true;
                }
            } else if (currentType == RANDOMISE) {
                random = (float) Math.random();
                loc.x += ((random * amount) - (amount / 2));
                loc.y += ((random * amount) - (amount / 2));
                loc.z += ((random * amount) - (amount / 2));

                child.setLocalTranslation(loc);
            }
        }
    }

}